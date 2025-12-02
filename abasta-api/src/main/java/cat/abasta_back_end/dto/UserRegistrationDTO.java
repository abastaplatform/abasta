package cat.abasta_back_end.dto;

import cat.abasta_back_end.entities.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) per al registre de nous usuaris.
 * <p>
 * Conté totes les dades necessàries per crear un nou compte d'usuari
 * al sistema Abasta. Inclou validacions per garantir la integritat
 * de les dades rebudes.
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDTO {

    /**
     * Adreça de correu electrònic de l'usuari.
     * <p>
     * Ha de ser una adreça de correu vàlida i única al sistema.
     * Camp obligatori.
     * </p>
     */
    @NotBlank(message = "L'email és obligatori")
    @Email(message = "L'email ha de ser vàlid")
    private String email;

    /**
     * Contrasenya de l'usuari.
     * <p>
     * Ha de complir els següents requisits de seguretat:
     * </p>
     * <ul>
     *   <li>Mínim 8 caràcters de longitud</li>
     *   <li>Almenys una lletra majúscula</li>
     *   <li>Almenys una lletra minúscula</li>
     *   <li>Almenys un número</li>
     *   <li>Almenys un caràcter especial</li>
     * </ul>
     * Camp obligatori.
     */
    @NotBlank(message = "La contrasenya és obligatòria")
    @Size(min = 8, message = "La contrasenya ha de tenir almenys 8 caràcters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).*$",
            message = "La contrasenya ha de contenir un mínim d'una majúscula, una minúscula, un número i un caràcter especial")
    private String password;

    /**
     * Nom de l'usuari.
     * <p>
     * Longitud màxima de 100 caràcters.
     * Camp obligatori.
     * </p>
     */
    @NotBlank(message = "El nom és obligatori")
    @Size(max = 100)
    private String firstName;

    /**
     * Cognoms de l'usuari.
     * <p>
     * Longitud màxima de 100 caràcters.
     * Camp obligatori.
     * </p>
     */
    @NotBlank(message = "Els cognoms són obligatoris")
    @Size(max = 100)
    private String lastName;

    /**
     * Rol de l'usuari al sistema.
     * <p>
     * Si no s'especifica, s'assignarà el rol USER per defecte.
     * Camp opcional.
     * </p>
     *
     * @see User.UserRole
     */
    private User.UserRole role;

    /**
     * Número de telèfon de l'usuari.
     * <p>
     * Longitud màxima de 50 caràcters.
     * Camp opcional.
     * </p>
     */
    @Size(max = 50)
    private String phone;
}