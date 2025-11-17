package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.entities.*;
import cat.abasta_back_end.entities.Order.OrderStatus;
import cat.abasta_back_end.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests per NotificationServiceImpl.
 * Comprova el funcionament del servei de notificacions de comandes,
 * especialment l'enviament d'emails als proveïdors.
 *
 * @author Enrique Pérez
 * @version 2.0 - Actualitzat amb nova signatura
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationServiceImpl Tests")
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private EmailService emailService;

    private Supplier supplier;
    private Order order;

    @BeforeEach
    void setUp() {
        // Configurar empresa
        Company company = Company.builder()
                .name("Restaurant El Bon Gust")
                .address("Carrer Major 123")
                .phone("666777888")
                .email("info@elbongust.com")
                .build();

        // Configurar proveïdor
        supplier = Supplier.builder()
                .name("Proveïdor ABC")
                .contactName("Joan Garcia")
                .email("joan@proveidorabc.com")
                .phone("666111222")
                .build();

        // Configurar producte
        Product product = Product.builder()
                .name("Tomàquets Ecològics")
                .unit("kg")
                .volume(new BigDecimal("2.5"))
                .build();

        // Configurar item de comanda
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(new BigDecimal("10"))
                .notes("Preferiblement madurs")
                .build();

        // Configurar comanda
        order = Order.builder()
                .uuid("order-uuid-123")
                .company(company)
                .supplier(supplier)
                .name("Comanda Setmanal #42")
                .totalAmount(new BigDecimal("123.45"))
                .status(OrderStatus.PENDING)
                .items(new java.util.ArrayList<>(List.of(orderItem)))  // ArrayList mutable
                .notes("Entrega abans de les 9h")
                .build();
    }

    @Test
    @DisplayName("Hauria de llançar excepció quan el proveïdor no té email")
    void testSendOrderNotification_WhenSupplierHasNoEmail_ShouldThrowException() {
        // Given
        supplier.setEmail(null);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificationService.sendOrderNotification(order));

        assertEquals("El proveïdor no té email configurat", ex.getMessage());
        verifyNoInteractions(emailService);
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    @DisplayName("Hauria de llançar excepció quan l'email està buit")
    void testSendOrderNotification_WhenSupplierHasEmptyEmail_ShouldThrowException() {
        // Given
        supplier.setEmail("");

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificationService.sendOrderNotification(order));

        assertEquals("El proveïdor no té email configurat", ex.getMessage());
        verifyNoInteractions(emailService);
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    @DisplayName("Hauria d'enviar l'email correctament i actualitzar l'estat a SENT")
    void testSendOrderNotification_ShouldSendEmailAndMarkAsSent() {
        // When
        notificationService.sendOrderNotification(order);

        // Then
        verify(emailService, times(1))
                .sendOrderNotification(
                        eq("joan@proveidorabc.com"),        // to (email del proveïdor)
                        eq("Joan Garcia"),                   // contactName
                        eq("Restaurant El Bon Gust"),        // companyName
                        eq("Carrer Major 123"),              // companyAddress
                        eq("666777888"),                     // companyPhone
                        eq("Comanda Setmanal #42"),          // orderName
                        anyString(),                         // orderDetails (HTML generat)
                        eq("Entrega abans de les 9h")        // notes
                );

        assertEquals(OrderStatus.SENT, order.getStatus());
    }

    @Test
    @DisplayName("Hauria de passar HTML amb els detalls dels productes")
    void testSendOrderNotification_ShouldIncludeProductDetailsInHtml() {
        // When
        notificationService.sendOrderNotification(order);

        // Then
        verify(emailService).sendOrderNotification(
                anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(),
                argThat(html ->
                        html.contains("Tomàquets Ecològics") &&
                                html.contains("10") &&
                                html.contains("2.5") &&
                                html.contains("kg") &&
                                html.contains("Preferiblement madurs") &&
                                html.contains("<table")
                ),
                anyString()
        );

        assertEquals(OrderStatus.SENT, order.getStatus());
    }

    @Test
    @DisplayName("Hauria de gestionar correctament una comanda sense notes")
    void testSendOrderNotification_WithoutNotes_ShouldSendNull() {
        // Given
        order.setNotes(null);

        // When
        notificationService.sendOrderNotification(order);

        // Then
        verify(emailService, times(1))
                .sendOrderNotification(
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString(),
                        isNull()  // notes hauria de ser null
                );

        assertEquals(OrderStatus.SENT, order.getStatus());
    }

    @Test
    @DisplayName("Hauria de propagar l'excepció quan emailService falla")
    void testSendOrderNotification_WhenEmailServiceFails_ShouldWrapException() {
        // Given
        doThrow(new RuntimeException("Error de connexió SMTP"))
                .when(emailService)
                .sendOrderNotification(
                        anyString(), anyString(), anyString(), anyString(),
                        anyString(), anyString(), anyString(), anyString()
                );

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificationService.sendOrderNotification(order));

        assertTrue(ex.getMessage().contains("Error en enviar la notificació"));
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    @DisplayName("Hauria de generar HTML vàlid amb múltiples productes")
    void testSendOrderNotification_WithMultipleProducts_ShouldGenerateValidHtml() {
        // Given
        Product product2 = Product.builder()
                .name("Enciams")
                .unit("unitats")
                .volume(new BigDecimal("1"))
                .build();

        OrderItem item2 = OrderItem.builder()
                .product(product2)
                .quantity(new BigDecimal("5"))
                .notes(null)
                .build();

        order.getItems().add(item2);

        // When
        notificationService.sendOrderNotification(order);

        // Then
        verify(emailService).sendOrderNotification(
                anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(),
                argThat(html ->
                        html.contains("Tomàquets Ecològics") &&
                                html.contains("Enciams") &&
                                html.contains("10") &&
                                html.contains("5")
                ),
                anyString()
        );
    }

    @Test
    @DisplayName("Hauria de gestionar productes sense notes addicionals")
    void testSendOrderNotification_ProductWithoutNotes_ShouldNotIncludeNotesLine() {
        // Given
        order.getItems().getFirst().setNotes(null);

        // When
        notificationService.sendOrderNotification(order);

        // Then
        verify(emailService).sendOrderNotification(
                anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(),
                argThat(html ->
                        html.contains("Tomàquets Ecològics") &&
                                !html.contains("Notes:")  // No hauria de contenir la línia de notes
                ),
                anyString()
        );
    }

    @Test
    @DisplayName("Hauria de gestionar productes desconeguts graciosament")
    void testSendOrderNotification_WithUnknownProduct_ShouldHandleGracefully() {
        // Given
        order.getItems().getFirst().setProduct(null);

        // When
        notificationService.sendOrderNotification(order);

        // Then
        verify(emailService).sendOrderNotification(
                anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(),
                argThat(html -> html.contains("Producte desconegut")),
                anyString()
        );

        assertEquals(OrderStatus.SENT, order.getStatus());
    }
}