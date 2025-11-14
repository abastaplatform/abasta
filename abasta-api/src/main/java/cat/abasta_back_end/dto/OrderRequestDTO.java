package cat.abasta_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) per a rebre les dades necessàries per crear una nova comanda.
 * <p>
 * Aquest DTO s’utilitza per validar i encapsular la informació enviada pel client abans
 * de crear els registres corresponents a la base de dades.
 * </p>
 *
 * <p>Es correspon amb l’estructura de la taula <strong>orders</strong>:</p>
 * <ul>
 *   <li><code>name</code> → Nom de la comanda (obligatori).</li>
 *   <li><code>supplierUuid</code> → Identificador únic del proveïdor (obligatori).</li>
 *   <li><code>notes</code> → Notes/observacions (opcional).</li>
 *   <li><code>deliveryDate</code> → Data d'entrega (opcional).</li>
 *   <li><code>List items</code> → Llistat d'ítems d'una comada.</li>
 * </ul>
 *
 * <p>Els camps <code>uuid</code>, <code>created_at</code> i <code>updated_at</code>
 * són gestionats automàticament pel sistema i no formen part d’aquest DTO.</p>
 *
 * @author Daniel Garcia
 * @version 1.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    /**
     *  Nom de la comanda
     */
    @NotBlank
    private String name;

    /**
     * Identificador únic del proveïdor
     */
    @NotBlank
    private String supplierUuid;

    /**
     * Notes/Observacions de la comanda
     */
    private String notes;

    /**
     * Data d'entrega de la comanda
     */
    private LocalDate deliveryDate;

    /**
     * Llistat d'items de la comanda (productes)
     */
    @NotNull
    private List<OrderItemRequestDTO> items;
}
