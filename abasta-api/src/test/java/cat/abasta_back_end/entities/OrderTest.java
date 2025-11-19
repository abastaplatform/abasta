package cat.abasta_back_end.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaris per a la classe {@link Order}.
 * <p>
 * Es valida la creació de l'entitat, la consistència dels camps,
 * la gestió dels items i la correcta assignació dels estats.
 * </p>
 *
 * @author : Daniel Garcia
 * @version : 1.0
 */
class OrderTest {

    // Objectes
    private Company testCompany;
    private User testUser;
    private Supplier testSupplier;
    private Product testProduct;
    private Order testOrder;
    private OrderItem testOrderItem;

    /**
     * Inicialitza instàncies de company, user, supplier, product, order i orderitem abans de cada test.
     */
    @BeforeEach
    public void setUp() {

        // Creació de la companyia
        testCompany = Company.builder().uuid("test-company-uuid").name("Test Companyia 1").taxId("55555555K").email("company1@test.com").phone("666666666").address("Carrer Barcelona").city("Barcelona").postalCode("08080").status(Company.CompanyStatus.ACTIVE).build();

        // Creació de l'usuari
        testUser = User.builder().uuid("test-user-uuid").company(testCompany).email("user@test.com").password("pass").firstName("User1").lastName("cognoms").role(User.UserRole.ADMIN).phone("777777777").isActive(true).emailVerified(true).build();

        // Creació del proveedor
        testSupplier = Supplier.builder().uuid("test-supplier-uuid").company(testCompany).name("Test supplier 1").contactName("Antonio").email("user@test.com").phone("444444444").address("Carrer Mallorca").notes("Treball 24/7").isActive(true).build();

        // Creació del producte de prova
        testProduct = Product.builder().uuid("test-product-uuid").supplier(testSupplier).category("Categoria").name("Test Producte 1").description("Descripció Producte 1").price(BigDecimal.valueOf(0.5)).volume(BigDecimal.valueOf(33)).unit("cl").imageUrl("/img/productes/producte1.jpg").isActive(true).build();

        // Creació de la comanda
        testOrder = Order.builder().uuid("test-order-uuid").company(testCompany).supplier(testSupplier).user(testUser).name("Test Comanda 1").status(Order.OrderStatus.PENDING).totalAmount(BigDecimal.valueOf(0)).notes("Test nota comanda 1").deliveryDate(LocalDate.now()).items(new ArrayList<>()).build();

        // Creació d'un order item
        testOrderItem = OrderItem.builder().uuid("test-orderitem-uuid").order(testOrder).product(testProduct).quantity(BigDecimal.valueOf(5)).unitPrice(BigDecimal.valueOf(0.5)).subtotal(BigDecimal.valueOf(0.5).multiply(BigDecimal.valueOf(5))).notes("Test Notes orderitem").createdAt(LocalDateTime.now()).build();

    }

    /**
     * Comprova la creació correcta de l'objecte Order
     */
    @Test
    @DisplayName("Comprova Creació d'Order")
    void testBuilder() {
        assertNotNull(testOrder);
        assertEquals("test-order-uuid", testOrder.getUuid());
        assertEquals(Order.OrderStatus.PENDING, testOrder.getStatus());
        assertEquals(testSupplier, testOrder.getSupplier());
        assertTrue(testOrder.getItems().isEmpty());
        assertEquals("Test Comanda 1",testOrder.getName());
    }

    /**
     * Comprova els canvis d'estat
     */
    @Test
    @DisplayName("Comprova canvis d'estat d'Order")
    void canviEstatOrder(){
        testOrder.setStatus(Order.OrderStatus.CONFIRMED);
        assertEquals(Order.OrderStatus.CONFIRMED, testOrder.getStatus());
        testOrder.setStatus(Order.OrderStatus.CONFIRMED);
        assertEquals(Order.OrderStatus.CONFIRMED, testOrder.getStatus());
    }

    /**
     * Comprova afegir items a Order
     */
    @Test
    @DisplayName("Comprova afegir items a Order")
    void afegitItemsOrder(){
        testOrder.addItem(testOrderItem);
        assertEquals(1, testOrder.getItems().size());
        assertEquals(testOrder, testOrderItem.getOrder());
        assertEquals(new BigDecimal("2.5"), testOrder.getTotalAmount());
        assertEquals("Test Producte 1", testOrder.getItems().get(0).getProduct().getName());
    }

    /**
     * Comprova eliminar items a Order
     */
    @Test
    @DisplayName("Comprova eliminar items a Order")
    void eliminarItemsOrder(){
        testOrder.removeItem(testOrderItem);
        assertEquals(0, testOrder.getItems().size());
        assertNull(testOrderItem.getOrder());
        assertEquals(new BigDecimal("0"), testOrder.getTotalAmount());
    }

}