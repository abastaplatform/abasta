package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.ProductFilterDTO;
import cat.abasta_back_end.dto.ProductRequestDTO;
import cat.abasta_back_end.dto.ProductResponseDTO;
import cat.abasta_back_end.dto.ProductSearchDTO;
import cat.abasta_back_end.entities.Product;
import cat.abasta_back_end.entities.Supplier;
import cat.abasta_back_end.repositories.ProductRepository;
import cat.abasta_back_end.repositories.SupplierRepository;
import cat.abasta_back_end.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Test unitari per al ProductServiceImpl.
 * <p>
 * Aquest test valida la lògica de negoci del servei de productes,
 * incloent creació, recuperació per UUID i paginació amb filtres.
 * S'utilitzen mocks per simular el comportament dels repositoris.
 * </p>
 *
 * Autor: Daniel Garcia
 * Versió: 1.0
 */
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Supplier supplier;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        supplier = Supplier.builder()
                .uuid("supplier-uuid")
                .name("Proveïdor Test")
                .isActive(true)
                .build();

        product = Product.builder()
                .uuid("product-uuid")
                .supplier(supplier)
                .name("Aigua Mineral")
                .description("1L")
                .price(BigDecimal.valueOf(1.5))
                .unit("l")
                .isActive(true)
                .build();
    }

    /**
     * Comprova que el servei crea un producte correctament.
     */
    @Test
    @DisplayName("Comrpova creació de producte")
    void testCrearProducte() {
        when(supplierRepository.findByUuid("supplier-uuid")).thenReturn(Optional.of(supplier));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductRequestDTO request = ProductRequestDTO.builder()
                .supplierUuid("supplier-uuid")
                .name("Aigua Mineral")
                .description("1L")
                .price(BigDecimal.valueOf(1.5))
                .unit("l")
                .build();

        ProductResponseDTO response = productService.createProduct(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Aigua Mineral");
        assertThat(response.getSupplier().getUuid()).isEqualTo("supplier-uuid");
    }

    /**
     * Comprova la recuperació d'un producte per UUID.
     */
    @Test
    @DisplayName("Comprova recuperació producte per uuid")
    void testObtenirProductePerUuid() {
        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));

        ProductResponseDTO response = productService.getProductByUuid("product-uuid");

        assertThat(response).isNotNull();
        assertThat(response.getUuid()).isEqualTo("product-uuid");
        assertThat(response.getName()).isEqualTo("Aigua Mineral");
    }

    /**
     * Comprova la recuperació de productes amb paginació i cerca bàsica
     */
    @Test
    @DisplayName("Comprova llistat de productes amb searchText i paginació")
    void testLlistatProductesAmbSearch() {

        // Inicializar el supplier con ID
        Supplier supplier = Supplier.builder()
                .uuid("supplier-uuid")
                .name("Proveidor Exemple")
                .build();
        supplier.setId(1L); // Clave para que el mock coincida

        // Crear productos de prueba
        Product product1 = Product.builder()
                .uuid("product-uuid-1")
                .supplier(supplier)
                .name("Aigua Mineral")
                .description("1L")
                .category("Bebida")
                .price(BigDecimal.valueOf(1.5))
                .volume(BigDecimal.valueOf(1.0))
                .unit("l")
                .isActive(true)
                .build();

        Product product2 = Product.builder()
                .uuid("product-uuid-2")
                .supplier(supplier)
                .name("Suc de taronja")
                .description("1L")
                .category("Bebida")
                .price(BigDecimal.valueOf(2.0))
                .volume(BigDecimal.valueOf(1.0))
                .unit("l")
                .isActive(true)
                .build();

        List<Product> products = Arrays.asList(product1, product2);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), products.size());

        // Mocks del repositorio
        when(supplierRepository.findByUuid("supplier-uuid")).thenReturn(Optional.of(supplier));

        // Usar anyLong(), anyString() y any(Pageable.class) para evitar problemas de coincidencia
        when(productRepository.searchProductsBySupplierId(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(productPage);

        // DTO con searchText vacío para simular búsqueda
        ProductSearchDTO search = ProductSearchDTO.builder()
                .supplierUuid("supplier-uuid")
                .searchText("") // importante no dejar null
                .page(0)
                .size(10)
                .sortBy("name")
                .sortDir("asc")
                .build();

        // Llamada al service
        Page<ProductResponseDTO> responsePage = productService.searchProducts(
                search, PageRequest.of(search.getPage(), search.getSize()));

        // Assertions
        assertThat(responsePage).isNotNull();
        assertThat(responsePage.getContent()).hasSize(2);
        assertThat(responsePage.getContent().get(0).getName()).isEqualTo("Aigua Mineral");
        assertThat(responsePage.getContent().get(1).getName()).isEqualTo("Suc de taronja");

    }

    /**
     * Comprova la recuperació de productes amb paginació i filtres bàsics.
     */
    @Test
    @DisplayName("Comprova llistat de productes amb filtres i paginació")
    void testLlistatProductesAmbFilter() {

        // Iniciar supplier amb id
        Supplier supplier = Supplier.builder()
                .uuid("supplier-uuid")
                .name("Proveidor Exemple")
                .build();
        supplier.setId(1L); // Clave para que el mock coincida

        // Crear productes de prova
        Product product1 = Product.builder()
                .uuid("product-uuid-1")
                .supplier(supplier)
                .name("Aigua Mineral")
                .description("1L")
                .category("Bebida")
                .price(BigDecimal.valueOf(1.5))
                .volume(BigDecimal.valueOf(1.0))
                .unit("l")
                .isActive(true)
                .build();

        Product product2 = Product.builder()
                .uuid("product-uuid-2")
                .supplier(supplier)
                .name("Suc de taronja")
                .description("1L")
                .category("Bebida")
                .price(BigDecimal.valueOf(2.0))
                .volume(BigDecimal.valueOf(1.0))
                .unit("l")
                .isActive(true)
                .build();

        List<Product> products = Arrays.asList(product1, product2);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), products.size());

        // Mocks del repositori
        when(supplierRepository.findByUuid("supplier-uuid")).thenReturn(Optional.of(supplier));
        when(productRepository.filterProductsBySupplierId(
                anyLong(),
                nullable(String.class),
                nullable(String.class),
                nullable(String.class),
                nullable(BigDecimal.class),
                nullable(String.class),
                nullable(BigDecimal.class),
                nullable(BigDecimal.class),
                anyBoolean(),
                any(Pageable.class)
        )).thenReturn(productPage);

        // DTO amb camps buits per cerca
        ProductFilterDTO filter = ProductFilterDTO.builder()
                .supplierUuid("supplier-uuid")
                .name(null)
                .description(null)
                .category(null)
                .minPrice(null)
                .maxPrice(null)
                .volume(null)
                .unit(null)
                .isActive(true)
                .page(0)
                .size(10)
                .sortBy("name")
                .sortDir("asc")
                .build();

        // Crida al service
        Page<ProductResponseDTO> responsePage = productService.filterProducts(
                filter, PageRequest.of(filter.getPage(), filter.getSize()));

        // Assertions
        assertThat(responsePage).isNotNull();
        assertThat(responsePage.getContent()).hasSize(2);
        assertThat(responsePage.getContent().get(0).getName()).isEqualTo("Aigua Mineral");
        assertThat(responsePage.getContent().get(1).getName()).isEqualTo("Suc de taronja");

    }

}