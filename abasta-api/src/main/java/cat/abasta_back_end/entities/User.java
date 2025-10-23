package cat.abasta_back_end.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entitat que representa un usuari dins del sistema.
 * <p>
 * Aquesta classe defineix tots els camps relacionats amb la informació d’un usuari,
 * així com les seves relacions amb altres entitats i els esdeveniments de persistència.
 * </p>
 *
 * <p>Inclou funcionalitats com:</p>
 * <ul>
 *   <li>Gestió d’identificadors únics (UUID).</li>
 *   <li>Control de verificació de correu electrònic i restabliment de contrasenya.</li>
 *   <li>Seguiment de dates de creació i actualització automàtiques.</li>
 * </ul>
 *
 * <p>Les anotacions de Lombok {@link Data}, {@link Builder}, {@link NoArgsConstructor}
 * i {@link AllArgsConstructor} permeten reduir el codi boilerplate.</p>
 *
 * @author Enrique Pérez
 * @since 1.0
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Identificador primari autogenerat de l’usuari.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador únic universal per a l’usuari.
     * Es genera automàticament durant la persistència si no existeix.
     */
    @Column(nullable = false, unique = true)
    private String uuid;

    /**
     * Empresa a la qual pertany l’usuari.
     * Relació molts a un amb l’entitat {@link Company}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /**
     * Adreça de correu electrònic única de l’usuari.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Contrasenya de l’usuari, emmagatzemada habitualment amb hash.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Nom propi de l’usuari.
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * Cognoms de l’usuari.
     */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Rol o nivell d’autorització de l’usuari dins del sistema.
     * Pot ser {@link UserRole#ADMIN} o {@link UserRole#USER}.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.USER;

    /**
     * Telèfon de contacte de l’usuari (opcional).
     */
    @Column(length = 50)
    private String phone;

    /**
     * Indica si el compte està actiu o no.
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Indica si el correu electrònic ha estat verificat.
     */
    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    /**
     * Token per verificar el correu electrònic de l’usuari.
     */
    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    /**
     * Data de caducitat del token de verificació de correu electrònic.
     */
    @Column(name = "email_verification_expires")
    private LocalDateTime emailVerificationExpires;

    /**
     * Token temporal per restablir la contrasenya.
     */
    @Column(name = "password_reset_token")
    private String passwordResetToken;

    /**
     * Data de caducitat del token de restabliment de contrasenya.
     */
    @Column(name = "password_reset_expires")
    private LocalDateTime passwordResetExpires;

    /**
     * Data i hora de l’últim inici de sessió de l’usuari.
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * Data i hora de creació del registre.
     * Assignada automàticament abans de la persistència inicial.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data i hora de l’última actualització del registre.
     * Actualitzada automàticament abans de cada modificació.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Mètode de cicle de vida JPA que s’executa abans de persistir un nou usuari.
     * <p>
     * Inicialitza els camps {@code createdAt}, {@code updatedAt} i {@code uuid} si no existeix.
     * </p>
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
     * Mètode de cicle de vida JPA que s’executa abans d’actualitzar un usuari existent.
     * <p>
     * Actualitza automàticament el camp {@code updatedAt}.
     * </p>
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Enumeració que defineix els rols disponibles dins del sistema.
     */
    public enum UserRole {
        /** Administrador del sistema amb permisos complets. */
        ADMIN,

        /** Usuari estàndard amb permisos limitats. */
        USER
    }
}
