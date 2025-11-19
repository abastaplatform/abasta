package cat.abasta_back_end.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de tests unitaris per als DTOs relacionats amb Producte.
 * <p>
 * Aquestes proves validen que els constructors, builders i getters/setters funcionin
 * correctament, assegurant la integritat dels DTOs abans d'usar-los en serveis o controladors.
 * </p>
 * <p>
 * Cobertura:
 * <ul>
 *     <li>ProductRequestDTO</li>
 *     <li>ProductResponseDTO</li>
 *     <li>ProductFilterDTO</li>
 *     <li>ProductSearchDTO</li>
 *     <li>ProductSupplierResponseDTO</li>
 * </ul>
 * </p>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
public class ProductDTOTest {

    /**
     * Test bàsic per ProductRequestDTO amb Builder i getters/setters.
     * Valida que es puguin establir i recuperar tots els camps correctament.
     */
    @Test
    @DisplayName("Comprova product request dto")
    public void testProductRequestDTO() {
        ProductRequestDTO dto = ProductRequestDTO.builder()
                .supplierUuid("supplier-uuid")
                .category("Fruites")
                .name("Poma")
                .description("Poma vermella")
                .price(BigDecimal.valueOf(1.5))
                .volume(BigDecimal.valueOf(0.25))
                .unit("kg")
                .imageUrl("http://imatge.com/poma.jpg")
                .build();

        assertEquals("supplier-uuid", dto.getSupplierUuid());
        assertEquals("Fruites", dto.getCategory());
        assertEquals("Poma", dto.getName());
        assertEquals("Poma vermella", dto.getDescription());
        assertEquals(BigDecimal.valueOf(1.5), dto.getPrice());
        assertEquals(BigDecimal.valueOf(0.25), dto.getVolume());
        assertEquals("kg", dto.getUnit());
        assertEquals("http://imatge.com/poma.jpg", dto.getImageUrl());
    }

    /**
     * Test bàsic per ProductResponseDTO amb Builder.
     * Valida que tots els camps es puguin establir i recuperar.
     */
    @Test
    @DisplayName("Comprova product response dto")
    public void testProductResponseDTO() {
        ProductSupplierResponseDTO supplierDTO = ProductSupplierResponseDTO.builder()
                .uuid("supplier-uuid")
                .name("Proveïdor X")
                .build();

        ProductResponseDTO dto = ProductResponseDTO.builder()
                .uuid("product-uuid")
                .supplier(supplierDTO)
                .category("Begudes")
                .name("Aigua")
                .description("Aigua mineral")
                .price(BigDecimal.valueOf(0.75))
                .volume(BigDecimal.valueOf(1))
                .unit("l")
                .imageUrl("http://imatge.com/aigua.jpg")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertEquals("product-uuid", dto.getUuid());
        assertEquals(supplierDTO, dto.getSupplier());
        assertEquals("Begudes", dto.getCategory());
        assertEquals("Aigua", dto.getName());
        assertEquals("Aigua mineral", dto.getDescription());
        assertEquals(BigDecimal.valueOf(0.75), dto.getPrice());
        assertEquals(BigDecimal.valueOf(1), dto.getVolume());
        assertEquals("l", dto.getUnit());
        assertEquals("http://imatge.com/aigua.jpg", dto.getImageUrl());
        assertTrue(dto.getIsActive());
    }

    /**
     * Test per ProductFilterDTO amb Builder i verificació de filtres de text.
     */
    @Test
    @DisplayName("Comprova product filter dto")
    public void testProductFilterDTO() {
        ProductFilterDTO dto = ProductFilterDTO.builder()
                .name("Poma")
                .description("Vermella")
                .category("Fruites")
                .minPrice(BigDecimal.valueOf(1))
                .maxPrice(BigDecimal.valueOf(5))
                .page(0)
                .size(20)
                .sortBy("name")
                .sortDir("asc")
                .build();

        assertTrue(dto.hasTextFilters());
        assertEquals("Poma", dto.getName());
        assertEquals("Vermella", dto.getDescription());
        assertEquals("Fruites", dto.getCategory());
        assertEquals(BigDecimal.valueOf(1), dto.getMinPrice());
        assertEquals(BigDecimal.valueOf(5), dto.getMaxPrice());
        assertEquals(0, dto.getPage());
        assertEquals(20, dto.getSize());
        assertEquals("name", dto.getSortBy());
        assertEquals("asc", dto.getSortDir());
    }

    /**
     * Test per ProductSearchDTO amb Builder i verificació de valors per defecte.
     */
    @Test
    @DisplayName("Comprova product search dto")
    public void testProductSearchDTO() {
        ProductSearchDTO dto = ProductSearchDTO.builder()
                .supplierUuid("supplier-uuid")
                .searchText("Aigua")
                .build();

        assertEquals("supplier-uuid", dto.getSupplierUuid());
        assertEquals("Aigua", dto.getSearchText());
        assertEquals(0, dto.getPage());
        assertEquals(10, dto.getSize());
        assertEquals("name", dto.getSortBy());
        assertEquals("asc", dto.getSortDir());
    }

    /**
     * Test per ProductSupplierResponseDTO amb Builder.
     */
    @Test
    @DisplayName("Comprova product supplier response dto")
    public void testProductSupplierResponseDTO() {
        ProductSupplierResponseDTO dto = ProductSupplierResponseDTO.builder()
                .uuid("supplier-uuid")
                .name("Proveïdor X")
                .build();

        assertEquals("supplier-uuid", dto.getUuid());
        assertEquals("Proveïdor X", dto.getName());
    }
}