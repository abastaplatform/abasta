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
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductByUuid(String uuid) {
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("No s'ha trobat cap producte amb el UUID: " + uuid));
        return mapToResponseDTO(product);
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public String saveProductImage(String productUuid, MultipartFile file) {

        // Validacions bàsiques de l'arxiu
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No s'ha rebut cap imatge.");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Només es permeten fitxers d’imatge.");
        }
        if (file.getSize() > 5_000_000) { // 5 MB
            throw new IllegalArgumentException("La imatge no pot superar els 5 MB.");
        }

        // Cercar el producte per UUid
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> new IllegalArgumentException("El producte especificat no existeix."));

        try {
            // Crear directori si no existeix
            String uploadDir = "img/productes/";
            Files.createDirectories(Paths.get(uploadDir));

            // Nom únic
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filepath = Paths.get(uploadDir, filename);

            // Guardar imatge
            Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

            // Guardar ruta en la BD
            String url = "/img/productes/" + filename;
            product.setImageUrl(url);
            productRepository.save(product);

            return url;

        } catch (IOException e) {
            throw new RuntimeException("Error al pujar la imatge: " + e.getMessage(), e);
        }
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