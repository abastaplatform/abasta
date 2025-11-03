package cat.abasta_back_end.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaris per SupplierResponseDTO.
 * Verifica la funcionalitat del DTO de resposta.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DisplayName("SupplierResponseDTO Tests")
class SupplierResponseDTOTest {

    @Test
    @DisplayName("Builder hauria de funcionar correctament amb tots els camps")
    void builder_ShouldWorkCorrectlyWithAllFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        String uuid = "supplier-uuid-123";
        String companyUuid = "company-uuid-456";
        String companyName = "Abasta SL";
        String name = "Proveïdors Catalunya SL";
        String contactName = "Joan Martínez";
        String email = "joan@provcat.com";
        String phone = "938765432";
        String address = "Av. Diagonal 123, Barcelona";
        String notes = "Notes del proveïdor";
        Boolean isActive = true;

        // When
        SupplierResponseDTO supplier = SupplierResponseDTO.builder()
                .uuid(uuid)
                .companyUuid(companyUuid)
                .companyName(companyName)
                .name(name)
                .contactName(contactName)
                .email(email)
                .phone(phone)
                .address(address)
                .notes(notes)
                .isActive(isActive)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertThat(supplier.getUuid()).isEqualTo(uuid);
        assertThat(supplier.getCompanyUuid()).isEqualTo(companyUuid);
        assertThat(supplier.getCompanyName()).isEqualTo(companyName);
        assertThat(supplier.getName()).isEqualTo(name);
        assertThat(supplier.getContactName()).isEqualTo(contactName);
        assertThat(supplier.getEmail()).isEqualTo(email);
        assertThat(supplier.getPhone()).isEqualTo(phone);
        assertThat(supplier.getAddress()).isEqualTo(address);
        assertThat(supplier.getNotes()).isEqualTo(notes);
        assertThat(supplier.getIsActive()).isEqualTo(isActive);
        assertThat(supplier.getCreatedAt()).isEqualTo(now);
        assertThat(supplier.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Builder hauria de funcionar amb camps mínims")
    void builder_ShouldWorkWithMinimalFields() {
        // Given & When
        SupplierResponseDTO supplier = SupplierResponseDTO.builder()
                .uuid("supplier-uuid-123")
                .companyUuid("company-uuid-456")
                .name("Proveïdor Test")
                .isActive(true)
                .build();

        // Then
        assertThat(supplier.getUuid()).isEqualTo("supplier-uuid-123");
        assertThat(supplier.getCompanyUuid()).isEqualTo("company-uuid-456");
        assertThat(supplier.getName()).isEqualTo("Proveïdor Test");
        assertThat(supplier.getIsActive()).isTrue();
        assertThat(supplier.getCompanyName()).isNull();
        assertThat(supplier.getContactName()).isNull();
        assertThat(supplier.getEmail()).isNull();
        assertThat(supplier.getPhone()).isNull();
        assertThat(supplier.getAddress()).isNull();
        assertThat(supplier.getNotes()).isNull();
        assertThat(supplier.getCreatedAt()).isNull();
        assertThat(supplier.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Constructor sense paràmetres hauria de crear objecte buit")
    void noArgsConstructor_ShouldCreateEmptyObject() {
        // When
        SupplierResponseDTO supplier = new SupplierResponseDTO();

        // Then
        assertThat(supplier.getUuid()).isNull();
        assertThat(supplier.getCompanyUuid()).isNull();
        assertThat(supplier.getCompanyName()).isNull();
        assertThat(supplier.getName()).isNull();
        assertThat(supplier.getContactName()).isNull();
        assertThat(supplier.getEmail()).isNull();
        assertThat(supplier.getPhone()).isNull();
        assertThat(supplier.getAddress()).isNull();
        assertThat(supplier.getNotes()).isNull();
        assertThat(supplier.getIsActive()).isNull();
        assertThat(supplier.getCreatedAt()).isNull();
        assertThat(supplier.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Constructor amb tots els paràmetres hauria de funcionar correctament")
    void allArgsConstructor_ShouldWorkCorrectly() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        SupplierResponseDTO supplier = new SupplierResponseDTO(
                "supplier-uuid-123",
                "company-uuid-456",
                "Abasta SL",
                "Proveïdor Test",
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
        assertThat(supplier.getUuid()).isEqualTo("supplier-uuid-123");
        assertThat(supplier.getCompanyUuid()).isEqualTo("company-uuid-456");
        assertThat(supplier.getCompanyName()).isEqualTo("Abasta SL");
        assertThat(supplier.getName()).isEqualTo("Proveïdor Test");
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
        SupplierResponseDTO supplier = new SupplierResponseDTO();
        LocalDateTime now = LocalDateTime.now();

        // When
        supplier.setUuid("test-uuid");
        supplier.setCompanyUuid("test-company-uuid");
        supplier.setCompanyName("Test Company");
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
        assertThat(supplier.getUuid()).isEqualTo("test-uuid");
        assertThat(supplier.getCompanyUuid()).isEqualTo("test-company-uuid");
        assertThat(supplier.getCompanyName()).isEqualTo("Test Company");
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

        SupplierResponseDTO supplier1 = SupplierResponseDTO.builder()
                .uuid("supplier-uuid-123")
                .companyUuid("company-uuid-456")
                .name("Test Supplier")
                .isActive(true)
                .createdAt(now)
                .build();

        SupplierResponseDTO supplier2 = SupplierResponseDTO.builder()
                .uuid("supplier-uuid-123")
                .companyUuid("company-uuid-456")
                .name("Test Supplier")
                .isActive(true)
                .createdAt(now)
                .build();

        SupplierResponseDTO supplier3 = SupplierResponseDTO.builder()
                .uuid("different-uuid")
                .companyUuid("company-uuid-456")
                .name("Test Supplier")
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
        SupplierResponseDTO supplier = SupplierResponseDTO.builder()
                .uuid("supplier-uuid-123")
                .companyUuid("company-uuid-456")
                .name("Test Supplier")
                .email("test@example.com")
                .isActive(true)
                .build();

        // When
        String result = supplier.toString();

        // Then
        assertThat(result).contains("supplier-uuid-123");
        assertThat(result).contains("company-uuid-456");
        assertThat(result).contains("Test Supplier");
        assertThat(result).contains("test@example.com");
        assertThat(result).contains("true");
    }

    @Test
    @DisplayName("proveïdor inactiu hauria de mostrar isActive false")
    void inactiveSupplier_ShouldShowIsActiveFalse() {
        // Given & When
        SupplierResponseDTO supplier = SupplierResponseDTO.builder()
                .uuid("supplier-uuid-123")
                .name("Proveïdor Inactiu")
                .isActive(false)
                .build();

        // Then
        assertThat(supplier.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("camps de dates haurien de gestionar null correctament")
    void dateFields_ShouldHandleNullCorrectly() {
        // Given & When
        SupplierResponseDTO supplier = SupplierResponseDTO.builder()
                .uuid("supplier-uuid-123")
                .name("Test Supplier")
                .createdAt(null)
                .updatedAt(null)
                .build();

        // Then
        assertThat(supplier.getCreatedAt()).isNull();
        assertThat(supplier.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("objecte amb camps opcionals buits hauria de ser vàlid")
    void objectWithEmptyOptionalFields_ShouldBeValid() {
        // Given & When
        SupplierResponseDTO supplier = SupplierResponseDTO.builder()
                .uuid("supplier-uuid-123")
                .companyUuid("company-uuid-456")
                .name("Essential Supplier")
                .isActive(true)
                // Deixem la resta de camps com null
                .build();

        // Then
        assertThat(supplier.getUuid()).isNotNull();
        assertThat(supplier.getCompanyUuid()).isNotNull();
        assertThat(supplier.getName()).isNotNull();
        assertThat(supplier.getIsActive()).isNotNull();

        // Camps opcionals
        assertThat(supplier.getCompanyName()).isNull();
        assertThat(supplier.getContactName()).isNull();
        assertThat(supplier.getEmail()).isNull();
        assertThat(supplier.getPhone()).isNull();
        assertThat(supplier.getAddress()).isNull();
        assertThat(supplier.getNotes()).isNull();
    }
}