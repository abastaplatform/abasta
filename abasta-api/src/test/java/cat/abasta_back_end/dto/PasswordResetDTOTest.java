package cat.abasta_back_end.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenBuildPasswordResetDTO_thenFieldsAreSet() {
        PasswordResetDTO dto = PasswordResetDTO.builder()
                .token("token123")
                .newPassword("Abcdef1@")
                .build();

        assertEquals("token123", dto.getToken());
        assertEquals("Abcdef1@", dto.getNewPassword());
    }

    @Test
    void whenPasswordMeetsRequirements_thenNoValidationErrors() {
        PasswordResetDTO dto = new PasswordResetDTO("token123", "Abcdef1@");
        Set<ConstraintViolation<PasswordResetDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenPasswordIsBlank_thenValidationError() {
        PasswordResetDTO dto = new PasswordResetDTO("token123", "");
        Set<ConstraintViolation<PasswordResetDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("obligatòria")));
    }

    @Test
    void whenPasswordDoesNotMeetPattern_thenValidationError() {
        PasswordResetDTO dto = new PasswordResetDTO("token123", "abcdefghi"); // sense majúscula, número ni especial
        Set<ConstraintViolation<PasswordResetDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("majúscula")));
    }

    @Test
    void whenTokenIsBlank_thenValidationError() {
        PasswordResetDTO dto = new PasswordResetDTO("", "Abcdef1@");
        Set<ConstraintViolation<PasswordResetDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("obligatori")));
    }
}
