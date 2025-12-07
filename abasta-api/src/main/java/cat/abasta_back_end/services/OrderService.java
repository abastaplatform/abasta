package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

/**
 * Servei responsable de la gestió de comandes (orders).
 * <p>
 * Aquesta interfície defineix les operacions principals que es poden realitzar
 * sobre les comandes dins del sistema, com ara crear, actualitzar o eliminar.
 * </p>
 *
 * <p>
 * La implementació concreta d’aquesta interfície s’encarregarà de la lògica de negoci,
 * incloent la creació de comandes i dels seus items associats dins d’una mateixa transacció.
 * </p>
 *
 * @author Daniel Garcia
 * @version 2.0
 */
public interface OrderService {

    /**
     * Crea una nova comanda associada a un usuari i a una empresa.
     * <p>
     * Aquesta operació rep un objecte {@link OrderRequestDTO} amb totes les dades
     * necessàries per crear la comanda i els seus items. El sistema s’encarrega
     * d’assignar l’usuari autenticat i de calcular els imports totals.
     * </p>
     *
     * @param orderRequestDTO dades de la nova comanda rebudes des del client.
     * @return {@link OrderResponseDTO} amb la informació de la comanda creada.
     */
    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO);

    /**
     * Realitza una cerca avançada de comandes amb múltiples filtres.
     * <p>
     * Aquesta operació permet aplicar filtres combinats sobre diversos camps
     * de la comanda (com nom, notes, estat, dates de creació/modificació, etc.),
     * així com paràmetres de paginació i ordenació.
     * </p>
     *
     * @param dto Objecte {@link OrderFilterDTO} amb tots els paràmetres de filtratge.
     * @param pageable Objecte {@link Pageable} per definir la paginació i ordenació dels resultats.
     * @return Una pàgina de {@link OrderResponseDTO} amb les comandes que compleixen els filtres especificats.
     */
    Page<OrderResponseDTO> filterOrders(OrderFilterDTO dto, Pageable pageable);

    /**
     * Retorna les dades d'una comanda concreta
     *
     * @param uuid de la comanda
     * @return OrderResponse amb la comanda
     */
    OrderResponseDTO getOrderByUuid(String uuid);

    /**
     * Elimina una comanda (de manera lògica).
     * Posa la comanda en estat DELETED i l'elimina lògicament.
     *
     * @param uuid de la comanda
     * @return OrderResponse amb la comanda eliminada.
     */
    OrderResponseDTO deleteOrder(String uuid);

    /**
     * Modifica una comanda
     * Les noves dades venen el un OrderRequestDTO.
     *
     * @param uuid de la comanda
     * @param dto dades de la comanda
     * @return OrderResponse amb la comanda actualitzada.
     */
    OrderResponseDTO updateOrder(String uuid, OrderRequestDTO dto);

    /**
     * Envia una comanda existent al proveïdor.
     *
     * <p>Busca la comanda per UUID, envia la notificació per email
     * i actualitza l'estat a SENT.</p>
     *
     * @param orderUuid l'UUID de la comanda a enviar
     * @return la comanda actualitzada amb estat SENT
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si no es troba la comanda
     * @throws RuntimeException si falla l'enviament de la notificació
     */
    OrderResponseDTO sendOrder(String orderUuid);
}
