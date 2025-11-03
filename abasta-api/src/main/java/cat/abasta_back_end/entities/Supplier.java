package cat.abasta_back_end.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Entitat JPA que representa un proveïdor associat a una empresa del sistema.
 * Gestiona tota la informació relacionada amb els proveïdors comercials de les empreses.
 *
 * <p>Aquesta entitat estableix una relació de molts-a-un amb l'entitat Company,
 * cosa que permet que cada empresa pugui tenir múltiples proveïdors associats, però
 * cada proveïdor pertany només a una empresa específica.</p>
 *
 * <p>Les característiques principals inclouen:
 * <ul>
 *   <li>Identificació única mitjançant ID numèric i UUID</li>
 *   <li>Informació de contacte completa (nom, email, telèfon, adreça)</li>
 *   <li>Gestió d'estat actiu/inactiu per controlar la disponibilitat</li>
 *   <li>Camp de notes per informació addicional</li>
 *   <li>Auditoria automàtica de dates de creació i actualització</li>
 *   <li>Validació d'unicitat del nom dins de la mateixa empresa</li>
 * </ul>
 * </p>
 *
 * <p>Les anotacions de Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor, @Builder)
 * generen automàticament:
 * <ul>
 *   <li>Getters i setters per a tots els camps</li>
 *   <li>Mètodes equals(), hashCode() i toString()</li>
 *   <li>Constructor sense paràmetres (requerit per JPA)</li>
 *   <li>Constructor amb tots els paràmetres</li>
 *   <li>Patró Builder per a la construcció fluent d'objectes</li>
 * </ul>
 * </p>
 *
 * <p>Exemple d'ús amb Builder:
 * <pre>
 * Supplier supplier = Supplier.builder()
 *     .company(company)
 *     .name("Proveïdors Catalunya SL")
 *     .contactName("Joan Martínez")
 *     .email("joan@provcat.com")
 *     .phone("938765432")
 *     .address("Av. Diagonal 123, Barcelona")
 *     .isActive(true)
 *     .build();
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 1.0
 * @see Company
 */
@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {

    /**
     * Identificador únic del proveïdor.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * UUID únic del proveïdor per a identificació externa.
     */
    @Column(nullable = false, unique = true)
    private String uuid;

    /**
     * Empresa a la qual pertany el proveïdor.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /**
     * Nom de l'empresa proveïdora.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Nom de la persona de contacte del proveïdor.
     */
    @Column(name = "contact_name")
    private String contactName;

    /**
     * Adreça de correu electrònic del proveïdor.
     */
    private String email;

    /**
     * Número de telèfon del proveïdor.
     */
    private String phone;

    /**
     * Adreça física del proveïdor.
     */
    @Column(columnDefinition = "TEXT")
    private String address;

    /**
     * Notes addicionals sobre el proveïdor.
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Indica si el proveïdor està actiu o no.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Data i hora de creació del registre.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data i hora de l'última actualització del registre.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Mètode de callback que s'executa abans de persistir l'entitat.
     * Genera un UUID si no en té un i estableix les dates.
     */
    @PrePersist
    private void onCreate() {
        if (this.uuid == null) {
            this.uuid = java.util.UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mètode de callback que s'executa abans d'actualitzar l'entitat.
     * Actualitza la data de l'última modificació.
     */
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}