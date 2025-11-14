package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
 * @version 1.1
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

}