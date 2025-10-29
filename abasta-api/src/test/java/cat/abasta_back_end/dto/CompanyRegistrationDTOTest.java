package cat.abasta_back_end.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompanyRegistrationDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenAllFieldsValid_thenNoValidationErrors() {
        CompanyRegistrationDTO dto = CompanyRegistrationDTO.builder()
                .companyName("Abasta SL")
                .taxId("B12345678")
                .companyEmail("empresa@abasta.com")
                .companyPhone("123456789")
                .companyAddress("Carrer Exemple, 1")
                .companyCity("Barcelona")
                .companyPostalCode("08001")
                .adminEmail("admin@abasta.com")
                .adminPassword("Aa123456!")
                .adminFirstName("Dani")
                .adminLastName("Garcia")
                .adminPhone("987654321")
                .build();

        Set<ConstraintViolation<CompanyRegistrationDTO>> violations = validator.validate(dto);
        assertEquals(0, violations.size());
    }

    @Test
    void whenMandatoryFieldsMissing_thenValidationErrors() {
        CompanyRegistrationDTO dto = new CompanyRegistrationDTO(); // tot null

        Set<ConstraintViolation<CompanyRegistrationDTO>> violations = validator.validate(dto);
        // Hauríem de tenir errors per: companyName, taxId, adminEmail, adminPassword, adminFirstName, adminLastName
        assertEquals(6, violations.size());
    }

    @Test
    void whenInvalidPassword_thenValidationError() {
        CompanyRegistrationDTO dto = CompanyRegistrationDTO.builder()
                .companyName("Abasta SL")
                .taxId("B12345678")
                .companyEmail("contact@abasta.com") // opcional però vàlid
                .adminEmail("admin@abasta.com")     // obligatori i vàlid
                .adminFirstName("Dani")
                .adminLastName("Garcia")
                .adminPassword("abc")               // incorrecte
                .build();

        Set<ConstraintViolation<CompanyRegistrationDTO>> violations = validator.validate(dto);
        violations.forEach(v -> System.out.println(v.getPropertyPath() + " -> " + v.getMessage()));

        assertEquals(2, violations.size()); // hauria d'anar bé
    }

}
