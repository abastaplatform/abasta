package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.ProductRequestDTO;
import cat.abasta_back_end.dto.ProductResponseDTO;
import cat.abasta_back_end.entities.Product;
import cat.abasta_back_end.entities.Supplier;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.ProductRepository;
import cat.abasta_back_end.repositories.SupplierRepository;
import cat.abasta_back_end.services.ProductService;
//import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

/**
 * Implementació de la interfície {@link ProductService}.
 * <p>
 * Aquesta classe conté la lògica de negoci per crear productes, validant dades i
 * gestionant la persistència de la informació mitjançant els repositoris.
 * </p>
 *
 * <p>Inclou:</p>
 * <ul>
 *   <li>Validació del proveïdor associat.</li>
 *   <li>Assignació automàtica d’un identificador únic (UUID).</li>
 *   <li>Conversió entre DTOs i entitats.</li>
 * </ul>
 *
 * @author Daniel Garcia
 * @since 1.0
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    /**
     * Constructor amb injecció de dependències.
     *
     * @param productRepository  repositori de productes
     * @param supplierRepository repositori de proveïdors
     */
    public ProductServiceImpl(ProductRepository productRepository, SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {

        // 1. Validar que el proveïdor existeix
        Supplier supplier = supplierRepository.findByUuid(productRequestDTO.getSupplierUuid()).orElseThrow(() -> new IllegalArgumentException("El proveïdor especificat no existeix."));

        // 2️. Crear entitat Product a partir del DTO
        Product product = Product.builder()
                .uuid(UUID.randomUUID().toString())
                .supplier(supplier)
                .category(productRequestDTO.getCategory())
                .name(productRequestDTO.getName())
                .description(productRequestDTO.getDescription())
                .price(productRequestDTO.getPrice())
                .unit(productRequestDTO.getUnit())
                .imageUrl(productRequestDTO.getImageUrl())
                .isActive(true)
                .build();

        // 3️. Guardar el producte
        product = productRepository.save(product);

        // 4️. Retornar el resultat com a DTO
        return mapToResponseDTO(product);
    }

    /**
     * Converteix un {@link Product} en un {@link ProductResponseDTO}.
     *
     * @param product entitat producte
     * @return DTO amb la informació del producte creat
     */
    private ProductResponseDTO mapToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .uuid(product.getUuid())
                .supplierId(product.getSupplier().getId())
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription())
                .price(product.getPrice())
                .unit(product.getUnit())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    /**
     * Recupera un producte a partir del seu identificador únic (UUID).
     * <p>
     * Aquest mètode busca el producte corresponent a la base de dades utilitzant el seu UUID.
     * Si no es troba cap coincidència, es llença una excepció {@link ResourceNotFoundException}.
     * </p>
     *
     * @param uuid Identificador únic del producte (UUID).
     * @return El producte trobat com a {@link ProductResponseDTO}.
     * @throws ResourceNotFoundException Si no existeix cap producte amb el UUID especificat.
     * @since 1.0
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductByUuid(String uuid) {
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("No s'ha trobat cap producte amb el UUID: " + uuid));
        return mapToResponseDTO(product);
    }

    /**
     * Recupera una pàgina de productes actius associats a un proveïdor.
     * <p>
     * Per defecte llistem només productes `is_active = true`. Si més endavant
     * necessites incloure inactius, es pot afegir un paràmetre booleà.
     * </p>
     *
     * @param supplierUuid identificador del proveïdor
     * @param pageable   objecte de paginació (page, size, sort)
     * @return {@link Page} de {@link ProductResponseDTO} amb els productes demanats
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> listProductsBySupplier(String supplierUuid, Pageable pageable) {
        // Busquem el proveïdor per uuid
        Supplier supplier = supplierRepository.findByUuid(supplierUuid)
                .orElseThrow(() -> new ResourceNotFoundException("No s'ha trobat cap proveïdor amb UUID: " + supplierUuid));
        // Busquem productes actius d'aquesst proveïdor
        Page<Product> products = productRepository.findBySupplierIdAndIsActiveTrue(supplier.getId(), pageable);

        // Mapear Page<Product> -> Page<ProductResponseDTO>
        return products.map(this::mapToResponseDTO);
    }

    /**
     * Actualitza un producte existent amb les dades rebudes.
     *
     * @param uuid identificador únic del producte a actualitzar
     * @param productRequestDTO dades noves per al producte
     * @return {@link ProductResponseDTO} amb les dades actualitzades
     * @throws ResourceNotFoundException si no es troba cap producte amb el UUID indicat
     */
    @Override
    @Transactional
    public ProductResponseDTO updateProduct(String uuid, ProductRequestDTO productRequestDTO) {

        // 1️. Comprovar que el producte existeix
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("No s'ha trobat cap producte amb el UUID: " + uuid));

        // 2️. Actualitzar camps (només els que vénen del DTO)
        product.setCategory(productRequestDTO.getCategory());
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setUnit(productRequestDTO.getUnit());
        product.setImageUrl(productRequestDTO.getImageUrl());

        // 3️. Guardar canvis
        product = productRepository.save(product);

        // 4️. Retornar el DTO de resposta
        return mapToResponseDTO(product);
    }

    /**
     * Desactiva (elimina lògicament) un producte existent.
     *
     * @param uuid Identificador únic del producte.
     * @return {@link ProductResponseDTO} amb el producte desactivat.
     * @throws ResourceNotFoundException Si no existeix cap producte amb el UUID especificat.
     */
    @Override
    @Transactional
    public ProductResponseDTO deactivateProduct(String uuid) {

        // 1️. Buscar el producte
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("No s'ha trobat cap producte amb el UUID: " + uuid));

        // 2️. Marcar com a inactiu
        product.setIsActive(false);

        // 3️. Guardar canvis
        product = productRepository.save(product);

        // 4️. Retornar DTO
        return mapToResponseDTO(product);
    }

    /**
     * Implementació del mètode de cerca i filtratge de productes.
     * <p>
     * Aquest mètode permet cercar productes actius segons diferents criteris opcionals:
     * nom, categoria i proveïdor.
     * Si algun d’aquests paràmetres és {@code null}, simplement no s’aplica el filtre corresponent.
     * </p>
     *
     * <ul>
     *     <li>Executa la consulta paginada definida al {@link cat.abasta_back_end.repositories.ProductRepository}.</li>
     *     <li>Mapeja els resultats de {@link Product} a {@link ProductResponseDTO} abans de retornar-los.</li>
     * </ul>
     *
     * @param name         (opcional) nom parcial o complet del producte.
     * @param category     (opcional) categoria del producte.
     * @param supplierUuid (opcional) UUID del proveïdor.
     * @param pageable     configuració de paginació i ordenació.
     * @return una pàgina de {@link ProductResponseDTO} amb els resultats de la cerca.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchProducts(String name, String category, String supplierUuid, Pageable pageable) {

        // Executa la consulta al repositori amb els filtres opcionals i la paginació.
        Page<Product> products = productRepository.searchProducts(name, category, supplierUuid, pageable);

        // Converteix cada entitat Product en un ProductResponseDTO abans de retornar la pàgina.
        return products.map(this::mapToResponseDTO);
    }


}