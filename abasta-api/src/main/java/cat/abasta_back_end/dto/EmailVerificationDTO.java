package cat.abasta_back_end.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) per gestionar les peticions de verificació d'email.
 * Conté el token de verificació que s'envia a l'usuari per correu electrònic.
 *
 * <p>Aquest DTO s'utilitza en el procés de verificació d'adreça de correu electrònic
 * després que un usuari es registri al sistema. La verificació d'email és obligatòria
 * per activar completament el compte i, en el cas d'administradors d'empresa, per
 * activar també l'empresa associada.</p>
 *
 * <p>Flux d'ús típic:
 * <ol>
 *   <li>L'usuari es registra i rep un correu de verificació amb un enllaç</li>
 *   <li>L'enllaç conté el token de verificació com a paràmetre</li>
 *   <li>En clicar l'enllaç, el frontend extreu el token i envia aquest DTO al backend</li>
 *   <li>El backend valida el token i marca l'email com a verificat</li>
 *   <li>Si és un administrador, també s'activa l'empresa associada</li>
 *   <li>L'usuari rep un correu de benvinguda confirmant la verificació</li>
 * </ol>
 * </p>
 *
 * <p>Les validacions asseguren que:
 * <ul>
 *   <li>El token estigui present (no pot ser buit o null)</li>
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
 * // Extracció del token de la URL
 * String tokenFromUrl = request.getParameter("token");
 * EmailVerificationDTO verificationDTO = new EmailVerificationDTO(tokenFromUrl);
 * userService.verifyEmail(verificationDTO.getToken());
 * </pre>
 * </p>
 *
 * <p>Estructura JSON de la petició:
 * <pre>
 * {
 *   "token": "550e8400-e29b-41d4-a716-446655440000"
 * }
 * </pre>
 * </p>
 *
 * <p>Exemple d'URL de verificació:
 * <pre>
 * https://deveps.ddns.net/abasta/verify-email?token=550e8400-e29b-41d4-a716-446655440000
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 * @see cat.abasta_back_end.services.UserService#verifyEmail(String)
 * @see cat.abasta_back_end.services.EmailService#sendEmailVerification(String, String, String)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationDTO {

    /**
     * Token de verificació d'email que s'envia per correu electrònic a l'usuari.
     * Aquest token és únic i temporal, i s'utilitza per confirmar que l'usuari
     * té accés a l'adreça de correu especificada durant el registre.
     *
     * <p>Validacions:
     * <ul>
     *   <li>@NotBlank: No pot ser null, buit o contenir només espais en blanc</li>
     * </ul>
     * </p>
     *
     * <p><strong>Característiques del token:</strong></p>
     * <ul>
     *   <li>Format: UUID versió 4 (exemple: 550e8400-e29b-41d4-a716-446655440000)</li>
     *   <li>Validesa: 24 hores des de la seva generació</li>
     *   <li>Ús únic: Després de la verificació, el token s'elimina i no es pot reutilitzar</li>
     *   <li>Seguretat: Generat criptogràficament per garantir la seva unicitat</li>
     * </ul>
     *
     * <p><strong>Comportament en cas d'error:</strong></p>
     * <ul>
     *   <li>Si el token no existeix: BadRequestException amb missatge "Token de verificació invàlid o expirat"</li>
     *   <li>Si el token ha expirat: BadRequestException amb el mateix missatge per seguretat</li>
     *   <li>Si l'email ja està verificat: El procés continua sense error (idempotent)</li>
     * </ul>
     *
     * <p><strong>Notes de seguretat:</strong></p>
     * <ul>
     *   <li>El token s'elimina de la base de dades després de ser utilitzat</li>
     *   <li>Els tokens expirats es poden netejar periòdicament per mantenir la base de dades neta</li>
     *   <li>No es revela informació sobre si un token va ser vàlid o simplement ha expirat</li>
     * </ul>
     *
     * <p>Exemple de token vàlid:
     * <pre>550e8400-e29b-41d4-a716-446655440000</pre>
     * </p>
     */
    @NotBlank(message = "El token és obligatori")
    private String token;
}