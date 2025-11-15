package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.entities.*;
import cat.abasta_back_end.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import cat.abasta_back_end.entities.Order.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris per OrderServiceImpl.
 * <p>
 * Verifica la lògica de negoci del servei de comandes.
 * Inclou proves d'èxit i de gestió d'errors per:
 * <ul>
 *     <li>Creació de comandes</li>
 *     <li>Enviament de comandes</li>
 * </ul>
 * Comprova la interacció amb els repositoris mockejats i la generació de subtotal dels items.
 * </p>
 *
 * Casos coberts:
 * <ul>
 *     <li>createOrder amb èxit</li>
 *     <li>createOrder amb proveïdor no existent</li>
 *     <li>createOrder amb producte no existent</li>
 *     <li>sendOrder amb èxit</li>
 *     <li>sendOrder quan la comanda no existeix</li>
 *     <li>sendOrder quan la comanda no està en estat PENDING</li>
 * </ul>
 *
 * @author Daniel Garcia
 * @version 1.1
 */
@SpringBootTest
@DisplayName("OrderServiceImpl Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Supplier supplier;
    private Product product;
    private Order order;

    /**
     * Inicialitza dades bàsiques abans de cada test.
     * <p>
     * S'instancien proveïdor, producte i comanda mockejats
     * que seran reutilitzats pels tests de creació i enviament de comandes.
     * </p>
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        supplier = Supplier.builder()
                .id(1L)
                .uuid("supplier-uuid")
                .build();

        product = Product.builder()
                .id(1L)
                .uuid("product-uuid")
                .price(new BigDecimal("10.00"))
                .build();

        order = Order.builder()
                .id(1L)
                .uuid("order-uuid")
                .status(OrderStatus.PENDING)
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
    }

    /**
     * Test que comprova la creació d'una comanda amb èxit.
     */
    @Test
    void createOrder_success() {
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .productUuid(product.getUuid())
                .quantity(new BigDecimal("2"))
                .build();

        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .name("Test Order")
                .supplierUuid(supplier.getUuid())
                .items(List.of(itemDTO))
                .build();

        when(supplierRepository.findByUuid(supplier.getUuid()))
                .thenReturn(Optional.of(supplier));
        when(productRepository.findByUuid(product.getUuid()))
                .thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        OrderResponseDTO response = orderService.createOrder(orderRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Order");
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getSubtotal())
                .isEqualByComparingTo(new BigDecimal("20.00"));
    }

    /**
     * Test que comprova excepció quan el proveïdor no existeix.
     */
    @Test
    void createOrder_supplierNotFound_throws() {
        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .name("Test")
                .supplierUuid("bad-uuid")
                .items(Collections.emptyList())
                .build();

        when(supplierRepository.findByUuid("bad-uuid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(orderRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Supplier not found");
    }

    /**
     * Test que comprova excepció quan el producte no existeix.
     */
    @Test
    void createOrder_productNotFound_throws() {
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .productUuid("bad-product")
                .quantity(new BigDecimal("1"))
                .build();

        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .name("Test")
                .supplierUuid(supplier.getUuid())
                .items(List.of(itemDTO))
                .build();

        when(supplierRepository.findByUuid(supplier.getUuid()))
                .thenReturn(Optional.of(supplier));
        when(productRepository.findByUuid("bad-product"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(orderRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
    }

    /**
     * Test que comprova l'enviament d'una comanda amb èxit.
     * <p>
     * Simula una comanda en estat PENDING i comprova que després
     * d'enviar-la, l'estat passa a SENT.
     * </p>
     */
    @Test
    void sendOrder_success() {
        when(orderRepository.findByUuid("order-uuid")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        OrderResponseDTO response = orderService.sendOrder("order-uuid");

        assertThat(response).isNotNull();
        assertThat(response.getUuid()).isEqualTo("order-uuid");
        assertThat(response.getStatus()).isEqualTo("SENT");
    }

    /**
     * Test que comprova llançament d'excepció si la comanda no existeix.
     */
    @Test
    void sendOrder_notFound_throws() {
        when(orderRepository.findByUuid("bad-uuid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.sendOrder("bad-uuid"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");
    }

    /**
     * Test que comprova llançament d'excepció si la comanda no està en estat PENDING.
     */
    @Test
    void sendOrder_invalidStatus_throws() {
        order.setStatus(OrderStatus.SENT);
        when(orderRepository.findByUuid("order-uuid")).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.sendOrder("order-uuid"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Order status must be PENDING");
    }
}
