package cat.abasta_back_end.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaris per UserFilterDTO.
 * Verifica la construcció, valors per defecte i mètodes utilitaris de filtres d'usuari.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DisplayName("UserFilterDTO Tests")
class UserFilterDTOTest {

    @Test
    @DisplayName("Hauria de construir DTO amb builder correctament")
    void whenBuildUserFilterDTO_thenFieldsAreSet() {
        // Given & When
        UserFilterDTO dto = UserFilterDTO.builder()
                .email("test@abasta.com")
                .firstName("Joan")
                .lastName("Garcia")
                .phone("600123456")
                .isActive(true)
                .page(1)
                .size(20)
                .sortBy("firstName")
                .sortDir("desc")
                .build();

        // Then
        assertEquals("test@abasta.com", dto.getEmail());
        assertEquals("Joan", dto.getFirstName());
        assertEquals("Garcia", dto.getLastName());
        assertEquals("600123456", dto.getPhone());
        assertTrue(dto.getIsActive());
        assertEquals(1, dto.getPage());
        assertEquals(20, dto.getSize());
        assertEquals("firstName", dto.getSortBy());
        assertEquals("desc", dto.getSortDir());
    }

    @Test
    @DisplayName("Hauria d'aplicar valors per defecte amb constructor sense arguments")
    void whenUseNoArgsConstructor_thenDefaultValuesAreSet() {
        // Given & When
        UserFilterDTO dto = new UserFilterDTO();

        // Then
        assertNull(dto.getEmail());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getPhone());
        assertNull(dto.getIsActive());
        assertEquals(0, dto.getPage());
        assertEquals(10, dto.getSize());
        assertEquals("email", dto.getSortBy());
        assertEquals("asc", dto.getSortDir());
    }

    @Test
    @DisplayName("Hauria d'aplicar valors per defecte amb builder sense valors")
    void whenUseBuilderWithoutValues_thenDefaultValuesAreSet() {
        // Given & When
        UserFilterDTO dto = UserFilterDTO.builder().build();

        // Then
        assertNull(dto.getEmail());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getPhone());
        assertNull(dto.getIsActive());
        assertEquals(0, dto.getPage());
        assertEquals(10, dto.getSize());
        assertEquals("email", dto.getSortBy());
        assertEquals("asc", dto.getSortDir());
    }

    @Test
    @DisplayName("Hauria de funcionar amb setters i getters")
    void whenSetFields_thenGettersReturnCorrectValues() {
        // Given
        UserFilterDTO dto = new UserFilterDTO();

        // When
        dto.setEmail("maria@abasta.com");
        dto.setFirstName("Maria");
        dto.setLastName("López");
        dto.setPhone("600999888");
        dto.setIsActive(false);
        dto.setPage(2);
        dto.setSize(50);
        dto.setSortBy("lastName");
        dto.setSortDir("desc");

        // Then
        assertEquals("maria@abasta.com", dto.getEmail());
        assertEquals("Maria", dto.getFirstName());
        assertEquals("López", dto.getLastName());
        assertEquals("600999888", dto.getPhone());
        assertFalse(dto.getIsActive());
        assertEquals(2, dto.getPage());
        assertEquals(50, dto.getSize());
        assertEquals("lastName", dto.getSortBy());
        assertEquals("desc", dto.getSortDir());
    }

    @Test
    @DisplayName("hasTextFilters hauria de retornar true quan hi ha email")
    void whenEmailIsSet_thenHasTextFiltersReturnsTrue() {
        // Given
        UserFilterDTO dto = UserFilterDTO.builder()
                .email("test@abasta.com")
                .build();

        // When & Then
        assertTrue(dto.hasTextFilters());
    }

    @Test
    @DisplayName("hasTextFilters hauria de retornar true quan hi ha firstName")
    void whenFirstNameIsSet_thenHasTextFiltersReturnsTrue() {
        // Given
        UserFilterDTO dto = UserFilterDTO.builder()
                .firstName("Joan")
                .build();

        // When & Then
        assertTrue(dto.hasTextFilters());
    }

    @Test
    @DisplayName("hasTextFilters hauria de retornar true quan hi ha lastName")
    void whenLastNameIsSet_thenHasTextFiltersReturnsTrue() {
        // Given
        UserFilterDTO dto = UserFilterDTO.builder()
                .lastName("Garcia")
                .build();

        // When & Then
        assertTrue(dto.hasTextFilters());
    }

    @Test
    @DisplayName("hasTextFilters hauria de retornar true quan hi ha phone")
    void whenPhoneIsSet_thenHasTextFiltersReturnsTrue() {
        // Given
        UserFilterDTO dto = UserFilterDTO.builder()
                .phone("600123456")
                .build();

        // When & Then
        assertTrue(dto.hasTextFilters());
    }

    @Test
    @DisplayName("hasTextFilters hauria de retornar false quan tots els filtres són null")
    void whenNoTextFiltersSet_thenHasTextFiltersReturnsFalse() {
        // Given
        UserFilterDTO dto = UserFilterDTO.builder()
                .isActive(true)
                .build();

        // When & Then
        assertFalse(dto.hasTextFilters());
    }

    @Test
    @DisplayName("hasTextFilters hauria de retornar false quan tots els filtres són strings buits")
    void whenAllTextFiltersAreEmpty_thenHasTextFiltersReturnsFalse() {
        // Given
        UserFilterDTO dto = UserFilterDTO.builder()
                .email("")
                .firstName("")
                .lastName("")
                .phone("")
                .build();

        // When & Then
        assertFalse(dto.hasTextFilters());
    }

    @Test
    @DisplayName("hasTextFilters hauria de retornar false quan tots els filtres són només espais")
    void whenAllTextFiltersAreWhitespace_thenHasTextFiltersReturnsFalse() {
        // Given
        UserFilterDTO dto = UserFilterDTO.builder()
                .email("   ")
                .firstName("  ")
                .lastName(" ")
                .phone("    ")
                .build();

        // When & Then
        assertFalse(dto.hasTextFilters());
    }

    @Test
    @DisplayName("hasTextFilters hauria de retornar true quan almenys un filtre té text")
    void whenAtLeastOneTextFilterSet_thenHasTextFiltersReturnsTrue() {
        // Given
        UserFilterDTO dto = UserFilterDTO.builder()
                .email("")
                .firstName("Joan")
                .lastName("")
                .phone("")
                .build();

        // When & Then
        assertTrue(dto.hasTextFilters());
    }

    @Test
    @DisplayName("Hauria de permetre isActive null (tots els usuaris)")
    void whenIsActiveIsNull_thenShouldRepresentAllUsers() {
        // Given & When
        UserFilterDTO dto = UserFilterDTO.builder()
                .email("test@abasta.com")
                .build();

        // Then
        assertNull(dto.getIsActive());
    }

    @Test
    @DisplayName("Hauria de permetre isActive true (només actius)")
    void whenIsActiveIsTrue_thenShouldRepresentActiveUsers() {
        // Given & When
        UserFilterDTO dto = UserFilterDTO.builder()
                .isActive(true)
                .build();

        // Then
        assertTrue(dto.getIsActive());
    }

    @Test
    @DisplayName("Hauria de permetre isActive false (només inactius)")
    void whenIsActiveIsFalse_thenShouldRepresentInactiveUsers() {
        // Given & When
        UserFilterDTO dto = UserFilterDTO.builder()
                .isActive(false)
                .build();

        // Then
        assertFalse(dto.getIsActive());
    }

    @Test
    @DisplayName("Hauria de permetre construir amb constructor amb tots els arguments")
    void whenUseAllArgsConstructor_thenFieldsAreSet() {
        // Given & When
        UserFilterDTO dto = new UserFilterDTO(
                "test@abasta.com",
                "Pere",
                "Martínez",
                "600777666",
                true,
                3,
                15,
                "phone",
                "asc"
        );

        // Then
        assertEquals("test@abasta.com", dto.getEmail());
        assertEquals("Pere", dto.getFirstName());
        assertEquals("Martínez", dto.getLastName());
        assertEquals("600777666", dto.getPhone());
        assertTrue(dto.getIsActive());
        assertEquals(3, dto.getPage());
        assertEquals(15, dto.getSize());
        assertEquals("phone", dto.getSortBy());
        assertEquals("asc", dto.getSortDir());
    }
}