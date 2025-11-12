package cat.abasta_back_end.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) per retornar la informació d’un item dins d’una comanda.
 * <p>
 * Inclou les dades bàsiques del producte, quantitat, preu i observacions.
 * </p>
 *
 * <p>Es correspon amb l’estructura de la taula <strong>order_items</strong>.</p>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDTO {

    /**
     * Identificador únic públic de l’item
     */
    private String uuid;

    /**
     * Identificador únic del producte associat
     */
    private String productUuid;

    /**
     * Nom del producte (opcional, per mostrar al frontend)
     */
    private String productName;

    /**
     * Quantitat sol·licitada
     */
    private BigDecimal quantity;

    /**
     * Preu unitari
     */
    private BigDecimal unitPrice;

    /**
     * Subtotal de l’item
     */
    private BigDecimal subtotal;

    /**
     * Notes o observacions del producte
     */
    private String notes;
}
