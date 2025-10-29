package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) per a la petició d'inici de sessió.
 * <p>
 * Conté les credencials bàsiques necessàries per autenticar un usuari:
 * el seu correu electrònic i la contrasenya.
 * </p>
 *
 * <p>
 * Inclou validacions per assegurar que els camps no estiguin buits
 * i que el correu tingui un format vàlid.
 * </p>
 *
 * @author Dani Garcia
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    /**
     * Adreça de correu electrònic de l'usuari.
     * Ha de ser un email vàlid i no pot estar buit.
     */
    @NotBlank(message = "El correu electrònic és obligatori")
    @Email(message = "El correu electrònic ha de tenir un format vàlid")
    private String email;

    /**
     * Contrasenya de l'usuari.
     * No pot estar buida.
     */
    @NotBlank(message = "La contrasenya és obligatòria")
    private String password;
}
