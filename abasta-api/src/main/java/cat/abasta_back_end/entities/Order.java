package cat.abasta_back_end.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entitat que representa una comanda registrada a la plataforma.
 * Cada comanda té relacionat una sèrie de registres que corresponen
 * a productes afegits per després enviar-li al proveïdor.
 *
 * @author Dani Garcia
 * @version 1.0
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    /** Identificador del registre a la taula */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador únic universal del registre */
    @Column(nullable = false, unique = true)
    private String uuid;

    /** Id de la companyia a la que pertany la comanda */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /** Id del proveïdor a qui es farà la comanda */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    /** Id de l'usuari que fa la comanda */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Nom representatiu de la comanda */
    @Column(length = 50, nullable = false)
    private String name;

    /** Estat de la comanda */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    /** Total de la comanda */
    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /** Notes/Observacions sobre la comanda */
    @Lob
    private String notes;

    /** Data d'entrega */
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    /** Data de creació de la comanda */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /** data de modificació de la comanda */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /** Llistat dels productes afegits a la comanda */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Funció per afegir Item
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        recalculateTotal();
    }

    /**
     * Funció per eliminar item
     * @param item
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        recalculateTotal();
    }

    /**
     * Funció per recalcular el total
     */
    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Funció abans de modificar que genera la data de modificació.
     */
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Estats possibles de la comanda
     * <ul>
     *   <li><b>PENDING:</b> Comanda creada com a borrador</li>
     *   <li><b>SENT:</b> Comanda enviada</li>
     *   <li><b>CONFIRMED:</b> Comanda confirmada</li>
     *   <li><b>REJECTED:</b> Comanda rebutjada</li>
     *   <li><b>COMPLETED:</b> Comanda completada</li>
     *   <li><b>CANCELLED:</b> Comanda cancel·lada</li>
     * </ul>
     */
    public enum OrderStatus {
        PENDING, SENT, CONFIRMED, REJECTED, COMPLETED, CANCELLED
    }

}