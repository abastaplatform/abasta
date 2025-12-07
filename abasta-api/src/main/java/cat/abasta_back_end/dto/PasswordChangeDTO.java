package cat.abasta_back_end.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per al canvi de contrasenya d'un usuari.
 * <p>
 * Encapsula les dades necessàries per realitzar un canvi de contrasenya,
 * incloent-hi la verificació de la contrasenya actual i la validació
 * dels requisits de seguretat de la nova contrasenya.
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
public class PasswordChangeDTO {

    /**
     * Contrasenya actual de l'usuari per verificar la seva identitat.
     */
    @NotBlank(message = "La contrasenya actual és obligatòria")
    private String currentPassword;

    /**
     * Nova contrasenya que substituirà l'actual.
     * <p>
     * Ha de complir els següents requisits de seguretat:
     * </p>
     * <ul>
     *     <li>Mínim 8 caràcters</li>
     *     <li>Almenys una lletra majúscula</li>
     *     <li>Almenys una lletra minúscula</li>
     *     <li>Almenys un número</li>
     *     <li>Almenys un caràcter especial</li>
     * </ul>
     */
    @NotBlank(message = "La nova contrasenya és obligatòria")
    @Size(min = 8, message = "La contrasenya ha de tenir un mínim de 8 caràcters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).*$",
            message = "La contrasenya ha de contenir un mínim d'una majúscula, una minúscula, un número i un caràcter especial")
    private String newPassword;
}