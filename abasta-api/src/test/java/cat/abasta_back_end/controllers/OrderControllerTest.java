package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris per OrderController.
 * <p>
 * Verifica la correcta crida als mètodes del servei de comandes
 * i la resposta HTTP enviada pel controlador.
 * </p>
 *
 * Casos coberts:
 * <ul>
 *     <li>Creació d'una comanda amb èxit.</li>
 *     <li>Enviament d'una comanda existent amb èxit.</li>
 *     <li>Llançament d'excepció quan l'enviament falla.</li>
 * </ul>
 *
 * @author : Daniel Garcia
 * @version : 1.0
 */
@DisplayName("OrderController Tests")
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderRequestDTO orderRequest;
    private OrderResponseDTO orderResponse;

    /**
     * Inicialitza dades comunes abans de cada test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Crear un item
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .productUuid("product-uuid")
                .quantity(new BigDecimal("2"))
                .build();

        // Crear la comanda per request
        orderRequest = OrderRequestDTO.builder()
                .name("Test Order")
                .supplierUuid("supplier-uuid")
                .items(List.of(itemDTO))
                .deliveryDate(LocalDate.now())
                .notes("Notes test")
                .build();

        // Crear la resposta esperada
        OrderItemResponseDTO itemResponse = OrderItemResponseDTO.builder()
                .productUuid("product-uuid")
                .quantity(new BigDecimal("2"))
                .unitPrice(new BigDecimal("10.00"))
                .subtotal(new BigDecimal("20.00"))
                .build();

        orderResponse = OrderResponseDTO.builder()
                .uuid("order-uuid")
                .name("Test Order")
                .status("PENDING")
                .totalAmount(new BigDecimal("20.00"))
                .items(List.of(itemResponse))
                .notes("Notes test")
                .supplierUuid("supplier-uuid")
                .build();
    }

    /**
     * Test que comprova la crida a createOrder i la resposta HTTP 201.
     */
    @Test
    @DisplayName("POST /api/orders/create : crear comanda")
    void createOrder_success() {
        // Configurar mock del servei
        when(orderService.createOrder(orderRequest)).thenReturn(orderResponse);

        // Executar controlador
        ResponseEntity<ApiResponseDTO<OrderResponseDTO>> responseEntity =
                orderController.createOrder(orderRequest);

        // Comprovacions
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getData()).isEqualTo(orderResponse);
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Comanda creada correctament");

        // Verificar que el servei ha estat cridat exactament una vegada
        verify(orderService, times(1)).createOrder(orderRequest);
    }

    /**
     * Test que comprova l'enviament d'una comanda existent amb èxit.
     */
    @Test
    @DisplayName("POST /api/orders/uuid/send : enviar comanda")
    void sendOrder_success() {
        // Configurar mock del servei
        when(orderService.sendOrder("order-uuid")).thenReturn(orderResponse);

        // Executar controlador
        ResponseEntity<ApiResponseDTO<OrderResponseDTO>> responseEntity =
                orderController.sendOrder("order-uuid");

        // Comprovacions
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getData()).isEqualTo(orderResponse);
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Comanda enviada correctament");

        verify(orderService, times(1)).sendOrder("order-uuid");
    }

    /**
     * Test que comprova llançament d'excepció quan el servei falla
     * en l'enviament de la comanda.
     */
    @Test
    @DisplayName("Comprova excepció en enviament de comanda")
    void sendOrder_serviceThrowsException() {
        // Configurar el mock perquè llenci RuntimeException
        when(orderService.sendOrder("order-uuid"))
                .thenThrow(new RuntimeException("Error enviant comanda"));

        // Executar i comprovar excepció
        try {
            orderController.sendOrder("order-uuid");
        } catch (RuntimeException ex) {
            assertThat(ex.getMessage()).isEqualTo("Error enviant comanda");
        }

        verify(orderService, times(1)).sendOrder("order-uuid");
    }

    /**
     * Test que comprova l'obtenció de comanda per uuid
     */
    @Test
    @DisplayName("GET /api/orders/{uuid} : obtenir comanda per UUID")
    void getOrder_success() {
        when(orderService.getOrderByUuid("order-uuid")).thenReturn(orderResponse);

        ResponseEntity<ApiResponseDTO<OrderResponseDTO>> response =
                orderController.getOrder("order-uuid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(orderResponse);
        assertThat(response.getBody().getMessage()).isEqualTo("Comanda trobada correctament");

        verify(orderService, times(1)).getOrderByUuid("order-uuid");
    }

    /**
     * Test que elimina una comanda
     */
    @Test
    @DisplayName("PATCH /api/orders/delete/{uuid} : eliminar comanda")
    void deleteOrder_success() {
        when(orderService.deleteOrder("order-uuid")).thenReturn(orderResponse);

        ResponseEntity<ApiResponseDTO<OrderResponseDTO>> response =
                orderController.deleteOrder("order-uuid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(orderResponse);
        assertThat(response.getBody().getMessage()).isEqualTo("Comanda eliminada correctament");

        verify(orderService, times(1)).deleteOrder("order-uuid");
    }

    /**
     * Tes per actualitzar una comanda
     */
    @Test
    @DisplayName("PUT /api/orders/update/{uuid} : actualitzar comanda")
    void updateOrder_success() {
        when(orderService.updateOrder("order-uuid", orderRequest)).thenReturn(orderResponse);

        ResponseEntity<ApiResponseDTO<OrderResponseDTO>> response =
                orderController.updateOrder("order-uuid", orderRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(orderResponse);
        assertThat(response.getBody().getMessage()).isEqualTo("Comanda actualitzada correctament");

        verify(orderService, times(1)).updateOrder("order-uuid", orderRequest);
    }

    /**
     * Test per filtrar comandes paginades
     */
    @Test
    @DisplayName("GET /api/orders/filter : filtrar comandes paginades")
    void filterOrders_success() {
        // Mock de la pàgina retornada pel servei
        Page<OrderResponseDTO> pageMock = new PageImpl<>(List.of(orderResponse), PageRequest.of(0, 10), 1);
        OrderFilterDTO filterDTO = OrderFilterDTO.builder()
                .page(0)
                .size(10)
                .sortBy("name")
                .sortDir("asc")
                .build();

        when(orderService.filterOrders(filterDTO, PageRequest.of(0, 10, Sort.by("name").ascending())))
                .thenReturn(pageMock);

        ResponseEntity<ApiResponseDTO<PagedResponseDTO<OrderResponseDTO>>> response =
                orderController.filterOrders(filterDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getContent()).hasSize(1);
        assertThat(response.getBody().getData().getContent().get(0)).isEqualTo(orderResponse);
        assertThat(response.getBody().getMessage())
                .contains("Cerca avançada de comandes completada");

        verify(orderService, times(1))
                .filterOrders(filterDTO, PageRequest.of(0, 10, Sort.by("name").ascending()));
    }


}