package cat.abasta_back_end.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entitat que representa un producte dintre de la comanda registrada a la plataforma.
 * Cada comanda té relacionat una sèrie de registres que corresponen
 * a productes afegits per després enviar-li al proveïdor.
 *
 * @author Dani Garcia
 * @version 1.0
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    /** Identificador del registre de la taula */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador únic universal del registre */
    @Column(nullable = false, unique = true)
    private String uuid;

    /** Identificador de l'ordre a la que pertany aquest registre */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** Identificador del producte de la comanda */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Quantitat de productes afegits */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    /** Preu únic del producte */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /** Subtotal de la comanda (quantitat*preu unitari) */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /** Notes/observacions del registre */
    @Lob
    private String notes;

    /** Data de creació */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Funcío abans de guardar el registre que calcula el subtotal multiplicant preu per quantitat
     */
    @PrePersist
    public void prePersist() {
        if (subtotal == null) {
            subtotal = unitPrice.multiply(quantity);
        }
    }

}