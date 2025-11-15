package cat.abasta_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) per a rebre les dades necessàries per crear un nou item de comanda.
 * <p>
 * Aquest DTO s’utilitza per validar i encapsular la informació enviada pel client abans
 * de crear el registre corresponent a la base de dades.
 * </p>
 *
 * <p>Es correspon amb l’estructura de la taula <strong>order_items</strong>:</p>
 * <ul>
 *   <li><code>product uuid</code> → Identificador únic del producte (obligatori).</li>
 *   <li><code>quantity</code> → Quantitat de producte (obligatori no null).</li>
 *   <li><code>notes</code> → Notes/Observacions (opcional).</li>
 * </ul>
 *
 * <p>Els camps <code>uuid</code>, <code>created_at</code>.
 * Són gestionats automàticament pel sistema i no formen part d’aquest DTO.</p>
 *
 * @author Daniel Garcia
 * @version 1.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {

    /**
     * Identificador únic del producte
     */
    @NotBlank
    private String productUuid;

    /**
     * Quantitat de producte
     */
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal quantity;

    /**
     * Notes/Observacions del producte
     */
    private String notes;

}
