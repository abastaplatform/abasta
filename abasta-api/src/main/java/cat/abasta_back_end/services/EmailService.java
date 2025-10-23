package cat.abasta_back_end.services;

/**
 * Interfície de servei per gestionar l'enviament de correus electrònics.
 * Proporciona mètodes per enviar diferents tipus de correus relacionats amb
 * l'autenticació i gestió d'usuaris.
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 */
public interface EmailService {

    /**
     * Envia un correu electrònic de restabliment de contrasenya.
     *
     * @param to l'adreça de correu electrònic del destinatari
     * @param token el token de restabliment de contrasenya
     * @param userName el nom de l'usuari
     */
    void sendPasswordResetEmail(String to, String token, String userName);

    /**
     * Envia un correu electrònic de benvinguda a un nou usuari.
     *
     * @param to l'adreça de correu electrònic del destinatari
     * @param userName el nom de l'usuari
     */
    void sendWelcomeEmail(String to, String userName);

    /**
     * Envia un correu electrònic de verificació d'email a un usuari.
     *
     * @param to l'adreça de correu electrònic del destinatari
     * @param token el token de verificació d'email
     * @param userName el nom de l'usuari
     */
    void sendEmailVerification(String to, String token, String userName);

    /**
     * Envia un correu electrònic de verificació a un administrador d'empresa.
     * Aquest correu conté informació específica sobre la verificació de l'empresa.
     *
     * @param to l'adreça de correu electrònic del destinatari
     * @param token el token de verificació
     * @param userName el nom de l'administrador
     * @param companyName el nom de l'empresa a verificar
     */
    void sendCompanyAdminVerification(String to, String token, String userName, String companyName);
}