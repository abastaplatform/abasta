package cat.abasta_back_end.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testos unitaris per a la classe {@link OrderItem}.
 * <p>
 * Es comprova la correcta creació de l'item, la lògica de prePersist
 * (càlcul automàtic del subtotal) i el comportament del builder.
 * </p>
 *
 * @author : Daniel Garcia
 * @version : 1.0
 */
class OrderItemTest {

    @Test
    void testPrePersistCalculatesSubtotal() {
        OrderItem item = OrderItem.builder()
                .unitPrice(new BigDecimal("3.00"))
                .quantity(new BigDecimal("4"))
                .build();

        item.prePersist();

        assertEquals(new BigDecimal("12.00"), item.getSubtotal());
    }

    @Test
    void testPrePersistDoesNotOverrideSubtotalIfExists() {
        OrderItem item = OrderItem.builder()
                .unitPrice(new BigDecimal("3.00"))
                .quantity(new BigDecimal("4"))
                .subtotal(new BigDecimal("50.00"))
                .build();

        item.prePersist();

        assertEquals(new BigDecimal("50.00"), item.getSubtotal());
    }

    @Test
    void testBuilderCreatesObjectAndFields() {
        Product product = Product.builder()
                .id(200L)
                .uuid("prod-200")
                .name("Product Test")
                .price(new BigDecimal("2.50"))
                .createdAt(LocalDateTime.now())
                .build();

        Order order = Order.builder()
                .id(300L)
                .uuid("order-300")
                .status(Order.OrderStatus.PENDING)
                .supplier(Supplier.builder().id(5L).uuid("sup-5").build())
                .createdAt(LocalDateTime.now())
                .items(new java.util.ArrayList<>())
                .build();

        OrderItem item = OrderItem.builder()
                .id(1L)
                .uuid("abc")
                .product(product)
                .order(order)
                .quantity(new BigDecimal("5"))
                .unitPrice(new BigDecimal("2"))
                .subtotal(new BigDecimal("10"))
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(item);
        assertEquals("abc", item.getUuid());
        assertEquals(product, item.getProduct());
        assertEquals(order, item.getOrder());
        assertEquals(new BigDecimal("5"), item.getQuantity());
        assertEquals(new BigDecimal("2"), item.getUnitPrice());
        assertEquals(new BigDecimal("10"), item.getSubtotal());
    }
}