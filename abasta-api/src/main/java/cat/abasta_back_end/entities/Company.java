package cat.abasta_back_end.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entitat que representa una empresa registrada a la plataforma.
 * Cada empresa pot gestionar els seus propis usuaris, productes, proveïdors i comandes.
 * En registrar-se, es crea automàticament un usuari administrador associat.
 *
 * @author Dani Garcia
 * @version 1.0
 */
@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    /** Identificador numèric únic autogenerat */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador UUID únic per a ús públic.
     * Es genera automàticament en crear l'empresa si no se'n proporciona cap.
     */
    @Column(nullable = false, unique = true)
    private String uuid;

    /** Nom comercial de l'empresa */
    @Column(nullable = false)
    private String name;

    /**
     * Número d’identificació fiscal (NIF/CIF).
     * Ha de ser únic dins del sistema.
     */
    @Column(name = "tax_id", nullable = false, unique = true, length = 50)
    private String taxId;

    /** Correu electrònic de contacte de l’empresa */
    @Column(nullable = false)
    private String email;

    /** Telèfon de contacte (opcional) */
    @Column(length = 50)
    private String phone;

    /** Adreça física completa de l’empresa */
    @Column(columnDefinition = "TEXT")
    private String address;

    /** Ciutat on es troba l’empresa */
    @Column(length = 100)
    private String city;

    /** Codi postal de l’empresa */
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /**
     * Estat actual de l’empresa.
     * Per defecte es crea en estat PENDING fins que es verifiqui el correu de l’administrador.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CompanyStatus status = CompanyStatus.PENDING;

    /** Data i hora de creació del registre (no modificable) */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Data i hora de l’última actualització */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Llista d’usuaris associats a aquesta empresa.
     * Inclou l’administrador i qualsevol altre usuari creat.
     * Càrrega “lazy” per optimitzar les consultes.
     */
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

    /**
     * Mètode executat automàticament abans de persistir una nova empresa.
     * Inicialitza les dates d’auditoria i genera el UUID si no existeix.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (uuid == null) {
            uuid = java.util.UUID.randomUUID().toString();
        }
    }

    /**
     * Mètode executat automàticament abans d’actualitzar una empresa existent.
     * Actualitza la data de l’última modificació.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Estats possibles d’una empresa a la plataforma.
     * <ul>
     *   <li><b>ACTIVE:</b> Empresa activa i operativa</li>
     *   <li><b>INACTIVE:</b> Empresa temporalment desactivada</li>
     *   <li><b>PENDING:</b> Empresa recentment registrada pendent de verificació del correu electrònic</li>
     * </ul>
     */
    public enum CompanyStatus {
        /** Empresa activa i operativa */
        ACTIVE,

        /** Empresa temporalment desactivada */
        INACTIVE,

        /** Empresa recentment registrada pendent de verificació */
        PENDING
    }
}