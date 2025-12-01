package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.services.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST per gestionar les operacions relacionades amb les comandes.
 * <p>
 * Aquest controlador s'encarrega de rebre les peticions HTTP provinents del client
 * i delegar la seva execució al servei {@link OrderService}.
 * </p>
 *
 * <p>Exposa els endpoints principals del recurs <strong>/api/orders</strong>.</p>
 *
 * @author Daniel Garcia
 * @author Enrique Pérez
 * @version 2.0
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Crea una nova comanda.
     * Es rep la informació per crear la comanda i a més a més un llistat de registres
     * que representen un producte afegit a la comanda.
     * Exemple: POST /api/orders/create
     *
     * @param orderRequestDTO dades de la comanda a crear, validades amb {@link Valid}.
     * @return {@link ResponseEntity} amb la comanda creada i codi HTTP 201 (Created).
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> createOrder(
            @Valid @RequestBody OrderRequestDTO orderRequestDTO){

        OrderResponseDTO createOrder = orderService.createOrder(orderRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(createOrder, "Comanda creada correctament"));
    }

    /**
     * Cerca avançada de comandes amb múltiples filtres.
     *
     * <p>Aquest endpoint permet filtrar comandes del proveïdor utilitzant
     * tots els camps disponibles, incloent-hi filtres de text, estat d'activitat i rangs de dates.</p>
     *
     * <p>Filtres disponibles:
     * <ul>
     *   <li><strong>Text:</strong> name, notes, searchText</li>
     *   <li><strong>Estat:</strong></li>
     *   <li><strong>Dates:</strong> createdAfter, createdBefore, updatedAfter, updatedBefore</li>
     * </ul>
     * </p>
     *
     * <p>Exemple d'ús complet:
     * <pre>
     * GET /api/orders/filter?supplierUuid=56632252-12551246&name=Nadal
     * </pre>
     * </p>
     *
     * <p>Exemple d'ús mínim:
     * <pre>
     * GET /filter?name=Nadal
     * </pre>
     * </p>
     *
     * @param filterDTO paràmetres de filtratge (Spring els mapeja automàticament des dels query params)
     * @return resposta amb la pàgina de comandes filtrades
     */
    @GetMapping({"/filter", "/list"})
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<OrderResponseDTO>>> filterOrders(@Valid OrderFilterDTO filterDTO){

        // Ordenació
        Sort sort = filterDTO.getSortDir().equalsIgnoreCase("desc") ?
                Sort.by(filterDTO.getSortBy()).descending() :
                Sort.by(filterDTO.getSortBy()).ascending();

        // Paginació
        Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);
        Page<OrderResponseDTO> orders = orderService.filterOrders(filterDTO, pageable);
        PagedResponseDTO<OrderResponseDTO> pagedResponse = PagedResponseDTO.of(orders);

        // Retorn
        String message = String.format("Cerca avançada de comandes completada. Filtres aplicats: text=%s", filterDTO.hasTextFilters());
        return ResponseEntity.ok(ApiResponseDTO.success(pagedResponse, message));
    }

    /**
     * Envia una comanda existent al proveïdor.
     *
     * <p>Busca la comanda per UUID, valida que estigui en estat PENDING,
     * envia la notificació per email al proveïdor i actualitza l'estat a SENT.</p>
     *
     * <p>Exemple: POST /api/orders/550e8400-e29b-41d4-a716-446655440000/send</p>
     *
     * @param uuid l'UUID de la comanda a enviar
     * @return {@link ResponseEntity} amb la comanda enviada i codi HTTP 200 (OK)
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si no es troba la comanda
     * @throws IllegalStateException si la comanda no està en estat PENDING
     * @throws RuntimeException si falla l'enviament de la notificació
     */
    @PostMapping("/{uuid}/send")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> sendOrder(
            @PathVariable String uuid) {

        OrderResponseDTO sentOrder = orderService.sendOrder(uuid);
        return ResponseEntity
                .ok(ApiResponseDTO.success(sentOrder, "Comanda enviada correctament"));
    }

    /**
     * Desactiva (elimina lògicament) una comanda pel seu UUID.
     * <p>
     * Aquesta operació marca el producte com a status canceled, però no l'elimina
     * físicament de la base de dades.
     * </p>
     *
     * Exemple: PATCH /api/orders/delete/{uuid}
     *
     * @param uuid Identificador únic de la comanda a desactivar.
     * @return {@link OrderResponseDTO} amb la comanda desactivada
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> getOrder(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid) {
        OrderResponseDTO order = orderService.getOrderByUuid(uuid);
        return ResponseEntity.ok(
                ApiResponseDTO.success(order, "Comanda trobada correctament"));
    }

    /**
     * Desactiva (elimina lògicament) una comanda pel seu UUID.
     * <p>
     * Aquesta operació marca el producte com a status canceled, però no l'elimina
     * físicament de la base de dades.
     * </p>
     *
     * Exemple: PATCH /api/orders/delete/{uuid}
     *
     * @param uuid Identificador únic de la comanda a desactivar.
     * @return {@link OrderResponseDTO} amb la comanda desactivada
     */
    @PatchMapping("/delete/{uuid}")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> deleteOrder(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid) {
        OrderResponseDTO deletedOrder = orderService.deleteOrder(uuid);
        return ResponseEntity.ok(
                ApiResponseDTO.success(deletedOrder, "Comanda eliminada correctament"));
    }

    /**
     * Actualitza una comanda existent pel seu UUID.
     * Exemple: PUT /api/orders/edit
     *
     * @param uuid identificador de la comanda
     * @param dto dades noves de la comanda
     * @return {@link OrderResponseDTO} amb les dades actualitzades
     */
    @PutMapping("/update/{uuid}")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> updateOrder(
            @PathVariable @NotBlank String uuid,
            @Valid @RequestBody OrderRequestDTO dto) {

        OrderResponseDTO updatedOrder = orderService.updateOrder(uuid, dto);
        return ResponseEntity.ok(
                ApiResponseDTO.success(updatedOrder, "Comanda actualitzada correctament")
        );
    }


}