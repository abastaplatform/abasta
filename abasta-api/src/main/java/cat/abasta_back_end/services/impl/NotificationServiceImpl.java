package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.entities.Order;
import cat.abasta_back_end.entities.OrderItem;
import cat.abasta_back_end.entities.Supplier;
import cat.abasta_back_end.services.EmailService;
import cat.abasta_back_end.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Implementació del servei de notificacions de comandes.
 *
 * <p>Aquest servei s'encarrega d'enviar notificacions per correu electrònic
 * quan es crea una nova comanda. Construeix els missatges formatats amb els
 * detalls de la comanda i delega l'enviament al servei d'email.</p>
 *
 * <p>Funcionalitats principals:
 * <ul>
 *   <li>Generació de missatges HTML per a correus electrònics</li>
 *   <li>Formatació d'imports monetaris i dates</li>
 *   <li>Gestió d'errors amb logging detallat</li>
 * </ul>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @see NotificationService
 * @see EmailService
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;

    // Constants per formatació
    private static final Locale CATALAN_LOCALE = Locale.of("ca", "ES");
    private static final String DATE_PATTERN = "d 'de' MMMM 'de' yyyy";

    /**
     * {@inheritDoc}
     *
     * <p>Aquest mètode construeix els detalls de la comanda en format HTML
     * i envia la notificació per correu electrònic al proveïdor.</p>
     *
     * <p>Validacions realitzades:
     * <ul>
     *   <li>Comprova que el proveïdor té email configurat</li>
     *   <li>Registra advertències si falta informació de contacte</li>
     * </ul>
     * </p>
     *
     * @throws RuntimeException si es produeix un error en l'enviament de notificacions
     */
    @Override
    public void sendOrderNotification(Order order) {
        log.info("Enviant notificació de comanda {} per email", order.getUuid());

        Supplier supplier = order.getSupplier();

        // Validar que el proveïdor té email
        if (supplier.getEmail() == null || supplier.getEmail().isEmpty()) {
            log.error("El proveïdor {} no té email configurat. No es pot enviar la comanda.",
                    supplier.getName());
            throw new RuntimeException("El proveïdor no té email configurat");
        }

        // Construir els detalls de la comanda
        String orderDetailsHtml = buildOrderDetailsHtml(order);
        String totalAmount = formatCurrency(order.getTotalAmount());
        String deliveryDate = order.getDeliveryDate() != null ?
                formatDate(order.getDeliveryDate()) : null;

        try {
            // Enviar email
            emailService.sendOrderNotification(
                    supplier.getEmail(),
                    supplier.getName(),
                    order.getCompany().getName(),
                    order.getCompany().getAddress(),
                    order.getCompany().getPhone(),
                    order.getName(),
                    orderDetailsHtml,
                    totalAmount,
                    deliveryDate,
                    order.getNotes()
            );

            // Actualitzar l'estat de la comanda a SENT després d'enviar
            order.setStatus(Order.OrderStatus.SENT);
            log.info("Notificació de comanda {} enviada correctament per email a {}",
                    order.getUuid(), supplier.getEmail());

        } catch (Exception e) {
            log.error("Error en enviar notificació de comanda {}: {}",
                    order.getUuid(), e.getMessage(), e);
            throw new RuntimeException("Error en enviar la notificació de la comanda", e);
        }
    }

    /**
     * Construeix els detalls de la comanda en format HTML per al correu electrònic.
     *
     * <p>Genera una taula HTML amb els productes de la comanda incloent:
     * <ul>
     *   <li>Nom del producte</li>
     *   <li>Quantitat i unitat</li>
     *   <li>Preu unitari</li>
     *   <li>Subtotal</li>
     *   <li>Notes (si n'hi ha)</li>
     * </ul>
     * </p>
     *
     * @param order la comanda amb els items
     * @return HTML amb la taula de productes
     */
    private String buildOrderDetailsHtml(Order order) {
        StringBuilder html = new StringBuilder();
        html.append("""
                <table width="100%" cellpadding="0" cellspacing="0" style="border: 1px solid #e0e0e0; border-radius: 6px; overflow: hidden;">
                    <thead>
                        <tr style="background-color: #667eea;">
                            <th style="padding: 12px; text-align: left; color: #ffffff; font-size: 14px;">Producte</th>
                            <th style="padding: 12px; text-align: center; color: #ffffff; font-size: 14px;">Quantitat</th>
                            <th style="padding: 12px; text-align: right; color: #ffffff; font-size: 14px;">Preu Unit.</th>
                            <th style="padding: 12px; text-align: right; color: #ffffff; font-size: 14px;">Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                """);

        for (OrderItem item : order.getItems()) {
            String productName = item.getProduct() != null ?
                    item.getProduct().getName() : "Producte desconegut";
            String unit = item.getProduct() != null && item.getProduct().getUnit() != null ?
                    item.getProduct().getUnit() : "";
            String notes = item.getNotes() != null && !item.getNotes().isEmpty() ?
                    "<br><span style=\"font-size: 12px; color: #999; font-style: italic;\">Notes: " +
                            item.getNotes() + "</span>" : "";

            html.append(String.format("""
                            <tr style="border-bottom: 1px solid #eeeeee;">
                                <td style="padding: 15px; color: #333333;">
                                    <strong>%s</strong>%s
                                </td>
                                <td style="padding: 15px; text-align: center; color: #666666;">%s %s</td>
                                <td style="padding: 15px; text-align: right; color: #666666;">%s</td>
                                <td style="padding: 15px; text-align: right; color: #667eea; font-weight: bold;">%s</td>
                            </tr>
                            """,
                    productName,
                    notes,
                    item.getQuantity(),
                    unit,
                    formatCurrency(item.getUnitPrice()),
                    formatCurrency(item.getSubtotal())
            ));
        }

        html.append("""
                    </tbody>
                </table>
                """);

        return html.toString();
    }

    /**
     * Formata un import monetari amb el símbol d'euro.
     *
     * @param amount l'import a formatar
     * @return l'import formatat amb dues decimals i el símbol €
     */
    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) return "0,00 €";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(CATALAN_LOCALE);
        return formatter.format(amount);
    }

    /**
     * Formata una data en format llegible català.
     *
     * @param date la data a formatar
     * @return la data formatada (ex: "15 de novembre de 2025")
     */
    private String formatDate(java.time.LocalDate date) {
        if (date == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN, CATALAN_LOCALE);
        return date.format(formatter);
    }
}