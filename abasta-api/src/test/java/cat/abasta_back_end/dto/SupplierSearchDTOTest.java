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
 * Tests unitaris per SupplierSearchDTO.
 * Verifica la validació i funcionalitat del DTO de cerca simple de proveïdors.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DisplayName("SupplierSearchDTO Tests")
class SupplierSearchDTOTest {

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
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Catalunya")
                .page(2)
                .size(25)
                .sortBy("email")
                .sortDir("desc")
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getCompanyUuid()).isEqualTo("company-uuid-123");
        assertThat(dto.getName()).isEqualTo("Catalunya");
        assertThat(dto.getPage()).isEqualTo(2);
        assertThat(dto.getSize()).isEqualTo(25);
        assertThat(dto.getSortBy()).isEqualTo("email");
        assertThat(dto.getSortDir()).isEqualTo("desc");
    }

    @Test
    @DisplayName("hauria de crear DTO vàlid amb camps obligatoris i valors per defecte")
    void shouldCreateValidDTO_WithRequiredFieldsAndDefaults() {
        // Given
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdors")
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getCompanyUuid()).isEqualTo("company-uuid-123");
        assertThat(dto.getName()).isEqualTo("Proveïdors");
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
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid(null)
                .name("Test")
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierSearchDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("companyUuid");
        assertThat(violation.getMessage()).isEqualTo("L'UUID de l'empresa és obligatori");
    }

    @Test
    @DisplayName("hauria de fallar validació quan companyUuid és buit")
    void shouldFailValidation_WhenCompanyUuidIsEmpty() {
        // Given
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("")
                .name("Test")
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierSearchDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("companyUuid");
        assertThat(violation.getMessage()).isEqualTo("L'UUID de l'empresa és obligatori");
    }

    @Test
    @DisplayName("hauria de fallar validació quan companyUuid és blanc")
    void shouldFailValidation_WhenCompanyUuidIsBlank() {
        // Given
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("   ")
                .name("Test")
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierSearchDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("companyUuid");
        assertThat(violation.getMessage()).isEqualTo("L'UUID de l'empresa és obligatori");
    }

    @Test
    @DisplayName("hauria de fallar validació quan page és negatiu")
    void shouldFailValidation_WhenPageIsNegative() {
        // Given
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Test")
                .page(-1)
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierSearchDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("page");
        assertThat(violation.getMessage()).isEqualTo("El número de pàgina ha de ser 0 o superior");
    }

    @Test
    @DisplayName("hauria d'acceptar page = 0")
    void shouldAcceptPageZero() {
        // Given
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Test")
                .page(0)
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getPage()).isEqualTo(0);
    }

    @Test
    @DisplayName("hauria de fallar validació quan size és 0")
    void shouldFailValidation_WhenSizeIsZero() {
        // Given
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Test")
                .size(0)
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierSearchDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("size");
        assertThat(violation.getMessage()).isEqualTo("La mida de pàgina ha de ser 1 o superior");
    }

    @Test
    @DisplayName("hauria de fallar validació quan size és negatiu")
    void shouldFailValidation_WhenSizeIsNegative() {
        // Given
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Test")
                .size(-5)
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<SupplierSearchDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("size");
        assertThat(violation.getMessage()).isEqualTo("La mida de pàgina ha de ser 1 o superior");
    }

    @Test
    @DisplayName("hauria d'acceptar size = 1")
    void shouldAcceptSizeOne() {
        // Given
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Test")
                .size(1)
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("hauria de fallar validació amb múltiples errors")
    void shouldFailValidation_WithMultipleErrors() {
        // Given
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("") // Error 1: buit
                .name("   ")
                .page(-1) // Error 3: negatiu
                .size(0) // Error 4: zero
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(3);
    }

    @Test
    @DisplayName("constructor sense paràmetres hauria de crear objecte amb valors per defecte")
    void noArgsConstructor_ShouldCreateObjectWithDefaultValues() {
        // When
        SupplierSearchDTO dto = new SupplierSearchDTO();

        // Then
        assertThat(dto.getCompanyUuid()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getPage()).isEqualTo(0);
        assertThat(dto.getSize()).isEqualTo(10);
        assertThat(dto.getSortBy()).isEqualTo("name");
        assertThat(dto.getSortDir()).isEqualTo("asc");
    }

    @Test
    @DisplayName("constructor amb tots els paràmetres hauria de funcionar correctament")
    void allArgsConstructor_ShouldWorkCorrectly() {
        // When
        SupplierSearchDTO dto = new SupplierSearchDTO(
                "company-uuid-123",
                "Catalunya",
                3,
                30,
                "email",
                "desc"
        );

        // Then
        assertThat(dto.getCompanyUuid()).isEqualTo("company-uuid-123");
        assertThat(dto.getName()).isEqualTo("Catalunya");
        assertThat(dto.getPage()).isEqualTo(3);
        assertThat(dto.getSize()).isEqualTo(30);
        assertThat(dto.getSortBy()).isEqualTo("email");
        assertThat(dto.getSortDir()).isEqualTo("desc");
    }

    @Test
    @DisplayName("hauria d'acceptar noms amb diferents formats")
    void shouldAcceptNamesWithDifferentFormats() {
        // Given
        String[] validNames = {
                "Proveïdors",
                "Proveïdors Catalunya SL",
                "PROVEÏDOR EN MAJÚSCULES",
                "proveïdor en minúscules",
                "Proveïdor123",
                "Proveïdor & Associats",
                "Proveïdor S.A.",
                "A"
        };

        for (String name : validNames) {
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .companyUuid("company-uuid-123")
                    .name(name)
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
            assertThat(dto.getName()).isEqualTo(name);
        }
    }

    @Test
    @DisplayName("hauria d'acceptar diferents valors de sortBy")
    void shouldAcceptDifferentSortByValues() {
        // Given
        String[] sortByValues = {
                "name",
                "email",
                "createdAt",
                "updatedAt",
                "contactName",
                "isActive"
        };

        for (String sortBy : sortByValues) {
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .companyUuid("company-uuid-123")
                    .name("Test")
                    .sortBy(sortBy)
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
            assertThat(dto.getSortBy()).isEqualTo(sortBy);
        }
    }

    @Test
    @DisplayName("hauria d'acceptar valors grans de page i size")
    void shouldAcceptLargePageAndSizeValues() {
        // Given
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Test")
                .page(999)
                .size(1000)
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getPage()).isEqualTo(999);
        assertThat(dto.getSize()).isEqualTo(1000);
    }

    @Test
    @DisplayName("equals i hashCode haurien de funcionar correctament")
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // Given
        SupplierSearchDTO dto1 = SupplierSearchDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Catalunya")
                .page(1)
                .size(20)
                .sortBy("email")
                .sortDir("desc")
                .build();

        SupplierSearchDTO dto2 = SupplierSearchDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Catalunya")
                .page(1)
                .size(20)
                .sortBy("email")
                .sortDir("desc")
                .build();

        SupplierSearchDTO dto3 = SupplierSearchDTO.builder()
                .companyUuid("different-uuid")
                .name("Catalunya")
                .page(1)
                .size(20)
                .sortBy("email")
                .sortDir("desc")
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
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Catalunya")
                .page(2)
                .size(25)
                .sortBy("email")
                .sortDir("desc")
                .build();

        // When
        String toString = dto.toString();

        // Then
        assertThat(toString).contains("company-uuid-123");
        assertThat(toString).contains("Catalunya");
        assertThat(toString).contains("2");
        assertThat(toString).contains("25");
        assertThat(toString).contains("email");
        assertThat(toString).contains("desc");
    }

    @Test
    @DisplayName("hauria de gestionar correctament espacis en els noms")
    void shouldHandleSpacesInNamesCorrectly() {
        // Given - Nom amb espais però no només espais
        SupplierSearchDTO dto = SupplierSearchDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdors Catalans SL")
                .build();

        // When
        Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getName()).isEqualTo("Proveïdors Catalans SL");
    }

    @Test
    @DisplayName("hauria de gestionar correctament UUID amb diferents formats")
    void shouldHandleDifferentUuidFormats() {
        // Given
        String[] validUuids = {
                "company-uuid-123",
                "123e4567-e89b-12d3-a456-426614174000",
                "simple-uuid",
                "COMPANY-UUID-UPPERCASE",
                "uuid_with_underscores",
                "12345"
        };

        for (String uuid : validUuids) {
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .companyUuid(uuid)
                    .name("Test")
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
            assertThat(dto.getCompanyUuid()).isEqualTo(uuid);
        }
    }
}