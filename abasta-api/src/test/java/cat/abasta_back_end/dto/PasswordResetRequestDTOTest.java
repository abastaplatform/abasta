package cat.abasta_back_end.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenBuildPasswordResetRequestDTO_thenFieldsAreSet() {
        PasswordResetRequestDTO dto = PasswordResetRequestDTO.builder()
                .email("user@example.com")
                .build();

        assertEquals("user@example.com", dto.getEmail());
    }

    @Test
    void whenEmailIsValid_thenNoValidationErrors() {
        PasswordResetRequestDTO dto = new PasswordResetRequestDTO("user@example.com");
        Set<ConstraintViolation<PasswordResetRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenEmailIsBlank_thenValidationError() {
        PasswordResetRequestDTO dto = new PasswordResetRequestDTO("");
        Set<ConstraintViolation<PasswordResetRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("No pot ser null")));
    }

    @Test
    void whenEmailIsInvalid_thenValidationError() {
        PasswordResetRequestDTO dto = new PasswordResetRequestDTO("invalid-email");
        Set<ConstraintViolation<PasswordResetRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("format d'email vàlid")));
    }
}
