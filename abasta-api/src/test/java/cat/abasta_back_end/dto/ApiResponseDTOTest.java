package cat.abasta_back_end.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseDTOTest {

    @Test
    void testSuccess_ShouldReturnSuccessfulResponse() {
        String message = "Operació completada";
        String data = "Dades de prova";

        ApiResponseDTO<String> response = ApiResponseDTO.success(data, message);

        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testError_ShouldReturnErrorResponse() {
        String message = "Error de prova";

        ApiResponseDTO<Object> response = ApiResponseDTO.error(message);

        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }
}
