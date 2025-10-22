package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyRequestDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.Company.CompanyStatus;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de empresas.
 * Contiene la lógica de negocio para operaciones CRUD de empresas,
 * incluyendo validaciones de duplicidad, registro con administrador
 * y conversiones entre entidades y DTOs.
 *
 * @author Tu equipo
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
     * Valida que no exista otra empresa con el mismo taxId o email antes de crear.
     */
    @Override
    public CompanyResponseDTO createCompany(CompanyRequestDTO companyRequestDTO) {
        if (companyRepository.existsByTaxId(companyRequestDTO.getTaxId())) {
            throw new DuplicateResourceException("Ya existe una empresa con el NIF/CIF: " + companyRequestDTO.getTaxId());
        }
        if (companyRepository.existsByEmail(companyRequestDTO.getEmail())) {
            throw new DuplicateResourceException("Ya existe una empresa con el email: " + companyRequestDTO.getEmail());
        }

        Company company = mapToEntity(companyRequestDTO);
        Company savedCompany = companyRepository.save(company);
        return mapToResponseDTO(savedCompany);
    }

    /**
     * {@inheritDoc}
     *
     * Valida que el nuevo taxId no esté en uso por otra empresa si se modifica.
     * Actualiza todos los campos excepto el UUID y las fechas de auditoría.
     */
    @Override
    public CompanyResponseDTO updateCompany(Long id, CompanyRequestDTO companyRequestDTO) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));

        if (!company.getTaxId().equals(companyRequestDTO.getTaxId()) &&
                companyRepository.existsByTaxId(companyRequestDTO.getTaxId())) {
            throw new DuplicateResourceException("Ya existe una empresa con el NIF/CIF: " + companyRequestDTO.getTaxId());
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
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CompanyResponseDTO getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));
        return mapToResponseDTO(company);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CompanyResponseDTO getCompanyByUuid(String uuid) {
        Company company = companyRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con UUID: " + uuid));
        return mapToResponseDTO(company);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getCompaniesByStatus(CompanyStatus status) {
        return companyRepository.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * Elimina la empresa y todos sus usuarios asociados en cascada.
     */
    @Override
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empresa no encontrada con ID: " + id);
        }
        companyRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompanyResponseDTO changeCompanyStatus(Long id, CompanyStatus status) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));
        company.setStatus(status);
        Company updatedCompany = companyRepository.save(company);
        return mapToResponseDTO(updatedCompany);
    }

    /**
     * {@inheritDoc}
     *
     * Proceso de registro completo:
     * <ol>
     *   <li>Valida que no exista el taxId ni el email del admin</li>
     *   <li>Crea la empresa con estado ACTIVE</li>
     *   <li>Crea el usuario administrador con rol ADMIN</li>
     *   <li>Genera un token de verificación con validez de 24 horas</li>
     *   <li>Envía email de verificación al administrador</li>
     * </ol>
     *
     * La cuenta del admin queda inactiva (emailVerified=false) hasta que
     * verifique su email mediante el token enviado.
     */
    @Override
    @Transactional
    public CompanyResponseDTO registerCompanyWithAdmin(CompanyRegistrationDTO registrationDTO) {

        // Validar que el taxId no exista
        if (companyRepository.existsByTaxId(registrationDTO.getTaxId())) {
            throw new DuplicateResourceException("El CIF/NIF ya está registrado");
        }

        // Validar que el email del admin no exista
        if (userRepository.existsByEmail(registrationDTO.getAdminEmail())) {
            throw new DuplicateResourceException("El email del administrador ya está registrado");
        }

        // 1. Crear la empresa
        Company company = Company.builder()
                .name(registrationDTO.getCompanyName())
                .taxId(registrationDTO.getTaxId())
                .email(registrationDTO.getCompanyEmail())
                .phone(registrationDTO.getCompanyPhone())
                .address(registrationDTO.getCompanyAddress())
                .city(registrationDTO.getCompanyCity())
                .postalCode(registrationDTO.getCompanyPostalCode())
                .status(Company.CompanyStatus.ACTIVE)
                .build();

        company = companyRepository.save(company);

        // 2. Crear el usuario administrador
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

        // 3. Enviar email de verificación
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
     * Convierte una entidad Company a su DTO de respuesta.
     *
     * @param company Entidad a convertir
     * @return DTO con los datos de la empresa
     */
    private CompanyResponseDTO mapToResponseDTO(Company company) {
        return CompanyResponseDTO.builder()
                .id(company.getId())
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

    /**
     * Convierte un DTO de request a una entidad Company.
     * Si no se especifica estado, asigna ACTIVE por defecto.
     *
     * @param dto DTO con los datos de la empresa
     * @return Entidad Company lista para persistir
     */
    private Company mapToEntity(CompanyRequestDTO dto) {
        return Company.builder()
                .name(dto.getName())
                .taxId(dto.getTaxId())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .city(dto.getCity())
                .postalCode(dto.getPostalCode())
                .status(dto.getStatus() != null ? dto.getStatus() : CompanyStatus.ACTIVE)
                .build();
    }
}