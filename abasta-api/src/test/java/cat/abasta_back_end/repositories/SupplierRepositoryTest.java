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

    // ================= TESTS PER NOUS MÈTODES =================

    @Test
    @DisplayName("findByUuid hauria de retornar proveïdor quan existeix")
    void findByUuid_ShouldReturnSupplier_WhenExists() {
        // When
        var result = supplierRepository.findByUuid(testSupplier.getUuid());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUuid()).isEqualTo(testSupplier.getUuid());
        assertThat(result.get().getName()).isEqualTo("Proveïdors Catalunya SL");
        assertThat(result.get().getCompany().getUuid()).isEqualTo(testCompany.getUuid());
    }

    @Test
    @DisplayName("findByUuid hauria de retornar Optional buit quan no existeix")
    void findByUuid_ShouldReturnEmpty_WhenNotExists() {
        // When
        var result = supplierRepository.findByUuid("uuid-inexistent");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUuid hauria de gestionar UUID null")
    void findByUuid_ShouldHandleNullUuid() {
        // When
        var result = supplierRepository.findByUuid(null);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByCompanyUuidAndNameIgnoreCaseAndUuidNot hauria de retornar false quan només existeix el proveïdor exclos")
    void existsByCompanyUuidAndNameIgnoreCaseAndUuidNot_ShouldReturnFalse_WhenOnlyExcludedExists() {
        // When - Excloem el proveïdor existent, per tant no hauria d'existir cap altre amb el mateix nom
        boolean exists = supplierRepository.existsByCompanyUuidAndNameIgnoreCaseAndUuidNot(
                testCompany.getUuid(),
                testSupplier.getName(),
                testSupplier.getUuid()
        );

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByCompanyUuidAndNameIgnoreCaseAndUuidNot hauria de retornar true quan existeix un altre proveïdor amb el mateix nom")
    void existsByCompanyUuidAndNameIgnoreCaseAndUuidNot_ShouldReturnTrue_WhenAnotherSupplierExists() {
        // Given - Crear un altre proveïdor amb nom similar
        Supplier anotherSupplier = new Supplier();
        anotherSupplier.setUuid("another-supplier-uuid");
        anotherSupplier.setCompany(testCompany);
        anotherSupplier.setName("Proveïdors Catalunya SL"); // Mateix nom
        anotherSupplier.setIsActive(true);
        entityManager.persistAndFlush(anotherSupplier);

        // When - Excloem el primer proveïdor, però el segon encara existeix
        boolean exists = supplierRepository.existsByCompanyUuidAndNameIgnoreCaseAndUuidNot(
                testCompany.getUuid(),
                testSupplier.getName(),
                testSupplier.getUuid()
        );

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByCompanyUuidAndNameIgnoreCaseAndUuidNot hauria de ser case insensitive")
    void existsByCompanyUuidAndNameIgnoreCaseAndUuidNot_ShouldBeCaseInsensitive() {
        // Given - Crear proveïdor amb nom en majúscules
        Supplier upperCaseSupplier = new Supplier();
        upperCaseSupplier.setUuid("uppercase-supplier-uuid");
        upperCaseSupplier.setCompany(testCompany);
        upperCaseSupplier.setName("PROVEÏDORS CATALUNYA SL");
        upperCaseSupplier.setIsActive(true);
        entityManager.persistAndFlush(upperCaseSupplier);

        // When - Cercar amb minúscules excloent el proveïdor original
        boolean exists = supplierRepository.existsByCompanyUuidAndNameIgnoreCaseAndUuidNot(
                testCompany.getUuid(),
                "proveïdors catalunya sl",
                testSupplier.getUuid()
        );

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByCompanyUuidAndNameIgnoreCaseAndUuidNot hauria de gestionar empresa diferent")
    void existsByCompanyUuidAndNameIgnoreCaseAndUuidNot_ShouldHandleDifferentCompany() {
        // Given - Crear altra empresa amb proveïdor del mateix nom
        Company otherCompany = new Company();
        otherCompany.setUuid("other-company-uuid");
        otherCompany.setName("Other Company SL");
        otherCompany.setTaxId("B87654321");
        otherCompany.setEmail("other@company.com");
        entityManager.persistAndFlush(otherCompany);

        Supplier otherCompanySupplier = new Supplier();
        otherCompanySupplier.setUuid("other-company-supplier-uuid");
        otherCompanySupplier.setCompany(otherCompany);
        otherCompanySupplier.setName("Proveïdors Catalunya SL"); // Mateix nom però altra empresa
        otherCompanySupplier.setIsActive(true);
        entityManager.persistAndFlush(otherCompanySupplier);

        // When - Cercar en l'empresa original
        boolean exists = supplierRepository.existsByCompanyUuidAndNameIgnoreCaseAndUuidNot(
                testCompany.getUuid(),
                testSupplier.getName(),
                testSupplier.getUuid()
        );

        // Then - No hauria de trobar el proveïdor de l'altra empresa
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByCompanyUuid hauria de retornar tots els proveïdors de l'empresa")
    void findByCompanyUuid_ShouldReturnAllSuppliersFromCompany() {
        // Given - Afegir més proveïdors a la mateixa empresa
        Supplier supplier2 = new Supplier();
        supplier2.setUuid("supplier-2-uuid");
        supplier2.setCompany(testCompany);
        supplier2.setName("Segon Proveïdor SL");
        supplier2.setIsActive(true);
        entityManager.persistAndFlush(supplier2);

        Supplier supplier3 = new Supplier();
        supplier3.setUuid("supplier-3-uuid");
        supplier3.setCompany(testCompany);
        supplier3.setName("Tercer Proveïdor SL");
        supplier3.setIsActive(false); // Inactiu
        entityManager.persistAndFlush(supplier3);

        // When
        var suppliers = supplierRepository.findByCompanyUuid(testCompany.getUuid());

        // Then
        assertThat(suppliers).hasSize(3); // Inclou actius i inactius
        assertThat(suppliers)
                .extracting(Supplier::getUuid)
                .containsExactlyInAnyOrder(
                        testSupplier.getUuid(),
                        supplier2.getUuid(),
                        supplier3.getUuid()
                );
        assertThat(suppliers)
                .allMatch(s -> s.getCompany().getUuid().equals(testCompany.getUuid()));
    }

    @Test
    @DisplayName("findByCompanyUuid hauria de retornar llista buida quan no hi ha proveïdors")
    void findByCompanyUuid_ShouldReturnEmptyList_WhenNoSuppliers() {
        // Given - Crear nova empresa sense proveïdors
        Company emptyCompany = new Company();
        emptyCompany.setUuid("empty-company-uuid");
        emptyCompany.setName("Empty Company SL");
        emptyCompany.setTaxId("B11111111");
        emptyCompany.setEmail("empty@company.com");
        entityManager.persistAndFlush(emptyCompany);

        // When
        var suppliers = supplierRepository.findByCompanyUuid(emptyCompany.getUuid());

        // Then
        assertThat(suppliers).isEmpty();
    }

    @Test
    @DisplayName("findByCompanyUuid hauria de retornar llista buida per empresa inexistent")
    void findByCompanyUuid_ShouldReturnEmptyList_WhenCompanyNotExists() {
        // When
        var suppliers = supplierRepository.findByCompanyUuid("company-uuid-inexistent");

        // Then
        assertThat(suppliers).isEmpty();
    }

    @Test
    @DisplayName("findByCompanyUuid no hauria de retornar proveïdors d'altres empreses")
    void findByCompanyUuid_ShouldNotReturnSuppliersFromOtherCompanies() {
        // Given - Crear altra empresa amb proveïdors
        Company otherCompany = new Company();
        otherCompany.setUuid("other-company-uuid-2");
        otherCompany.setName("Other Company 2 SL");
        otherCompany.setTaxId("B99999999");
        otherCompany.setEmail("other2@company.com");
        entityManager.persistAndFlush(otherCompany);

        Supplier otherSupplier = new Supplier();
        otherSupplier.setUuid("other-supplier-uuid");
        otherSupplier.setCompany(otherCompany);
        otherSupplier.setName("Proveïdor Altra Empresa");
        otherSupplier.setIsActive(true);
        entityManager.persistAndFlush(otherSupplier);

        // When - Cercar proveïdors de l'empresa original
        var suppliers = supplierRepository.findByCompanyUuid(testCompany.getUuid());

        // Then - Només hauria de trobar el proveïdor de l'empresa original
        assertThat(suppliers).hasSize(1);
        assertThat(suppliers.getFirst().getUuid()).isEqualTo(testSupplier.getUuid());
        assertThat(suppliers)
                .allMatch(s -> s.getCompany().getUuid().equals(testCompany.getUuid()));
    }
}