package cat.abasta_back_end.dto;

import cat.abasta_back_end.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaris per UserRegistrationDTO.
 * Verifica la construcció i validació de dades de registre d'usuari.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DisplayName("UserRegistrationDTO Tests")
class UserRegistrationDTOTest {

    @Test
    @DisplayName("Hauria de construir DTO amb builder correctament")
    void whenBuildUserRegistrationDTO_thenFieldsAreSet() {
        // Given & When
        UserRegistrationDTO dto = UserRegistrationDTO.builder()
                .email("test@abasta.com")
                .password("Password123!")
                .firstName("Joan")
                .lastName("Garcia")
                .role(User.UserRole.USER)
                .phone("600123456")
                .build();

        // Then
        assertEquals("test@abasta.com", dto.getEmail());
        assertEquals("Password123!", dto.getPassword());
        assertEquals("Joan", dto.getFirstName());
        assertEquals("Garcia", dto.getLastName());
        assertEquals(User.UserRole.USER, dto.getRole());
        assertEquals("600123456", dto.getPhone());
    }

    @Test
    @DisplayName("Hauria de funcionar amb setters i getters")
    void whenSetFields_thenGettersReturnCorrectValues() {
        // Given
        UserRegistrationDTO dto = new UserRegistrationDTO();

        // When
        dto.setEmail("maria@abasta.com");
        dto.setPassword("SecurePass1@");
        dto.setFirstName("Maria");
        dto.setLastName("López");
        dto.setRole(User.UserRole.ADMIN);
        dto.setPhone("600999888");

        // Then
        assertEquals("maria@abasta.com", dto.getEmail());
        assertEquals("SecurePass1@", dto.getPassword());
        assertEquals("Maria", dto.getFirstName());
        assertEquals("López", dto.getLastName());
        assertEquals(User.UserRole.ADMIN, dto.getRole());
        assertEquals("600999888", dto.getPhone());
    }

    @Test
    @DisplayName("Hauria de permetre role null (valor per defecte USER)")
    void whenRoleIsNull_thenShouldBeAllowed() {
        // Given & When
        UserRegistrationDTO dto = UserRegistrationDTO.builder()
                .email("test@abasta.com")
                .password("Password123!")
                .firstName("Joan")
                .lastName("Garcia")
                .phone("600123456")
                .build();

        // Then
        assertNull(dto.getRole());
    }

    @Test
    @DisplayName("Hauria de permetre phone null")
    void whenPhoneIsNull_thenShouldBeAllowed() {
        // Given & When
        UserRegistrationDTO dto = UserRegistrationDTO.builder()
                .email("test@abasta.com")
                .password("Password123!")
                .firstName("Joan")
                .lastName("Garcia")
                .role(User.UserRole.USER)
                .build();

        // Then
        assertNull(dto.getPhone());
    }

    @Test
    @DisplayName("Hauria de construir DTO amb tots els camps obligatoris")
    void whenAllRequiredFieldsSet_thenDTOIsValid() {
        // Given & When
        UserRegistrationDTO dto = UserRegistrationDTO.builder()
                .email("valid@abasta.com")
                .password("ValidPass1@")
                .firstName("Pere")
                .lastName("Martínez")
                .build();

        // Then
        assertNotNull(dto.getEmail());
        assertNotNull(dto.getPassword());
        assertNotNull(dto.getFirstName());
        assertNotNull(dto.getLastName());
    }

    @Test
    @DisplayName("Hauria de permetre construir amb constructor amb tots els arguments")
    void whenUseAllArgsConstructor_thenFieldsAreSet() {
        // Given & When
        UserRegistrationDTO dto = new UserRegistrationDTO(
                "test@abasta.com",
                "Password123!",
                "Anna",
                "Sánchez",
                User.UserRole.USER,
                "600777666"
        );

        // Then
        assertEquals("test@abasta.com", dto.getEmail());
        assertEquals("Password123!", dto.getPassword());
        assertEquals("Anna", dto.getFirstName());
        assertEquals("Sánchez", dto.getLastName());
        assertEquals(User.UserRole.USER, dto.getRole());
        assertEquals("600777666", dto.getPhone());
    }
}