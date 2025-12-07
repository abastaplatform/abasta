package cat.abasta_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) per retornar la informació de despesa per proveïdor.
 * <p>
 * Aquest DTO encapsula les dades dels proveïdors, recuperant informació per presentar a l'informe.
 *
 * <p>Aquest DTO s’utilitza principalment com a resposta en operacions REST per recuperar informació de proveidors.</p>
 *
 * @author Daniel Garcia
 * @author Enrique Pérez
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DespesaPerProveidorDTO {

    /**
     * Proveïdor
     */
    private String proveidor;

    /**
     * Nombre de comandes
     */
    private Integer numComandes;

    /**
     * Despesa total
     */
    private BigDecimal despesaTotal;

    /**
     * Percentatge del total general
     */
    private BigDecimal percentatge;
}

