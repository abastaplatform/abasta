package cat.abasta_back_end.exceptions;

import cat.abasta_back_end.dto.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestor global d'excepcions per a tota l'aplicació.
 * <p>
 * Aquesta classe centralitza el tractament de totes les excepcions
 * llançades als controladors REST. Garanteix que les respostes d'error
 * segueixin un format comú i coherent mitjançant {@link ApiResponseDTO}.
 * </p>
 *
 * <p>Les excepcions controlades inclouen:</p>
 * <ul>
 *   <li>{@link ResourceNotFoundException}: recurs no trobat (HTTP 404)</li>
 *   <li>{@link DuplicateResourceException}: recurs duplicat (HTTP 409)</li>
 *   <li>{@link BadRequestException}: petició invàlida (HTTP 400)</li>
 *   <li>{@link MethodArgumentNotValidException}: errors de validació (HTTP 400)</li>
 *   <li>{@link Exception}: errors interns no previstos (HTTP 500)</li>
 * </ul>
 *
 * @author Enrique Pérez
 * @see ApiResponseDTO
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestiona les excepcions {@link ResourceNotFoundException}.
     *
     * @param ex excepció capturada.
     * @return resposta HTTP amb estat 404 i missatge d'error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }

    /**
     * Gestiona les excepcions {@link DuplicateResourceException}.
     *
     * @param ex excepció capturada.
     * @return resposta HTTP amb estat 409 i missatge d'error.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleDuplicateResource(DuplicateResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }

    /**
     * Gestiona les excepcions {@link BadRequestException}.
     *
     * @param ex excepció capturada.
     * @return resposta HTTP amb estat 400 i missatge d'error.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }

    /**
     * Gestiona els errors de validació provinents de {@code @Valid}.
     * <p>
     * Recull tots els camps que no compleixen les restriccions de validació
     * i els retorna com un mapa clau-valor (camp → missatge d'error).
     * </p>
     *
     * @param ex excepció de validació capturada.
     * @return resposta HTTP amb estat 400 i detalls dels errors de camp.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.<Map<String, String>>builder()
                        .success(false)
                        .message("Error de validació")
                        .data(errors)
                        .build());
    }

    /**
     * Gestiona qualsevol altra excepció no contemplada específicament.
     * <p>
     * Serveix com a mecanisme de seguretat per evitar que errors interns
     * sense tractament específic exposin detalls interns del sistema.
     * </p>
     *
     * @param ex excepció capturada.
     * @return resposta HTTP amb estat 500 i missatge genèric d'error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error intern del servidor: " + ex.getMessage()));
    }
}
