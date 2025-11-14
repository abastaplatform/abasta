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

    /**
     * Envia un correu electrònic amb els detalls d'una comanda a un proveïdor.
     *
     * @param to l'adreça de correu electrònic del proveïdor
     * @param supplierName el nom del proveïdor
     * @param orderName el nom de la comanda
     * @param orderDetails els detalls de la comanda en format HTML
     * @param totalAmount l'import total de la comanda
     * @param deliveryDate la data d'entrega prevista (pot ser null)
     * @param notes notes addicionals de la comanda (pot ser null)
     */
    void sendOrderNotification(String to, String supplierName, String orderName,
                               String orderDetails, String totalAmount,
                               String deliveryDate, String notes);
}