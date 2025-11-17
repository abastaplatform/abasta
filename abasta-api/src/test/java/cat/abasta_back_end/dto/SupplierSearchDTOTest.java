package cat.abasta_back_end.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaris CORREGITS per SupplierSearchDTO.
 * Verificació de funcionalitat de cerca de proveïdors amb paginació i ordenació.
 *
 * @author Enrique Pérez
 * @version 2.0 - Corrected
 */
@DisplayName("SupplierSearchDTO Tests Corrected")
class SupplierSearchDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Tests de construcció i valors per defecte")
    class ConstructorAndDefaultTests {

        @Test
        @DisplayName("constructor sense paràmetres - comportament real amb @Builder.Default")
        void noArgsConstructor_ShouldCreateObjectWithBuilderDefaults() {
            // Given & When
            SupplierSearchDTO dto = new SupplierSearchDTO();

            // Then - searchText és null
            assertThat(dto.getSearchText()).isNull();

            // Els camps amb @Builder.Default s'apliquen fins i tot amb constructor directe
            assertThat(dto.getPage()).isEqualTo(0);          // @Builder.Default
            assertThat(dto.getSize()).isEqualTo(10);         // @Builder.Default ← CORRECCIÓ!
            assertThat(dto.getSortBy()).isEqualTo("name");   // @Builder.Default ← CORRECCIÓ!
            assertThat(dto.getSortDir()).isEqualTo("asc");   // @Builder.Default ← CORRECCIÓ!
        }

        @Test
        @DisplayName("builder amb valors per defecte")
        void builder_ShouldApplyDefaults() {
            // Given & When
            SupplierSearchDTO dto = SupplierSearchDTO.builder().build();

            // Then
            assertThat(dto.getSearchText()).isNull();
            assertThat(dto.getPage()).isEqualTo(0);
            assertThat(dto.getSize()).isEqualTo(10);
            assertThat(dto.getSortBy()).isEqualTo("name");
            assertThat(dto.getSortDir()).isEqualTo("asc");
        }

        @Test
        @DisplayName("constructor amb tots els paràmetres")
        void allArgsConstructor_ShouldWork() {
            // Given & When
            SupplierSearchDTO dto = new SupplierSearchDTO(
                    "Barcelona",  // searchText
                    1,            // page
                    20,           // size
                    "email",      // sortBy
                    "desc"        // sortDir
            );

            // Then
            assertThat(dto.getSearchText()).isEqualTo("Barcelona");
            assertThat(dto.getPage()).isEqualTo(1);
            assertThat(dto.getSize()).isEqualTo(20);
            assertThat(dto.getSortBy()).isEqualTo("email");
            assertThat(dto.getSortDir()).isEqualTo("desc");
        }
    }

    @Nested
    @DisplayName("Tests del Builder")
    class BuilderTests {

        @Test
        @DisplayName("builder amb tots els camps")
        void builder_ShouldWorkWithAllFields() {
            // Given & When
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .searchText("Catalunya")
                    .page(2)
                    .size(25)
                    .sortBy("contactName")
                    .sortDir("desc")
                    .build();

            // Then
            assertThat(dto.getSearchText()).isEqualTo("Catalunya");
            assertThat(dto.getPage()).isEqualTo(2);
            assertThat(dto.getSize()).isEqualTo(25);
            assertThat(dto.getSortBy()).isEqualTo("contactName");
            assertThat(dto.getSortDir()).isEqualTo("desc");
        }

        @Test
        @DisplayName("builder amb camps mínims mantenint defaults")
        void builder_ShouldKeepDefaultValues_WhenPartiallyBuilt() {
            // Given & When
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .searchText("Test")
                    // No especifiquem els altres camps
                    .build();

            // Then
            assertThat(dto.getSearchText()).isEqualTo("Test");

            // Valors per defecte mantinguts
            assertThat(dto.getPage()).isEqualTo(0);
            assertThat(dto.getSize()).isEqualTo(10);
            assertThat(dto.getSortBy()).isEqualTo("name");
            assertThat(dto.getSortDir()).isEqualTo("asc");
        }

        @Test
        @DisplayName("builder buit hauria d'usar tots els valors per defecte")
        void builder_ShouldUseAllDefaults_WhenEmpty() {
            // Given & When
            SupplierSearchDTO dto = SupplierSearchDTO.builder().build();

            // Then - Tots els valors per defecte aplicats
            assertThat(dto.getSearchText()).isNull();      // Sense @Builder.Default
            assertThat(dto.getPage()).isEqualTo(0);        // @Builder.Default
            assertThat(dto.getSize()).isEqualTo(10);       // @Builder.Default
            assertThat(dto.getSortBy()).isEqualTo("name"); // @Builder.Default
            assertThat(dto.getSortDir()).isEqualTo("asc"); // @Builder.Default
        }
    }

    @Nested
    @DisplayName("Tests de validació")
    class ValidationTests {

        @Test
        @DisplayName("valors per defecte haurien de passar validació")
        void defaultValues_ShouldPassValidation() {
            // Given
            SupplierSearchDTO dto = SupplierSearchDTO.builder().build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("page negatiu hauria de fallar validació")
        void negativePage_ShouldFailValidation() {
            // Given
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .page(-1)
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("page");
            assertThat(violations.iterator().next().getMessage()).contains("El número de pàgina ha de ser 0 o superior");
        }

        @Test
        @DisplayName("size zero hauria de fallar validació")
        void zeroSize_ShouldFailValidation() {
            // Given
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .size(0)
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("size");
            assertThat(violations.iterator().next().getMessage()).contains("La mida de pàgina ha de ser 1 o superior");
        }

        @Test
        @DisplayName("size negatiu hauria de fallar validació")
        void negativeSize_ShouldFailValidation() {
            // Given
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .size(-5)
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("size");
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalid", "ASC", "DESC", "ascending", "descending", ""})
        @DisplayName("sortDir invàlid hauria de fallar validació")
        void invalidSortDir_ShouldFailValidation(String invalidSortDir) {
            // Given
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .sortDir(invalidSortDir)
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("sortDir");
            assertThat(violations.iterator().next().getMessage()).contains("La direcció d'ordenació ha de ser 'asc' o 'desc'");
        }

        @ParameterizedTest
        @ValueSource(strings = {"asc", "desc"})
        @DisplayName("sortDir vàlid hauria de passar validació")
        void validSortDir_ShouldPassValidation(String validSortDir) {
            // Given
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .sortDir(validSortDir)
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Tests de funcionalitat específica")
    class FunctionalityTests {

        @Test
        @DisplayName("searchText null hauria de ser vàlid per mostrar tots els proveïdors")
        void nullSearchText_ShouldBeValid() {
            // Given
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .searchText(null)
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
            assertThat(dto.getSearchText()).isNull();
        }

        @Test
        @DisplayName("searchText buit hauria de ser vàlid")
        void emptySearchText_ShouldBeValid() {
            // Given
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .searchText("")
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
            assertThat(dto.getSearchText()).isEmpty();
        }

        @Test
        @DisplayName("searchText amb espais hauria de ser vàlid")
        void searchTextWithSpaces_ShouldBeValid() {
            // Given
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .searchText("  Barcelona  ")
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
            assertThat(dto.getSearchText()).isEqualTo("  Barcelona  ");
        }

        @Test
        @DisplayName("valors de paginació alts haurien de ser vàlids")
        void highPaginationValues_ShouldBeValid() {
            // Given
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .page(999)
                    .size(1000)
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("sortBy personalitzat hauria de ser vàlid")
        void customSortBy_ShouldBeValid() {
            // Given
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .sortBy("createdAt")
                    .build();

            // When
            Set<ConstraintViolation<SupplierSearchDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
            assertThat(dto.getSortBy()).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("Tests de casos d'ús reals")
    class RealWorldUseCaseTests {

        @Test
        @DisplayName("cerca per nom d'empresa")
        void searchByCompanyName() {
            // Given & When
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .searchText("Catalunya")
                    .page(0)
                    .size(20)
                    .sortBy("name")
                    .sortDir("asc")
                    .build();

            // Then
            assertThat(dto.getSearchText()).isEqualTo("Catalunya");
            assertThat(dto.getPage()).isEqualTo(0);
            assertThat(dto.getSize()).isEqualTo(20);
            assertThat(dto.getSortBy()).isEqualTo("name");
            assertThat(dto.getSortDir()).isEqualTo("asc");
        }

        @Test
        @DisplayName("cerca per email parcial")
        void searchByPartialEmail() {
            // Given & When
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .searchText("@gmail.com")
                    .sortBy("email")
                    .build();

            // Then
            assertThat(dto.getSearchText()).isEqualTo("@gmail.com");
            assertThat(dto.getSortBy()).isEqualTo("email");
            // Valors per defecte mantinguts
            assertThat(dto.getPage()).isEqualTo(0);
            assertThat(dto.getSize()).isEqualTo(10);
            assertThat(dto.getSortDir()).isEqualTo("asc");
        }

        @Test
        @DisplayName("cerca per telèfon")
        void searchByPhone() {
            // Given & When
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .searchText("93")
                    .sortBy("phone")
                    .sortDir("desc")
                    .build();

            // Then
            assertThat(dto.getSearchText()).isEqualTo("93");
            assertThat(dto.getSortBy()).isEqualTo("phone");
            assertThat(dto.getSortDir()).isEqualTo("desc");
        }

        @Test
        @DisplayName("llistar tots els proveïdors amb paginació")
        void listAllSuppliersWithPagination() {
            // Given & When
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .searchText(null) // Sense filtre de text
                    .page(1)
                    .size(50)
                    .sortBy("createdAt")
                    .sortDir("desc")
                    .build();

            // Then
            assertThat(dto.getSearchText()).isNull();
            assertThat(dto.getPage()).isEqualTo(1);
            assertThat(dto.getSize()).isEqualTo(50);
            assertThat(dto.getSortBy()).isEqualTo("createdAt");
            assertThat(dto.getSortDir()).isEqualTo("desc");
        }
    }

    @Nested
    @DisplayName("Tests de getters i setters")
    class GettersSettersTests {

        @Test
        @DisplayName("getters i setters haurien de funcionar correctament")
        void gettersAndSetters_ShouldWork() {
            // Given
            SupplierSearchDTO dto = new SupplierSearchDTO();

            // When & Then
            dto.setSearchText("Test Search");
            assertThat(dto.getSearchText()).isEqualTo("Test Search");

            dto.setPage(5);
            assertThat(dto.getPage()).isEqualTo(5);

            dto.setSize(100);
            assertThat(dto.getSize()).isEqualTo(100);

            dto.setSortBy("updatedAt");
            assertThat(dto.getSortBy()).isEqualTo("updatedAt");

            dto.setSortDir("desc");
            assertThat(dto.getSortDir()).isEqualTo("desc");
        }
    }

    @Nested
    @DisplayName("Tests de mètodes Lombok")
    class LombokGeneratedMethodsTests {

        @Test
        @DisplayName("equals hauria de funcionar correctament")
        void equals_ShouldWork() {
            // Given
            SupplierSearchDTO dto1 = SupplierSearchDTO.builder()
                    .searchText("Test")
                    .page(1)
                    .size(20)
                    .build();

            SupplierSearchDTO dto2 = SupplierSearchDTO.builder()
                    .searchText("Test")
                    .page(1)
                    .size(20)
                    .build();

            SupplierSearchDTO dto3 = SupplierSearchDTO.builder()
                    .searchText("Different")
                    .page(1)
                    .size(20)
                    .build();

            // When & Then
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1).isNotEqualTo(dto3);
            assertThat(dto1).isNotEqualTo(null);
        }

        @Test
        @DisplayName("hashCode hauria de ser consistent amb equals")
        void hashCode_ShouldBeConsistentWithEquals() {
            // Given
            SupplierSearchDTO dto1 = SupplierSearchDTO.builder()
                    .searchText("Test")
                    .page(0)
                    .size(10)
                    .build();

            SupplierSearchDTO dto2 = SupplierSearchDTO.builder()
                    .searchText("Test")
                    .page(0)
                    .size(10)
                    .build();

            // When & Then
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        @Test
        @DisplayName("toString hauria de contenir informació dels camps")
        void toString_ShouldContainFieldInformation() {
            // Given
            SupplierSearchDTO dto = SupplierSearchDTO.builder()
                    .searchText("Barcelona")
                    .page(1)
                    .size(20)
                    .build();

            // When
            String result = dto.toString();

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result).contains("SupplierSearchDTO");
        }
    }
}