package cat.abasta_back_end.dto;

import cat.abasta_back_end.entities.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseDTOTest {

    @Test
    void whenBuildUserResponseDTO_thenFieldsAreSet() {
        LocalDateTime now = LocalDateTime.now();

        UserResponseDTO dto = UserResponseDTO.builder()
                .uuid("550e8400-e29b-41d4-a716-446655440000")
                .companyUuid("550e8400-e29b-41d4-a716-446655440001")
                .companyName("Abasta Tech SL")
                .email("joan.garcia@example.com")
                .firstName("Joan")
                .lastName("Garcia")
                .role(User.UserRole.ADMIN)
                .phone("+34612345678")
                .isActive(true)
                .emailVerified(true)
                .lastLogin(now)
                .createdAt(now.minusDays(10))
                .updatedAt(now)
                .build();

        assertEquals("550e8400-e29b-41d4-a716-446655440000", dto.getUuid());
        assertEquals("Abasta Tech SL", dto.getCompanyName());
        assertEquals(User.UserRole.ADMIN, dto.getRole());
        assertTrue(dto.getIsActive());
        assertTrue(dto.getEmailVerified());
        assertEquals(now, dto.getLastLogin());
    }

    @Test
    void whenSetFields_thenGettersReturnCorrectValues() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setEmail("user@example.com");
        dto.setFirstName("Joan");
        dto.setLastName("Garcia");
        dto.setIsActive(true);

        assertEquals("user@example.com", dto.getEmail());
        assertEquals("Joan", dto.getFirstName());
        assertEquals("Garcia", dto.getLastName());
        assertTrue(dto.getIsActive());
    }
}
