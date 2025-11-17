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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris CORREGITS per CompanyServiceImpl.
 * Verificació de la lògica de negoci del servei d'empreses.
 * ACTUALITZAT per coincidir amb la implementació real que usa SecurityContext.
 *
 * @author Enrique Pérez
 * @version 2.0 - Corrected
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CompanyServiceImpl Tests Corrected")
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    // Security mocks - només quan es necessitin
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private CompanyRegistrationDTO registrationDTO;
    private CompanyRequestDTO updateDTO;
    private Company testCompany;
    private User testAdmin;
    private final String TEST_USER_EMAIL = "admin@test.com";
    private final String TEST_COMPANY_UUID = "company-uuid-123";

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        registrationDTO = CompanyRegistrationDTO.builder()
                .companyName("Test Company SL")
                .taxId("B12345678")
                .companyEmail("empresa@test.com")
                .companyPhone("123456789")
                .companyAddress("Carrer Test 123")
                .companyCity("Barcelona")
                .companyPostalCode("08001")
                .adminEmail(TEST_USER_EMAIL)
                .adminPassword("password123")
                .adminFirstName("Joan")
                .adminLastName("Garcia")
                .adminPhone("987654321")
                .build();

        updateDTO = CompanyRequestDTO.builder()
                .name("Updated Company SL")
                .taxId("B12345678")
                .email("updated@empresa.com")
                .phone("987654321")
                .address("Carrer Updated 456")
                .city("Madrid")
                .postalCode("28001")
                .status(CompanyStatus.ACTIVE)
                .build();

        testCompany = Company.builder()
                .id(1L)
                .uuid(TEST_COMPANY_UUID)
                .name("Test Company SL")
                .taxId("B12345678")
                .email("empresa@test.com")
                .phone("123456789")
                .address("Carrer Test 123")
                .city("Barcelona")
                .postalCode("08001")
                .status(CompanyStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testAdmin = User.builder()
                .id(1L)
                .uuid("admin-uuid")
                .email(TEST_USER_EMAIL)
                .firstName("Joan")
                .lastName("Garcia")
                .role(UserRole.ADMIN)
                .company(testCompany)
                .isActive(true)
                .emailVerified(false)
                .build();
    }

    // Mètode auxiliar per configurar SecurityContext només quan es necessiti
    private void setupSecurityContextForAuthenticatedUser() {
        when(authentication.getName()).thenReturn(TEST_USER_EMAIL);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Nested
    @DisplayName("Tests de registerCompanyWithAdmin")
    class RegisterCompanyWithAdminTests {

        @Test
        @DisplayName("hauria de crear empresa i administrador correctament")
        void registerCompanyWithAdmin_ShouldCreateCompanyAndAdmin_Successfully() {
            // Given
            when(companyRepository.existsByTaxId("B12345678")).thenReturn(false);
            when(userRepository.existsByEmail(TEST_USER_EMAIL)).thenReturn(false);
            when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
            when(userRepository.save(any(User.class))).thenReturn(testAdmin);
            when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
            doNothing().when(emailService).sendCompanyAdminVerification(
                    anyString(), anyString(), anyString(), anyString());

            // When
            CompanyResponseDTO response = companyService.registerCompanyWithAdmin(registrationDTO);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getUuid()).isEqualTo(TEST_COMPANY_UUID);
            assertThat(response.getName()).isEqualTo("Test Company SL");
            assertThat(response.getTaxId()).isEqualTo("B12345678");
            assertThat(response.getEmail()).isEqualTo("empresa@test.com");
            assertThat(response.getStatus()).isEqualTo(CompanyStatus.PENDING);

            // Verificar interaccions
            verify(companyRepository).existsByTaxId("B12345678");
            verify(userRepository).existsByEmail(TEST_USER_EMAIL);
            verify(companyRepository).save(any(Company.class));
            verify(userRepository).save(any(User.class));
            verify(passwordEncoder).encode("password123");
            verify(emailService).sendCompanyAdminVerification(
                    eq(TEST_USER_EMAIL), anyString(), eq("Joan"), eq("Test Company SL"));
        }

        @Test
        @DisplayName("hauria de llançar excepció quan taxId ja existeix")
        void registerCompanyWithAdmin_ShouldThrowException_WhenTaxIdExists() {
            // Given
            when(companyRepository.existsByTaxId("B12345678")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> companyService.registerCompanyWithAdmin(registrationDTO))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("El CIF/NIF ja està registrat");

            verify(companyRepository).existsByTaxId("B12345678");
            verify(userRepository, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("hauria de llançar excepció quan email admin ja existeix")
        void registerCompanyWithAdmin_ShouldThrowException_WhenAdminEmailExists() {
            // Given
            when(companyRepository.existsByTaxId("B12345678")).thenReturn(false);
            when(userRepository.existsByEmail(TEST_USER_EMAIL)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> companyService.registerCompanyWithAdmin(registrationDTO))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("L'email de l'administrador ja està registrat");

            verify(companyRepository).existsByTaxId("B12345678");
            verify(userRepository).existsByEmail(TEST_USER_EMAIL);
            verify(companyRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests de getCompanyByUuid")
    class GetCompanyByUuidTests {

        @Test
        @DisplayName("hauria de retornar empresa de l'usuari autenticat")
        void getCompanyByUuid_ShouldReturnCompany_WhenAuthenticated() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testAdmin));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.of(testCompany));

            // When
            CompanyResponseDTO response;
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                response = companyService.getCompanyByUuid();
            }

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getUuid()).isEqualTo(TEST_COMPANY_UUID);
            assertThat(response.getName()).isEqualTo("Test Company SL");
            assertThat(response.getTaxId()).isEqualTo("B12345678");

            verify(userRepository).findByEmail(TEST_USER_EMAIL);
            verify(companyRepository).findByUuid(TEST_COMPANY_UUID);
        }

        @Test
        @DisplayName("hauria de llançar excepció quan usuari no existeix")
        void getCompanyByUuid_ShouldThrowException_WhenUserNotFound() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.empty());

            // When & Then
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                assertThatThrownBy(() -> companyService.getCompanyByUuid())
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("Usuari no trobat");
            }
        }

        @Test
        @DisplayName("hauria de llançar excepció quan usuari no té empresa")
        void getCompanyByUuid_ShouldThrowException_WhenUserHasNoCompany() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            User userWithoutCompany = User.builder()
                    .email(TEST_USER_EMAIL)
                    .company(null)
                    .build();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(userWithoutCompany));

            // When & Then
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                assertThatThrownBy(() -> companyService.getCompanyByUuid())
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("L'usuari no té empresa assignada");
            }
        }

        @Test
        @DisplayName("hauria de llançar excepció quan empresa no existeix")
        void getCompanyByUuid_ShouldThrowException_WhenCompanyNotFound() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testAdmin));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.empty());

            // When & Then
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                assertThatThrownBy(() -> companyService.getCompanyByUuid())
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("Empresa no trobada amb UUID");
            }
        }
    }

    @Nested
    @DisplayName("Tests de updateCompany")
    class UpdateCompanyTests {

        @Test
        @DisplayName("hauria d'actualitzar empresa correctament")
        void updateCompany_ShouldUpdateCompany_Successfully() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            Company updatedCompany = Company.builder()
                    .uuid(TEST_COMPANY_UUID)
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

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testAdmin));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.of(testCompany));
            when(companyRepository.save(any(Company.class))).thenReturn(updatedCompany);

            // When
            CompanyResponseDTO response;
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                response = companyService.updateCompany(updateDTO);
            }

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getUuid()).isEqualTo(TEST_COMPANY_UUID);
            assertThat(response.getName()).isEqualTo("Updated Company SL");
            assertThat(response.getEmail()).isEqualTo("updated@empresa.com");
            assertThat(response.getCity()).isEqualTo("Madrid");
            assertThat(response.getStatus()).isEqualTo(CompanyStatus.ACTIVE);

            verify(userRepository).findByEmail(TEST_USER_EMAIL);
            verify(companyRepository).findByUuid(TEST_COMPANY_UUID);
            verify(companyRepository).save(any(Company.class));
        }

        @Test
        @DisplayName("hauria d'actualitzar taxId quan no hi ha duplicat")
        void updateCompany_ShouldAllowTaxIdChange_WhenNoDuplicate() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            CompanyRequestDTO updateDTOWithNewTaxId = CompanyRequestDTO.builder()
                    .name("Updated Company SL")
                    .taxId("B87654321") // Nou taxId
                    .email("updated@empresa.com")
                    .phone("987654321")
                    .address("Carrer Updated 456")
                    .city("Madrid")
                    .postalCode("28001")
                    .build();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testAdmin));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.of(testCompany));
            when(companyRepository.existsByTaxId("B87654321")).thenReturn(false);
            when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

            // When
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                companyService.updateCompany(updateDTOWithNewTaxId);
            }

            // Then
            verify(companyRepository).existsByTaxId("B87654321");
            verify(companyRepository).save(any(Company.class));
        }

        @Test
        @DisplayName("hauria de llançar excepció quan nou taxId ja existeix")
        void updateCompany_ShouldThrowException_WhenNewTaxIdExists() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            CompanyRequestDTO updateDTOWithExistingTaxId = CompanyRequestDTO.builder()
                    .name("Updated Company SL")
                    .taxId("B87654321") // TaxId que ja existeix
                    .email("updated@empresa.com")
                    .build();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testAdmin));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.of(testCompany));
            when(companyRepository.existsByTaxId("B87654321")).thenReturn(true);

            // When & Then
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                assertThatThrownBy(() -> companyService.updateCompany(updateDTOWithExistingTaxId))
                        .isInstanceOf(DuplicateResourceException.class)
                        .hasMessageContaining("Ja existeix una empresa amb el NIF/CIF");
            }

            verify(companyRepository).existsByTaxId("B87654321");
            verify(companyRepository, never()).save(any());
        }

        @Test
        @DisplayName("hauria de llançar excepció quan empresa no existeix")
        void updateCompany_ShouldThrowException_WhenCompanyNotFound() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testAdmin));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.empty());

            // When & Then
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                assertThatThrownBy(() -> companyService.updateCompany(updateDTO))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("Empresa no trobada amb UUID");
            }
        }
    }

    @Nested
    @DisplayName("Tests del mapeig mapToResponseDTO")
    class MapToResponseDTOTests {

        @Test
        @DisplayName("hauria de mapar entitat a DTO correctament")
        void mapToResponseDTO_ShouldMapCorrectly() {
            // Given - configurem el test per verificar el mapping indirectament
            setupSecurityContextForAuthenticatedUser();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testAdmin));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.of(testCompany));

            // When
            CompanyResponseDTO result;
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                result = companyService.getCompanyByUuid();
            }

            // Then - verifica que tots els camps s'hagin mapejat correctament
            assertThat(result.getUuid()).isEqualTo(testCompany.getUuid());
            assertThat(result.getName()).isEqualTo(testCompany.getName());
            assertThat(result.getTaxId()).isEqualTo(testCompany.getTaxId());
            assertThat(result.getEmail()).isEqualTo(testCompany.getEmail());
            assertThat(result.getPhone()).isEqualTo(testCompany.getPhone());
            assertThat(result.getAddress()).isEqualTo(testCompany.getAddress());
            assertThat(result.getCity()).isEqualTo(testCompany.getCity());
            assertThat(result.getPostalCode()).isEqualTo(testCompany.getPostalCode());
            assertThat(result.getStatus()).isEqualTo(testCompany.getStatus());
            assertThat(result.getCreatedAt()).isEqualTo(testCompany.getCreatedAt());
            assertThat(result.getUpdatedAt()).isEqualTo(testCompany.getUpdatedAt());
        }
    }
}