package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("OrderItemRepository Tests")
class OrderItemRepositoryTest {

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
     * Comprova la creació i recuperació d'OrderItem
     */
    @Test
    @DisplayName("Comprova la creació i recuperació d'OrderItem")
    void saveAndFindById_success() {

        companyRepository.save(testCompany);
        userRepository.save(testUser);
        supplierRepository.save(testSupplier);
        productRepository.save(testProduct);
        orderRepository.save(testOrder);

        OrderItem saved = orderItemRepository.save(testOrderItem);
        assertThat(saved.getId()).isNotNull();
        Optional<OrderItem> found = orderItemRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }

    /**
     * Comprova la recuperació del llistat d'OrderItem
     */
    @Test
    @DisplayName("Comprova la recuperació del llistat d'OrderItem")
    void findAllItems_success() {
        companyRepository.save(testCompany);
        userRepository.save(testUser);
        supplierRepository.save(testSupplier);
        productRepository.save(testProduct);
        orderRepository.save(testOrder);
        OrderItem saved = orderItemRepository.save(testOrderItem);
        OrderItem saved2 = orderItemRepository.save(testOrderItem);
        List<OrderItem> items = orderItemRepository.findAll();
        assertThat(items.size()).isGreaterThan(0);
        assertThat(items).isNotEmpty();
    }

    /**
     * Comprova l'eliminació d'OrderItem
     */
    @Test
    @DisplayName("Comprova l'eliminació d'OrderItem")
    void deleteItem_success() {
        companyRepository.save(testCompany);
        userRepository.save(testUser);
        supplierRepository.save(testSupplier);
        productRepository.save(testProduct);
        orderRepository.save(testOrder);
        OrderItem saved = orderItemRepository.save(testOrderItem);
        orderItemRepository.delete(saved);
        Optional<OrderItem> found = orderItemRepository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }

}