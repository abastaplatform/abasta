package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.services.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Controlador REST per gestionar les operacions relacionades amb els productes.
 * <p>
 * Aquest controlador s'encarrega de rebre les peticions HTTP provinents del client
 * i delegar la seva execució al servei {@link ProductService}.
 * </p>
 *
 * <p>Exposa els endpoints principals del recurs <strong>/api/products</strong>.</p>
 *
 * <p><b>Exemple d’ús:</b></p>
 * <pre>
 * POST /api/products
 * {
 *   "supplierId": 1,
 *   "category": "Begudes",
 *   "name": "Aigua mineral 1L",
 *   "description": "Ampolla d'aigua natural sense gas",
 *   "price": 0.80,
 *   "unit": "litre",
 *   "imageUrl": "aigua1l.png"
 * }
 * </pre>
 *
 * <p>Resposta:</p>
 * <pre>
 * {
 *   "id": 12,
 *   "uuid": "e5c1f7c4-8f1a-4b72-b3a9-01b4a5cb41b1",
 *   "supplierId": 1,
 *   "category": "Begudes",
 *   "name": "Aigua mineral 1L",
 *   "description": "Ampolla d'aigua natural sense gas",
 *   "price": 0.80,
 *   "unit": "litre",
 *   "imageUrl": "aigua1l.png",
 *   "isActive": true,
 *   "createdAt": "2025-11-01T10:45:32",
 *   "updatedAt": "2025-11-01T10:45:32"
 * }
 * </pre>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    /** Servei encarregat de la lògica de negoci dels productes. */
    private final ProductService productService;

    /**
     * Crea un nou producte.
     *
     * @param productRequestDTO dades del producte a crear, validades amb {@link Valid}.
     * @return {@link ResponseEntity} amb el producte creat i codi HTTP 201 (Created).
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> createProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(productRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(createdProduct, "Producte creat correctament"));
    }

    /**
     * Recupera un producte pel seu identificador únic (UUID).
     * Exemple: GET /api/products/{uuid}
     *
     * @param uuid identificador únic del producte
     * @return {@link ProductResponseDTO} amb les dades del producte
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> getProductByUuid(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid) {
        ProductResponseDTO product = productService.getProductByUuid(uuid);
        return ResponseEntity.ok(
                ApiResponseDTO.success(product, "Producte obtingut correctament"));
    }

    /**
     * Actualitza un producte existent pel seu UUID.
     * Exemple: PUT /api/products/{uuid}
     *
     * @param uuid identificador únic del producte
     * @param productRequestDTO dades noves del producte
     * @return {@link ProductResponseDTO} amb les dades actualitzades
     */
    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> updateProduct(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid,
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productService.updateProduct(uuid, productRequestDTO);
        return ResponseEntity.ok(
                ApiResponseDTO.success(updatedProduct, "Producte actualitzat correctament"));
    }

    /**
     * Desactiva (elimina lògicament) un producte pel seu UUID.
     * <p>
     * Aquesta operació marca el producte com a inactiu, però no l'elimina
     * físicament de la base de dades.
     * </p>
     *
     * Exemple: PATCH /api/products/{uuid}/deactivate
     *
     * @param uuid Identificador únic del producte a desactivar.
     * @return {@link ProductResponseDTO} amb el producte desactivat.
     */
    @PatchMapping("/deactivate/{uuid}")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> deactivateProduct(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid) {
        ProductResponseDTO deactivatedProduct = productService.deactivateProduct(uuid);
        return ResponseEntity.ok(
                ApiResponseDTO.success(deactivatedProduct, "Producte eliminat correctament"));
    }

    /**
     * Llista productes d'un proveïdor (només actius) amb paginació.
     *
     * Exemple: GET /api/products?supplierId=1&page=0&size=20&sort=name,asc
     *
     * @param supplierUuid identificador del proveïdor (paràmetre obligatori)
     * @param searchDTO   objecte amb paràmetres de paginació (page, size, sort)
     * @return pàgina de {@link ProductResponseDTO}
     */
    @GetMapping("/search/supplier/{supplierUuid}")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<ProductResponseDTO>>> listProductsBySupplierWithSearch(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String supplierUuid, @Valid ProductSearchDTO searchDTO) {

        Sort sort = searchDTO.getSortDir().equalsIgnoreCase("desc") ?
                Sort.by(searchDTO.getSortBy()).descending() :
                Sort.by(searchDTO.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), sort);

        Page<ProductResponseDTO> products = productService.searchProductsBySupplierWithSearch(supplierUuid, searchDTO.getSearchText(), pageable);

        // Convertir Page a PagedResponseDTO per evitar warning de serialització
        PagedResponseDTO<ProductResponseDTO> pagedResponse = PagedResponseDTO.of(products);

        return ResponseEntity.ok(
                ApiResponseDTO.success(pagedResponse, "Cerca bàsica de productes per proveïdor completada"));
    }

    /**
     * Cerca avançada de productes amb múltiples filtres.
     *
     * <p>Aquest endpoint permet filtrar productes del proveïdor utilitzant
     * tots els camps disponibles, incloent-hi filtres de text, estat d'activitat i rangs de dates.</p>
     *
     * <p>Filtres disponibles:
     * <ul>
     *   <li><strong>Text:</strong> name, contactName, email, phone, address, notes</li>
     *   <li><strong>Estat:</strong> isActive (true/false/null)</li>
     *   <li><strong>Dates:</strong> createdAfter, createdBefore, updatedAfter, updatedBefore</li>
     * </ul>
     * </p>
     *
     * <p>Exemple d'ús complet:
     * <pre>
     * GET /filter/supplier/{supplierUuid}?name=Catalunya&contactName=Joan&email=@provcat.com
     *     &phone=93&address=Barcelona&notes=important&isActive=true
     *     &createdAfter=2024-01-01T00:00:00&createdBefore=2024-12-31T23:59:59
     *     &page=0&size=10&sortBy=name&sortDir=asc
     * </pre>
     * </p>
     *
     * <p>Exemple d'ús mínim:
     * <pre>
     * GET /filter/supplier/{supplierUuid}?name=Catalunya
     * </pre>
     * </p>
     *
     * @param filterDTO paràmetres de filtratge (Spring els mapeja automàticament des dels query params)
     * @return resposta amb la pàgina de proveïdors filtrats
     */
    @GetMapping("/filter/supplier/{supplierUuid}")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<ProductResponseDTO>>> listProductsBySupplierWithFilter(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String supplierUuid, @Valid ProductFilterDTO filterDTO) {

        Sort sort = filterDTO.getSortDir().equalsIgnoreCase("desc") ?
                Sort.by(filterDTO.getSortBy()).descending() :
                Sort.by(filterDTO.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);

        Page<ProductResponseDTO> products = productService.searchProductsBySupplierWithFilter(supplierUuid, filterDTO, pageable);

        // Convertir Page a PagedResponseDTO per evitar warning de serialització
        PagedResponseDTO<ProductResponseDTO> pagedResponse = PagedResponseDTO.of(products);

        String message = String.format("Cerca avançada completada. Filtres aplicats: text=%s",
                filterDTO.hasTextFilters());

        return ResponseEntity.ok(
                ApiResponseDTO.success(pagedResponse, message));
    }

    /**
     * Puja una imatge a un producte existent.
     *
     * <p>Aquest mètode permet associar una imatge a un producte ja creat.
     * La imatge es desa al directori <code>/img/productes/</code> i es guarda
     * la seva URL a la base de dades, en el camp <code>imageUrl</code> del producte.</p>
     *
     * <p><strong>Requisits:</strong></p>
     * <ul>
     *   <li>El producte ha d'existir (identificat pel seu <code>UUID</code>).</li>
     *   <li>El fitxer ha de ser una imatge (content-type que comenci amb <code>image/</code>).</li>
     *   <li>Mida màxima recomanada: 5 MB.</li>
     * </ul>
     *
     * <p><strong>Exemple amb Postman:</strong></p>
     * <ul>
     *   <li><b>Mètode:</b> POST</li>
     *   <li><b>URL:</b> <code>http://localhost:8084/api/products/upload/{productUuid}</code></li>
     *   <li><b>Body:</b> form-data → key: <code>image</code> (type: File) → value: [selecciona la imatge]</li>
     * </ul>
     *
     * <p><strong>Resposta:</strong></p>
     * <pre>
     * {
     *   "success": true,
     *   "message": "Imatge pujada correctament",
     *   "data": "/img/productes/uuid_nomOriginal.jpg"
     * }
     * </pre>
     *
     * @param productUuid identificador únic del producte (UUID)
     * @param file arxiu de la imatge enviada com a multipart/form-data
     * @return URL relativa de la imatge desada dins la carpeta /img/productes/
     */
    @PostMapping("/upload/{productUuid}")
    public ResponseEntity<ApiResponseDTO<String>> uploadProductImage(
            @PathVariable String productUuid,
            @RequestParam("image") MultipartFile file) {

        String imageUrl = productService.saveProductImage(productUuid, file);

        return ResponseEntity.ok(
                ApiResponseDTO.success(imageUrl, "Imatge pujada correctament")
        );
    }

    /**
     * Puja una imatge temporal (sense producte associat encara).
     *
     * <p>Aquest endpoint permet pujar una imatge al servidor abans de crear el producte.
     * És útil quan el frontend necessita mostrar una previsualització o conservar la URL
     * mentre es completa el formulari de creació.</p>
     *
     * <p>La imatge es desa físicament a <code>/img/productes/</code> amb un nom únic (UUID + nom original),
     * i es retorna la seva URL pública perquè el client la pugui utilitzar temporalment.</p>
     *
     * <p><strong>Exemple amb Postman:</strong></p>
     * <ul>
     *   <li><b>Mètode:</b> POST</li>
     *   <li><b>URL:</b> <code>http://localhost:8084/api/products/upload-temp</code></li>
     *   <li><b>Body:</b> form-data → key: <code>image</code> (type: File) → value: [selecciona la imatge]</li>
     * </ul>
     *
     * <p><strong>Resposta:</strong></p>
     * <pre>
     * {
     *   "success": true,
     *   "message": "Imatge pujada correctament",
     *   "data": "/img/productes/uuid_nomOriginal.jpg"
     * }
     * </pre>
     *
     * @param file arxiu de la imatge (multipart/form-data)
     * @return URL relativa de la imatge desada dins la carpeta /img/productes/
     */
    @PostMapping("/upload-temp")
    public ResponseEntity<ApiResponseDTO<String>> uploadTempImage(@RequestParam("image") MultipartFile file) {

        try {
            String uploadDir = "img/productes/";
            Files.createDirectories(Paths.get(uploadDir));

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filepath = Paths.get(uploadDir, filename);

            Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

            String url = "/img/productes/" + filename;

            return ResponseEntity.ok(ApiResponseDTO.success(url, "Imatge pujada correctament"));

        } catch (IOException e) {
            throw new RuntimeException("Error al pujar la imatge: " + e.getMessage(), e);
        }
    }

}

