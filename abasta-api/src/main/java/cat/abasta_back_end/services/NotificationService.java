package cat.abasta_back_end.services;

import cat.abasta_back_end.entities.Order;

/**
 * Interfície de servei per gestionar l'enviament de notificacions de comandes.
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 */
public interface NotificationService {

    /**
     * Envia una notificació de comanda al proveïdor per email.
     *
     * <p>Aquest mètode determina el canal d'email per la notificació</p>
     *
     * @param order la comanda a notificar amb tota la informació necessària
     */
    void sendOrderNotification(Order order);
}
