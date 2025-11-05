package cat.abasta_back_end.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entitat que representa un producte dins del sistema.
 * <p>
 * Aquesta classe defineix tots els camps relacionats amb la informació d’un producte,
 * així com les seves relacions amb altres entitats i els esdeveniments de persistència.
 * </p>
 *
 * <p>Inclou característiques com:</p>
 * <ul>
 *   <li>Gestió d’identificadors únics (UUID).</li>
 *   <li>Camps propis de definició: Nom, descripció, preu, unitat, imatge, actiu, etc.</li>
 *   <li>Seguiment de dates de creació i actualització automàtiques.</li>
 * </ul>
 *
 * <p>Les anotacions de Lombok {@link Data}, {@link Builder}, {@link NoArgsConstructor}
 * i {@link AllArgsConstructor} permeten reduir el codi boilerplate.</p>
 *
 * @author Daniel Garcia
 * @since 1.0
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    /** Identificador primari autogenerat del producte. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador únic universal per el producte. */
    @Column(nullable = false, unique = true)
    private String uuid;

    /** Proveïdor al qual pertany el producte. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    /** Categoria del producte. */
    @Column(name = "category", length = 255)
    private String category;

    /** Nom del producte. */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /** Descripció del producte. */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Preu del producte. */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Unitat de mesura del producte (kg, litres, unitats, etc.). */
    @Column(name = "unit", length = 50)
    private String unit;

    /** URL de la imatge del producte. */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** Indica si el producte està actiu o no. */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /** Data i hora de creació del registre. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Data i hora de l’última actualització del registre. */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Assigna automàticament la data de creació abans de guardar el registre. */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    /** Actualitza automàticament la data de modificació abans d’actualitzar el registre. */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}