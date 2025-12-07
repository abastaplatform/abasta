package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Order;
import cat.abasta_back_end.entities.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositori per operacions d'accés a dades de comandes (orders)
 * Proporciona mètodes estàndards de JPA i consultes personalitzades per la gestió de comandes a la plataforma
 * Els mètodes per defecte de JPA són save, findById, findAll, deleteById, existsById i count.
 *
 * @author Daniel Garcia
 * @author Enrique Pérez
 * @version 3.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    /**
     * Cerca Comanda per el seu Uuid
     *
     * @param uuid
     * @return Comanda
     */
    Optional<Order> findByUuid(String uuid);

    /**
     * Cerca de comandes per període de creació amb llistat d'items (productes de la comanda)
     * @param companyId id de la companyia
     * @param startDate data d'inici del periode
     * @param endDate data de fi del periode
     * @return
     */
    @Query("""
        SELECT DISTINCT o 
        FROM Order o 
        LEFT JOIN FETCH o.items
        WHERE o.company.id = :companyId
          AND o.createdAt BETWEEN :startDate AND :endDate
    """)
    List<Order> getOrdersByCompanyIdAndPeriodWithOrderItems(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Cerca de comandes per període de creació sense llistat d'items (productes de la comanda)
     * @param companyId id de la companyia
     * @param startDate data d'inici del periode
     * @param endDate data de fi del periode
     * @return
     */
    @Query("""
        SELECT DISTINCT o 
        FROM Order o 
        WHERE o.company.id = :companyId
          AND o.createdAt BETWEEN :startDate AND :endDate
    """)
    List<Order> getOrdersByCompanyIdAndPeriodWithoutOrderItems(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}