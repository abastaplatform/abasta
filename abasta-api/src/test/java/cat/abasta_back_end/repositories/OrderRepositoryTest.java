package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Order;
import cat.abasta_back_end.entities.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests per a OrderRepository.
 * <p>
 * Verifica que les operacions CRUD i la consulta personalitzada
 * findByUuid funcionen correctament amb la base de dades en memòria H2.
 * </p>
 *
 * Casos coberts:
 * <ul>
 *     <li>Guardar una comanda.</li>
 *     <li>Trobar per ID i UUID.</li>
 *     <li>Trobar totes les comandes.</li>
 *     <li>Eliminar una comanda.</li>
 * </ul>
 *
 * @author : Daniel Garcia
 * @version : 1.0
 */
@DataJpaTest
@DisplayName("OrderRepository Tests")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    /**
     * Test que comprova que es pot guardar una comanda i recuperar-la per ID.
     */
    @Test
    void saveAndFindById_success() {
        Supplier supplier = Supplier.builder().uuid("supplier-uuid").build();

        Order order = Order.builder()
                .uuid("order-uuid")
                .status(Order.OrderStatus.PENDING)
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .build();

        // Guardar
        Order saved = orderRepository.save(order);
        assertThat(saved.getId()).isNotNull();

        // Recuperar per ID
        Optional<Order> found = orderRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUuid()).isEqualTo("order-uuid");
    }

    /**
     * Test que comprova la consulta findByUuid.
     */
    @Test
    void findByUuid_success() {
        Supplier supplier = Supplier.builder().uuid("supplier-uuid").build();

        Order order = Order.builder()
                .uuid("order-uuid-123")
                .status(Order.OrderStatus.PENDING)
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);

        Optional<Order> found = orderRepository.findByUuid("order-uuid-123");
        assertThat(found).isPresent();
        assertThat(found.get().getUuid()).isEqualTo("order-uuid-123");
    }

    /**
     * Test que comprova que es poden recuperar totes les comandes.
     */
    @Test
    void findAllOrders_success() {
        Supplier supplier = Supplier.builder().uuid("supplier-uuid").build();

        Order order1 = Order.builder().uuid("o1").status(Order.OrderStatus.PENDING).supplier(supplier).createdAt(LocalDateTime.now()).build();
        Order order2 = Order.builder().uuid("o2").status(Order.OrderStatus.PENDING).supplier(supplier).createdAt(LocalDateTime.now()).build();

        orderRepository.saveAll(List.of(order1, order2));

        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(2);
    }

    /**
     * Test que comprova que es pot eliminar una comanda.
     */
    @Test
    void deleteOrder_success() {
        Supplier supplier = Supplier.builder().uuid("supplier-uuid").build();
        Order order = Order.builder().uuid("to-delete").status(Order.OrderStatus.PENDING).supplier(supplier).createdAt(LocalDateTime.now()).build();

        Order saved = orderRepository.save(order);
        orderRepository.delete(saved);

        Optional<Order> found = orderRepository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }
}