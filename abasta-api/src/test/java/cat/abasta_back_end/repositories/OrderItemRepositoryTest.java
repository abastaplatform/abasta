package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests per a OrderItemRepository.
 * <p>
 * Verifica que les operacions CRUD funcionen correctament amb la base de dades en memòria H2.
 * </p>
 *
 * Casos coberts:
 * <ul>
 *     <li>Guardar un item de comanda.</li>
 *     <li>Trobar per ID.</li>
 *     <li>Trobar tots els items.</li>
 *     <li>Eliminar un item.</li>
 * </ul>
 *
 * @author : Daniel Garcia
 * @version : 1.0
 */
@DataJpaTest
@DisplayName("OrderItemRepository Tests")
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    void saveAndFindById_success() {
        OrderItem item = OrderItem.builder()
                .quantity(new BigDecimal("2"))
                .unitPrice(new BigDecimal("10.00"))
                .build();

        OrderItem saved = orderItemRepository.save(item);
        assertThat(saved.getId()).isNotNull();

        Optional<OrderItem> found = orderItemRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getQuantity()).isEqualByComparingTo(new BigDecimal("2"));
    }

    @Test
    void findAllItems_success() {
        OrderItem item1 = OrderItem.builder().quantity(new BigDecimal("1")).unitPrice(new BigDecimal("5.00")).build();
        OrderItem item2 = OrderItem.builder().quantity(new BigDecimal("3")).unitPrice(new BigDecimal("7.00")).build();

        orderItemRepository.saveAll(List.of(item1, item2));

        List<OrderItem> items = orderItemRepository.findAll();
        assertThat(items).hasSize(2);
    }

    @Test
    void deleteItem_success() {
        OrderItem item = OrderItem.builder().quantity(new BigDecimal("2")).unitPrice(new BigDecimal("10.00")).build();

        OrderItem saved = orderItemRepository.save(item);
        orderItemRepository.delete(saved);

        Optional<OrderItem> found = orderItemRepository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }
}
