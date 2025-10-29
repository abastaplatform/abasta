package cat.abasta_back_end.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) per gestionar les peticions de restabliment de contrasenya.
 * Conté el token de verificació i la nova contrasenya que l'usuari vol establir.
 *
 * <p>Aquest DTO s'utilitza en el procés de restabliment de contrasenya quan un usuari
 * ha rebut un enllaç de recuperació per correu electrònic i vol establir una nova contrasenya.</p>
 *
 * <p>Les validacions asseguren que:
 * <ul>
 *   <li>El token estigui present (no pot ser buit)</li>
 *   <li>La contrasenya tingui almenys 8 caràcters</li>
 *   <li>La contrasenya contingui almenys una majúscula, una minúscula, un número i un caràcter especial</li>
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
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 * @see cat.abasta_back_end.services.UserService#resetPassword(PasswordResetDTO)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetDTO {

    /**
     * Token de restabliment de contrasenya que s'envia per correu electrònic.
     * Aquest token és únic i temporal, i s'utilitza per verificar la identitat de l'usuari.
     *
     * <p>Validacions:
     * <ul>
     *   <li>@NotBlank: No pot ser null, buit o contenir només espais en blanc</li>
     * </ul>
     * </p>
     */
    @NotBlank(message = "El token es obligatori")
    private String token;

    /**
     * Nova contrasenya que l'usuari vol establir per al seu compte.
     * Ha de complir amb els requisits de seguretat definits per les validacions.
     *
     * <p>Validacions:
     * <ul>
     *   <li>@NotBlank: No pot ser null, buit o contenir només espais en blanc</li>
     *   <li>@Size(min = 8): Ha de tenir com a mínim 8 caràcters</li>
     *   <li>@Pattern: Ha de contenir almenys:
     *     <ul>
     *       <li>Una lletra majúscula (A-Z)</li>
     *       <li>Una lletra minúscula (a-z)</li>
     *       <li>Un dígit numèric (0-9)</li>
     *       <li>Un caràcter especial (@#$%^&+=...)</li>
     *     </ul>
     *   </li>
     * </ul>
     * </p>
     *
     * <p>Aquesta contrasenya s'encriptarà amb BCrypt abans de ser desada a la base de dades.</p>
     */
    @NotBlank(message = "La contrasenya és obligatòria")
    @Size(min = 8, message = "La contrasenya ha de tenir un mínim de 8 caràcters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).*$",
            message = "La contrasenya ha de contenir un mínim d'una majúscula, una minúscula, un número i un caràcter especial")
    private String newPassword;
}