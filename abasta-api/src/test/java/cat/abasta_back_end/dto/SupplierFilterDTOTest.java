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
 * Tests unitaris per SupplierFilterDTO.
 * Verifica la validació i funcionalitat del DTO de filtratge de proveïdors.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DisplayName("SupplierFilterDTO Tests")
class SupplierFilterDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("hauria de crear DTO vàlid amb tots els camps")
    void shouldCreateValidDTO_WithAllFields() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Catalunya")
                .email("test@provcat.com")
                .isActive(true)
                .page(1)
                .size(20)
                .sortBy("email")
                .sortDir("desc")
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getCompanyUuid()).isEqualTo("company-uuid-123");
        assertThat(dto.getName()).isEqualTo("Catalunya");
        assertThat(dto.getEmail()).isEqualTo("test@provcat.com");
        assertThat(dto.getIsActive()).isTrue();
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getSize()).isEqualTo(20);
        assertThat(dto.getSortBy()).isEqualTo("email");
        assertThat(dto.getSortDir()).isEqualTo("desc");
    }

    @Test
    @DisplayName("hauria de crear DTO vàlid només amb camps obligatoris")
    void shouldCreateValidDTO_WithOnlyRequiredFields() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getCompanyUuid()).isEqualTo("company-uuid-123");
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getIsActive()).isNull();
        // Verificar valors per defecte
        assertThat(dto.getPage()).isEqualTo(0);
        assertThat(dto.getSize()).isEqualTo(10);
        assertThat(dto.getSortBy()).isEqualTo("name");
        assertThat(dto.getSortDir()).isEqualTo("asc");
    }

    @Test
    @DisplayName("hauria de fallar validació quan companyUuid és null")
    void shouldFailValidation_WhenCompanyUuidIsNull() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid(null)
                .name("Test")
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierFilterDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("companyUuid");
        assertThat(violation.getMessage()).isEqualTo("L'UUID de l'empresa és obligatori");
    }

    @Test
    @DisplayName("hauria de fallar validació quan companyUuid és buit")
    void shouldFailValidation_WhenCompanyUuidIsEmpty() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("")
                .name("Test")
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierFilterDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("companyUuid");
        assertThat(violation.getMessage()).isEqualTo("L'UUID de l'empresa és obligatori");
    }

    @Test
    @DisplayName("hauria de fallar validació quan companyUuid és blanc")
    void shouldFailValidation_WhenCompanyUuidIsBlank() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("   ")
                .name("Test")
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierFilterDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("companyUuid");
        assertThat(violation.getMessage()).isEqualTo("L'UUID de l'empresa és obligatori");
    }

    @Test
    @DisplayName("hauria de fallar validació quan email té format invàlid")
    void shouldFailValidation_WhenEmailFormatIsInvalid() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .email("invalid-email")
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierFilterDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
        assertThat(violation.getMessage()).isEqualTo("L'email ha de tenir un format vàlid");
    }

    @Test
    @DisplayName("hauria d'acceptar email null")
    void shouldAcceptNullEmail() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .email(null)
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    @DisplayName("hauria de fallar validació quan page és negatiu")
    void shouldFailValidation_WhenPageIsNegative() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .page(-1)
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierFilterDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("page");
        assertThat(violation.getMessage()).isEqualTo("La pàgina ha de ser >= 0");
    }

    @Test
    @DisplayName("hauria d'acceptar page = 0")
    void shouldAcceptPageZero() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .page(0)
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getPage()).isEqualTo(0);
    }

    @Test
    @DisplayName("hauria de fallar validació quan size és 0")
    void shouldFailValidation_WhenSizeIsZero() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .size(0)
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierFilterDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("size");
        assertThat(violation.getMessage()).isEqualTo("La mida ha de ser >= 1");
    }

    @Test
    @DisplayName("hauria de fallar validació quan size és negatiu")
    void shouldFailValidation_WhenSizeIsNegative() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .size(-5)
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierFilterDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("size");
        assertThat(violation.getMessage()).isEqualTo("La mida ha de ser >= 1");
    }

    @Test
    @DisplayName("hauria d'acceptar size = 1")
    void shouldAcceptSizeOne() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .size(1)
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("hauria de fallar validació amb múltiples errors")
    void shouldFailValidation_WithMultipleErrors() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("") // Error 1: buit
                .email("invalid") // Error 2: format invàlid
                .page(-1) // Error 3: negatiu
                .size(0) // Error 4: zero
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(4);
    }

    @Test
    @DisplayName("hauria de gestionar correctament isActive true")
    void shouldHandleIsActiveTrue() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .isActive(true)
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("hauria de gestionar correctament isActive false")
    void shouldHandleIsActiveFalse() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .isActive(false)
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("hauria de gestionar correctament isActive null")
    void shouldHandleIsActiveNull() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .isActive(null)
                .build();

        // When
        Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getIsActive()).isNull();
    }

    @Test
    @DisplayName("constructor sense paràmetres hauria de crear objecte amb valors per defecte")
    void noArgsConstructor_ShouldCreateObjectWithDefaultValues() {
        // When
        SupplierFilterDTO dto = new SupplierFilterDTO();

        // Then
        assertThat(dto.getCompanyUuid()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getIsActive()).isNull();
        assertThat(dto.getPage()).isEqualTo(0);
        assertThat(dto.getSize()).isEqualTo(10);
        assertThat(dto.getSortBy()).isEqualTo("name");
        assertThat(dto.getSortDir()).isEqualTo("asc");
    }

    @Test
    @DisplayName("constructor amb tots els paràmetres hauria de funcionar correctament")
    void allArgsConstructor_ShouldWorkCorrectly() {
        // When
        SupplierFilterDTO dto = new SupplierFilterDTO(
                "company-uuid-123",
                "Catalunya",
                "test@provcat.com",
                true,
                2,
                25,
                "email",
                "desc"
        );

        // Then
        assertThat(dto.getCompanyUuid()).isEqualTo("company-uuid-123");
        assertThat(dto.getName()).isEqualTo("Catalunya");
        assertThat(dto.getEmail()).isEqualTo("test@provcat.com");
        assertThat(dto.getIsActive()).isTrue();
        assertThat(dto.getPage()).isEqualTo(2);
        assertThat(dto.getSize()).isEqualTo(25);
        assertThat(dto.getSortBy()).isEqualTo("email");
        assertThat(dto.getSortDir()).isEqualTo("desc");
    }

    @Test
    @DisplayName("hauria d'acceptar emails vàlids amb diferents formats")
    void shouldAcceptValidEmailFormats() {
        // Given
        String[] validEmails = {
                "test@example.com",
                "user.name@domain.co.uk",
                "user+tag@example.org",
                "123@numbers.com",
                "a@b.co"
        };

        for (String email : validEmails) {
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .companyUuid("company-uuid-123")
                    .email(email)
                    .build();

            // When
            Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Test
    @DisplayName("hauria de rebutjar emails amb format invàlid")
    void shouldRejectInvalidEmailFormats() {
        // Given
        String[] invalidEmails = {
                "invalid",
                "@domain.com",
                "user@",
                "user..name@domain.com",
                "user name@domain.com"
        };

        for (String email : invalidEmails) {
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .companyUuid("company-uuid-123")
                    .email(email)
                    .build();

            // When
            Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
        }
    }

    @Test
    @DisplayName("equals i hashCode haurien de funcionar correctament")
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // Given
        SupplierFilterDTO dto1 = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Catalunya")
                .email("test@provcat.com")
                .isActive(true)
                .build();

        SupplierFilterDTO dto2 = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Catalunya")
                .email("test@provcat.com")
                .isActive(true)
                .build();

        SupplierFilterDTO dto3 = SupplierFilterDTO.builder()
                .companyUuid("different-uuid")
                .name("Catalunya")
                .email("test@provcat.com")
                .isActive(true)
                .build();

        // Then
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
    }

    @Test
    @DisplayName("toString hauria de contenir tots els camps")
    void toString_ShouldContainAllFields() {
        // Given
        SupplierFilterDTO dto = SupplierFilterDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Catalunya")
                .email("test@provcat.com")
                .isActive(true)
                .page(1)
                .size(20)
                .sortBy("email")
                .sortDir("desc")
                .build();

        // When
        String toString = dto.toString();

        // Then
        assertThat(toString).contains("company-uuid-123");
        assertThat(toString).contains("Catalunya");
        assertThat(toString).contains("test@provcat.com");
        assertThat(toString).contains("true");
        assertThat(toString).contains("1");
        assertThat(toString).contains("20");
        assertThat(toString).contains("email");
        assertThat(toString).contains("desc");
    }
}