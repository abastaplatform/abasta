package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.PasswordResetDTO;

/**
 * Interfície de servei per gestionar operacions relacionades amb usuaris.
 * Proporciona funcionalitats per al restabliment de contrasenyes i verificació d'emails.
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 */
public interface UserService {

    /**
     * Inicia el procés de restabliment de contrasenya per a un usuari.
     * Genera un token de restabliment i envia un correu electrònic amb les instruccions.
     *
     * @param email l'adreça de correu electrònic de l'usuari que vol restablir la contrasenya
     */
    void requestPasswordReset(String email);

    /**
     * Restableix la contrasenya d'un usuari utilitzant un token de restabliment vàlid.
     *
     * @param passwordResetDTO l'objecte que conté el token i la nova contrasenya
     */
    void resetPassword(PasswordResetDTO passwordResetDTO);

    /**
     * Verifica l'adreça de correu electrònic d'un usuari utilitzant un token de verificació.
     * Si l'usuari és un administrador d'empresa, també activa l'empresa associada.
     *
     * @param token el token de verificació d'email
     */
    void verifyEmail(String token);

    /**
     * Reenvia el correu electrònic de verificació a un usuari.
     * Genera un nou token de verificació abans d'enviar el correu.
     *
     * @param email l'adreça de correu electrònic de l'usuari
     */
    void resendVerificationEmail(String email);
}