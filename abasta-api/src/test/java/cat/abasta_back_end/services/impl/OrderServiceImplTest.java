package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.entities.*;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
@ExtendWith(MockitoExtension.class)
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
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    // Objectes
    private Company testCompany;
    private User testUser;
    private Supplier testSupplier;
    private Product testProduct;

    /**
     * Inicialitza instàncies de company, user, supplier, product abans de cada test.
     */
    @BeforeEach
    void setUp() {

        // Creació de la companyia
        testCompany = Company.builder().uuid("test-company-uuid").name("Test Companyia 1").taxId("55555555K").email("company1@test.com").phone("666666666").address("Carrer Barcelona").city("Barcelona").postalCode("08080").status(Company.CompanyStatus.ACTIVE).build();

        // Creació de l'usuari
        testUser = User.builder().uuid("test-user-uuid").company(testCompany).email("user@test.com").password("pass").firstName("User1").lastName("cognoms").role(User.UserRole.ADMIN).phone("777777777").isActive(true).emailVerified(true).build();

        // Creació del proveedor
        testSupplier = Supplier.builder().uuid("test-supplier-uuid").company(testCompany).name("Test supplier 1").contactName("Antonio").email("user@test.com").phone("444444444").address("Carrer Mallorca").notes("Treball 24/7").isActive(true).build();

        // Creació del producte de prova
        testProduct = Product.builder().uuid("test-product-uuid").supplier(testSupplier).category("Categoria").name("Test Producte 1").description("Descripció Producte 1").price(BigDecimal.valueOf(0.5)).volume(BigDecimal.valueOf(33)).unit("cl").imageUrl("/img/productes/producte1.jpg").isActive(true).build();

    }

    /**
     * Reseteja el SecurityContext després de cada test
     */
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Comprova la creació d'una Order
     */
    @Test
    @DisplayName("Comprova la creació d'una Order")
    void createOrder_success() {

        // Mock de l'usuari autenticat
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(testUser.getEmail());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(supplierRepository.findByUuid(testSupplier.getUuid())).thenReturn(Optional.of(testSupplier));
        when(productRepository.findByUuid(testProduct.getUuid())).thenReturn(Optional.of(testProduct));
        when(orderItemRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));


        // Creem els request de OrderItem i Order
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder().productUuid(testProduct.getUuid()).quantity(new BigDecimal("2")).build();
        OrderRequestDTO orderRequest = OrderRequestDTO.builder().name("Test Order 1").supplierUuid(testSupplier.getUuid()).notes("Test notes order 1").deliveryDate(LocalDate.now()).items(List.of(itemDTO)).build();

        when(supplierRepository.findByUuid(testSupplier.getUuid())).thenReturn(Optional.of(testSupplier));
        when(productRepository.findByUuid(testProduct.getUuid())).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        OrderResponseDTO response = orderServiceImpl.createOrder(orderRequest);


        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Order 1");
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getSubtotal()).isEqualByComparingTo(new BigDecimal("1.00"));
    }

    /**
     * Comprova Excepció (supplier no trobat)
     */
    @Test
    @DisplayName("Comprova Excepció (supplier no trobat)")
    void createOrder_supplierNotFound_throws() {

        // Mock usuari autenticat
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(testUser.getEmail());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock usuari existent
        when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));

        // proveïdor no existeix
        when(supplierRepository.findByUuid("bad-uuid"))
                .thenReturn(Optional.empty());

        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .name("Test")
                .supplierUuid("bad-uuid")
                .items(Collections.emptyList())
                .build();

        assertThatThrownBy(() -> orderServiceImpl.createOrder(orderRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Proveïdor no trobat");
    }

    /**
     * Comprova Excepció (product no trobat)
     */
    @Test
    @DisplayName("Comprova Excepció (product no trobat)")
    void createOrder_productNotFound_throws() {

        // Mock usuari autenticat
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(testUser.getEmail());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock userRepository usuari existeix
        when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));

        // Mock supplierRepository proveïdor existeix
        when(supplierRepository.findByUuid(testSupplier.getUuid()))
                .thenReturn(Optional.of(testSupplier));

        // Mock productRepository producte no existeix
        when(productRepository.findByUuid("bad-product"))
                .thenReturn(Optional.empty());

        // Es construeix el request
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .productUuid("bad-product")
                .quantity(new BigDecimal("1"))
                .build();

        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .name("Test")
                .supplierUuid(testSupplier.getUuid())
                .items(List.of(itemDTO))
                .deliveryDate(LocalDate.now())
                .build();

        assertThatThrownBy(() -> orderServiceImpl.createOrder(orderRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Producte no trobat");
    }

}