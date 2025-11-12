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
 * @version 1.0
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


}