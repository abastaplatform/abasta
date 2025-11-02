package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyRequestDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.User;
import cat.abasta_back_end.entities.Company.CompanyStatus;
import cat.abasta_back_end.entities.User.UserRole;
import cat.abasta_back_end.exceptions.DuplicateResourceException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.CompanyRepository;
import cat.abasta_back_end.repositories.UserRepository;
import cat.abasta_back_end.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris simplificats per CompanyServiceImpl.
 * Verifica el registre d'empreses amb administrador i operacions CRUD.
 *
 * @author Test Author
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("CompanyServiceImpl Tests")
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private CompanyRegistrationDTO registrationDTO;
    private CompanyRequestDTO updateDTO;
    private Company testCompany;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        registrationDTO = CompanyRegistrationDTO.builder()
                .companyName("Test Company SL")
                .taxId("B12345678")
                .companyEmail("empresa@test.com")
                .companyPhone("123456789")
                .companyAddress("Carrer Test 123")
                .companyCity("Barcelona")
                .companyPostalCode("08001")
                .adminEmail("admin@test.com")
                .adminPassword("password123")
                .adminFirstName("Joan")
                .adminLastName("Garcia")
                .adminPhone("987654321")
                .build();

        updateDTO = CompanyRequestDTO.builder()
                .name("Updated Company SL")
                .taxId("B12345678") // Mateix taxId per defecte
                .email("updated@empresa.com")
                .phone("987654321")
                .address("Carrer Updated 456")
                .city("Madrid")
                .postalCode("28001")
                .build();

        testCompany = Company.builder()
                .id(1L)
                .uuid("company-uuid")
                .name("Test Company SL")
                .taxId("B12345678")
                .email("empresa@test.com")
                .status(CompanyStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testAdmin = User.builder()
                .id(1L)
                .uuid("admin-uuid")
                .email("admin@test.com")
                .firstName("Joan")
                .lastName("Garcia")
                .role(UserRole.ADMIN)
                .company(testCompany)
                .isActive(true)
                .emailVerified(false)
                .build();
    }

    @Test
    @DisplayName("RegisterCompanyWithAdmin hauria de crear empresa i administrador correctament")
    void registerCompanyWithAdmin_ShouldCreateCompanyAndAdmin_Successfully() {
        // Given
        when(companyRepository.existsByTaxId("B12345678")).thenReturn(false);
        when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
        when(userRepository.save(any(User.class))).thenReturn(testAdmin);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        // When
        CompanyResponseDTO response = companyService.registerCompanyWithAdmin(registrationDTO);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Company SL");
        assertThat(response.getTaxId()).isEqualTo("B12345678");
        assertThat(response.getStatus()).isEqualTo(CompanyStatus.PENDING);

        verify(companyRepository).save(any(Company.class));
        verify(userRepository).save(any(User.class));
        verify(emailService).sendCompanyAdminVerification(
                eq("admin@test.com"),
                anyString(),
                eq("Joan"),
                eq("Test Company SL")
        );
    }

    @Test
    @DisplayName("RegisterCompanyWithAdmin hauria de llançar excepció quan el taxId ja existeix")
    void registerCompanyWithAdmin_ShouldThrowException_WhenTaxIdExists() {
        // Given
        when(companyRepository.existsByTaxId("B12345678")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> companyService.registerCompanyWithAdmin(registrationDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("El CIF/NIF ja està registrat");

        verify(companyRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("RegisterCompanyWithAdmin hauria de llançar excepció quan l'email admin ja existeix")
    void registerCompanyWithAdmin_ShouldThrowException_WhenAdminEmailExists() {
        // Given
        when(companyRepository.existsByTaxId("B12345678")).thenReturn(false);
        when(userRepository.existsByEmail("admin@test.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> companyService.registerCompanyWithAdmin(registrationDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("L'email de l'administrador ja està registrat");

        verify(companyRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("GetCompanyByUuid hauria de retornar empresa quan UUID existeix")
    void getCompanyByUuid_ShouldReturnCompany_WhenUuidExists() {
        // Given
        String companyUuid = "company-uuid";
        when(companyRepository.findByUuid(companyUuid)).thenReturn(Optional.of(testCompany));

        // When
        CompanyResponseDTO response = companyService.getCompanyByUuid(companyUuid);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUuid()).isEqualTo("company-uuid");
        assertThat(response.getName()).isEqualTo("Test Company SL");
        assertThat(response.getTaxId()).isEqualTo("B12345678");
        assertThat(response.getEmail()).isEqualTo("empresa@test.com");

        verify(companyRepository).findByUuid(companyUuid);
    }

    @Test
    @DisplayName("GetCompanyByUuid hauria de llançar excepció quan UUID no existeix")
    void getCompanyByUuid_ShouldThrowException_WhenUuidNotExists() {
        // Given
        String nonExistentUuid = "non-existent-uuid";
        when(companyRepository.findByUuid(nonExistentUuid)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> companyService.getCompanyByUuid(nonExistentUuid))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Empresa no trobada amb UUID: " + nonExistentUuid);

        verify(companyRepository).findByUuid(nonExistentUuid);
    }

    @Test
    @DisplayName("UpdateCompany hauria d'actualitzar empresa correctament")
    void updateCompany_ShouldUpdateCompany_Successfully() {
        // Given
        String companyUuid = "company-uuid";
        Company updatedCompany = Company.builder()
                .id(1L)
                .uuid(companyUuid)
                .name("Updated Company SL")
                .taxId("B12345678")
                .email("updated@empresa.com")
                .phone("987654321")
                .address("Carrer Updated 456")
                .city("Madrid")
                .postalCode("28001")
                .status(CompanyStatus.ACTIVE)
                .createdAt(testCompany.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(companyRepository.findByUuid(companyUuid)).thenReturn(Optional.of(testCompany));
        when(companyRepository.save(any(Company.class))).thenReturn(updatedCompany);

        // When
        CompanyResponseDTO response = companyService.updateCompany(companyUuid, updateDTO);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUuid()).isEqualTo(companyUuid);
        assertThat(response.getName()).isEqualTo("Updated Company SL");
        assertThat(response.getEmail()).isEqualTo("updated@empresa.com");
        assertThat(response.getCity()).isEqualTo("Madrid");

        verify(companyRepository).findByUuid(companyUuid);
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    @DisplayName("UpdateCompany hauria de llançar excepció quan UUID no existeix")
    void updateCompany_ShouldThrowException_WhenUuidNotExists() {
        // Given
        String nonExistentUuid = "non-existent-uuid";
        when(companyRepository.findByUuid(nonExistentUuid)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> companyService.updateCompany(nonExistentUuid, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Empresa no trobada amb ID: " + nonExistentUuid);

        verify(companyRepository).findByUuid(nonExistentUuid);
        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("UpdateCompany hauria de permetre canviar taxId si no existeix duplicat")
    void updateCompany_ShouldAllowTaxIdChange_WhenNoDuplicate() {
        // Given
        String companyUuid = "company-uuid";
        String newTaxId = "C87654321";

        CompanyRequestDTO updateDTOWithNewTaxId = CompanyRequestDTO.builder()
                .name("Updated Company SL")
                .taxId(newTaxId)
                .email("updated@empresa.com")
                .phone("987654321")
                .address("Carrer Updated 456")
                .city("Madrid")
                .postalCode("28001")
                .build();

        when(companyRepository.findByUuid(companyUuid)).thenReturn(Optional.of(testCompany));
        when(companyRepository.existsByTaxId(newTaxId)).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

        // When
        companyService.updateCompany(companyUuid, updateDTOWithNewTaxId);

        // Then
        verify(companyRepository).save(argThat(company ->
                company.getTaxId().equals(newTaxId)
        ));
    }

    @Test
    @DisplayName("UpdateCompany hauria de llançar excepció quan el nou taxId ja existeix")
    void updateCompany_ShouldThrowException_WhenNewTaxIdExists() {
        // Given
        String companyUuid = "company-uuid";
        String existingTaxId = "C87654321";

        CompanyRequestDTO updateDTOWithExistingTaxId = CompanyRequestDTO.builder()
                .name("Updated Company SL")
                .taxId(existingTaxId)
                .email("updated@empresa.com")
                .build();

        when(companyRepository.findByUuid(companyUuid)).thenReturn(Optional.of(testCompany));
        when(companyRepository.existsByTaxId(existingTaxId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> companyService.updateCompany(companyUuid, updateDTOWithExistingTaxId))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Ja existeix una empresa amb el NIF/CIF: " + existingTaxId);

        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("RegisterCompanyWithAdmin hauria de crear empresa amb estat PENDING")
    void registerCompanyWithAdmin_ShouldCreateCompanyWithPendingStatus() {
        // Given
        when(companyRepository.existsByTaxId("B12345678")).thenReturn(false);
        when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
        when(userRepository.save(any(User.class))).thenReturn(testAdmin);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

        // When
        companyService.registerCompanyWithAdmin(registrationDTO);

        // Then
        verify(companyRepository).save(argThat(company ->
                company.getStatus() == CompanyStatus.PENDING &&
                        company.getName().equals("Test Company SL") &&
                        company.getTaxId().equals("B12345678")
        ));
    }

    @Test
    @DisplayName("RegisterCompanyWithAdmin hauria de crear administrador amb rol ADMIN")
    void registerCompanyWithAdmin_ShouldCreateAdminWithCorrectRole() {
        // Given
        when(companyRepository.existsByTaxId("B12345678")).thenReturn(false);
        when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
        when(userRepository.save(any(User.class))).thenReturn(testAdmin);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        // When
        companyService.registerCompanyWithAdmin(registrationDTO);

        // Then
        verify(userRepository).save(argThat(user ->
                user.getRole() == UserRole.ADMIN &&
                        user.getEmail().equals("admin@test.com") &&
                        user.getFirstName().equals("Joan") &&
                        user.getIsActive() == true &&
                        user.getEmailVerified() == false &&
                        user.getEmailVerificationToken() != null &&
                        user.getEmailVerificationExpires() != null
        ));
    }

    @Test
    @DisplayName("RegisterCompanyWithAdmin hauria d'encriptar la contrasenya de l'administrador")
    void registerCompanyWithAdmin_ShouldEncryptAdminPassword() {
        // Given
        when(companyRepository.existsByTaxId("B12345678")).thenReturn(false);
        when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
        when(userRepository.save(any(User.class))).thenReturn(testAdmin);
        when(passwordEncoder.encode("password123")).thenReturn("super-secure-encoded-password");

        // When
        companyService.registerCompanyWithAdmin(registrationDTO);

        // Then
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user ->
                user.getPassword().equals("super-secure-encoded-password")
        ));
    }

    @Test
    @DisplayName("RegisterCompanyWithAdmin hauria de configurar token de verificació amb 24h de validesa")
    void registerCompanyWithAdmin_ShouldSetVerificationTokenWith24HoursExpiry() {
        // Given
        LocalDateTime beforeTest = LocalDateTime.now();

        when(companyRepository.existsByTaxId("B12345678")).thenReturn(false);
        when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
        when(userRepository.save(any(User.class))).thenReturn(testAdmin);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

        // When
        companyService.registerCompanyWithAdmin(registrationDTO);

        // Then
        verify(userRepository).save(argThat(user -> {
            LocalDateTime expectedExpiry = beforeTest.plusHours(24);
            return user.getEmailVerificationToken() != null &&
                    user.getEmailVerificationExpires() != null &&
                    user.getEmailVerificationExpires().isAfter(expectedExpiry.minusMinutes(1)) &&
                    user.getEmailVerificationExpires().isBefore(expectedExpiry.plusMinutes(1));
        }));
    }
}