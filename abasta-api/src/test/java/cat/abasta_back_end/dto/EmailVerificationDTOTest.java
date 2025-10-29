package cat.abasta_back_end.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmailVerificationDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenAllArgsConstructor_thenFieldsAreSet() {
        EmailVerificationDTO dto = new EmailVerificationDTO("550e8400-e29b-41d4-a716-446655440000");

        assertEquals("550e8400-e29b-41d4-a716-446655440000", dto.getToken());
    }

    @Test
    void whenUsingBuilder_thenFieldsAreSetCorrectly() {
        EmailVerificationDTO dto = EmailVerificationDTO.builder()
                .token("123e4567-e89b-12d3-a456-426614174000")
                .build();

        assertEquals("123e4567-e89b-12d3-a456-426614174000", dto.getToken());
    }

    @Test
    void whenTokenIsBlank_thenValidationFails() {
        EmailVerificationDTO dto = new EmailVerificationDTO("");

        Set<ConstraintViolation<EmailVerificationDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals("El token és obligatori", violations.iterator().next().getMessage());
    }

    @Test
    void whenTokenIsNull_thenValidationFails() {
        EmailVerificationDTO dto = new EmailVerificationDTO(null);

        Set<ConstraintViolation<EmailVerificationDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals("El token és obligatori", violations.iterator().next().getMessage());
    }

    @Test
    void whenTokenIsValid_thenValidationPasses() {
        EmailVerificationDTO dto = new EmailVerificationDTO("550e8400-e29b-41d4-a716-446655440000");

        Set<ConstraintViolation<EmailVerificationDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}
