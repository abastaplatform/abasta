package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositori per operacions d'accés a dades de comandes (orders)
 * Proporciona mètodes estàndards de JPA i consultes personalitzades per la gestió de comandes a la plataforma
 * Els mètodes per defecte de JPA són save, findById, findAll, deleteById, existsById i count.
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUuid(String uuid);
}
