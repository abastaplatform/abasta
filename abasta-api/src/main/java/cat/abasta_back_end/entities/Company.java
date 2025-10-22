package cat.abasta_back_end.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entitat que representa una empresa registrada en la plataforma.
 * Cada empresa pot gestionar els seus propis usuaris, productes, proveïdors i comandes.
 * Al registrar-se, es crea auotmàticament un usuari administrador associat.
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

    /** Identificador único numérico autogenerado */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador UUID único para uso público.
     * Se genera automáticamente al crear la empresa si no se proporciona.
     */
    @Column(nullable = false, unique = true)
    private String uuid;

    /** Nombre comercial de la empresa */
    @Column(nullable = false)
    private String name;

    /**
     * Número de identificación fiscal (NIF/CIF).
     * Debe ser único en el sistema.
     */
    @Column(name = "tax_id", nullable = false, unique = true, length = 50)
    private String taxId;

    /** Email de contacto de la empresa */
    @Column(nullable = false)
    private String email;

    /** Teléfono de contacto (opcional) */
    @Column(length = 50)
    private String phone;

    /** Dirección física completa de la empresa */
    @Column(columnDefinition = "TEXT")
    private String address;

    /** Ciudad donde se ubica la empresa */
    @Column(length = 100)
    private String city;

    /** Código postal de la empresa */
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /**
     * Estado actual de la empresa.
     * Por defecto se crea en estado PENDING hasta que se verifique el email del administrador.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CompanyStatus status = CompanyStatus.PENDING;

    /** Fecha y hora de creación del registro (no modificable) */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Lista de usuarios asociados a esta empresa.
     * Incluye el administrador y cualquier usuario adicional creado.
     * Carga lazy para optimizar consultas.
     */
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

    /**
     * Método ejecutado automáticamente antes de persistir una nueva empresa.
     * Inicializa las fechas de auditoría y genera el UUID si no existe.
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
     * Método ejecutado automáticamente antes de actualizar una empresa existente.
     * Actualiza la fecha de última modificación.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Estados posibles de una empresa en la plataforma.
     * <ul>
     *   <li><b>ACTIVE:</b> Empresa activa y operativa</li>
     *   <li><b>INACTIVE:</b> Empresa temporalmente desactivada</li>
     *   <li><b>PENDING:</b> Empresa recién registrada pendiente de verificación de email</li>
     * </ul>
     */
    public enum CompanyStatus {
        /** Empresa activa y operativa */
        ACTIVE,

        /** Empresa temporalmente desactivada */
        INACTIVE,

        /** Empresa recién registrada pendiente de verificación */
        PENDING
    }
}