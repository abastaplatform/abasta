package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("OrderRepository Tests")
class OrderRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    // Objectes
    private Company testCompany;
    private User testUser;
    private Supplier testSupplier;
    private Product testProduct;
    private Order testOrder;
    private OrderItem testOrderItem;

    /**
     * Inicialització d'entitats
     */
    @BeforeEach
    void setup() {

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
     * Comprova la creció i recuperació d'Order
     */
    @Test
    @DisplayName("Comprova la creció i recuperació d'Order")
    void saveAndFindById_success() {

        companyRepository.save(testCompany);
        userRepository.save(testUser);
        supplierRepository.save(testSupplier);
        orderRepository.save(testOrder);

        assertThat(testOrder.getId()).isNotNull();
        Optional<Order> found = orderRepository.findById(testOrder.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUuid()).isEqualTo("test-order-uuid");
    }

    /**
     * Comprova cerca per Uuid
     */
    @Test
    @DisplayName("Comprova la cerca per Uuid")
    void findByUuid_success() {

        companyRepository.save(testCompany);
        userRepository.save(testUser);
        supplierRepository.save(testSupplier);
        orderRepository.save(testOrder);

        Optional<Order> found = orderRepository.findByUuid("test-order-uuid");
        assertThat(found).isPresent();
        assertThat(found.get().getUuid()).isEqualTo("test-order-uuid");
    }

    /**
     * Comprova la recuperació de totes Orders
     */
    @Test
    @DisplayName("Comprova la recuperació de totes Orders")
    void findAllOrders_success() {

        companyRepository.save(testCompany);
        userRepository.save(testUser);
        supplierRepository.save(testSupplier);
        orderRepository.save(testOrder);

        List<Order> orders = orderRepository.findAll();
        assertThat(orders.size()).isGreaterThan(0);
        assertThat(orders).isNotEmpty();
    }

    /**
     * Comprova l'eliminació d'una Order
     */
    @Test
    @DisplayName("Comprova l'eliminació d'una Order")
    void deleteOrder_success() {
        companyRepository.save(testCompany);
        userRepository.save(testUser);
        supplierRepository.save(testSupplier);
        orderRepository.save(testOrder);
        orderRepository.delete(testOrder);

        Optional<Order> found = orderRepository.findById(testOrder.getId());
        assertThat(found).isNotPresent();
    }


    @Test
    @DisplayName("Comprova findByUuid amb UUID inexistant")
    void findByUuid_notFound() {
        Optional<Order> found = orderRepository.findByUuid("nonexistent-uuid");
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Comprova guardar Order amb OrderItems")
    void saveOrderWithItems_success() {
        companyRepository.save(testCompany);
        userRepository.save(testUser);
        supplierRepository.save(testSupplier);
        productRepository.save(testProduct);

        testOrder.getItems().add(testOrderItem);

        orderRepository.save(testOrder);
        orderItemRepository.save(testOrderItem);

        Optional<Order> found = orderRepository.findByUuid(testOrder.getUuid());
        assertThat(found).isPresent();
        assertThat(found.get().getItems()).hasSize(1);
        assertThat(found.get().getItems().get(0).getProduct().getUuid()).isEqualTo("test-product-uuid");
    }

    @Test
    @DisplayName("Comprova findAll amb Pageable i Sort")
    void findAllWithPageable_success() {
        companyRepository.save(testCompany);
        userRepository.save(testUser);
        supplierRepository.save(testSupplier);
        orderRepository.save(testOrder);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Order> page = orderRepository.findAll(pageable);

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isGreaterThan(0);
        assertThat(page.getContent().getLast().getName()).isEqualTo("Test Comanda 1");
    }

    @Test
    @DisplayName("Comprova findAll amb Specification")
    void findAllWithSpecification_success() {
        companyRepository.save(testCompany);
        userRepository.save(testUser);
        supplierRepository.save(testSupplier);
        orderRepository.save(testOrder);

        Specification<Order> spec = (root, query, cb) ->
                cb.equal(root.get("uuid"), "test-order-uuid");

        List<Order> result = orderRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUuid()).isEqualTo("test-order-uuid");
    }

    @Test
    @DisplayName("Comprova actualització d'una Order")
    void updateOrder_success() {
        companyRepository.save(testCompany);
        userRepository.save(testUser);
        supplierRepository.save(testSupplier);
        orderRepository.save(testOrder);

        testOrder.setName("Nou Nom");
        orderRepository.save(testOrder);

        Optional<Order> found = orderRepository.findByUuid("test-order-uuid");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Nou Nom");
    }



}