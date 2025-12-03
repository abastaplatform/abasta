package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyRequestDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.User;
import cat.abasta_back_end.exceptions.BadRequestException;
import cat.abasta_back_end.exceptions.DuplicateResourceException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.CompanyRepository;
import cat.abasta_back_end.repositories.UserRepository;
import cat.abasta_back_end.services.CompanyService;
import cat.abasta_back_end.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static cat.abasta_back_end.entities.User.UserRole.ADMIN;

/**
 * Implementació del servei de gestió d'empreses
 * Conté la lògica de negoci per operacions CRUD d'empresa.
 * Inclou validacions de duplicitat, registre com a administrador i conversions entre entitats i DTOs.
 *
 * @author Dani Garcia
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CompanyResponseDTO registerCompanyWithAdmin(CompanyRegistrationDTO registrationDTO) {

        // Validar que el taxId no existeixi
        if (companyRepository.existsByTaxId(registrationDTO.getTaxId())) {
            throw new DuplicateResourceException("El CIF/NIF ja està registrat");
        }

        // Validar que l'email del admin no existeixi
        if (userRepository.existsByEmail(registrationDTO.getAdminEmail())) {
            throw new DuplicateResourceException("L'email de l'administrador ja està registrat");
        }

        // 1. Crear l'empresa
        Company company = Company.builder()
                .name(registrationDTO.getCompanyName())
                .taxId(registrationDTO.getTaxId())
                .email(registrationDTO.getCompanyEmail())
                .phone(registrationDTO.getCompanyPhone())
                .address(registrationDTO.getCompanyAddress())
                .city(registrationDTO.getCompanyCity())
                .postalCode(registrationDTO.getCompanyPostalCode())
                .status(Company.CompanyStatus.PENDING)
                .build();

        company = companyRepository.save(company);

        // 2. Crear l'usuari administrador
        String verificationToken = UUID.randomUUID().toString();

        User admin = User.builder()
                .company(company)
                .email(registrationDTO.getAdminEmail())
                .password(passwordEncoder.encode(registrationDTO.getAdminPassword()))
                .firstName(registrationDTO.getAdminFirstName())
                .lastName(registrationDTO.getAdminLastName())
                .phone(registrationDTO.getAdminPhone())
                .role(User.UserRole.ADMIN)
                .isActive(true)
                .emailVerified(false)
                .emailVerificationToken(verificationToken)
                .emailVerificationExpires(LocalDateTime.now().plusHours(24))
                .build();

        admin = userRepository.save(admin);

        // 3. Enviar email de verificació
        emailService.sendCompanyAdminVerification(
                admin.getEmail(),
                verificationToken,
                admin.getFirstName(),
                company.getName()
        );

        // 4. Retornar la empresa creada
        return mapToResponseDTO(company);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CompanyResponseDTO getCompanyByUuid() {
        //Validar que el rol sigui administrador
        if (!isAdminUser()) {
            throw new BadRequestException("L'usuari ha de ser Administrador");
        }
        String companyUuid = getCompanyUuidFromAuthenticatedUser();

        // Verificar que l'empresa existeix
        Company company = companyRepository.findByUuid(companyUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no trobada amb UUID: " + companyUuid));
        return mapToResponseDTO(company);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CompanyResponseDTO updateCompany(CompanyRequestDTO companyRequestDTO) {
        //Validar que el rol sigui administrador
        if (!isAdminUser()) {
            throw new BadRequestException("L'usuari ha de ser Administrador");
        }
        String companyUuid = getCompanyUuidFromAuthenticatedUser();

        // Verificar que l'empresa existeix
        Company company = companyRepository.findByUuid(companyUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no trobada amb UUID: " + companyUuid));

        if (!company.getTaxId().equals(companyRequestDTO.getTaxId()) &&
                companyRepository.existsByTaxId(companyRequestDTO.getTaxId())) {
            throw new DuplicateResourceException("Ja existeix una empresa amb el NIF/CIF: " + companyRequestDTO.getTaxId());
        }

        company.setName(companyRequestDTO.getName());
        company.setTaxId(companyRequestDTO.getTaxId());
        company.setEmail(companyRequestDTO.getEmail());
        company.setPhone(companyRequestDTO.getPhone());
        company.setAddress(companyRequestDTO.getAddress());
        company.setCity(companyRequestDTO.getCity());
        company.setPostalCode(companyRequestDTO.getPostalCode());

        if (companyRequestDTO.getStatus() != null) {
            company.setStatus(companyRequestDTO.getStatus());
        }

        Company updatedCompany = companyRepository.save(company);
        return mapToResponseDTO(updatedCompany);
    }

    /**
     * Obté l'UUID de l'empresa de l'usuari autenticat des del context de Spring Security.
     * Aquest mètode s'utilitza en els endpoints del controlador per garantir que l'usuari
     * només pugui accedir a la seva pròpia empresa.
     *
     * @return UUID de l'empresa associada a l'usuari autenticat
     * @throws ResourceNotFoundException si l'usuari no existeix o no té empresa assignada
     */
    private String getCompanyUuidFromAuthenticatedUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuari no trobat: " + username));

        if (user.getCompany() == null || user.getCompany().getUuid() == null) {
            throw new ResourceNotFoundException("L'usuari no té empresa assignada");
        }

        return user.getCompany().getUuid();
    }

    /**
     * Verifica si l'usuari autenticat actual té rol d'administrador.
     * <p>
     * Obté l'usuari actual del context de seguretat de Spring Security
     * i comprova si el seu rol és {@link User.UserRole#ADMIN}.
     * </p>
     *
     * @return {@code true} si l'usuari autenticat és administrador,
     * {@code false} en cas contrari
     * @throws ResourceNotFoundException si l'usuari autenticat no existeix a la base de dades
     */
    private Boolean isAdminUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuari no trobat: " + username));

        return user.getRole() == ADMIN;
    }

    /**
     * Converteix una entitat Company al seu DTO de resposta.
     *
     * @param company entitat a convertir
     * @return DTO amb les dades de l'empresa
     */
    private CompanyResponseDTO mapToResponseDTO(Company company) {
        return CompanyResponseDTO.builder()
                .uuid(company.getUuid())
                .name(company.getName())
                .taxId(company.getTaxId())
                .email(company.getEmail())
                .phone(company.getPhone())
                .address(company.getAddress())
                .city(company.getCity())
                .postalCode(company.getPostalCode())
                .status(company.getStatus())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }

}