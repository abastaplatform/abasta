package cat.abasta_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) per retornar la informació de comandes l'últim mes al dashboard.
 * <p>
 * Aquest DTO encapsula les dades de les comandes l'últim mes, recuperant informació per presentar al dashboard.
 *
 * <p>Aquest DTO s’utilitza principalment com a resposta en operacions REST per recuperar informació de comandes.</p>
 *
 * @author Daniel Garcia
 * @author Enrique Pérez
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponseDTO {

    /**
     * Nombre total comandes l'últim mes.
     */
    private Integer totalComandes;

    /**
     * Despesa total l'últim mes
     */
    private BigDecimal despesaComandes;

    /**
     * Nombre de comandes pendents l'últim mes
     */
    private Integer comandesPendents;

}
