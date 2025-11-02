package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.Company.CompanyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaris simplificats per al repositori CompanyRepository.
 * Utilitza @Sql per configurar l'esquema de manera simple.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/test-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("CompanyRepository Tests")
class CompanyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompanyRepository companyRepository;

    private Company testCompany;

    @BeforeEach
    void setUp() {
        testCompany = Company.builder()
                .name("Empresa Test SL")
                .taxId("B12345678")
                .email("test@empresa.com")
                .phone("123456789")
                .address("Carrer Test 123")
                .city("Barcelona")
                .postalCode("08001")
                .status(CompanyStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Hauria de retornar true quan existeix una empresa amb el taxId especificat")
    void existsByTaxId_ShouldReturnTrue_WhenCompanyExistsWithGivenTaxId() {
        // Given
        entityManager.persistAndFlush(testCompany);

        // When
        boolean exists = companyRepository.existsByTaxId("B12345678");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Hauria de retornar false quan no existeix cap empresa amb el taxId especificat")
    void existsByTaxId_ShouldReturnFalse_WhenNoCompanyExistsWithGivenTaxId() {
        // When
        boolean exists = companyRepository.existsByTaxId("B99999999");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Hauria de retornar false quan el taxId és null")
    void existsByTaxId_ShouldReturnFalse_WhenTaxIdIsNull() {
        // Given
        entityManager.persistAndFlush(testCompany);

        // When
        boolean exists = companyRepository.existsByTaxId(null);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Hauria de desar i recuperar una empresa correctament")
    void save_ShouldPersistAndRetrieveCompany_Successfully() {
        // When
        Company savedCompany = companyRepository.save(testCompany);

        // Then
        assertThat(savedCompany).isNotNull();
        assertThat(savedCompany.getId()).isNotNull();
        assertThat(savedCompany.getName()).isEqualTo("Empresa Test SL");
        assertThat(savedCompany.getTaxId()).isEqualTo("B12345678");
        assertThat(savedCompany.getEmail()).isEqualTo("test@empresa.com");
    }

    @Test
    @DisplayName("Hauria de trobar una empresa per ID quan existeix")
    void findById_ShouldReturnCompany_WhenCompanyExists() {
        // Given
        Company savedCompany = entityManager.persistAndFlush(testCompany);

        // When
        Optional<Company> foundCompany = companyRepository.findById(savedCompany.getId());

        // Then
        assertThat(foundCompany).isPresent();
        assertThat(foundCompany.get().getName()).isEqualTo("Empresa Test SL");
        assertThat(foundCompany.get().getTaxId()).isEqualTo("B12345678");
    }

    @Test
    @DisplayName("Hauria de comptar correctament el nombre d'empreses")
    void count_ShouldReturnCorrectCount() {
        // Given
        Company company1 = Company.builder()
                .name("Empresa 1")
                .taxId("B11111111")
                .email("empresa1@test.com")
                .status(CompanyStatus.ACTIVE)
                .build();

        Company company2 = Company.builder()
                .name("Empresa 2")
                .taxId("B22222222")
                .email("empresa2@test.com")
                .status(CompanyStatus.PENDING)
                .build();

        entityManager.persistAndFlush(company1);
        entityManager.persistAndFlush(company2);

        // When
        long count = companyRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("ExistsByTaxId hauria de ser case-sensitive")
    void existsByTaxId_ShouldBeCaseSensitive() {
        // Given
        Company company = Company.builder()
                .name("Empresa Case Test")
                .taxId("B12345678")
                .email("case@test.com")
                .status(CompanyStatus.ACTIVE)
                .build();
        entityManager.persistAndFlush(company);

        // When
        boolean existsUpper = companyRepository.existsByTaxId("B12345678");
        boolean existsLower = companyRepository.existsByTaxId("b12345678");

        // Then
        assertThat(existsUpper).isTrue();
        assertThat(existsLower).isFalse();
    }

    @Test
    @DisplayName("FindByUuid hauria de retornar empresa quan UUID existeix")
    void findByUuid_ShouldReturnCompany_WhenUuidExists() {
        // Given
        Company savedCompany = entityManager.persistAndFlush(testCompany);
        String uuid = savedCompany.getUuid();

        // When
        Optional<Company> found = companyRepository.findByUuid(uuid);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Empresa Test SL");
        assertThat(found.get().getUuid()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("FindByUuid hauria de retornar empty quan UUID no existeix")
    void findByUuid_ShouldReturnEmpty_WhenUuidNotExists() {
        // When
        Optional<Company> found = companyRepository.findByUuid("non-existent-uuid");

        // Then
        assertThat(found).isEmpty();
    }
}