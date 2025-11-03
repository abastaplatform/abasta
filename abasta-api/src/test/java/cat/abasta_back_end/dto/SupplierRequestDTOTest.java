package cat.abasta_back_end.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaris per SupplierRequestDTO.
 * Verifica les validacions dels camps del DTO.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DisplayName("SupplierRequestDTO Tests")
class SupplierRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("DTO vàlid hauria de passar totes les validacions")
    void validSupplier_ShouldPassAllValidations() {
        // Given
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdor Test SL")
                .contactName("Joan Garcia")
                .email("joan@test.com")
                .phone("123456789")
                .address("Carrer Test 123")
                .notes("Notes de test")
                .isActive(true)
                .build();

        // When
        Set<ConstraintViolation<SupplierRequestDTO>> violations = validator.validate(supplier);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("companyUuid buit hauria de fallar la validació")
    void emptyCompanyUuid_ShouldFailValidation() {
        // Given
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("")
                .name("Proveïdor Test SL")
                .build();

        // When
        Set<ConstraintViolation<SupplierRequestDTO>> violations = validator.validate(supplier);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("L'UUID de l'empresa és obligatori");
    }

    @Test
    @DisplayName("companyUuid null hauria de fallar la validació")
    void nullCompanyUuid_ShouldFailValidation() {
        // Given
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid(null)
                .name("Proveïdor Test SL")
                .build();

        // When
        Set<ConstraintViolation<SupplierRequestDTO>> violations = validator.validate(supplier);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("L'UUID de l'empresa és obligatori");
    }

    @Test
    @DisplayName("nom buit hauria de fallar la validació")
    void emptyName_ShouldFailValidation() {
        // Given
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("")
                .build();

        // When
        Set<ConstraintViolation<SupplierRequestDTO>> violations = validator.validate(supplier);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("El nom del proveïdor és obligatori");
    }

    @Test
    @DisplayName("nom massa llarg hauria de fallar la validació")
    void tooLongName_ShouldFailValidation() {
        // Given
        String longName = "A".repeat(256); // 256 caràcters
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name(longName)
                .build();

        // When
        Set<ConstraintViolation<SupplierRequestDTO>> violations = validator.validate(supplier);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("El nom no pot superar els 255 caràcters");
    }

    @Test
    @DisplayName("email invàlid hauria de fallar la validació")
    void invalidEmail_ShouldFailValidation() {
        // Given
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdor Test SL")
                .email("email-invalid")
                .build();

        // When
        Set<ConstraintViolation<SupplierRequestDTO>> violations = validator.validate(supplier);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("L'adreça de correu electrònic ha de tenir un format vàlid");
    }

    @Test
    @DisplayName("email massa llarg hauria de fallar la validació")
    void tooLongEmail_ShouldFailValidation() {
        // Given - Crearem un email vàlid però massa llarg
        String longEmail = "a".repeat(240) + "@valid-domain.com"; // 256 caràcters total
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdor Test SL")
                .email(longEmail)
                .build();

        // When
        Set<ConstraintViolation<SupplierRequestDTO>> violations = validator.validate(supplier);

        // Then - Pot haver-hi 1 o 2 violacions depenent del validador
        assertThat(violations).hasSizeGreaterThanOrEqualTo(1);
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().equals("L'email no pot superar els 255 caràcters")))
                .isTrue();
    }

    @Test
    @DisplayName("telèfon massa llarg hauria de fallar la validació")
    void tooLongPhone_ShouldFailValidation() {
        // Given
        String longPhone = "1".repeat(51); // 51 caràcters
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdor Test SL")
                .phone(longPhone)
                .build();

        // When
        Set<ConstraintViolation<SupplierRequestDTO>> violations = validator.validate(supplier);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("El telèfon no pot superar els 50 caràcters");
    }

    @Test
    @DisplayName("contactName massa llarg hauria de fallar la validació")
    void tooLongContactName_ShouldFailValidation() {
        // Given
        String longContactName = "A".repeat(256); // 256 caràcters
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdor Test SL")
                .contactName(longContactName)
                .build();

        // When
        Set<ConstraintViolation<SupplierRequestDTO>> violations = validator.validate(supplier);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("El nom de contacte no pot superar els 255 caràcters");
    }

    @Test
    @DisplayName("camps opcionals null haurien de ser vàlids")
    void optionalFieldsNull_ShouldBeValid() {
        // Given
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdor Test SL")
                .contactName(null)
                .email(null)
                .phone(null)
                .address(null)
                .notes(null)
                .isActive(null)
                .build();

        // When
        Set<ConstraintViolation<SupplierRequestDTO>> violations = validator.validate(supplier);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("isActive per defecte hauria de ser true")
    void isActive_ShouldDefaultToTrue() {
        // Given
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdor Test SL")
                .build();

        // Then
        assertThat(supplier.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Builder hauria de funcionar correctament")
    void builder_ShouldWorkCorrectly() {
        // Given & When
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("test-uuid")
                .name("Test Company")
                .contactName("Test Contact")
                .email("test@example.com")
                .phone("123456789")
                .address("Test Address")
                .notes("Test Notes")
                .isActive(false)
                .build();

        // Then
        assertThat(supplier.getCompanyUuid()).isEqualTo("test-uuid");
        assertThat(supplier.getName()).isEqualTo("Test Company");
        assertThat(supplier.getContactName()).isEqualTo("Test Contact");
        assertThat(supplier.getEmail()).isEqualTo("test@example.com");
        assertThat(supplier.getPhone()).isEqualTo("123456789");
        assertThat(supplier.getAddress()).isEqualTo("Test Address");
        assertThat(supplier.getNotes()).isEqualTo("Test Notes");
        assertThat(supplier.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("email buit hauria de ser vàlid")
    void emptyEmail_ShouldBeValid() {
        // Given
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdor Test SL")
                .email("")
                .build();

        // When
        Set<ConstraintViolation<SupplierRequestDTO>> violations = validator.validate(supplier);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("múltiples errors de validació haurien de ser detectats")
    void multipleValidationErrors_ShouldBeDetected() {
        // Given
        SupplierRequestDTO supplier = SupplierRequestDTO.builder()
                .companyUuid("") // Error: buit
                .name("") // Error: buit
                .email("invalid-email") // Error: format invàlid
                .phone("1".repeat(51)) // Error: massa llarg
                .build();

        // When
        Set<ConstraintViolation<SupplierRequestDTO>> violations = validator.validate(supplier);

        // Then
        assertThat(violations).hasSize(4);
    }
}