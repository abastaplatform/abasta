package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.entities.Product;
import cat.abasta_back_end.entities.Supplier;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.ProductRepository;
import cat.abasta_back_end.repositories.SupplierRepository;
import cat.abasta_back_end.services.ProductService;
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

        // Validar que el proveïdor existeix
        Supplier supplier = supplierRepository.findByUuid(productRequestDTO.getSupplierUuid())
                .orElseThrow(() -> new IllegalArgumentException("El proveïdor especificat no existeix."));

        // Crear entitat Product a partir del DTO
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

        // Guardar el producte
        product = productRepository.save(product);

        // Retornar el resultat com a DTO
        return mapToResponseDTO(product);
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

        // Comprovar que el producte existeix
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("No s'ha trobat cap producte amb el UUID: " + uuid));

        // Actualitzar camps (només els que vénen del DTO)
        product.setCategory(productRequestDTO.getCategory());
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setUnit(productRequestDTO.getUnit());
        product.setImageUrl(productRequestDTO.getImageUrl());

        // Guardar canvis
        product = productRepository.save(product);

        // Retornar el DTO de resposta
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

        // Buscar el producte
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("No s'ha trobat cap producte amb el UUID: " + uuid));

        // Marcar com a inactiu
        product.setIsActive(false);

        // Guardar canvis
        product = productRepository.save(product);

        // Retornar DTO
        return mapToResponseDTO(product);
    }

    /**
     * Cerca productes per proveïdor amb filtre bàsic de text
     *
     * @param searchText
     * @param pageable
     * @return llistat de productes
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchProductsBySupplierWithSearch(String supplierUuid, String searchText, Pageable pageable){

        // Validar que el proveïdor existeix
        Supplier supplier = supplierRepository.findByUuid(supplierUuid)
                .orElseThrow(() -> new IllegalArgumentException("El proveïdor especificat no existeix."));

        Page<Product> products = productRepository.findProductsBySupplierWithSearch(supplier.getId(), searchText, pageable);

        return products.map(this::mapToResponseDTO);

    }

    /**
     * Cerca productes per proveïdor amb filtres expandits
     *
     * @param filterDTO
     * @param pageable
     * @return llistat de productes
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchProductsBySupplierWithFilter(String supplierUuid, ProductFilterDTO filterDTO, Pageable pageable){

        // Validar que el proveïdor existeix
        Supplier supplier = supplierRepository.findByUuid(supplierUuid)
                .orElseThrow(() -> new IllegalArgumentException("El proveïdor especificat no existeix."));

        // Usar el mètode del repositori amb tots els filtres expandits
        Page<Product> products = productRepository.findProductsBySupplierWithFilter(
                supplier.getId(),
                filterDTO.getName(),
                filterDTO.getDescription(),
                filterDTO.getCategory(),
                filterDTO.getUnit(),
                filterDTO.getMinPrice(),
                filterDTO.getMaxPrice(),
                filterDTO.getIsActive(),
                pageable
        );

        return products.map(this::mapToResponseDTO);

    }

    /**
     * Converteix un {@link Product} en un {@link ProductResponseDTO}.
     *
     * @param product entitat producte
     * @return DTO amb la informació del producte creat
     */
    private ProductResponseDTO mapToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .uuid(product.getUuid())
                .supplier(ProductSupplierResponseDTO.builder()
                        .uuid(product.getSupplier().getUuid())
                        .name(product.getSupplier().getName())
                        .build())
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

}