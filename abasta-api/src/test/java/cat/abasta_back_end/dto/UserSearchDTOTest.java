package cat.abasta_back_end.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaris per UserSearchDTO.
 * Verifica la construcció i valors per defecte de dades de cerca d'usuari.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DisplayName("UserSearchDTO Tests")
class UserSearchDTOTest {

    @Test
    @DisplayName("Hauria de construir DTO amb builder correctament")
    void whenBuildUserSearchDTO_thenFieldsAreSet() {
        // Given & When
        UserSearchDTO dto = UserSearchDTO.builder()
                .searchText("Joan")
                .page(1)
                .size(20)
                .sortBy("firstName")
                .sortDir("desc")
                .build();

        // Then
        assertEquals("Joan", dto.getSearchText());
        assertEquals(1, dto.getPage());
        assertEquals(20, dto.getSize());
        assertEquals("firstName", dto.getSortBy());
        assertEquals("desc", dto.getSortDir());
    }

    @Test
    @DisplayName("Hauria d'aplicar valors per defecte amb constructor sense arguments")
    void whenUseNoArgsConstructor_thenDefaultValuesAreSet() {
        // Given & When
        UserSearchDTO dto = new UserSearchDTO();

        // Then
        assertNull(dto.getSearchText());
        assertEquals(0, dto.getPage());
        assertEquals(10, dto.getSize());
        assertEquals("email", dto.getSortBy());
        assertEquals("asc", dto.getSortDir());
    }

    @Test
    @DisplayName("Hauria d'aplicar valors per defecte amb builder sense valors")
    void whenUseBuilderWithoutValues_thenDefaultValuesAreSet() {
        // Given & When
        UserSearchDTO dto = UserSearchDTO.builder().build();

        // Then
        assertNull(dto.getSearchText());
        assertEquals(0, dto.getPage());
        assertEquals(10, dto.getSize());
        assertEquals("email", dto.getSortBy());
        assertEquals("asc", dto.getSortDir());
    }

    @Test
    @DisplayName("Hauria de funcionar amb setters i getters")
    void whenSetFields_thenGettersReturnCorrectValues() {
        // Given
        UserSearchDTO dto = new UserSearchDTO();

        // When
        dto.setSearchText("Maria");
        dto.setPage(2);
        dto.setSize(50);
        dto.setSortBy("lastName");
        dto.setSortDir("desc");

        // Then
        assertEquals("Maria", dto.getSearchText());
        assertEquals(2, dto.getPage());
        assertEquals(50, dto.getSize());
        assertEquals("lastName", dto.getSortBy());
        assertEquals("desc", dto.getSortDir());
    }

    @Test
    @DisplayName("Hauria de permetre searchText null")
    void whenSearchTextIsNull_thenShouldBeAllowed() {
        // Given & When
        UserSearchDTO dto = UserSearchDTO.builder()
                .page(0)
                .size(10)
                .build();

        // Then
        assertNull(dto.getSearchText());
    }

    @Test
    @DisplayName("Hauria de permetre construir amb constructor amb tots els arguments")
    void whenUseAllArgsConstructor_thenFieldsAreSet() {
        // Given & When
        UserSearchDTO dto = new UserSearchDTO("Pere", 3, 15, "phone", "asc");

        // Then
        assertEquals("Pere", dto.getSearchText());
        assertEquals(3, dto.getPage());
        assertEquals(15, dto.getSize());
        assertEquals("phone", dto.getSortBy());
        assertEquals("asc", dto.getSortDir());
    }

    @Test
    @DisplayName("Hauria de permetre page 0 (primera pàgina)")
    void whenPageIsZero_thenShouldBeAllowed() {
        // Given & When
        UserSearchDTO dto = UserSearchDTO.builder()
                .page(0)
                .build();

        // Then
        assertEquals(0, dto.getPage());
    }

    @Test
    @DisplayName("Hauria de permetre size mínim 1")
    void whenSizeIsOne_thenShouldBeAllowed() {
        // Given & When
        UserSearchDTO dto = UserSearchDTO.builder()
                .size(1)
                .build();

        // Then
        assertEquals(1, dto.getSize());
    }

    @Test
    @DisplayName("Hauria de permetre sortDir 'asc'")
    void whenSortDirIsAsc_thenShouldBeAllowed() {
        // Given & When
        UserSearchDTO dto = UserSearchDTO.builder()
                .sortDir("asc")
                .build();

        // Then
        assertEquals("asc", dto.getSortDir());
    }

    @Test
    @DisplayName("Hauria de permetre sortDir 'desc'")
    void whenSortDirIsDesc_thenShouldBeAllowed() {
        // Given & When
        UserSearchDTO dto = UserSearchDTO.builder()
                .sortDir("desc")
                .build();

        // Then
        assertEquals("desc", dto.getSortDir());
    }
}