package cat.abasta_back_end.exceptions;

import cat.abasta_back_end.dto.ApiResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionTests {

    // -----------------------------
    // Tests de les excepcions simples
    // -----------------------------
    @Test
    void testBadRequestExceptionMessage() {
        BadRequestException ex = new BadRequestException("Missatge d'error");
        assertThat(ex.getMessage()).isEqualTo("Missatge d'error");
    }

    @Test
    void testDuplicateResourceExceptionMessage() {
        DuplicateResourceException ex = new DuplicateResourceException("Recurs duplicat");
        assertThat(ex.getMessage()).isEqualTo("Recurs duplicat");
    }

    @Test
    void testResourceNotFoundExceptionMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurs no trobat");
        assertThat(ex.getMessage()).isEqualTo("Recurs no trobat");
    }

    // -----------------------------
    // Tests per al GlobalExceptionHandler
    // -----------------------------
    @Test
    void testHandleResourceNotFound() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResourceNotFoundException ex = new ResourceNotFoundException("Usuari no trobat");

        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleResourceNotFound(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Usuari no trobat");
    }

    @Test
    void testHandleDuplicateResource() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        DuplicateResourceException ex = new DuplicateResourceException("Email duplicat");

        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleDuplicateResource(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Email duplicat");
    }

    @Test
    void testHandleBadRequest() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        BadRequestException ex = new BadRequestException("Petició invàlida");

        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleBadRequest(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Petició invàlida");
    }

    @Test
    void testHandleValidationExceptions() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        // Simulem error de validació
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
        bindingResult.addError(new FieldError("object", "email", "Email obligatori"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiResponseDTO<Map<String, String>>> response = handler.handleValidationExceptions(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Error de validació");
        assertThat(response.getBody().getData()).containsEntry("email", "Email obligatori");
    }

    @Test
    void testHandleGenericException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Exception ex = new Exception("Error inesperat");

        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("Error intern del servidor: Error inesperat");
    }
}
