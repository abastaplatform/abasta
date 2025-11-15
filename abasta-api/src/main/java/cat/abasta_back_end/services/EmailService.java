package cat.abasta_back_end.services;

/**
 * Interfície que defineix les operacions per enviar correus electrònics.
 *
 * <p>Aquesta interfície estableix el contracte per a l'enviament de diferents
 * tipus de correus electrònics dins del sistema Abasta, incloent-hi:
 * <ul>
 *   <li>Recuperació de contrasenyes</li>
 *   <li>Verificació de comptes d'usuari</li>
 *   <li>Verificació d'empreses i administradors</li>
 *   <li>Notificacions de comandes</li>
 * </ul>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.1
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
    void sendOrderNotification(String to, String supplierName, String companyName,
                               String companyAddress, String companyPhone,
                               String orderName, String orderDetails, String totalAmount,
                               String deliveryDate, String notes);
}