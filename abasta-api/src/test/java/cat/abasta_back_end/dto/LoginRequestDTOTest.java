package cat.abasta_back_end.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenValidLoginRequest_thenNoValidationErrors() {
        // DTO vàlid
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("user@example.com")
                .password("P4ssword!")
                .build();

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

        // Esperem 0 errors
        assertEquals(0, violations.size());
    }

    @Test
    void whenBlankEmail_thenValidationError() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("")
                .password("P4ssword!")
                .build();

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

        // Ha de detectar 1 error per email
        assertEquals(1, violations.size());
        violations.forEach(v -> System.out.println(v.getPropertyPath() + " -> " + v.getMessage()));
    }

    @Test
    void whenInvalidEmailFormat_thenValidationError() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("invalid-email")
                .password("P4ssword!")
                .build();

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

        // Ha de detectar 1 error per email
        assertEquals(1, violations.size());
        violations.forEach(v -> System.out.println(v.getPropertyPath() + " -> " + v.getMessage()));
    }

    @Test
    void whenBlankPassword_thenValidationError() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("user@example.com")
                .password("")
                .build();

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

        // Ha de detectar 1 error per password
        assertEquals(1, violations.size());
        violations.forEach(v -> System.out.println(v.getPropertyPath() + " -> " + v.getMessage()));
    }
}
