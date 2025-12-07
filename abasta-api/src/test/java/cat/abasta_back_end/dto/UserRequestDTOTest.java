package cat.abasta_back_end.dto;

import cat.abasta_back_end.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaris per UserRequestDTO.
 * Verifica la construcció i validació de dades d'actualització d'usuari.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DisplayName("UserRequestDTO Tests")
class UserRequestDTOTest {

    @Test
    @DisplayName("Hauria de construir DTO amb builder correctament")
    void whenBuildUserRequestDTO_thenFieldsAreSet() {
        // Given & When
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("test@abasta.com")
                .firstName("Joan")
                .lastName("Garcia")
                .role(User.UserRole.USER)
                .phone("600123456")
                .isActive(true)
                .build();

        // Then
        assertEquals("test@abasta.com", dto.getEmail());
        assertEquals("Joan", dto.getFirstName());
        assertEquals("Garcia", dto.getLastName());
        assertEquals(User.UserRole.USER, dto.getRole());
        assertEquals("600123456", dto.getPhone());
        assertTrue(dto.getIsActive());
    }

    @Test
    @DisplayName("Hauria de funcionar amb setters i getters")
    void whenSetFields_thenGettersReturnCorrectValues() {
        // Given
        UserRequestDTO dto = new UserRequestDTO();

        // When
        dto.setEmail("maria@abasta.com");
        dto.setFirstName("Maria");
        dto.setLastName("López");
        dto.setRole(User.UserRole.ADMIN);
        dto.setPhone("600999888");
        dto.setIsActive(false);

        // Then
        assertEquals("maria@abasta.com", dto.getEmail());
        assertEquals("Maria", dto.getFirstName());
        assertEquals("López", dto.getLastName());
        assertEquals(User.UserRole.ADMIN, dto.getRole());
        assertEquals("600999888", dto.getPhone());
        assertFalse(dto.getIsActive());
    }

    @Test
    @DisplayName("Hauria de permetre firstName null")
    void whenFirstNameIsNull_thenShouldBeAllowed() {
        // Given & When
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("test@abasta.com")
                .lastName("Garcia")
                .build();

        // Then
        assertNull(dto.getFirstName());
    }

    @Test
    @DisplayName("Hauria de permetre lastName null")
    void whenLastNameIsNull_thenShouldBeAllowed() {
        // Given & When
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("test@abasta.com")
                .firstName("Joan")
                .build();

        // Then
        assertNull(dto.getLastName());
    }

    @Test
    @DisplayName("Hauria de permetre role null")
    void whenRoleIsNull_thenShouldBeAllowed() {
        // Given & When
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("test@abasta.com")
                .firstName("Joan")
                .lastName("Garcia")
                .build();

        // Then
        assertNull(dto.getRole());
    }

    @Test
    @DisplayName("Hauria de permetre phone null")
    void whenPhoneIsNull_thenShouldBeAllowed() {
        // Given & When
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("test@abasta.com")
                .firstName("Joan")
                .lastName("Garcia")
                .build();

        // Then
        assertNull(dto.getPhone());
    }

    @Test
    @DisplayName("Hauria de permetre isActive null")
    void whenIsActiveIsNull_thenShouldBeAllowed() {
        // Given & When
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("test@abasta.com")
                .firstName("Joan")
                .lastName("Garcia")
                .build();

        // Then
        assertNull(dto.getIsActive());
    }

    @Test
    @DisplayName("Hauria de permetre construir amb constructor amb tots els arguments")
    void whenUseAllArgsConstructor_thenFieldsAreSet() {
        // Given & When
        UserRequestDTO dto = new UserRequestDTO(
                "test@abasta.com",
                "Anna",
                "Sánchez",
                User.UserRole.USER,
                "600777666",
                true
        );

        // Then
        assertEquals("test@abasta.com", dto.getEmail());
        assertEquals("Anna", dto.getFirstName());
        assertEquals("Sánchez", dto.getLastName());
        assertEquals(User.UserRole.USER, dto.getRole());
        assertEquals("600777666", dto.getPhone());
        assertTrue(dto.getIsActive());
    }

    @Test
    @DisplayName("Hauria de permetre actualitzar només l'email")
    void whenOnlyEmailSet_thenOtherFieldsAreNull() {
        // Given & When
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("updated@abasta.com")
                .build();

        // Then
        assertEquals("updated@abasta.com", dto.getEmail());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getRole());
        assertNull(dto.getPhone());
        assertNull(dto.getIsActive());
    }
}