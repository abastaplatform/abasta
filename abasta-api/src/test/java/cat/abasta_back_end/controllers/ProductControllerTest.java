package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.ProductRequestDTO;
import cat.abasta_back_end.dto.ProductResponseDTO;
import cat.abasta_back_end.dto.ProductSupplierResponseDTO;
import cat.abasta_back_end.entities.Product;
import cat.abasta_back_end.entities.Supplier;
import cat.abasta_back_end.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test unitari per al ProductController.
 * <p>
 * Aquest test valida els endpoints REST relacionats amb productes.
 * S'utilitza MockMvc per simular les peticions HTTP i es mockeja
 * el servei ProductService per evitar dependències de base de dades.
 * </p>
 *
 * @autor: Daniel Garcia
 * @version: 1.0
 */
@DisplayName("ProductController Tests")
public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;

    private ProductResponseDTO productResponse;

    private ProductSupplierResponseDTO productSupplierResponse;

    /**
     * Inicialització de mocks i entitats abans de cada test
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();

        productSupplierResponse = ProductSupplierResponseDTO.builder()
                .uuid("suuplier-uuid")
                .name("Proveïdor test")
                .build();

        productResponse = ProductResponseDTO.builder()
                .uuid("product-uuid")
                .supplier(productSupplierResponse)
                .name("Aigua Mineral")
                .description("1L")
                .price(BigDecimal.valueOf(1.5))
                .unit("l")
                .isActive(true)
                .build();
    }

    /**
     * Comprova l'endpoint POST per crear un producte.
     */
    @Test
    @DisplayName("POST /api/products/create : crear producte")
    void testCreateProduct() throws Exception {
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(productResponse);

        ProductRequestDTO request = ProductRequestDTO.builder()
                .supplierUuid("supplier-uuid")
                .name("Aigua Mineral")
                .description("1L")
                .price(BigDecimal.valueOf(1.5))
                .unit("l")
                .build();

        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producte creat correctament"))
                .andExpect(jsonPath("$.data.uuid").value("product-uuid"))
                .andExpect(jsonPath("$.data.name").value("Aigua Mineral"))
                .andExpect(jsonPath("$.data.supplier.uuid").value("suuplier-uuid"))
                .andExpect(jsonPath("$.data.supplier.name").value("Proveïdor test"));
    }

    /**
     * Comprova l'endpoint GET per recuperar un producte per UUID.
     */

    @Test
    @DisplayName("GET /api/products/{uuid} : obtenir producte")
    void testGetProductByUuid() throws Exception {
        when(productService.getProductByUuid("product-uuid")).thenReturn(productResponse);

        mockMvc.perform(get("/api/products/product-uuid")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uuid").value("product-uuid"))
                .andExpect(jsonPath("$.data.name").value("Aigua Mineral"));
    }

    /**
     * Comprova l'endpoint PUT per actualitzar un producte.
     */
    @Test
    @DisplayName("PUT /api/products/{uuid} : actualitzar producte")
    void testUpdateProduct() throws Exception {
        // Creamos el ProductRequestDTO que enviaremos
        ProductRequestDTO request = ProductRequestDTO.builder()
                .supplierUuid("supplier-uuid")
                .name("Aigua Mineral")
                .description("1L actualitzada")
                .price(BigDecimal.valueOf(1.7))
                .unit("l")
                .build();

        // Creamos un ProductResponseDTO actualizado que devolverá el mock
        ProductResponseDTO updatedResponse = ProductResponseDTO.builder()
                .uuid("product-uuid")
                .supplier(ProductSupplierResponseDTO.builder()
                        .uuid("supplier-uuid")
                        .name("Gaseosas Mallorca")
                        .build())
                .category("Begudes")
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .unit(request.getUnit())
                .imageUrl("")
                .isActive(true)
                .build();

        // Mock del service
        when(productService.updateProduct(any(String.class), any(ProductRequestDTO.class)))
                .thenReturn(updatedResponse);

        // Petició i comprovació de resposta
        mockMvc.perform(put("/api/products/product-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uuid").value("product-uuid"))
                .andExpect(jsonPath("$.data.name").value("Aigua Mineral"))
                .andExpect(jsonPath("$.data.description").value("1L actualitzada"))
                .andExpect(jsonPath("$.data.price").value(1.7))
                .andExpect(jsonPath("$.data.unit").value("l"))
                .andExpect(jsonPath("$.data.isActive").value(true));
    }

    /**
     * Comprova l'endpoint PATCH per desactivar un producte (eliminació lògica).
     */
    @Test
    @DisplayName("PATCH /api/products/deactivate/{uuid} : desactivar/eliminar producte")
    void testDeactivateProduct() throws Exception {

        ProductResponseDTO deactivatedResponse = ProductResponseDTO.builder()
                .uuid("fa5fc192-770a-4d44-ba52-56414980d328")
                .isActive(false)
                .build();

        when(productService.deactivateProduct("fa5fc192-770a-4d44-ba52-56414980d328"))
                .thenReturn(deactivatedResponse);

        // petició patch
        mockMvc.perform(patch("/api/products/deactivate/fa5fc192-770a-4d44-ba52-56414980d328")
                        .header("Authorization", "Bearer TOKEN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producte eliminat correctament"))
                .andExpect(jsonPath("$.data.uuid").value("fa5fc192-770a-4d44-ba52-56414980d328"))
                .andExpect(jsonPath("$.data.isActive").value(false));
    }

}