package cat.abasta_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) per retornar la informació del TOP de productes
 * <p>
 * Aquest DTO encapsula les dades dels productes, recuperant informació per presentar a l'informe
 *
 * <p>Aquest DTO s’utilitza principalment com a resposta en operacions REST per recuperar informació de productes.</p>
 *
 * @author Daniel Garcia
 * @author Enrique Pérez
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class ProducteTopDTO {

    /**
     * Nom del producte
     */
    private String nomProducte;

    /**
     * Quantitat total del producte
     */
    private BigDecimal quantitatTotal;

    /**
     * Despesa total d'aquest producte
     */
    private BigDecimal despesaTotal;
}