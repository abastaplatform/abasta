package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests simplificats per SupplierRepository.
 * Utilitza una base de dades H2 en memòria per testar les operacions de repositori.
 *
 * @author Enrique Pérez
 * @version 3.0 - Simplified
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("SupplierRepository - Tests Simplificats")
class SupplierRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SupplierRepository supplierRepository;

    private Company testCompany;
    private Company otherCompany;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        // Crear empresa de test principal
        testCompany = Company.builder()
                .uuid("test-company-uuid")
                .name("Test Company")
                .taxId("12345678A")
                .email("test@company.com")
                .phone("666777888")
                .address("Test Street 123")
                .city("Test City")
                .postalCode("08001")
                .build();
        testCompany = entityManager.persistAndFlush(testCompany);

        // Crear segona empresa per tests d'aïllament
        otherCompany = Company.builder()
                .uuid("other-company-uuid")
                .name("Other Company")
                .taxId("87654321B")
                .email("other@company.com")
                .phone("999888777")
                .address("Other Street 456")
                .city("Other City")
                .postalCode("08002")
                .build();
        otherCompany = entityManager.persistAndFlush(otherCompany);
    }

    private Supplier createSupplier(Company company, String name, boolean isActive) {
        Supplier supplier = Supplier.builder()
                .company(company)
                .name(name)
                .contactName("Contact " + name)
                .email(name.toLowerCase().replace(" ", "") + "@supplier.com")
                .phone("666" + Math.abs(name.hashCode()) % 1000000)
                .address("Address " + name)
                .isActive(isActive)
                .build();
        return entityManager.persistAndFlush(supplier);
    }

    // ==================== Tests Bàsics ====================

    @Test
    @DisplayName("Hauria de trobar un proveïdor per UUID")
    void shouldFindSupplierByUuid() {
        // Given
        Supplier supplier = createSupplier(testCompany, "Test Supplier", true);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<Supplier> found = supplierRepository.findByUuid(supplier.getUuid());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Supplier");
        assertThat(found.get().getCompany().getUuid()).isEqualTo(testCompany.getUuid());
    }

    @Test
    @DisplayName("Hauria de retornar Optional buit quan no existeix l'UUID")
    void shouldReturnEmptyWhenUuidNotFound() {
        // When
        Optional<Supplier> found = supplierRepository.findByUuid("non-existent-uuid");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Hauria de verificar existència per nom (case insensitive)")
    void shouldCheckExistenceByNameIgnoreCase() {
        // Given
        createSupplier(testCompany, "Acme Corp", true);
        entityManager.flush();

        // When
        boolean exists1 = supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                testCompany.getUuid(), "Acme Corp");
        boolean exists2 = supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                testCompany.getUuid(), "ACME CORP");
        boolean exists3 = supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                testCompany.getUuid(), "acme corp");
        boolean notExists = supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                testCompany.getUuid(), "Other Supplier");

        // Then
        assertThat(exists1).isTrue();
        assertThat(exists2).isTrue();
        assertThat(exists3).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Hauria de verificar existència excloent UUID específic")
    void shouldCheckExistenceExcludingUuid() {
        // Given
        Supplier supplier1 = createSupplier(testCompany, "Test Supplier", true);
        Supplier supplier2 = createSupplier(testCompany, "Another Supplier", true);
        entityManager.flush();

        // When
        boolean exists = supplierRepository.existsByCompanyUuidAndNameIgnoreCaseAndUuidNot(
                testCompany.getUuid(), "Test Supplier", supplier2.getUuid());
        boolean notExists = supplierRepository.existsByCompanyUuidAndNameIgnoreCaseAndUuidNot(
                testCompany.getUuid(), "Test Supplier", supplier1.getUuid());

        // Then
        assertThat(exists).isTrue();  // Existeix perquè supplier1 té aquest nom i no l'excloem
        assertThat(notExists).isFalse();  // No existeix perquè excloem l'únic que té aquest nom
    }

    // ==================== Tests de findByCompanyUuidAndIsActiveTrue ====================

    @Test
    @DisplayName("Hauria de retornar només proveïdors actius d'una empresa")
    void shouldFindOnlyActiveSuppliersByCompany() {
        // Given
        createSupplier(testCompany, "Active Supplier 1", true);
        createSupplier(testCompany, "Active Supplier 2", true);
        createSupplier(testCompany, "Inactive Supplier", false);
        createSupplier(otherCompany, "Other Company Supplier", true);
        entityManager.flush();

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Supplier> result = supplierRepository.findByCompanyUuidAndIsActiveTrue(
                testCompany.getUuid(), pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Supplier::getName)
                .containsExactlyInAnyOrder("Active Supplier 1", "Active Supplier 2");

    }

    @Test
    @DisplayName("Hauria de respectar la paginació")
    void shouldRespectPagination() {
        // Given
        createSupplier(testCompany, "Supplier A", true);
        createSupplier(testCompany, "Supplier B", true);
        createSupplier(testCompany, "Supplier C", true);
        entityManager.flush();

        // When
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name"));
        Page<Supplier> firstPage = supplierRepository.findByCompanyUuidAndIsActiveTrue(
                testCompany.getUuid(), pageable);

        // Then
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getTotalElements()).isEqualTo(3);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getContent())
                .extracting(Supplier::getName)
                .containsExactly("Supplier A", "Supplier B");
    }

    @Test
    @DisplayName("Hauria de respectar l'ordenació")
    void shouldRespectSorting() {
        // Given
        createSupplier(testCompany, "Zebra Supplier", true);
        createSupplier(testCompany, "Alpha Supplier", true);
        createSupplier(testCompany, "Beta Supplier", true);
        entityManager.flush();

        // When
        Pageable ascending = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Supplier> ascResult = supplierRepository.findByCompanyUuidAndIsActiveTrue(
                testCompany.getUuid(), ascending);

        Pageable descending = PageRequest.of(0, 10, Sort.by("name").descending());
        Page<Supplier> descResult = supplierRepository.findByCompanyUuidAndIsActiveTrue(
                testCompany.getUuid(), descending);

        // Then
        assertThat(ascResult.getContent())
                .extracting(Supplier::getName)
                .containsExactly("Alpha Supplier", "Beta Supplier", "Zebra Supplier");

        assertThat(descResult.getContent())
                .extracting(Supplier::getName)
                .containsExactly("Zebra Supplier", "Beta Supplier", "Alpha Supplier");
    }

    @Test
    @DisplayName("Hauria de retornar pàgina buida quan no hi ha proveïdors actius")
    void shouldReturnEmptyPageWhenNoActiveSuppliers() {
        // Given
        createSupplier(testCompany, "Inactive Supplier", false);
        entityManager.flush();

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Supplier> result = supplierRepository.findByCompanyUuidAndIsActiveTrue(
                testCompany.getUuid(), pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Hauria d'aïllar dades per empresa")
    void shouldIsolateDataByCompany() {
        // Given
        createSupplier(testCompany, "Test Company Supplier", true);
        createSupplier(otherCompany, "Other Company Supplier 1", true);
        createSupplier(otherCompany, "Other Company Supplier 2", true);
        entityManager.flush();

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Supplier> testResult = supplierRepository.findByCompanyUuidAndIsActiveTrue(
                testCompany.getUuid(), pageable);
        Page<Supplier> otherResult = supplierRepository.findByCompanyUuidAndIsActiveTrue(
                otherCompany.getUuid(), pageable);

        // Then
        assertThat(testResult.getContent()).hasSize(1);
        assertThat(testResult.getContent().getFirst().getName()).isEqualTo("Test Company Supplier");

        assertThat(otherResult.getContent()).hasSize(2);
        assertThat(otherResult.getContent())
                .extracting(Supplier::getName)
                .containsExactlyInAnyOrder("Other Company Supplier 1", "Other Company Supplier 2");
    }

    // ==================== Tests CRUD Bàsics ====================

    @Test
    @DisplayName("Hauria de guardar un proveïdor correctament")
    void shouldSaveSupplier() {
        // Given
        Supplier supplier = Supplier.builder()
                .company(testCompany)
                .name("New Supplier")
                .contactName("John Doe")
                .email("john@newsupplier.com")
                .phone("666111222")
                .address("New Address 123")
                .isActive(true)
                .build();

        // When
        Supplier saved = supplierRepository.save(supplier);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUuid()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        Optional<Supplier> found = supplierRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("New Supplier");
    }

    @Test
    @DisplayName("Hauria d'actualitzar un proveïdor correctament")
    void shouldUpdateSupplier() {
        // Given
        Supplier supplier = createSupplier(testCompany, "Original Name", true);
        entityManager.flush();
        Long supplierId = supplier.getId();

        // When
        supplier.setName("Updated Name");
        supplier.setEmail("updated@email.com");
        supplierRepository.save(supplier);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Supplier> updated = supplierRepository.findById(supplierId);
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated Name");
        assertThat(updated.get().getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    @DisplayName("Hauria de comptar correctament els proveïdors")
    void shouldCountSuppliers() {
        // Given
        createSupplier(testCompany, "Supplier 1", true);
        createSupplier(testCompany, "Supplier 2", true);
        createSupplier(testCompany, "Supplier 3", false);
        createSupplier(otherCompany, "Other Supplier", true);
        entityManager.flush();

        // When
        long totalCount = supplierRepository.count();

        // Then
        assertThat(totalCount).isEqualTo(4);
    }
}