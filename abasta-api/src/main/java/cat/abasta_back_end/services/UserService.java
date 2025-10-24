package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.LoginRequestDTO;
import cat.abasta_back_end.dto.LoginResponseDTO;
import cat.abasta_back_end.dto.PasswordResetDTO;
import cat.abasta_back_end.exceptions.BadRequestException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;

/**
 * Interfície de servei per gestionar operacions relacionades amb usuaris.
 * Proporciona funcionalitats per a l'autenticació, restabliment de contrasenyes
 * i verificació d'emails.
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 */
public interface UserService {

    /**
     * Autentica un usuari a la plataforma mitjançant les seves credencials (correu electrònic i la contrasenya).
     * Si les credencials són correctes, retorna un token o la informació necessària per mantenir la sessió iniciada.
     *
     * @param loginDTO l'objecte que conté les credencials d'inici de sessió (correu electrònic i contrasenya)
     * @return un {@link LoginResponseDTO} amb la informació de l'usuari autenticat i/o el token de sessió
     */
    LoginResponseDTO login(LoginRequestDTO loginDTO);

    /**
     * Inicia el procés de restabliment de contrasenya per a un usuari.
     * Genera un token de restabliment i envia un correu electrònic amb les instruccions.
     *
     * @param email l'adreça de correu electrònic de l'usuari que vol restablir la contrasenya
     * @throws ResourceNotFoundException si no es troba cap usuari amb l'email especificat
     */
    void requestPasswordReset(String email);

    /**
     * Restableix la contrasenya d'un usuari utilitzant un token de restabliment vàlid.
     *
     * @param passwordResetDTO l'objecte que conté el token i la nova contrasenya
     * @throws BadRequestException si el token és invàlid o ha expirat
     */
    void resetPassword(PasswordResetDTO passwordResetDTO);

    /**
     * Verifica l'adreça de correu electrònic d'un usuari utilitzant un token de verificació.
     * Si l'usuari és un administrador d'empresa, també activa l'empresa associada.
     *
     * @param token el token de verificació d'email
     * @throws BadRequestException si el token és invàlid o ha expirat
     */
    void verifyEmail(String token);

    /**
     * Reenvia el correu electrònic de verificació a un usuari.
     * Genera un nou token de verificació abans d'enviar el correu.
     *
     * @param email l'adreça de correu electrònic de l'usuari
     * @throws ResourceNotFoundException si no es troba cap usuari amb l'email especificat
     * @throws BadRequestException si l'email ja està verificat
     */
    void resendVerificationEmail(String email);
}