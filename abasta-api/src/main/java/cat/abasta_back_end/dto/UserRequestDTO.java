package cat.abasta_back_end.dto;

import cat.abasta_back_end.entities.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per a les peticions d'actualització d'usuaris.
 * <p>
 * Conté les dades modificables d'un usuari amb les validacions
 * corresponents. S'utilitza principalment per a operacions
 * d'actualització de perfil d'usuari.
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
public class UserRequestDTO {

    /**
     * Adreça de correu electrònic de l'usuari.
     * Ha de ser un email vàlid i és obligatori.
     */
    @NotBlank(message = "L'email és obligatori")
    @Email(message = "L'email ha de ser vàlid")
    private String email;

    /**
     * Nom de l'usuari.
     * Camp opcional i no pot excedir els 100 caràcters.
     */
    @Size(max = 100, message = "El nom no pot excedir 100 caràcters")
    private String firstName;

    /**
     * Cognom de l'usuari.
     * Camp opcional i no pot excedir els 100 caràcters.
     */
    @Size(max = 100, message = "El cognom no pot excedir 100 caràcters")
    private String lastName;

    /**
     * Rol de l'usuari dins del sistema.
     * Camp opcional que determina els permisos de l'usuari.
     *
     * @see User.UserRole
     */
    private User.UserRole role;

    /**
     * Número de telèfon de contacte de l'usuari.
     * Camp opcional amb un màxim de 50 caràcters.
     */
    @Size(max = 50, message = "El telèfon no pot excedir 50 caràcters")
    private String phone;

    /**
     * Indica si l'usuari està actiu al sistema.
     * Camp opcional; si és {@code null}, no es modifica l'estat actual.
     */
    private Boolean isActive;
}