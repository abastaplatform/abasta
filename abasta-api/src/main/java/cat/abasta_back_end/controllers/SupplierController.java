package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.ApiResponseDTO;
import cat.abasta_back_end.dto.SupplierRequestDTO;
import cat.abasta_back_end.dto.SupplierResponseDTO;
import cat.abasta_back_end.services.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Controlador REST per a la gestió integral de proveïdors a través d'endpoints HTTP.
 * Proporciona una API completa per administrar proveïdors amb operacions CRUD i cerca avançada.
 *
 * <p>Aquest controlador exposa tots els endpoints necessaris per gestionar proveïdors,
 * seguint les millors pràctiques de REST i proporcionant respostes estandarditzades.</p>
 *
 * <p>Endpoints disponibles:
 * <ul>
 *   <li>POST /api/suppliers - Crear nou proveïdor</li>
 *   <li>GET /api/suppliers/{uuid} - Obtenir proveïdor per UUID</li>
 *   <li>PUT /api/suppliers/{uuid} - Actualitzar proveïdor existent</li>
 *   <li>GET /api/suppliers/company/{companyUuid} - Proveïdors d'una empresa</li>
 *   <li>GET /api/suppliers/search - Cerca per nom amb paginació</li>
 *   <li>GET /api/suppliers/filter - Cerca avançada amb múltiples filtres</li>
 * </ul>
 * </p>
 *
 * <p>Funcionalitats de paginació i ordenació:
 * <ul>
 *   <li>Paginació automàtica amb paràmetres page i size</li>
 *   <li>Ordenació configurable amb sortBy i sortDir</li>
 *   <li>Valores per defecte raonables (page=0, size=10, sortBy="name")</li>
 *   <li>Validació de paràmetres amb @Min</li>
 * </ul>
 * </p>
 *
 * <p>Format de resposta estandarditzat:
 * <ul>
 *   <li>Totes les respostes utilitzen {@link cat.abasta_back_end.dto.ApiResponseDTO}</li>
 *   <li>Codis d'estat HTTP apropiats (200, 201, 400, 404, etc.)</li>
 *   <li>Dades de resposta tipades</li>
 * </ul>
 * </p>
 *
 * <p>Exemple d'ús des d'un client:
 * <pre>
 * // Crear un nou proveïdor
 * POST /api/suppliers
 * Content-Type: application/json
 * Authorization: Bearer {jwt_token}
 *
 * {
 *   "companyUuid": "123e4567-e89b-12d3-a456-426614174000",
 *   "name": "Proveïdors Catalunya SL",
 *   "contactName": "Joan Martínez",
 *   "email": "joan@provcat.com",
 *   "phone": "938765432",
 *   "address": "Av. Diagonal 123, Barcelona",
 *   "isActive": true
 * }
 *
 * // Resposta
 * {
 *   "success": true,
 *   "message": "Proveïdor creat correctament",
 *   "data": { ... },
 *   "timestamp": "2024-01-15T10:30:00"
 * }
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @see SupplierService
 * @see SupplierRequestDTO
 * @see SupplierResponseDTO
 * @see cat.abasta_back_end.dto.ApiResponseDTO
 * @since 1.0
 */
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * Crea un nou proveïdor.
     *
     * @param supplierRequestDTO les dades del proveïdor a crear
     * @return resposta amb el proveïdor creat
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<SupplierResponseDTO>> createSupplier(
            @Valid @RequestBody SupplierRequestDTO supplierRequestDTO) {
        SupplierResponseDTO createdSupplier = supplierService.createSupplier(supplierRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(createdSupplier, "Proveïdor creat correctament"));
    }

    /**
     * Obté un proveïdor pel seu UUID.
     *
     * @param uuid l'UUID del proveïdor
     * @return resposta amb les dades del proveïdor
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponseDTO<SupplierResponseDTO>> getSupplierByUuid(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid) {
        SupplierResponseDTO supplier = supplierService.getSupplierByUuid(uuid);
        return ResponseEntity.ok(
                ApiResponseDTO.success(supplier, "Proveïdor obtingut correctament"));
    }

    /**
     * Actualitza un proveïdor existent.
     *
     * @param uuid               l'UUID del proveïdor a actualitzar
     * @param supplierRequestDTO les noves dades del proveïdor
     * @return resposta amb el proveïdor actualitzat
     */
    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponseDTO<SupplierResponseDTO>> updateSupplier(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid,
            @Valid @RequestBody SupplierRequestDTO supplierRequestDTO) {
        SupplierResponseDTO updatedSupplier = supplierService.updateSupplier(uuid, supplierRequestDTO);
        return ResponseEntity.ok(
                ApiResponseDTO.success(updatedSupplier, "Proveïdor actualitzat correctament"));
    }

    /**
     * Obté tots els proveïdors d'una empresa específica.
     *
     * @param companyUuid l'UUID de l'empresa
     * @return resposta amb la llista de proveïdors
     */
    @GetMapping("/company/{companyUuid}")
    public ResponseEntity<ApiResponseDTO<List<SupplierResponseDTO>>> getSuppliersByCompany(
            @PathVariable @NotBlank(message = "L'UUID de l'empresa no pot estar buit") String companyUuid) {
        List<SupplierResponseDTO> suppliers = supplierService.getSuppliersByCompanyUuid(companyUuid);
        return ResponseEntity.ok(
                ApiResponseDTO.success(suppliers, "Proveïdors de l'empresa obtinguts correctament"));
    }

    /**
     * Activa o desactiva un proveïdor.
     *
     * @param uuid l'UUID del proveïdor
     * @param isActive l'estat d'activitat a establir
     * @return resposta amb el proveïdor actualitzat
     */
    @PatchMapping("/{uuid}/status")
    public ResponseEntity<ApiResponseDTO<SupplierResponseDTO>> toggleSupplierStatus(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid,
            @RequestParam Boolean isActive) {
        SupplierResponseDTO updatedSupplier = supplierService.toggleSupplierStatus(uuid, isActive);
        return ResponseEntity.ok(
                ApiResponseDTO.success(updatedSupplier, "Estat del proveïdor actualitzat correctament"));
    }
}