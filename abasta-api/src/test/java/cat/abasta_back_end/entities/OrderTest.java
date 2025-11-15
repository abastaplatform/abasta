package cat.abasta_back_end.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testos unitaris per a la classe {@link Order}.
 * <p>
 * Es valida la creació de l'entitat, la consistència dels camps,
 * la gestió dels items i la correcta assignació dels estats.
 * </p>
 *
 * @author : Daniel Garcia
 * @version : 1.0
 */
class OrderTest {

    private Supplier supplier;

    @BeforeEach
    void setup() {
        supplier = Supplier.builder()
                .id(1L)
                .uuid("supplier-uuid")
                .name("Proveïdor Test")
                .email("test@supplier.com")
                .phone("666777888")
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Comprova que un objecte {@link Order} es crea correctament
     * amb tots els seus camps obligatoris.
     */
    @Test
    void testCrearOrderCorrectament() {
        Order order = Order.builder()
                .id(1L)
                .uuid("order-uuid")
                .status(Order.OrderStatus.PENDING)
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        assertNotNull(order);
        assertEquals("order-uuid", order.getUuid());
        assertEquals(Order.OrderStatus.PENDING, order.getStatus());
        assertEquals(supplier, order.getSupplier());
        assertTrue(order.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
    }

    /**
     * Verifica que els canvis d'estat de la comanda funcionin correctament.
     */
    @Test
    void testCanviarEstat() {
        Order order = Order.builder()
                .id(1L)
                .uuid("order-uuid")
                .status(Order.OrderStatus.PENDING)
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        order.setStatus(Order.OrderStatus.CONFIRMED);
        assertEquals(Order.OrderStatus.CONFIRMED, order.getStatus());

        order.setStatus(Order.OrderStatus.CANCELLED);
        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
    }

    /**
     * Verifica que es puguin afegir items a la comanda correctament,
     * que s'assigni la relació i que es recalculi el total.
     */
    @Test
    void testAfegirItems() {
        Order order = Order.builder()
                .id(1L)
                .uuid("order-uuid")
                .status(Order.OrderStatus.PENDING)
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        // Crear producte per assignar a l'item
        Product product = Product.builder()
                .id(100L)
                .uuid("prod-uuid")
                .name("Producte A")
                .price(new BigDecimal("5.50"))
                .createdAt(LocalDateTime.now())
                .build();

        OrderItem item = OrderItem.builder()
                .id(10L)
                .uuid("item-uuid")
                .product(product)
                .quantity(new BigDecimal("2"))
                .unitPrice(new BigDecimal("5.50"))
                .subtotal(new BigDecimal("11.00"))
                .createdAt(LocalDateTime.now())
                .build();

        // Usar el mètode addItem per activar la lògica de relació i recàlcul
        order.addItem(item);

        assertEquals(1, order.getItems().size());
        assertEquals(order, item.getOrder());                      // relació assignada
        assertEquals(new BigDecimal("11.00"), order.getTotalAmount()); // total recalculat
        assertEquals("Producte A", order.getItems().get(0).getProduct().getName());
    }

    /**
     * Verifica que removeItem elimina l'item i recalcula el total.
     */
    @Test
    void testRemoveItem() {
        Order order = Order.builder()
                .id(1L)
                .uuid("order-uuid")
                .status(Order.OrderStatus.PENDING)
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        Product product1 = Product.builder().id(101L).uuid("p1").name("P1").price(new BigDecimal("10.00")).createdAt(LocalDateTime.now()).build();
        Product product2 = Product.builder().id(102L).uuid("p2").name("P2").price(new BigDecimal("5.00")).createdAt(LocalDateTime.now()).build();

        OrderItem item1 = OrderItem.builder()
                .id(11L)
                .uuid("item-1")
                .product(product1)
                .quantity(new BigDecimal("2"))
                .unitPrice(new BigDecimal("10.00"))
                .subtotal(new BigDecimal("20.00"))
                .createdAt(LocalDateTime.now())
                .build();

        OrderItem item2 = OrderItem.builder()
                .id(12L)
                .uuid("item-2")
                .product(product2)
                .quantity(new BigDecimal("3"))
                .unitPrice(new BigDecimal("5.00"))
                .subtotal(new BigDecimal("15.00"))
                .createdAt(LocalDateTime.now())
                .build();

        order.addItem(item1);
        order.addItem(item2);

        // Ara eliminar item1
        order.removeItem(item1);

        assertEquals(1, order.getItems().size());
        assertNull(item1.getOrder());
        assertEquals(new BigDecimal("15.00"), order.getTotalAmount());
    }

    /**
     * Verifica que preUpdate actualitza updatedAt.
     */
    @Test
    void testPreUpdateUpdatesTimestamp() {
        Order order = Order.builder()
                .id(1L)
                .uuid("order-uuid")
                .status(Order.OrderStatus.PENDING)
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        LocalDateTime before = order.getUpdatedAt();
        order.preUpdate();
        assertTrue(order.getUpdatedAt().isAfter(before));
    }

    /**
     * Comprova l'enumeració d'estats.
     */
    @Test
    void testOrderStatusEnum() {
        Order.OrderStatus status = Order.OrderStatus.valueOf("PENDING");
        assertEquals(Order.OrderStatus.PENDING, status);
    }
}