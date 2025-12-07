package cat.abasta_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) per a rebre un període de temps.
 * <p>
 * Aquest DTO s’utilitza per validar i encapsular la informació enviada pel client abans
 * de retornar la informació desitjada que necessita d'un periode per ser estreta.
 * </p>
 *
 * @author Daniel Garcia
 * @author Enrique Pérez
 * @version 1.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodRequestDTO {

    /**
     * Data inicial del període
     */
    private LocalDateTime dataInicial;

    /**
     * Data final del període
     */
    private LocalDateTime dataFinal;

}

