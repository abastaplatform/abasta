package cat.abasta_back_end.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaris ACTUALITZATS per SupplierFilterDTO.
 * Verificació de la funcionalitat de filtratge de proveïdors amb Builder i validacions.
  * NOTA: Actualitzat per reflectir l'eliminació del camp isActive del DTO.
 *
 * @author Enrique Pérez
 * @version 3.0 - Updated (sense isActive)
 */
@DisplayName("SupplierFilterDTO Tests Updated")
class SupplierFilterDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Nested
    @DisplayName("Tests de construcció i valors per defecte")
    class ConstructorAndDefaultTests {

        @Test
        @DisplayName("constructor sense paràmetres amb valors per defecte aplicats")
        void noArgsConstructor_ShouldCreateObjectWithBuilderDefaults() {
            // Given & When
            SupplierFilterDTO dto = new SupplierFilterDTO();

            // Then - Camps de filtre de text inicialitzats a null
            assertThat(dto.getName()).isNull();
            assertThat(dto.getContactName()).isNull();
            assertThat(dto.getEmail()).isNull();
            assertThat(dto.getPhone()).isNull();
            assertThat(dto.getAddress()).isNull();

            // Camps amb @Builder.Default aplicats
            assertThat(dto.getPage()).isEqualTo(0);
            assertThat(dto.getSize()).isEqualTo(10);         // @Builder.Default
            assertThat(dto.getSortBy()).isEqualTo("name");   // @Builder.Default
            assertThat(dto.getSortDir()).isEqualTo("asc");   // @Builder.Default
        }

        @Test
        @DisplayName("builder amb valors per defecte aplicats correctament")
        void builder_ShouldApplyBuilderDefaults() {
            // Given & When
            SupplierFilterDTO dto = SupplierFilterDTO.builder().build();

            // Then - Camps de filtre sense valor per defecte
            assertThat(dto.getName()).isNull();
            assertThat(dto.getContactName()).isNull();
            assertThat(dto.getEmail()).isNull();
            assertThat(dto.getPhone()).isNull();
            assertThat(dto.getAddress()).isNull();

            // Camps amb @Builder.Default aplicats correctament
            assertThat(dto.getPage()).isEqualTo(0);
            assertThat(dto.getSize()).isEqualTo(10);
            assertThat(dto.getSortBy()).isEqualTo("name");
            assertThat(dto.getSortDir()).isEqualTo("asc");
        }

        @Test
        @DisplayName("constructor amb tots els paràmetres")
        void allArgsConstructor_ShouldWork() {
            // Given & When
            SupplierFilterDTO dto = new SupplierFilterDTO(
                    "Catalunya",    // name
                    "Joan",         // contactName
                    "@provcat.com", // email
                    "93",           // phone
                    "Barcelona",    // address
                    1,              // page
                    20,             // size
                    "contactName",  // sortBy
                    "desc"          // sortDir
            );

            // Then
            assertThat(dto.getName()).isEqualTo("Catalunya");
            assertThat(dto.getContactName()).isEqualTo("Joan");
            assertThat(dto.getEmail()).isEqualTo("@provcat.com");
            assertThat(dto.getPhone()).isEqualTo("93");
            assertThat(dto.getAddress()).isEqualTo("Barcelona");
            assertThat(dto.getPage()).isEqualTo(1);
            assertThat(dto.getSize()).isEqualTo(20);
            assertThat(dto.getSortBy()).isEqualTo("contactName");
            assertThat(dto.getSortDir()).isEqualTo("desc");
        }
    }

    @Nested
    @DisplayName("Tests del Builder")
    class BuilderTests {

        @Test
        @DisplayName("builder amb tots els camps especificats")
        void builder_ShouldWorkWithAllFields() {
            // Given & When
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .name("Catalunya")
                    .contactName("Joan")
                    .email("@provcat.com")
                    .phone("93")
                    .address("Barcelona")
                    .page(2)
                    .size(25)
                    .sortBy("email")
                    .sortDir("desc")
                    .build();

            // Then
            assertThat(dto.getName()).isEqualTo("Catalunya");
            assertThat(dto.getContactName()).isEqualTo("Joan");
            assertThat(dto.getEmail()).isEqualTo("@provcat.com");
            assertThat(dto.getPhone()).isEqualTo("93");
            assertThat(dto.getAddress()).isEqualTo("Barcelona");
            assertThat(dto.getPage()).isEqualTo(2);
            assertThat(dto.getSize()).isEqualTo(25);
            assertThat(dto.getSortBy()).isEqualTo("email");
            assertThat(dto.getSortDir()).isEqualTo("desc");
        }

        @Test
        @DisplayName("builder parcial mantinint valors per defecte")
        void builder_ShouldKeepDefaultValues_WhenPartiallyBuilt() {
            // Given & When
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .name("Test")
                    // No especifiquem altres camps
                    .build();

            // Then
            assertThat(dto.getName()).isEqualTo("Test");

            // Valors per defecte del @Builder.Default mantinguts
            assertThat(dto.getPage()).isEqualTo(0);
            assertThat(dto.getSize()).isEqualTo(10);
            assertThat(dto.getSortBy()).isEqualTo("name");
            assertThat(dto.getSortDir()).isEqualTo("asc");

            // Camps no especificats
            assertThat(dto.getContactName()).isNull();
            assertThat(dto.getEmail()).isNull();
            assertThat(dto.getPhone()).isNull();
            assertThat(dto.getAddress()).isNull();
        }
    }

    @Nested
    @DisplayName("Tests del mètode hasTextFilters")
    class HasTextFiltersTests {

        @Test
        @DisplayName("hasTextFilters retorna false quan tots els filtres són null")
        void hasTextFilters_ShouldReturnFalse_WhenAllFiltersAreNull() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder().build();

            // When
            boolean result = dto.hasTextFilters();

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("hasTextFilters retorna false quan tots els filtres són buits")
        void hasTextFilters_ShouldReturnFalse_WhenAllFiltersAreEmpty() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .name("")
                    .contactName("   ")
                    .email("")
                    .phone("  ")
                    .address("")
                    .build();

            // When
            boolean result = dto.hasTextFilters();

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("hasTextFilters retorna true quan name té valor")
        void hasTextFilters_ShouldReturnTrue_WhenNameHasValue() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .name("Catalunya")
                    .build();

            // When
            boolean result = dto.hasTextFilters();

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("hasTextFilters retorna true quan contactName té valor")
        void hasTextFilters_ShouldReturnTrue_WhenContactNameHasValue() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .contactName("Joan")
                    .build();

            // When
            boolean result = dto.hasTextFilters();

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("hasTextFilters retorna true quan email té valor")
        void hasTextFilters_ShouldReturnTrue_WhenEmailHasValue() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .email("@test.com")
                    .build();

            // When
            boolean result = dto.hasTextFilters();

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("hasTextFilters retorna true quan phone té valor")
        void hasTextFilters_ShouldReturnTrue_WhenPhoneHasValue() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .phone("93")
                    .build();

            // When
            boolean result = dto.hasTextFilters();

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("hasTextFilters retorna true quan address té valor")
        void hasTextFilters_ShouldReturnTrue_WhenAddressHasValue() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .address("Barcelona")
                    .build();

            // When
            boolean result = dto.hasTextFilters();

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("hasTextFilters retorna true quan múltiples camps tenen valor")
        void hasTextFilters_ShouldReturnTrue_WhenMultipleFieldsHaveValue() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .name("Catalunya")
                    .contactName("Joan")
                    .email("@test.com")
                    .build();

            // When
            boolean result = dto.hasTextFilters();

            // Then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("Tests de validació Jakarta")
    class ValidationTests {

        @Test
        @DisplayName("validació passa amb valors per defecte del builder")
        void validation_ShouldPass_WithBuilderDefaults() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder().build();

            // When
            Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("validació falla amb page negatiu")
        void validation_ShouldFail_WithNegativePage() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .page(-1)
                    .build();

            // When
            Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("El número de pàgina ha de ser 0 o superior");
        }

        @Test
        @DisplayName("validació falla amb size zero")
        void validation_ShouldFail_WithZeroSize() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .size(0)
                    .build();

            // When
            Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("La mida de pàgina ha de ser 1 o superior");
        }

        @Test
        @DisplayName("validació falla amb size negatiu")
        void validation_ShouldFail_WithNegativeSize() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .size(-5)
                    .build();

            // When
            Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("La mida de pàgina ha de ser 1 o superior");
        }

        @Test
        @DisplayName("validació falla amb sortDir invàlid")
        void validation_ShouldFail_WithInvalidSortDir() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .sortDir("invalid")
                    .build();

            // When
            Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("La direcció d'ordenació ha de ser 'asc' o 'desc'");
        }

        @Test
        @DisplayName("validació passa amb valors vàlids")
        void validation_ShouldPass_WithValidValues() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .name("Catalunya")
                    .contactName("Joan")
                    .email("joan@test.com")
                    .phone("123456789")
                    .address("Barcelona")
                    .page(1)
                    .size(20)
                    .sortBy("name")
                    .sortDir("desc")
                    .build();

            // When
            Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Tests de getters i setters")
    class GettersSettersTests {

        @Test
        @DisplayName("getters i setters funcionen correctament")
        void gettersAndSetters_ShouldWorkCorrectly() {
            // Given
            SupplierFilterDTO dto = new SupplierFilterDTO();

            // When & Then - camps de filtre de text
            dto.setName("Test Name");
            assertThat(dto.getName()).isEqualTo("Test Name");

            dto.setContactName("Test Contact");
            assertThat(dto.getContactName()).isEqualTo("Test Contact");

            dto.setEmail("test@email.com");
            assertThat(dto.getEmail()).isEqualTo("test@email.com");

            dto.setPhone("123456789");
            assertThat(dto.getPhone()).isEqualTo("123456789");

            dto.setAddress("Test Address");
            assertThat(dto.getAddress()).isEqualTo("Test Address");

            // Camps de paginació i ordenació
            dto.setPage(5);
            assertThat(dto.getPage()).isEqualTo(5);

            dto.setSize(50);
            assertThat(dto.getSize()).isEqualTo(50);

            dto.setSortBy("email");
            assertThat(dto.getSortBy()).isEqualTo("email");

            dto.setSortDir("desc");
            assertThat(dto.getSortDir()).isEqualTo("desc");
        }
    }

    @Nested
    @DisplayName("Tests de equals, hashCode i toString")
    class LombokGeneratedMethodsTests {

        @Test
        @DisplayName("equals funciona correctament")
        void equals_ShouldWorkCorrectly() {
            // Given
            SupplierFilterDTO dto1 = SupplierFilterDTO.builder()
                    .name("Catalunya")
                    .contactName("Joan")
                    .page(1)
                    .size(10)
                    .build();

            SupplierFilterDTO dto2 = SupplierFilterDTO.builder()
                    .name("Catalunya")
                    .contactName("Joan")
                    .page(1)
                    .size(10)
                    .build();

            SupplierFilterDTO dto3 = SupplierFilterDTO.builder()
                    .name("Diferentes")
                    .contactName("Joan")
                    .page(1)
                    .size(10)
                    .build();

            // When & Then
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1).isNotEqualTo(dto3);
            assertThat(dto1).isNotEqualTo(null);
        }

        @Test
        @DisplayName("hashCode és consistent amb equals")
        void hashCode_ShouldBeConsistentWithEquals() {
            // Given
            SupplierFilterDTO dto1 = SupplierFilterDTO.builder()
                    .name("Catalunya")
                    .build();

            SupplierFilterDTO dto2 = SupplierFilterDTO.builder()
                    .name("Catalunya")
                    .build();

            // When & Then
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        @Test
        @DisplayName("toString conté informació dels camps")
        void toString_ShouldContainFieldInformation() {
            // Given
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .name("Catalunya")
                    .contactName("Joan")
                    .page(1)
                    .build();

            // When
            String result = dto.toString();

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result).contains("SupplierFilterDTO");
            assertThat(result).contains("Catalunya");
            assertThat(result).contains("Joan");
        }
    }

    @Nested
    @DisplayName("Tests de casos d'ús realistes")
    class RealWorldUseCaseTests {

        @Test
        @DisplayName("filtre només per nom amb valors per defecte")
        void filterByNameOnly_ShouldUseDefaults() {
            // Given & When
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .name("Catalunya")
                    .build();

            // Then
            assertThat(dto.getName()).isEqualTo("Catalunya");
            // Verifica valors per defecte
            assertThat(dto.getPage()).isEqualTo(0);
            assertThat(dto.getSize()).isEqualTo(10);
            assertThat(dto.getSortBy()).isEqualTo("name");
            assertThat(dto.getSortDir()).isEqualTo("asc");
            // Verifica que hi ha filtres de text
            assertThat(dto.hasTextFilters()).isTrue();
        }

        @Test
        @DisplayName("filtre complex amb múltiples criteris")
        void complexFilter_ShouldWorkCorrectly() {
            // Given & When
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .name("Catalunya")
                    .contactName("Joan")
                    .email("@provcat.com")
                    .phone("93")
                    .address("Barcelona")
                    .page(2)
                    .size(25)
                    .sortBy("email")
                    .sortDir("desc")
                    .build();

            // When
            Set<ConstraintViolation<SupplierFilterDTO>> violations = validator.validate(dto);

            // Then - validació passa
            assertThat(violations).isEmpty();
            // Verifica que hi ha filtres de text
            assertThat(dto.hasTextFilters()).isTrue();
            // Verifica tots els camps
            assertThat(dto.getName()).isEqualTo("Catalunya");
            assertThat(dto.getContactName()).isEqualTo("Joan");
            assertThat(dto.getEmail()).isEqualTo("@provcat.com");
            assertThat(dto.getPhone()).isEqualTo("93");
            assertThat(dto.getAddress()).isEqualTo("Barcelona");
        }

        @Test
        @DisplayName("filtre només amb paginació sense text")
        void paginationOnlyFilter_ShouldWork() {
            // Given & When
            SupplierFilterDTO dto = SupplierFilterDTO.builder()
                    .page(3)
                    .size(50)
                    .sortBy("createdAt")
                    .sortDir("desc")
                    .build();

            // Then
            assertThat(dto.hasTextFilters()).isFalse();
            assertThat(dto.getPage()).isEqualTo(3);
            assertThat(dto.getSize()).isEqualTo(50);
            assertThat(dto.getSortBy()).isEqualTo("createdAt");
            assertThat(dto.getSortDir()).isEqualTo("desc");
        }
    }
}