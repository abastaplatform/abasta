package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'integració per SupplierRepository.
 * Utilitza una base de dades H2 en memòria per testar les operacions de repositori.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("SupplierRepository Tests")
@Sql(scripts = "classpath:test-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SupplierRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SupplierRepository supplierRepository;

    private Company testCompany;
    private Supplier testSupplier;

    @BeforeEach
    void setUp() {
        // Crear empresa de test
        testCompany = new Company();
        testCompany.setUuid("company-uuid-123");
        testCompany.setName("Test Company SL");
        testCompany.setTaxId("B12345678");
        testCompany.setEmail("test@company.com");

        testCompany = entityManager.persistAndFlush(testCompany);

        // Crear proveïdor de test
        testSupplier = new Supplier();
        testSupplier.setUuid("supplier-uuid-123");
        testSupplier.setCompany(testCompany);
        testSupplier.setName("Proveïdors Catalunya SL");
        testSupplier.setContactName("Joan Martínez");
        testSupplier.setEmail("joan@provcat.com");
        testSupplier.setPhone("938765432");
        testSupplier.setIsActive(true);

        testSupplier = entityManager.persistAndFlush(testSupplier);
    }

    @Test
    @DisplayName("existsByCompanyUuidAndNameIgnoreCase hauria de retornar true quan el proveïdor existeix")
    void existsByCompanyUuidAndNameIgnoreCase_ShouldReturnTrue_WhenSupplierExists() {
        // When
        boolean exists = supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                testCompany.getUuid(),
                testSupplier.getName()
        );

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByCompanyUuidAndNameIgnoreCase hauria de retornar false quan el proveïdor no existeix")
    void existsByCompanyUuidAndNameIgnoreCase_ShouldReturnFalse_WhenSupplierDoesNotExist() {
        // When
        boolean exists = supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                testCompany.getUuid(),
                "Proveïdor Inexistent"
        );

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByCompanyUuidAndNameIgnoreCase hauria de ser case insensitive")
    void existsByCompanyUuidAndNameIgnoreCase_ShouldBeCaseInsensitive() {
        // When
        boolean existsLowerCase = supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                testCompany.getUuid(),
                testSupplier.getName().toLowerCase()
        );

        boolean existsUpperCase = supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                testCompany.getUuid(),
                testSupplier.getName().toUpperCase()
        );

        // Then
        assertThat(existsLowerCase).isTrue();
        assertThat(existsUpperCase).isTrue();
    }

    @Test
    @DisplayName("repositori hauria de permetre operacions CRUD bàsiques")
    void repository_ShouldSupportBasicCrudOperations() {
        // Given
        Supplier newSupplier = new Supplier();
        newSupplier.setUuid("new-supplier-uuid");
        newSupplier.setCompany(testCompany);
        newSupplier.setName("Nou Proveïdor SL");
        newSupplier.setIsActive(true);

        // When - Create
        Supplier savedSupplier = supplierRepository.save(newSupplier);

        // Then - Read
        assertThat(savedSupplier.getId()).isNotNull();
        assertThat(supplierRepository.findById(savedSupplier.getId())).isPresent();

        // When - Update
        savedSupplier.setName("Nom Actualitzat");
        Supplier updatedSupplier = supplierRepository.save(savedSupplier);

        // Then
        assertThat(updatedSupplier.getName()).isEqualTo("Nom Actualitzat");

        // When - Delete
        supplierRepository.delete(updatedSupplier);

        // Then
        assertThat(supplierRepository.findById(savedSupplier.getId())).isEmpty();
    }
}