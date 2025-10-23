package cat.abasta_back_end.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) per al canvi de contrasenya d'un usuari.
 * <p>
 * Conté la contrasenya actual i la nova contrasenya que es vol establir.
 * Inclou validacions per assegurar que la nova contrasenya compleixi els requisits de seguretat.
 * </p>
 *
 * @author Dani Garcia
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDTO {

    /**
     * Contrasenya actual de l'usuari.
     * Aquest camp és obligatori per verificar la identitat abans de canviar la contrasenya.
     */
    @NotBlank(message = "La contrasenya actual és obligatòria")
    private String currentPassword;

    /**
     * Nova contrasenya que l'usuari vol establir.
     * <ul>
     *   <li>Ha de tenir almenys 8 caràcters</li>
     *   <li>Ha de contenir almenys una lletra majúscula</li>
     *   <li>Ha de contenir almenys una lletra minúscula</li>
     *   <li>Ha de contenir almenys un número</li>
     *   <li>Ha de contenir almenys un caràcter especial (@#$%^&+=)</li>
     * </ul>
     */
    @NotBlank(message = "La nova contrasenya és obligatòria")
    @Size(min = 8, message = "La contrasenya ha de tenir com a mínim 8 caràcters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "La contrasenya ha de contenir almenys una majúscula, una minúscula, un número i un caràcter especial")
    private String newPassword;
}
