package cat.abasta_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO genèric per estandaritzar les respostes de la API REST.
 * Proporciona una estructura uniforme per a totes les respostes HTTP,
 * incloent-hi indicadors d'èxit, missatges descriptius i dades de resposta.
 *
 * @param <T> Tipus de dades continguts en la resposta
 * @author Dani Garcia
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDTO<T> {

    /** Indica si l'operació ha sigut exitosa (true) o fallida (false) */
    private boolean success;

    /** Missatge descriptiu de l'operació realitzada */
    private String message;

    /** Ddes de resposta de tius genèric. Pot ser null en cas d'error o eliminació. */
    private T data;

    /** Marca temporal de quan s'ha generat la resposta */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Crea una resposta exitosa amb dades i missatge.
     *
     * @param <T> Tipus de dades de la resposta
     * @param data Dades a incloure en la resposta
     * @param message Missatge descriptiu del èxit de l'operació
     * @return ApiResponseDTO configurat com a resposta exitosa
     */
    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Crea una resposta d'error sense dades.
     *
     * @param <T> Tipus de dades de la resposta (serà null)
     * @param message Missatge descriptiu de l'error
     * @return ApiResponseDTO configurat com a resposta d'error
     */
    public static <T> ApiResponseDTO<T> error(String message) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
