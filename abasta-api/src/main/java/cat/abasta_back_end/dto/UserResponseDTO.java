package cat.abasta_back_end.dto;

import cat.abasta_back_end.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) per a la resposta amb informació d'usuari.
 * Conté totes les dades públiques d'un usuari que són segures per enviar al client,
 * excloent informació delicada com contrasenyes o tokens.
 *
 * <p>Aquest DTO s'utilitza per retornar informació d'usuaris en diverses operacions:
 * <ul>
 *   <li>Resposta després del login (dins de LoginResponseDTO)</li>
 *   <li>Consulta de perfil d'usuari</li>
 *   <li>Llistats d'usuaris d'una empresa</li>
 *   <li>Resposta després de crear o actualitzar un usuari</li>
 * </ul>
 * </p>
 *
 * <p>Exemple d'ús típic:
 * <pre>
 * UserResponseDTO userDTO = UserResponseDTO.builder()
 *     .uuid(user.getUuid())
 *     .email(user.getEmail())
 *     .firstName(user.getFirstName())
 *     .lastName(user.getLastName())
 *     .role(user.getRole())
 *     .emailVerified(user.getEmailVerified())
 *     .isActive(user.getIsActive())
 *     .build();
 * </pre>
 * </p>
 *
 * <p>Les anotacions de Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor, @Builder)
 * generen automàticament:
 * <ul>
 *   <li>Getters i setters per a tots els camps</li>
 *   <li>Mètodes equals(), hashCode() i toString()</li>
 *   <li>Constructor sense paràmetres</li>
 *   <li>Constructor amb tots els paràmetres</li>
 *   <li>Patró Builder per a la construcció fluent d'objectes</li>
 * </ul>
 * </p>
 *
 * <p>Estructura JSON de la resposta:
 * <pre>
 * {
 *   "uuid": "550e8400-e29b-41d4-a716-446655440000",
 *   "companyUuid": "550e8400-e29b-41d4-a716-446655440000",
 *   "companyName": "Abasta Tech SL",
 *   "email": "joan.garcia@example.com",
 *   "firstName": "Joan",
 *   "lastName": "Garcia",
 *   "role": "ADMIN",
 *   "phone": "+34612345678",
 *   "isActive": true,
 *   "emailVerified": true,
 *   "lastLogin": "2025-10-23T10:30:00",
 *   "createdAt": "2025-01-15T09:00:00",
 *   "updatedAt": "2025-10-23T10:30:00"
 * }
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @see User
 * @see LoginResponseDTO
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    /**
     * Identificador únic universal (UUID) de l'usuari.
     * Utilitzat per identificar l'usuari de forma única en contextos públics o APIs externes.
     *
     * <p>A diferència de l'ID numèric, el UUID és més segur per exposar públicament,
     * ja que no revela informació sobre el nombre d'usuaris o l'ordre de creació.</p>
     *
     * <p>Format estàndard UUID versió 4: <code>550e8400-e29b-41d4-a716-446655440000</code></p>
     */
    private String uuid;

    /**
     * Identificador de l'empresa a la qual pertany l'usuari.
     *
     * <p>Aquest camp pot ser null si l'usuari no està associat a cap empresa
     * o si és un usuari del sistema.</p>
     */
    private String companyUuid;

    /**
     * Nom de l'empresa a la qual pertany l'usuari.
     *
     * <p>Aquest camp facilita mostrar informació de l'empresa sense necessitat
     * de fer una petició addicional a l'API. Pot ser null si l'usuari no està
     * associat a cap empresa.</p>
     */
    private String companyName;

    /**
     * Adreça de correu electrònic de l'usuari.
     * S'utilitza com a identificador únic per al login i per enviar notificacions.
     *
     * <p>Aquest camp és únic en l'àmbit de sistema i serveix com a nom d'usuari
     * per a l'autenticació.</p>
     *
     * <p>Exemple: <code>joan.garcia@example.com</code></p>
     */
    private String email;

    /**
     * Nom de l'usuari.
     *
     * <p>Aquest camp s'utilitza per personalitzar la interfície i els correus
     * enviats a l'usuari.</p>
     */
    private String firstName;

    /**
     * Cognoms de l'usuari.
     *
     * <p>Juntament amb firstName, forma el nom complet de l'usuari.</p>
     */
    private String lastName;

    /**
     * Rol de l'usuari dins del sistema.
     * Determina els permisos i funcionalitats a les quals pot accedir l'usuari.
     *
     * <p>Els rols possibles estan definits a l'enumeració User.UserRole i poden incloure:
     * <ul>
     *   <li>ADMIN: Administrador amb permisos complets sobre l'empresa</li>
     *   <li>MANAGER: Gestor amb permisos limitats</li>
     *   <li>USER: Usuari estàndard amb permisos bàsics</li>
     * </ul>
     * </p>
     *
     * @see User.UserRole
     */
    private User.UserRole role;

    /**
     * Número de telèfon de l'usuari.
     *
     * <p>Aquest camp és opcional i pot ser null. S'utilitza per a comunicacions
     * alternatives o autenticació de dos factors.</p>
     *
     * <p>Format recomanat amb prefix internacional: <code>+34612345678</code></p>
     */
    private String phone;

    /**
     * Indica si el compte de l'usuari està actiu.
     *
     * <p>Un usuari inactiu no pot iniciar sessió a l'aplicació. Els administradors
     * poden desactivar comptes temporalment sense eliminar-los.</p>
     *
     * <p>Valors:
     * <ul>
     *   <li>true: L'usuari pot accedir al sistema</li>
     *   <li>false: L'usuari està desactivat i no pot iniciar sessió</li>
     * </ul>
     * </p>
     */
    private Boolean isActive;

    /**
     * Indica si el compte de l'usuari s'ha esborrat.
     *
     * <p>Un usuari esborrat no pot iniciar sessió a l'aplicació. Els administradors
     * poden eliminar comptes.</p>
     *
     * <p>Valors:
     * <ul>
     *   <li>false: L'usuari pot accedir al sistema</li>
     *   <li>true: L'usuari està esborrat i no pot iniciar sessió</li>
     * </ul>
     * </p>
     */
    private Boolean isDeleted;

    /**
     * Indica si l'usuari ha verificat la seva adreça de correu electrònic.
     *
     * <p>La verificació d'email és un pas important per assegurar que l'usuari
     * té accés a l'adreça de correu proporcionada. Alguns usuaris (com a administradors
     * d'empresa) poden tenir funcionalitats limitades fins que verifiquin el seu email.</p>
     *
     * <p>Valors:
     * <ul>
     *   <li>true: L'email ha estat verificat</li>
     *   <li>false: L'email encara no ha estat verificat</li>
     * </ul>
     * </p>
     */
    private Boolean emailVerified;

    /**
     * Data i hora de l'últim inici de sessió de l'usuari.
     *
     * <p>Aquest camp s'actualitza automàticament cada vegada que l'usuari
     * inicia sessió amb èxit. Pot ser null si l'usuari mai ha iniciat sessió.</p>
     *
     * <p>Útil per a auditoria i per identificar comptes inactius.</p>
     */
    private LocalDateTime lastLogin;

    /**
     * Data i hora de creació del compte d'usuari.
     *
     * <p>Aquest camp s'estableix automàticament quan es crea el registre
     * a la base de dades i no canvia mai.</p>
     *
     * <p>Format ISO 8601: <code>2025-01-15T09:00:00</code></p>
     */
    private LocalDateTime createdAt;

    /**
     * Data i hora de l'última actualització de les dades de l'usuari.
     *
     * <p>Aquest camp s'actualitza automàticament cada vegada que es modifica
     * qualsevol dada de l'usuari (nom, email, rol, etc.).</p>
     *
     * <p>Útil per a auditoria i per detectar canvis recents en el perfil.</p>
     */
    private LocalDateTime updatedAt;
}