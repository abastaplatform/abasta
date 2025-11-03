package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.SupplierRequestDTO;
import cat.abasta_back_end.dto.SupplierResponseDTO;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.Supplier;
import cat.abasta_back_end.exceptions.DuplicateResourceException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.CompanyRepository;
import cat.abasta_back_end.repositories.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaris per SupplierServiceImpl.
 * Verifica la lògica de negoci del servei de proveïdors.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierService Tests")
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private SupplierRequestDTO validSupplierRequest;
    private Company testCompany;
    private Supplier savedSupplier;

    @BeforeEach
    void setUp() {
        // Company de test
        testCompany = Company.builder()
                .id(1L)
                .uuid("company-uuid-123")
                .name("Test Company SL")
                .taxId("B12345678")
                .email("test@company.com")
                .build();

        // SupplierRequestDTO vàlid
        validSupplierRequest = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdors Catalunya SL")
                .contactName("Joan Martínez")
                .email("joan@provcat.com")
                .phone("938765432")
                .address("Av. Diagonal 123, Barcelona")
                .notes("Notes del proveïdor")
                .isActive(true)
                .build();

        // Supplier que retornaria save()
        savedSupplier = Supplier.builder()
                .id(1L)
                .uuid("supplier-uuid-123")
                .company(testCompany)
                .name("Proveïdors Catalunya SL")
                .contactName("Joan Martínez")
                .email("joan@provcat.com")
                .phone("938765432")
                .address("Av. Diagonal 123, Barcelona")
                .notes("Notes del proveïdor")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("createSupplier hauria de crear proveïdor correctament quan les dades són vàlides")
    void createSupplier_ShouldCreateSupplier_WhenValidData() {
        // Given
        when(companyRepository.findByUuid(validSupplierRequest.getCompanyUuid()))
                .thenReturn(Optional.of(testCompany));
        when(supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                validSupplierRequest.getCompanyUuid(),
                validSupplierRequest.getName()))
                .thenReturn(false);
        when(supplierRepository.save(any(Supplier.class)))
                .thenReturn(savedSupplier);

        // When
        SupplierResponseDTO result = supplierService.createSupplier(validSupplierRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo("supplier-uuid-123");
        assertThat(result.getCompanyUuid()).isEqualTo("company-uuid-123");
        assertThat(result.getCompanyName()).isEqualTo("Test Company SL");
        assertThat(result.getName()).isEqualTo("Proveïdors Catalunya SL");
        assertThat(result.getContactName()).isEqualTo("Joan Martínez");
        assertThat(result.getEmail()).isEqualTo("joan@provcat.com");
        assertThat(result.getPhone()).isEqualTo("938765432");
        assertThat(result.getAddress()).isEqualTo("Av. Diagonal 123, Barcelona");
        assertThat(result.getNotes()).isEqualTo("Notes del proveïdor");
        assertThat(result.getIsActive()).isTrue();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();

        // Verify
        verify(companyRepository).findByUuid(validSupplierRequest.getCompanyUuid());
        verify(supplierRepository).existsByCompanyUuidAndNameIgnoreCase(
                validSupplierRequest.getCompanyUuid(),
                validSupplierRequest.getName());
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    @DisplayName("createSupplier hauria de llançar ResourceNotFoundException quan l'empresa no existeix")
    void createSupplier_ShouldThrowResourceNotFoundException_WhenCompanyNotFound() {
        // Given
        when(companyRepository.findByUuid(validSupplierRequest.getCompanyUuid()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> supplierService.createSupplier(validSupplierRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Empresa no trobada amb UUID: company-uuid-123");

        // Verify
        verify(companyRepository).findByUuid(validSupplierRequest.getCompanyUuid());
        verify(supplierRepository, never()).existsByCompanyUuidAndNameIgnoreCase(anyString(), anyString());
        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    @Test
    @DisplayName("createSupplier hauria de llançar DuplicateResourceException quan ja existeix proveïdor amb mateix nom")
    void createSupplier_ShouldThrowDuplicateResourceException_WhenSupplierNameAlreadyExists() {
        // Given
        when(companyRepository.findByUuid(validSupplierRequest.getCompanyUuid()))
                .thenReturn(Optional.of(testCompany));
        when(supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                validSupplierRequest.getCompanyUuid(),
                validSupplierRequest.getName()))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> supplierService.createSupplier(validSupplierRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Ja existeix un proveïdor amb el nom 'Proveïdors Catalunya SL' a l'empresa especificada");

        // Verify
        verify(companyRepository).findByUuid(validSupplierRequest.getCompanyUuid());
        verify(supplierRepository).existsByCompanyUuidAndNameIgnoreCase(
                validSupplierRequest.getCompanyUuid(),
                validSupplierRequest.getName());
        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    @Test
    @DisplayName("createSupplier hauria de crear proveïdor amb camps opcionals null")
    void createSupplier_ShouldCreateSupplier_WithOptionalFieldsNull() {
        // Given
        SupplierRequestDTO minimalRequest = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdor Mínim")
                .isActive(true)
                .build();

        Supplier minimalSupplier = Supplier.builder()
                .id(2L)
                .uuid("supplier-uuid-456")
                .company(testCompany)
                .name("Proveïdor Mínim")
                .contactName(null)
                .email(null)
                .phone(null)
                .address(null)
                .notes(null)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(companyRepository.findByUuid(minimalRequest.getCompanyUuid()))
                .thenReturn(Optional.of(testCompany));
        when(supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                minimalRequest.getCompanyUuid(),
                minimalRequest.getName()))
                .thenReturn(false);
        when(supplierRepository.save(any(Supplier.class)))
                .thenReturn(minimalSupplier);

        // When
        SupplierResponseDTO result = supplierService.createSupplier(minimalRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo("supplier-uuid-456");
        assertThat(result.getName()).isEqualTo("Proveïdor Mínim");
        assertThat(result.getContactName()).isNull();
        assertThat(result.getEmail()).isNull();
        assertThat(result.getPhone()).isNull();
        assertThat(result.getAddress()).isNull();
        assertThat(result.getNotes()).isNull();
        assertThat(result.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("createSupplier hauria de crear proveïdor inactiu quan isActive és false")
    void createSupplier_ShouldCreateInactiveSupplier_WhenIsActiveFalse() {
        // Given
        SupplierRequestDTO inactiveRequest = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdor Inactiu")
                .isActive(false)
                .build();

        Supplier inactiveSupplier = Supplier.builder()
                .id(3L)
                .uuid("supplier-uuid-789")
                .company(testCompany)
                .name("Proveïdor Inactiu")
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(companyRepository.findByUuid(inactiveRequest.getCompanyUuid()))
                .thenReturn(Optional.of(testCompany));
        when(supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                inactiveRequest.getCompanyUuid(),
                inactiveRequest.getName()))
                .thenReturn(false);
        when(supplierRepository.save(any(Supplier.class)))
                .thenReturn(inactiveSupplier);

        // When
        SupplierResponseDTO result = supplierService.createSupplier(inactiveRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("createSupplier hauria de gestionar noms amb majúscules i minúscules")
    void createSupplier_ShouldHandleCaseInsensitiveNames() {
        // Given
        SupplierRequestDTO upperCaseRequest = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("PROVEÏDOR EN MAJÚSCULES")
                .isActive(true)
                .build();

        when(companyRepository.findByUuid(upperCaseRequest.getCompanyUuid()))
                .thenReturn(Optional.of(testCompany));
        when(supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                upperCaseRequest.getCompanyUuid(),
                upperCaseRequest.getName()))
                .thenReturn(true); // Simular que ja existeix (case insensitive)

        // When & Then
        assertThatThrownBy(() -> supplierService.createSupplier(upperCaseRequest))
                .isInstanceOf(DuplicateResourceException.class);

        // Verify que es va cridar amb case insensitive
        verify(supplierRepository).existsByCompanyUuidAndNameIgnoreCase(
                upperCaseRequest.getCompanyUuid(),
                "PROVEÏDOR EN MAJÚSCULES");
    }

    @Test
    @DisplayName("mapToResponseDTO hauria de mapejar correctament tots els camps")
    void mapToResponseDTO_ShouldMapAllFieldsCorrectly() {
        // Given
        when(companyRepository.findByUuid(validSupplierRequest.getCompanyUuid()))
                .thenReturn(Optional.of(testCompany));
        when(supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                validSupplierRequest.getCompanyUuid(),
                validSupplierRequest.getName()))
                .thenReturn(false);
        when(supplierRepository.save(any(Supplier.class)))
                .thenReturn(savedSupplier);

        // When
        SupplierResponseDTO result = supplierService.createSupplier(validSupplierRequest);

        // Then - Verificar que tots els camps es mapegen correctament
        assertThat(result.getUuid()).isEqualTo(savedSupplier.getUuid());
        assertThat(result.getCompanyUuid()).isEqualTo(savedSupplier.getCompany().getUuid());
        assertThat(result.getCompanyName()).isEqualTo(savedSupplier.getCompany().getName());
        assertThat(result.getName()).isEqualTo(savedSupplier.getName());
        assertThat(result.getContactName()).isEqualTo(savedSupplier.getContactName());
        assertThat(result.getEmail()).isEqualTo(savedSupplier.getEmail());
        assertThat(result.getPhone()).isEqualTo(savedSupplier.getPhone());
        assertThat(result.getAddress()).isEqualTo(savedSupplier.getAddress());
        assertThat(result.getNotes()).isEqualTo(savedSupplier.getNotes());
        assertThat(result.getIsActive()).isEqualTo(savedSupplier.getIsActive());
        assertThat(result.getCreatedAt()).isEqualTo(savedSupplier.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(savedSupplier.getUpdatedAt());
    }

    @Test
    @DisplayName("createSupplier hauria de gestionar UUID d'empresa null")
    void createSupplier_ShouldHandleNullCompanyUuid() {
        // Given
        SupplierRequestDTO nullUuidRequest = SupplierRequestDTO.builder()
                .companyUuid(null)
                .name("Test Supplier")
                .isActive(true)
                .build();

        when(companyRepository.findByUuid(null))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> supplierService.createSupplier(nullUuidRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Empresa no trobada amb UUID: null");
    }
}