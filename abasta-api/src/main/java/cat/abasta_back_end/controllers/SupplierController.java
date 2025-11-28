package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.services.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Controlador REST per a la gestió integral de proveïdors a través d'endpoints HTTP.
 * Proporciona una API completa per administrar proveïdors amb operacions CRUD i cerca avançada.
 *
 * <p>Aquest controlador exposa tots els endpoints necessaris per gestionar proveïdors,
 * seguint les millors pràctiques de REST i proporcionant respostes estandarditzades.
 * Inclou múltiples nivells de cerca des de bàsica fins avançada amb tots els filtres disponibles.</p>
 *
 * <p>Endpoints disponibles:
 * <ul>
 *   <li>GET /api/suppliers/ - Proveïdors d'una empresa</li>
 *   <li>GET /api/suppliers/{uuid} - Obtenir proveïdor per UUID</li>
 *   <li>GET /api/suppliers/search - Cerca bàsica per text</li>
 *   <li>GET /api/suppliers/filter - Cerca avançada amb tots els filtres</li>
 *   <li>POST /api/suppliers - Crear nou proveïdor</li>
 *   <li>PUT /api/suppliers/{uuid} - Actualitzar proveïdor existent</li>
 *   <li>PATCH /api/suppliers/{uuid}/status - Canviar estat actiu/inactiu</li>
 *
 * </ul>
 * </p>
 *
 * <p>Nivells de cerca implementats:
 * <ul>
 *   <li><strong>Cerca bàsica (/search):</strong> Cerca per qualsevol camp</li>
 *   <li><strong>Cerca avançada (/filter):</strong> Filtres per tots els camps</li>
 * </ul>
 * </p>
 *
 * <p>Filtres disponibles en cerca avançada:
 * <ul>
 *   <li><strong>Filtres de text:</strong> name, contactName, email, phone, address</li>
 * </ul>
 * </p>
 *
 * <p>Funcionalitats de paginació i ordenació:
 * <ul>
 *   <li>Paginació automàtica amb paràmetres page i size</li>
 *   <li>Ordenació configurable amb sortBy i sortDir</li>
 *   <li>Camps d'ordenació: name, contactName, email, phone, createdAt, updatedAt</li>
 *   <li>Valors per defecte raonables (page=0, size=10, sortBy="name", sortDir="asc")</li>
 *   <li>Validació de paràmetres amb Bean Validation</li>
 * </ul>
 * </p>
 *
 * <p>Seguretat implementada:
 * <ul>
 *   <li>Un usuari només pot veure proveïdors de la seva empresa</li>
 *   <li>No es pot manipular el companyUuid des del client</li>
 * </ul>
 * </p>
 *
 * @author Enrique Pérez
 * @version 3.0
 * @see SupplierService
 * @see SupplierRequestDTO
 * @see SupplierResponseDTO
 * @see SupplierSearchDTO
 * @see SupplierFilterDTO
 * @since 1.0
 */
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * Obté tots els proveïdors de l'empresa de l'usuari autenticat.
     * El companyUuid s'extreu automàticament de l'usuari.
     *
     * <p>Aquest endpoint retorna la llista completa de proveïdors de l'empresa
     * de l'usuari autenticat, amb paginació.</p>
     *
     * <p>Exemple d'ús:
     * <pre>
     * GET /api/suppliers
     * Authorization: Bearer {token}
     * </pre>
     * </p>
     *
     * @param searchDTO paràmetres de paginació
     * @return resposta amb la llista de proveïdors
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<SupplierResponseDTO>>> getAllSuppliers(
            @Valid SupplierSearchDTO searchDTO) {
        Sort sort = searchDTO.getSortDir().equalsIgnoreCase("desc") ?
                Sort.by(searchDTO.getSortBy()).descending() :
                Sort.by(searchDTO.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), sort);

        Page<SupplierResponseDTO> suppliers = supplierService.getAllSuppliersPaginated(pageable);
        // Convertir Page a PagedResponseDTO per evitar warning de serialització
        PagedResponseDTO<SupplierResponseDTO> pagedResponse = PagedResponseDTO.of(suppliers);

        return ResponseEntity.ok(
                ApiResponseDTO.success(pagedResponse, "Proveïdors de l'empresa obtinguts correctament"));
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
     * Cerca bàsica de proveïdors per text en múltiples camps amb paginació.
     * El companyUuid s'obté automàticament de l'usuari autenticat.
     *
     * <p>Aquest endpoint permet cercar proveïdors de l'empresa de l'usuari filtrant
     * simultàniament en múltiples camps: name, contactName, email, phone i address.
     * Si no s'especifica text, retorna tots els proveïdors.</p>
     *
     * <p>Camps de cerca inclosos:
     * <ul>
     *   <li><strong>Nom de l'empresa</strong> (name)</li>
     *   <li><strong>Nom de contacte</strong> (contactName)</li>
     *   <li><strong>Email</strong> (email)</li>
     *   <li><strong>Telèfon</strong> (phone)</li>
     *   <li><strong>Adreça</strong> (address)</li>
     * </ul>
     * </p>
     *
     * <p>Exemples d'ús:
     * <pre>
     * GET /api/suppliers/search?searchText=Barcelona&page=0&size=10&sortBy=name&sortDir=asc
     * GET /api/suppliers/search?searchText=@gmail.com
     * GET /api/suppliers/search?searchText=93
     * </pre>
     * </p>
     *
     * @param searchDTO paràmetres de cerca (Spring els mapeja automàticament des dels query params)
     * @return resposta amb la pàgina de proveïdors trobats
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<SupplierResponseDTO>>> searchSuppliersByText(
            @Valid SupplierSearchDTO searchDTO) {

        Sort sort = searchDTO.getSortDir().equalsIgnoreCase("desc") ?
                Sort.by(searchDTO.getSortBy()).descending() :
                Sort.by(searchDTO.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), sort);

        Page<SupplierResponseDTO> suppliers = supplierService.searchSuppliersByText(
                searchDTO.getSearchText(), pageable);

        // Convertir Page a PagedResponseDTO per evitar warning de serialització
        PagedResponseDTO<SupplierResponseDTO> pagedResponse = PagedResponseDTO.of(suppliers);

        return ResponseEntity.ok(
                ApiResponseDTO.success(pagedResponse, "Cerca bàsica de proveïdors completada"));
    }

    /**
     * Cerca avançada de proveïdors amb múltiples filtres.
     * El companyUuid s'obté automàticament de l'usuari autenticat.
     *
     * <p>Aquest endpoint permet filtrar proveïdors de l'empresa de l'usuari utilitzant
     * tots els camps disponibles, incloent-hi filtres de text.</p>
     *
     * <p>Filtres disponibles:
     * <ul>
     *   <li><strong>Text:</strong> name, contactName, email, phone, address</li>
     * </ul>
     * </p>
     *
     * <p>Exemple d'ús complet:
     * <pre>
     * GET /api/suppliers/filter?name=Catalunya&contactName=Joan&email=@provcat.com
     *     &phone=93&address=Barcelona&page=0&size=10&sortBy=name&sortDir=asc
     * </pre>
     * </p>
     *
     * <p>Exemple d'ús mínim:
     * <pre>
     * GET /api/suppliers/filter?name=Catalunya
     * </pre>
     * </p>
     *
     * @param filterDTO paràmetres de filtratge (Spring els mapeja automàticament des dels query params)
     * @return resposta amb la pàgina de proveïdors filtrats
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<SupplierResponseDTO>>> filterSuppliers(
            @Valid SupplierFilterDTO filterDTO) {

        Sort sort = filterDTO.getSortDir().equalsIgnoreCase("desc") ?
                Sort.by(filterDTO.getSortBy()).descending() :
                Sort.by(filterDTO.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);

        Page<SupplierResponseDTO> suppliers = supplierService.searchSuppliersWithFilters(
                filterDTO, pageable);

        // Convertir Page a PagedResponseDTO per evitar warning de serialització
        PagedResponseDTO<SupplierResponseDTO> pagedResponse = PagedResponseDTO.of(suppliers);

        String message = String.format("Cerca avançada completada. Filtres aplicats: text=%s",
                filterDTO.hasTextFilters());

        return ResponseEntity.ok(
                ApiResponseDTO.success(pagedResponse, message));
    }

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
     * Actualitza un proveïdor existent.
     *
     * @param uuid l'UUID del proveïdor a actualitzar
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