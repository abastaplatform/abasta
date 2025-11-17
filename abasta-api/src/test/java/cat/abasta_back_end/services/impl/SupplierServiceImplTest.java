package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.SupplierFilterDTO;
import cat.abasta_back_end.dto.SupplierRequestDTO;
import cat.abasta_back_end.dto.SupplierResponseDTO;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.Supplier;
import cat.abasta_back_end.entities.User;
import cat.abasta_back_end.exceptions.DuplicateResourceException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.CompanyRepository;
import cat.abasta_back_end.repositories.SupplierRepository;
import cat.abasta_back_end.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris ACTUALITZATS per SupplierServiceImpl.
 * Verificació de la lògica de negoci del servei de proveïdors.
 * ACTUALITZAT per incloure els nous mètodes i noms de repositori actualitzats.
 *
 * @author Enrique Pérez
 * @version 3.0 - Updated
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierServiceImpl Tests Updated")
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    // Security mocks - només quan es necessitin
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private SupplierRequestDTO validSupplierRequest;
    private Company testCompany;
    private User testUser;
    private Supplier testSupplier;
    private final String TEST_USER_EMAIL = "test@company.com";
    private final String TEST_COMPANY_UUID = "company-uuid-123";
    private final String TEST_SUPPLIER_UUID = "supplier-uuid-456";

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        validSupplierRequest = SupplierRequestDTO.builder()
                .name("Proveïdors Catalunya SL")
                .contactName("Joan Martinez")
                .email("joan@provcat.com")
                .phone("938765432")
                .address("Av. Diagonal 123, Barcelona")
                .notes("Proveïdor de materials de construcció")
                .isActive(true)
                .build();

        testCompany = Company.builder()
                .id(1L)
                .uuid(TEST_COMPANY_UUID)
                .name("Test Company SL")
                .build();

        testUser = User.builder()
                .id(1L)
                .email(TEST_USER_EMAIL)
                .company(testCompany)
                .build();

        testSupplier = Supplier.builder()
                .id(1L)
                .uuid(TEST_SUPPLIER_UUID)
                .company(testCompany)
                .name("Proveïdors Catalunya SL")
                .contactName("Joan Martinez")
                .email("joan@provcat.com")
                .phone("938765432")
                .address("Av. Diagonal 123, Barcelona")
                .notes("Proveïdor de materials de construcció")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void setupSecurityContextForAuthenticatedUser() {
        when(authentication.getName()).thenReturn(TEST_USER_EMAIL);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Nested
    @DisplayName("Tests de createSupplier")
    class CreateSupplierTests {

        @Test
        @DisplayName("hauria de crear proveïdor correctament")
        void createSupplier_ShouldCreateSupplier_WhenValidData() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testUser));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.of(testCompany));
            when(supplierRepository.existsByCompanyUuidAndNameIgnoreCase(TEST_COMPANY_UUID, "Proveïdors Catalunya SL"))
                    .thenReturn(false);
            when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

            // When
            SupplierResponseDTO result;
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                result = supplierService.createSupplier(validSupplierRequest);
            }

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Proveïdors Catalunya SL");
            assertThat(result.getCompanyUuid()).isEqualTo(TEST_COMPANY_UUID);
            assertThat(result.getEmail()).isEqualTo("joan@provcat.com");

            verify(supplierRepository).save(any(Supplier.class));
            verify(companyRepository).findByUuid(TEST_COMPANY_UUID);
        }

        @Test
        @DisplayName("hauria de llançar excepció quan l'empresa no existeix")
        void createSupplier_ShouldThrowException_WhenCompanyNotFound() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testUser));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.empty());

            // When & Then
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                assertThatThrownBy(() -> supplierService.createSupplier(validSupplierRequest))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("Empresa no trobada amb UUID");
            }
        }

        @Test
        @DisplayName("hauria de llançar excepció quan ja existeix proveïdor amb mateix nom")
        void createSupplier_ShouldThrowException_WhenDuplicateName() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testUser));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.of(testCompany));
            when(supplierRepository.existsByCompanyUuidAndNameIgnoreCase(TEST_COMPANY_UUID, "Proveïdors Catalunya SL"))
                    .thenReturn(true);

            // When & Then
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                assertThatThrownBy(() -> supplierService.createSupplier(validSupplierRequest))
                        .isInstanceOf(DuplicateResourceException.class)
                        .hasMessageContaining("Ja existeix un proveïdor amb el nom");
            }
        }
    }

    @Nested
    @DisplayName("Tests de getSupplierByUuid")
    class GetSupplierByUuidTests {

        @Test
        @DisplayName("hauria de retornar proveïdor quan UUID existeix")
        void getSupplierByUuid_ShouldReturnSupplier_WhenUuidExists() {
            // Given
            when(supplierRepository.findByUuid(TEST_SUPPLIER_UUID)).thenReturn(Optional.of(testSupplier));

            // When
            SupplierResponseDTO result = supplierService.getSupplierByUuid(TEST_SUPPLIER_UUID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUuid()).isEqualTo(TEST_SUPPLIER_UUID);
            assertThat(result.getName()).isEqualTo("Proveïdors Catalunya SL");

            verify(supplierRepository).findByUuid(TEST_SUPPLIER_UUID);
        }

        @Test
        @DisplayName("hauria de llançar excepció quan UUID no existeix")
        void getSupplierByUuid_ShouldThrowException_WhenUuidNotFound() {
            // Given
            String nonExistentUuid = "non-existent-uuid";
            when(supplierRepository.findByUuid(nonExistentUuid)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> supplierService.getSupplierByUuid(nonExistentUuid))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Proveïdor no trobat amb UUID");
        }
    }

    @Nested
    @DisplayName("Tests de updateSupplier")
    class UpdateSupplierTests {

        @Test
        @DisplayName("hauria d'actualitzar proveïdor correctament")
        void updateSupplier_ShouldUpdateSupplier_WhenValidData() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            SupplierRequestDTO updateRequest = SupplierRequestDTO.builder()
                    .name("Proveïdors Barcelona SL")
                    .contactName("Maria Garcia")
                    .email("maria@provbar.com")
                    .phone("934567890")
                    .address("Carrer Valencia 456, Barcelona")
                    .notes("Proveïdor actualitzat")
                    .isActive(true)
                    .build();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testUser));
            when(supplierRepository.findByUuid(TEST_SUPPLIER_UUID)).thenReturn(Optional.of(testSupplier));
            when(supplierRepository.existsByCompanyUuidAndNameIgnoreCaseAndUuidNot(
                    TEST_COMPANY_UUID, "Proveïdors Barcelona SL", TEST_SUPPLIER_UUID))
                    .thenReturn(false);
            when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

            // When
            SupplierResponseDTO result;
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                result = supplierService.updateSupplier(TEST_SUPPLIER_UUID, updateRequest);
            }

            // Then
            assertThat(result).isNotNull();
            verify(supplierRepository).save(any(Supplier.class));
        }

        @Test
        @DisplayName("hauria de llançar excepció quan proveïdor no existeix")
        void updateSupplier_ShouldThrowException_WhenSupplierNotFound() {
            // Given
            String nonExistentUuid = "non-existent-uuid";
            when(supplierRepository.findByUuid(nonExistentUuid)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> supplierService.updateSupplier(nonExistentUuid, validSupplierRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Proveïdor no trobat amb UUID");
        }
    }

    @Nested
    @DisplayName("Tests de toggleSupplierStatus")
    class ToggleSupplierStatusTests {

        @Test
        @DisplayName("hauria de canviar estat del proveïdor")
        void toggleSupplierStatus_ShouldChangeStatus() {
            // Given
            when(supplierRepository.findByUuid(TEST_SUPPLIER_UUID)).thenReturn(Optional.of(testSupplier));
            when(supplierRepository.save(testSupplier)).thenReturn(testSupplier);

            // When
            SupplierResponseDTO result = supplierService.toggleSupplierStatus(TEST_SUPPLIER_UUID, false);

            // Then
            assertThat(result).isNotNull();
            verify(supplierRepository).save(testSupplier);
        }

        @Test
        @DisplayName("hauria de llançar excepció quan proveïdor no existeix")
        void toggleSupplierStatus_ShouldThrowException_WhenSupplierNotFound() {
            // Given
            String nonExistentUuid = "non-existent-uuid";
            when(supplierRepository.findByUuid(nonExistentUuid)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> supplierService.toggleSupplierStatus(nonExistentUuid, false))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Proveïdor no trobat amb UUID");
        }
    }

    @Nested
    @DisplayName("Tests de getAllSuppliersPaginated - NOUVEAU")
    class GetAllSuppliersPaginatedTests {

        @Test
        @DisplayName("hauria de retornar tots els proveïdors actius paginats")
        void getAllSuppliersPaginated_ShouldReturnActiveSuppliers() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            Pageable pageable = PageRequest.of(0, 10);
            Page<Supplier> supplierPage = new PageImpl<>(List.of(testSupplier));

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testUser));
            when(supplierRepository.findByCompanyUuidAndIsActiveTrue(TEST_COMPANY_UUID, pageable))
                    .thenReturn(supplierPage);

            // When
            Page<SupplierResponseDTO> result;
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                result = supplierService.getAllSuppliersPaginated(pageable);
            }

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().getFirst().getName()).isEqualTo("Proveïdors Catalunya SL");
            assertThat(result.getContent().getFirst().getIsActive()).isTrue();

            verify(supplierRepository).findByCompanyUuidAndIsActiveTrue(TEST_COMPANY_UUID, pageable);
        }
    }

    @Nested
    @DisplayName("Tests de searchSuppliersByText - ACTUALITZAT")
    class SearchSuppliersByTextTests {

        @Test
        @DisplayName("hauria de retornar proveïdors actius per text de cerca")
        void searchSuppliersByText_ShouldReturnActiveSuppliers() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            String searchText = "Barcelona";
            Pageable pageable = PageRequest.of(0, 10);
            Page<Supplier> supplierPage = new PageImpl<>(List.of(testSupplier));

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testUser));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.of(testCompany));
            when(supplierRepository.findByCompanyIdAndMultipleFieldsContainingActive(
                    testCompany.getId(), searchText, pageable))
                    .thenReturn(supplierPage);

            // When
            Page<SupplierResponseDTO> result;
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                result = supplierService.searchSuppliersByText(searchText, pageable);
            }

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().getFirst().getName()).isEqualTo("Proveïdors Catalunya SL");

            // Verificar que s'usa el NOU nom del mètode del repositori
            verify(supplierRepository).findByCompanyIdAndMultipleFieldsContainingActive(
                    testCompany.getId(), searchText, pageable);
        }

        @Test
        @DisplayName("hauria de llançar excepció quan empresa no existeix")
        void searchSuppliersByText_ShouldThrowException_WhenCompanyNotFound() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testUser));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.empty());

            // When & Then
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                assertThatThrownBy(() -> supplierService.searchSuppliersByText("test", PageRequest.of(0, 10)))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("Empresa no trobada amb UUID");
            }
        }
    }

    @Nested
    @DisplayName("Tests de searchSuppliersWithFilters - ACTUALITZAT")
    class SearchSuppliersWithFiltersTests {

        @Test
        @DisplayName("hauria de retornar proveïdors actius amb filtres")
        void searchSuppliersWithFilters_ShouldReturnActiveFilteredSuppliers() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            SupplierFilterDTO filterDTO = SupplierFilterDTO.builder()
                    .name("Catalunya")
                    .page(0)
                    .size(10)
                    .sortBy("name")
                    .sortDir("asc")
                    .build();

            Pageable pageable = PageRequest.of(0, 10);
            Page<Supplier> supplierPage = new PageImpl<>(List.of(testSupplier));

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(testUser));
            when(companyRepository.findByUuid(TEST_COMPANY_UUID)).thenReturn(Optional.of(testCompany));
            when(supplierRepository.findByCompanyIdAndCriteriaActive(
                    eq(testCompany.getId()),
                    eq("Catalunya"),
                    eq(null),
                    eq(null),
                    eq(null),
                    eq(null),
                    eq(pageable)))
                    .thenReturn(supplierPage);

            // When
            Page<SupplierResponseDTO> result;
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                result = supplierService.searchSuppliersWithFilters(filterDTO, pageable);
            }

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            // Verificar que s'usa el NOU nom del mètode del repositori
            verify(supplierRepository).findByCompanyIdAndCriteriaActive(
                    eq(testCompany.getId()),
                    eq("Catalunya"),
                    eq(null),
                    eq(null),
                    eq(null),
                    eq(null),
                    eq(pageable));
        }
    }

    @Nested
    @DisplayName("Tests de getCompanyUuidFromAuthenticatedUser")
    class GetCompanyUuidTests {

        @Test
        @DisplayName("hauria de llançar excepció quan usuari no existeix")
        void getCompanyUuid_ShouldThrowException_WhenUserNotFound() {
            // Given
            setupSecurityContextForAuthenticatedUser();

            when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.empty());

            // When & Then
            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                assertThatThrownBy(() -> supplierService.createSupplier(validSupplierRequest))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("Usuari no trobat");
            }
        }

        @Test
        @DisplayName("hauria de llançar excepció quan usuari no té empresa assignada")
        void getCompanyUuid_ShouldThrowException_WhenUserHasNoCompany() {
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

                assertThatThrownBy(() -> supplierService.createSupplier(validSupplierRequest))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("L'usuari no té empresa assignada");
            }
        }
    }

    @Nested
    @DisplayName("Tests de mapToResponseDTO")
    class MapToResponseDTOTests {

        @Test
        @DisplayName("hauria de mapar entitat a DTO correctament")
        void mapToResponseDTO_ShouldMapCorrectly() {
            // Given - configurem un supplier per testejar el mapping directament
            when(supplierRepository.findByUuid(TEST_SUPPLIER_UUID)).thenReturn(Optional.of(testSupplier));

            // When
            SupplierResponseDTO result = supplierService.getSupplierByUuid(TEST_SUPPLIER_UUID);

            // Then
            assertThat(result.getUuid()).isEqualTo(testSupplier.getUuid());
            assertThat(result.getCompanyUuid()).isEqualTo(testSupplier.getCompany().getUuid());
            assertThat(result.getCompanyName()).isEqualTo(testSupplier.getCompany().getName());
            assertThat(result.getName()).isEqualTo(testSupplier.getName());
            assertThat(result.getContactName()).isEqualTo(testSupplier.getContactName());
            assertThat(result.getEmail()).isEqualTo(testSupplier.getEmail());
            assertThat(result.getPhone()).isEqualTo(testSupplier.getPhone());
            assertThat(result.getAddress()).isEqualTo(testSupplier.getAddress());
            assertThat(result.getNotes()).isEqualTo(testSupplier.getNotes());
            assertThat(result.getIsActive()).isEqualTo(testSupplier.getIsActive());
            assertThat(result.getCreatedAt()).isEqualTo(testSupplier.getCreatedAt());
            assertThat(result.getUpdatedAt()).isEqualTo(testSupplier.getUpdatedAt());
        }
    }
}