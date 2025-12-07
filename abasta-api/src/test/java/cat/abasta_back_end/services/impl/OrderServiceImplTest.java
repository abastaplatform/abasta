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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
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
     * Comprova la modificació d'una Order
     */
    @Test
    @DisplayName("Comprova la modificació d'una Order")
    void updateOrder_success() {

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

        // Mock save: retorna la comanda passada
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Crear ordre inicial
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder().productUuid(testProduct.getUuid()).quantity(new BigDecimal("2")).build();
        OrderRequestDTO orderRequest = OrderRequestDTO.builder().name("Test Order 1").supplierUuid(testSupplier.getUuid()).notes("Test notes order 1").deliveryDate(LocalDate.now()).items(List.of(itemDTO)).build();
        OrderResponseDTO response = orderServiceImpl.createOrder(orderRequest);

        // Uuid de la comanda inicial
        String uuidNewOrder = response.getUuid();

        // Creem ordre real
        Order existingOrder = new Order();
        existingOrder.setUuid(uuidNewOrder);
        existingOrder.setName(response.getName());
        existingOrder.setSupplier(testSupplier);
        existingOrder.setItems(new ArrayList<>());

        when(orderRepository.findByUuid(uuidNewOrder)).thenReturn(Optional.of(existingOrder));

        // Crear dades modificades
        OrderItemRequestDTO itemDTOMod1 = OrderItemRequestDTO.builder().productUuid(testProduct.getUuid()).quantity(new BigDecimal("1")).build();
        OrderItemRequestDTO itemDTOMod2 = OrderItemRequestDTO.builder().productUuid(testProduct.getUuid()).quantity(new BigDecimal("3")).build();

        List<OrderItemRequestDTO> itemDTOModList = new ArrayList<>();
        itemDTOModList.add(itemDTOMod1);
        itemDTOModList.add(itemDTOMod2);

        OrderRequestDTO orderRequestMod1 = OrderRequestDTO.builder().name("Test Order 2").supplierUuid(testSupplier.getUuid()).notes("Test notes order 1").deliveryDate(LocalDate.now()).items(itemDTOModList).build();

        OrderResponseDTO responseMod = orderServiceImpl.updateOrder(uuidNewOrder, orderRequestMod1);

        // Asserts
        assertThat(responseMod).isNotNull();
        assertThat(responseMod.getName()).isNotEqualTo(response.getName());
        assertThat(responseMod.getItems()).hasSize(2);
        assertThat(responseMod.getItems().get(0).getSubtotal()).isEqualByComparingTo(new BigDecimal("0.50"));
    }

    /**
     * Comprova l'eliminació d'una comanda
     */
    @Test
    @DisplayName("Comprova l'esborrat d'una Order")
    void deleteOrder_success() {

        // Mock de l'usuari autenticat
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // Crear Order existent per eliminar
        String orderUuid = UUID.randomUUID().toString();

        Order existingOrder = new Order();
        existingOrder.setUuid(orderUuid);
        existingOrder.setName("Test Order");
        existingOrder.setStatus(Order.OrderStatus.PENDING);
        existingOrder.setSupplier(testSupplier);
        existingOrder.setItems(new ArrayList<>());

        // Mock findByUuid: retorna l'Order trobada
        when(orderRepository.findByUuid(orderUuid))
                .thenReturn(Optional.of(existingOrder));

        // Mock save: retorna la mateixa Order passada
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        OrderResponseDTO response = orderServiceImpl.deleteOrder(orderUuid);

        // Asserts
        assertThat(response).isNotNull();
        assertThat(response.getUuid()).isEqualTo(orderUuid);
        assertThat(response.getStatus()).isEqualTo(Order.OrderStatus.DELETED.toString());

        // Verificar que realment s'ha guardat la comanda amb status canviat
        verify(orderRepository).save(argThat(order ->
                order.getStatus() == Order.OrderStatus.DELETED
        ));

        // Verificar que findByUuid es va cridar correctament
        verify(orderRepository).findByUuid(orderUuid);
    }

    /**
     * Comprova el retorn d'una comanda pel seu uuid
     */
    @Test
    @DisplayName("Comprova retorn d'Order per UUID")
    void getOrderByUuid_success() {

        // Definició
        String orderUuid = UUID.randomUUID().toString();

        Order existingOrder = new Order();
        existingOrder.setUuid(orderUuid);
        existingOrder.setName("Test Order");
        existingOrder.setStatus(Order.OrderStatus.PENDING);
        existingOrder.setSupplier(testSupplier);
        existingOrder.setItems(new ArrayList<>());

        // Mock findByUuid
        when(orderRepository.findByUuid(orderUuid)).thenReturn(Optional.of(existingOrder));

        OrderResponseDTO response = orderServiceImpl.getOrderByUuid(orderUuid);

        // Asserts
        assertThat(response).isNotNull();
        assertThat(response.getUuid()).isEqualTo(orderUuid);
        assertThat(response.getName()).isEqualTo("Test Order");
        assertThat(response.getStatus()).isEqualTo(Order.OrderStatus.PENDING.name());

        // Verificar que es va cridar al repository
        verify(orderRepository).findByUuid(orderUuid);
    }

    /**
     * Comprova excepció si l'ordre no existeix
     */
    @Test
    @DisplayName("Comprova excepció si la Order no existeix")
    void getOrderByUuid_notFound() {

        String orderUuid = UUID.randomUUID().toString();

        // Mock que NO troba la comanda
        when(orderRepository.findByUuid(orderUuid))
                .thenReturn(Optional.empty());

        // Executar i verificar excepció
        assertThatThrownBy(() -> orderServiceImpl.getOrderByUuid(orderUuid))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(orderUuid);

        // Verificar que findByUuid es va cridar
        verify(orderRepository).findByUuid(orderUuid);
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

    @Test
    @DisplayName("Comprova el filtre d'ordres amb diversos criteris")
    void filterOrders_success() {

        // --- Mock de l'usuari autenticat ---
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(testUser.getEmail());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // --- Mock del userRepository ---
        Company company = new Company();
        company.setId(10L);
        testUser.setCompany(company);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // --- DTO de filtre ---
        OrderFilterDTO filterDTO = OrderFilterDTO.builder()
                .orderUuid(null)
                .supplierUuid(null)
                .userUuid(null)
                .status(null)
                .searchText("test")
                .build();

        Pageable pageable = Pageable.ofSize(10);

        // --- Crear ordre de prova ---
        Supplier supplier = new Supplier();
        supplier.setUuid(UUID.randomUUID().toString());

        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setName("Test Order 1");
        order.setStatus(Order.OrderStatus.PENDING);
        order.setSupplier(supplier);
        order.setCompany(company);
        order.setItems(new ArrayList<>());
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setNotes("Notes test");
        order.setDeliveryDate(LocalDate.now());

        // --- Mock del repository per findAll amb specification ---
        Page<Order> pageOrders = new PageImpl<>(List.of(order), pageable, 1);
        when(orderRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(pageOrders);

        // --- Executar el servei ---
        Page<OrderResponseDTO> result = orderServiceImpl.filterOrders(filterDTO, pageable);

        // --- Asserts ---
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getUuid()).isEqualTo(order.getUuid());
        assertThat(result.getContent().get(0).getName()).isEqualTo(order.getName());
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(order.getStatus().name());
        assertThat(result.getContent().get(0).getSupplierUuid()).isEqualTo(order.getSupplier().getUuid());

        // --- Verificacions Mockito ---
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(orderRepository).findAll(any(Specification.class), eq(pageable));
    }


}