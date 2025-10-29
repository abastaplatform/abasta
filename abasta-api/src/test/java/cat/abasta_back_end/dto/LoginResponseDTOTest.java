package cat.abasta_back_end.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoginResponseDTOTest {

    @Test
    void whenBuildLoginResponseDTO_thenFieldsAreSetCorrectly() {
        // Crear un UserResponseDTO fictici
        UserResponseDTO userDTO = UserResponseDTO.builder()
                .uuid(String.valueOf(1L))
                .email("user@example.com")
                .firstName("Joan")
                .lastName("Garcia")
                .build();

        // Crear LoginResponseDTO amb Builder
        LoginResponseDTO loginResponse = LoginResponseDTO.builder()
                .token("eyJhbGciOiJIUzUxMiJ9...")
                .user(userDTO)
                .build();

        // Comprovar que els camps es van assignar correctament
        assertEquals("eyJhbGciOiJIUzUxMiJ9...", loginResponse.getToken());
        assertEquals("Bearer", loginResponse.getType()); // valor per defecte
        assertNotNull(loginResponse.getUser());
        assertEquals("user@example.com", loginResponse.getUser().getEmail());
        assertEquals("Joan", loginResponse.getUser().getFirstName());
    }

    @Test
    void whenSetTypeExplicitly_thenTypeIsOverridden() {
        LoginResponseDTO loginResponse = LoginResponseDTO.builder()
                .token("token123")
                .type("Custom")
                .build();

        assertEquals("Custom", loginResponse.getType());
    }
}
