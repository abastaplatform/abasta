package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositori per operacions d'accés als registres de les comandes (orders)
 * Proporciona mètodes estàndards de JPA i consultes personalitzades per la gestió de productes
 * dintre de la comanda a la plataforma
 * Els mètodes per defecte de JPA són save, findById, findAll, deleteById, existsById i count.
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}