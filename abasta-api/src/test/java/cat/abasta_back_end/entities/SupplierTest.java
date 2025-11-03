package cat.abasta_back_end.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaris per l'entitat Supplier.
 * Verifica la funcionalitat bàsica de l'entitat JPA.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DisplayName("Supplier Entity Tests")
class SupplierTest {

    private Company company;

    @BeforeEach
    void setUp() {
        company = Company.builder()
                .id(1L)
                .uuid("company-uuid-123")
                .name("Test Company SL")
                .build();
    }

    @Test
    @DisplayName("Builder hauria de funcionar correctament amb tots els camps")
    void builder_ShouldWorkCorrectlyWithAllFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        Supplier supplier = Supplier.builder()
                .id(1L)
                .uuid("supplier-uuid-123")
                .company(company)
                .name("Proveïdors Catalunya SL")
                .contactName("Joan Martínez")
                .email("joan@provcat.com")
                .phone("938765432")
                .address("Av. Diagonal 123, Barcelona")
                .notes("Notes del proveïdor")
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertThat(supplier.getId()).isEqualTo(1L);
        assertThat(supplier.getUuid()).isEqualTo("supplier-uuid-123");
        assertThat(supplier.getCompany()).isEqualTo(company);
        assertThat(supplier.getName()).isEqualTo("Proveïdors Catalunya SL");
        assertThat(supplier.getContactName()).isEqualTo("Joan Martínez");
        assertThat(supplier.getEmail()).isEqualTo("joan@provcat.com");
        assertThat(supplier.getPhone()).isEqualTo("938765432");
        assertThat(supplier.getAddress()).isEqualTo("Av. Diagonal 123, Barcelona");
        assertThat(supplier.getNotes()).isEqualTo("Notes del proveïdor");
        assertThat(supplier.getIsActive()).isTrue();
        assertThat(supplier.getCreatedAt()).isEqualTo(now);
        assertThat(supplier.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Builder hauria de funcionar amb camps mínims")
    void builder_ShouldWorkWithMinimalFields() {
        // Given & When
        Supplier supplier = Supplier.builder()
                .company(company)
                .name("Proveïdor Test")
                .build();

        // Then
        assertThat(supplier.getCompany()).isEqualTo(company);
        assertThat(supplier.getName()).isEqualTo("Proveïdor Test");
        assertThat(supplier.getIsActive()).isTrue(); // Valor per defecte
        assertThat(supplier.getId()).isNull();
        assertThat(supplier.getUuid()).isNull();
        assertThat(supplier.getContactName()).isNull();
        assertThat(supplier.getEmail()).isNull();
        assertThat(supplier.getPhone()).isNull();
        assertThat(supplier.getAddress()).isNull();
        assertThat(supplier.getNotes()).isNull();
        assertThat(supplier.getCreatedAt()).isNull();
        assertThat(supplier.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("isActive hauria de tenir valor per defecte true")
    void isActive_ShouldDefaultToTrue() {
        // Given & When
        Supplier supplier = Supplier.builder()
                .company(company)
                .name("Test Supplier")
                .build();

        // Then
        assertThat(supplier.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Constructor sense paràmetres hauria de crear objecte amb isActive per defecte")
    void noArgsConstructor_ShouldCreateEmptyObject() {
        // When
        Supplier supplier = new Supplier();

        // Then
        assertThat(supplier.getId()).isNull();
        assertThat(supplier.getUuid()).isNull();
        assertThat(supplier.getCompany()).isNull();
        assertThat(supplier.getName()).isNull();
        assertThat(supplier.getContactName()).isNull();
        assertThat(supplier.getEmail()).isNull();
        assertThat(supplier.getPhone()).isNull();
        assertThat(supplier.getAddress()).isNull();
        assertThat(supplier.getNotes()).isNull();
        assertThat(supplier.getIsActive()).isTrue(); // Valor per defecte de @Builder.Default
        assertThat(supplier.getCreatedAt()).isNull();
        assertThat(supplier.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Constructor amb tots els paràmetres hauria de funcionar correctament")
    void allArgsConstructor_ShouldWorkCorrectly() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        Supplier supplier = new Supplier(
                1L,
                "supplier-uuid-123",
                company,
                "Test Supplier",
                "Joan Garcia",
                "joan@test.com",
                "123456789",
                "Carrer Test 123",
                "Notes de test",
                true,
                now,
                now
        );

        // Then
        assertThat(supplier.getId()).isEqualTo(1L);
        assertThat(supplier.getUuid()).isEqualTo("supplier-uuid-123");
        assertThat(supplier.getCompany()).isEqualTo(company);
        assertThat(supplier.getName()).isEqualTo("Test Supplier");
        assertThat(supplier.getContactName()).isEqualTo("Joan Garcia");
        assertThat(supplier.getEmail()).isEqualTo("joan@test.com");
        assertThat(supplier.getPhone()).isEqualTo("123456789");
        assertThat(supplier.getAddress()).isEqualTo("Carrer Test 123");
        assertThat(supplier.getNotes()).isEqualTo("Notes de test");
        assertThat(supplier.getIsActive()).isTrue();
        assertThat(supplier.getCreatedAt()).isEqualTo(now);
        assertThat(supplier.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("setters i getters haurien de funcionar correctament")
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        Supplier supplier = new Supplier();
        LocalDateTime now = LocalDateTime.now();

        // When
        supplier.setId(1L);
        supplier.setUuid("test-uuid");
        supplier.setCompany(company);
        supplier.setName("Test Supplier");
        supplier.setContactName("Test Contact");
        supplier.setEmail("test@example.com");
        supplier.setPhone("123456789");
        supplier.setAddress("Test Address");
        supplier.setNotes("Test Notes");
        supplier.setIsActive(false);
        supplier.setCreatedAt(now);
        supplier.setUpdatedAt(now);

        // Then
        assertThat(supplier.getId()).isEqualTo(1L);
        assertThat(supplier.getUuid()).isEqualTo("test-uuid");
        assertThat(supplier.getCompany()).isEqualTo(company);
        assertThat(supplier.getName()).isEqualTo("Test Supplier");
        assertThat(supplier.getContactName()).isEqualTo("Test Contact");
        assertThat(supplier.getEmail()).isEqualTo("test@example.com");
        assertThat(supplier.getPhone()).isEqualTo("123456789");
        assertThat(supplier.getAddress()).isEqualTo("Test Address");
        assertThat(supplier.getNotes()).isEqualTo("Test Notes");
        assertThat(supplier.getIsActive()).isFalse();
        assertThat(supplier.getCreatedAt()).isEqualTo(now);
        assertThat(supplier.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("equals i hashCode haurien de funcionar correctament")
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        Supplier supplier1 = Supplier.builder()
                .id(1L)
                .uuid("supplier-uuid-123")
                .company(company)
                .name("Test Supplier")
                .isActive(true)
                .createdAt(now)
                .build();

        Supplier supplier2 = Supplier.builder()
                .id(1L)
                .uuid("supplier-uuid-123")
                .company(company)
                .name("Test Supplier")
                .isActive(true)
                .createdAt(now)
                .build();

        Supplier supplier3 = Supplier.builder()
                .id(2L)
                .uuid("different-uuid")
                .company(company)
                .name("Different Supplier")
                .isActive(true)
                .createdAt(now)
                .build();

        // Then
        assertThat(supplier1).isEqualTo(supplier2);
        assertThat(supplier1).isNotEqualTo(supplier3);
        assertThat(supplier1.hashCode()).isEqualTo(supplier2.hashCode());
        assertThat(supplier1.hashCode()).isNotEqualTo(supplier3.hashCode());
    }

    @Test
    @DisplayName("toString hauria de contenir informació rellevant")
    void toString_ShouldContainRelevantInformation() {
        // Given
        Supplier supplier = Supplier.builder()
                .id(1L)
                .uuid("supplier-uuid-123")
                .company(company)
                .name("Test Supplier")
                .email("test@example.com")
                .isActive(true)
                .build();

        // When
        String result = supplier.toString();

        // Then
        assertThat(result).contains("1");
        assertThat(result).contains("supplier-uuid-123");
        assertThat(result).contains("Test Supplier");
        assertThat(result).contains("test@example.com");
        assertThat(result).contains("true");
    }

    @Test
    @DisplayName("proveïdor inactiu hauria de mostrar isActive false")
    void inactiveSupplier_ShouldShowIsActiveFalse() {
        // Given & When
        Supplier supplier = Supplier.builder()
                .company(company)
                .name("Proveïdor Inactiu")
                .isActive(false)
                .build();

        // Then
        assertThat(supplier.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("supplier amb company null hauria de ser possible")
    void supplierWithNullCompany_ShouldBePossible() {
        // Given & When
        Supplier supplier = Supplier.builder()
                .name("Supplier sense company")
                .build();

        // Then
        assertThat(supplier.getCompany()).isNull();
        assertThat(supplier.getName()).isEqualTo("Supplier sense company");
    }

    @Test
    @DisplayName("camps de text llargs haurien de gestionar-se correctament")
    void longTextFields_ShouldBeHandledCorrectly() {
        // Given
        String longAddress = "A".repeat(500);
        String longNotes = "B".repeat(1000);

        // When
        Supplier supplier = Supplier.builder()
                .company(company)
                .name("Test Supplier")
                .address(longAddress)
                .notes(longNotes)
                .build();

        // Then
        assertThat(supplier.getAddress()).hasSize(500);
        assertThat(supplier.getNotes()).hasSize(1000);
        assertThat(supplier.getAddress()).isEqualTo(longAddress);
        assertThat(supplier.getNotes()).isEqualTo(longNotes);
    }

    @Test
    @DisplayName("simulació de PrePersist hauria de generar UUID i dates")
    void simulatePrePersist_ShouldGenerateUuidAndDates() {
        // Given
        Supplier supplier = Supplier.builder()
                .company(company)
                .name("Test Supplier")
                .build();

        LocalDateTime beforeCall = LocalDateTime.now();

        // When - Simulem el comportament de @PrePersist
        if (supplier.getUuid() == null) {
            supplier.setUuid(java.util.UUID.randomUUID().toString());
        }
        supplier.setCreatedAt(LocalDateTime.now());
        supplier.setUpdatedAt(LocalDateTime.now());

        LocalDateTime afterCall = LocalDateTime.now();

        // Then
        assertThat(supplier.getUuid()).isNotNull();
        assertThat(supplier.getUuid()).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        assertThat(supplier.getCreatedAt()).isBetween(beforeCall, afterCall);
        assertThat(supplier.getUpdatedAt()).isBetween(beforeCall, afterCall);
    }

    @Test
    @DisplayName("simulació de PreUpdate hauria d'actualitzar updatedAt")
    void simulatePreUpdate_ShouldUpdateUpdatedAt() {
        // Given
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusHours(1);

        Supplier supplier = Supplier.builder()
                .company(company)
                .name("Test Supplier")
                .createdAt(originalCreatedAt)
                .updatedAt(originalUpdatedAt)
                .build();

        LocalDateTime beforeUpdate = LocalDateTime.now();

        // When - Simulem el comportament de @PreUpdate
        supplier.setUpdatedAt(LocalDateTime.now());

        LocalDateTime afterUpdate = LocalDateTime.now();

        // Then
        assertThat(supplier.getCreatedAt()).isEqualTo(originalCreatedAt); // No canvia
        assertThat(supplier.getUpdatedAt()).isBetween(beforeUpdate, afterUpdate); // S'actualitza
        assertThat(supplier.getUpdatedAt()).isAfter(originalUpdatedAt);
    }
}