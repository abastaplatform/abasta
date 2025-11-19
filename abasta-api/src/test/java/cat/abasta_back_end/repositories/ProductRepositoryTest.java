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
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de control per al repositori de Product.
 * <p>
 * Aquest test valida les operacions bàsiques del repositori,
 * incloent-hi guardat, recuperació per UUID i paginació.
 * Totes les proves s’executen en una base de dades en memòria H2.
 * </p>
 *
 * Autor: Daniel Garcia
 * Versió: 1.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private Company testCompany;
    private User testUser;
    private Supplier testSupplier;

    /**
     * Inicialització d'entitats
     */
    @BeforeEach
    void setup() {
        // Crear y guardar company
        testCompany = Company.builder()
                .uuid("test-company-uuid")
                .name("Company Test")
                .taxId("55555555K")
                .email("test@test.com")
                .phone("666666666")
                .address("Calle 1")
                .city("Barcelona")
                .postalCode("08080")
                .status(Company.CompanyStatus.ACTIVE)
                .build();
        companyRepository.save(testCompany);

        // Crear y guardar user
        testUser = User.builder()
                .uuid("test-user-uuid")
                .company(testCompany)
                .email("user@test.com")
                .password("hola")
                .firstName("User")
                .lastName("Last")
                .role(User.UserRole.ADMIN)
                .phone("666666666")
                .isActive(true)
                .emailVerified(true)
                .build();
        userRepository.save(testUser);

        // Crear y guardar supplier
        testSupplier = Supplier.builder()
                .uuid("test-supplier-uuid")
                .company(testCompany)
                .name("Proveïdor Test")
                .isActive(true)
                .build();
        supplierRepository.save(testSupplier);
    }

    /**
     * Comprova que un producte es pot guardar correctament al repositori.
     */
    @Test
    @DisplayName("Comprova Guardar i recuperar un producte")
    void testGuardarIRecuperarProducte() {

        // Crear producte associat
        Product product = Product.builder()
                .uuid("test-product-uuid")
                .supplier(testSupplier)
                .name("Aigua Mineral")
                .description("Aigua natural 1L")
                .price(BigDecimal.valueOf(1.50))
                .unit("l")
                .isActive(true)
                .build();

        productRepository.save(product);

        // Recuperar producte pel UUID
        Optional<Product> optProduct = productRepository.findByUuid("test-product-uuid");
        assertThat(optProduct).isPresent();
        assertThat(optProduct.get().getName()).isEqualTo("Aigua Mineral");
    }

    /**
     * Comprova la funcionalitat de paginació del repositori.
     */
    @Test
    @DisplayName("Comprova Consultar productes amb paginació")
    void testProductesAmbPaginacio() {

        // Crear múltiples productes
        for (int i = 1; i <= 15; i++) {
            Product p = Product.builder()
                    .uuid("prod-" + i)
                    .supplier(testSupplier)
                    .name("Producte " + i)
                    .price(BigDecimal.valueOf(1.0 * i))
                    .isActive(true)
                    .build();
            productRepository.save(p);
        }

        Pageable pageable = PageRequest.of(0, 10);
        // Usar searchProductsBySupplierId amb supplierId
        Page<Product> page = productRepository.searchProductsBySupplierId(testSupplier.getId(), null, pageable);


        // Comprovacions
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

}