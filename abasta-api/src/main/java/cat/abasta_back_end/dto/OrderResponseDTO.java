package cat.abasta_back_end.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) per retornar la informació d'una comanda creada o consultada.
 * <p>
 * Aquest DTO encapsula totes les dades d'una comanda, incloent-hi les seves línies
 * (items) associades, per ser enviades al client.
 * </p>
 *
 * <p>Es correspon amb l’estructura de la taula <strong>orders</strong>.</p>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    /**
     * Identificador únic públic de la comanda
     */
    private String uuid;

    /**
     * Nom de la comanda
     */
    private String name;

    /**
     * Estat actual de la comanda (PENDING, SENT, CONFIRMED, etc.)
     */
    private String status;

    /**
     * Import total de la comanda
     */
    private BigDecimal totalAmount;

    /**
     * Notes o observacions associades
     */
    private String notes;

    /**
     * Data prevista d'entrega
     */
    private LocalDate deliveryDate;

    /**
     * Data de creació de la comanda
     */
    private LocalDateTime createdAt;

    /**
     * Data d'última modificació de la comanda
     */
    private LocalDateTime updatedAt;

    /**
     * Identificador únic o nom del proveïdor
     */
    private String supplierUuid;

    /**
     * Llista d'items associats a la comanda
     */
    private List<OrderItemResponseDTO> items;
}
