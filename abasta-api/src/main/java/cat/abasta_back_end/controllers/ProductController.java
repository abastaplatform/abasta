package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.ProductRequestDTO;
import cat.abasta_back_end.dto.ProductResponseDTO;
import cat.abasta_back_end.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

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
     * Endpoint per crear un nou producte.
     *
     * @param productRequestDTO dades del producte a crear, validades amb {@link Valid}.
     * @return {@link ResponseEntity} amb el producte creat i codi HTTP 201 (Created).
     */
    @PostMapping("/create")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Recupera un producte pel seu identificador únic (UUID).
     *
     * Exemple: GET /api/products/{uuid}
     *
     * @param uuid identificador únic del producte
     * @return {@link ProductResponseDTO} amb les dades del producte
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<ProductResponseDTO> getProductByUuid(@PathVariable String uuid) {
        ProductResponseDTO product = productService.getProductByUuid(uuid);
        return ResponseEntity.ok(product);
    }

    /**
     * Actualitza un producte existent pel seu UUID.
     *
     * Exemple: PUT /api/products/{uuid}
     *
     * @param uuid identificador únic del producte
     * @param productRequestDTO dades noves del producte
     * @return {@link ProductResponseDTO} amb les dades actualitzades
     */
    @PutMapping("/{uuid}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String uuid,
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {

        ProductResponseDTO updatedProduct = productService.updateProduct(uuid, productRequestDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Llista productes d'un proveïdor (només actius) amb paginació.
     *
     * Exemple: GET /api/products?supplierId=1&page=0&size=20&sort=name,asc
     *
     * @param supplierId identificador del proveïdor (paràmetre obligatori)
     * @param pageable   objecte amb paràmetres de paginació (page, size, sort)
     * @return pàgina de {@link ProductResponseDTO}
     */
    @GetMapping("/supplier/{supplierUuid}")
    public ResponseEntity<Page<ProductResponseDTO>> listProductsBySupplier(
            @PathVariable String supplierUuid,
            @PageableDefault(page = 0, size = 20) Pageable pageable) {

        Page<ProductResponseDTO> page = productService.listProductsBySupplier(supplierUuid, pageable);
        return ResponseEntity.ok(page);
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
    public ResponseEntity<ProductResponseDTO> deactivateProduct(@PathVariable String uuid) {
        ProductResponseDTO deactivatedProduct = productService.deactivateProduct(uuid);
        return ResponseEntity.ok(deactivatedProduct);
    }

    /**
     * Cerca i filtra productes segons els criteris proporcionats.
     * <p>
     * Els paràmetres {@code name}, {@code category} i {@code supplierUuid} són opcionals.
     * Si no s’especifica cap filtre, es retornaran tots els productes actius.
     * </p>
     *
     * Exemple d'ús:
     * <pre>
     * GET /api/products?name=oli
     * GET /api/products?category=begudes
     * GET /api/products?name=pa&category=forn
     * GET /api/products?supplierUuid=abc-123
     * </pre>
     *
     * @param name         (opcional) nom parcial o complet del producte.
     * @param category     (opcional) categoria del producte.
     * @param supplierUuid (opcional) identificador únic del proveïdor.
     * @param pageable     configuració de paginació i ordenació.
     * @return una pàgina de {@link ProductResponseDTO} amb els resultats de la cerca.
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String supplierUuid,
            @PageableDefault(page = 0, size = 20, sort = "name") Pageable pageable) {

        Page<ProductResponseDTO> page = productService.searchProducts(q, name, category, supplierUuid, pageable);
        return ResponseEntity.ok(page);
    }

}

