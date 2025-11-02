package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyRequestDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.User;
import cat.abasta_back_end.exceptions.DuplicateResourceException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.CompanyRepository;
import cat.abasta_back_end.repositories.UserRepository;
import cat.abasta_back_end.services.CompanyService;
import cat.abasta_back_end.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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
     *
     * Procés de registre complet:
     * <ol>
     *   <li>Valida que no existeixi el taxID ni l'email del admin</li>
     *   <li>Crea l'empresa amb estat ACTIVE</li>
     *   <li>Crea l'usuari administrador amb rol ADMIN</li>
     *   <li>Genera un token de verificació amb validesa de 24 hores</li>
     *   <li>Envia email de verificació a l'administrador</li>
     * </ol>
     *
     * El compte del admin queda inactiu (emailVerified=false) fins verificació mitjançant token enviat al correu.
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
     * Obté la informació d'una empresa pel seu identificador UUID.
     * Operació de només lectura que recupera una empresa de la base de dades
     * i la converteix al DTO de resposta.
     *
     * @param uuid l'identificador únic (UUID) de l'empresa a recuperar
     * @return CompanyResponseDTO amb la informació completa de l'empresa
     * @throws ResourceNotFoundException si no existeix cap empresa amb l'UUID proporcionat
     */
    @Override
    @Transactional(readOnly = true)
    public CompanyResponseDTO getCompanyByUuid(String uuid) {
        Company company = companyRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no trobada amb UUID: " + uuid));
        return mapToResponseDTO(company);
    }

    /**
     * Actualitza la informació d'una empresa existent.
     * Modifica les dades d'una empresa identificada pel seu UUID, validant que no es
     * produeixi duplicació del NIF/CIF si aquest canvia. Tots els camps proporcionats
     * al DTO s'actualitzen, excepte l'estat que només es modifica si es proporciona explícitament.
     *
     * <p>Validacions realitzades:
     * <ul>
     *   <li>Verifica que l'empresa existeixi</li>
     *   <li>Comprova que el nou NIF/CIF no estigui ja assignat a una altra empresa</li>
     *   <li>Actualitza l'estat només si es proporciona al DTO</li>
     * </ul>
     * </p>
     *
     * @param uuid l'identificador únic (UUID) de l'empresa a actualitzar
     * @param companyRequestDTO objecte amb les noves dades de l'empresa
     * @return CompanyResponseDTO amb la informació actualitzada de l'empresa
     * @throws ResourceNotFoundException si no existeix cap empresa amb l'UUID proporcionat
     * @throws DuplicateResourceException si el nou NIF/CIF ja està assignat a una altra empresa
     */
    @Override
    @Transactional
    public CompanyResponseDTO updateCompany(String uuid, CompanyRequestDTO companyRequestDTO) {
        Company company = companyRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no trobada amb ID: " + uuid));

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