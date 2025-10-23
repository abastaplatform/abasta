package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) per gestionar les peticions d'inici de restabliment de contrasenya.
 * Conté únicament l'adreça de correu electrònic de l'usuari que ha oblidat la contrasenya.
 *
 * <p>Aquest DTO s'utilitza en el primer pas del procés de recuperació de contrasenya,
 * quan l'usuari sol·licita un enllaç de restabliment. Si l'email existeix al sistema,
 * s'envia un correu amb un token temporal per procedir amb el canvi de contrasenya.</p>
 *
 * <p>Flux d'ús típic:
 * <ol>
 *   <li>L'usuari introdueix el seu email al formulari de recuperació</li>
 *   <li>El frontend envia aquest DTO al backend (POST /api/auth/forgot-password)</li>
 *   <li>El backend genera un token de restabliment i l'envia per correu</li>
 *   <li>L'usuari rep l'email amb un enllaç que conté el token</li>
 *   <li>L'usuari utilitza el token per establir una nova contrasenya (amb PasswordResetDTO)</li>
 * </ol>
 * </p>
 *
 * <p>Les validacions asseguren que:
 * <ul>
 *   <li>L'email estigui present (no pot ser buit o null)</li>
 *   <li>L'email tingui un format vàlid (conté @ i domini)</li>
 * </ul>
 * </p>
 *
 * <p>Les anotacions de Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor) generen automàticament:
 * <ul>
 *   <li>Getters i setters per a tots els camps</li>
 *   <li>Mètodes equals(), hashCode() i toString()</li>
 *   <li>Constructor sense paràmetres</li>
 *   <li>Constructor amb tots els paràmetres</li>
 * </ul>
 * </p>
 *
 * <p>Exemple d'ús:
 * <pre>
 * PasswordResetRequestDTO request = new PasswordResetRequestDTO("user@example.com");
 * userService.requestPasswordReset(request.getEmail());
 * </pre>
 * </p>
 *
 * <p>Estructura JSON de la petició:
 * <pre>
 * {
 *   "email": "user@example.com"
 * }
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 * @see PasswordResetDTO
 * @see cat.abasta_back_end.services.UserService#requestPasswordReset(String)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestDTO {

    /**
     * Adreça de correu electrònic de l'usuari que vol restablir la seva contrasenya.
     * S'utilitza per identificar el compte i enviar l'enllaç de recuperació.
     *
     * <p>Validacions:
     * <ul>
     *   <li>@NotBlank: No pot ser null, buit o contenir només espais en blanc</li>
     *   <li>@Email: Ha de tenir un format d'email vàlid (exemple: user@domain.com)</li>
     * </ul>
     * </p>
     *
     * <p><strong>Notes de seguretat:</strong></p>
     * <ul>
     *   <li>El sistema no revela si l'email existeix o no per prevenir enumeració d'usuaris</li>
     *   <li>Si l'email no existeix, el backend pot retornar èxit igualment sense enviar cap correu</li>
     *   <li>Això impedeix que atacants descobreixin quins emails estan registrats al sistema</li>
     * </ul>
     *
     * <p>Exemples vàlids:
     * <ul>
     *   <li>user@example.com</li>
     *   <li>joan.garcia@empresa.cat</li>
     *   <li>admin+test@abasta.com</li>
     * </ul>
     * </p>
     */
    @NotBlank(message = "No pot ser null, buit o contenir només espais en blanc")
    @Email(message = "Ha de tenir un format d'email vàlid (exemple: user@domain.com)")
    private String email;
}