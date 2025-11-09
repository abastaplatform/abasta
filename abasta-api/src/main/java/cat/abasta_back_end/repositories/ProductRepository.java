package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositori per operacions d'accés a dades de productes.
 * Proporciona mètodes estàndards de JPA i consultes personalitzades per la gestió de productes a la plataforma
 * Els mètodes per defecte de JPA són save, findById, findAll, deleteById, existsById i count.
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Cerca un producte pel seu uuid.
     *
     * @param uuid del producte a cercar
     * @return un Optional que conté el producte si existeix, o Optional.empty() si no es troba
     */
    Optional<Product> findByUuid(String uuid);

    /**
     * Retorna una pàgina de productes associats a un proveïdor i marcats com a actius.
     *
     * @param supplierId identificador del proveïdor
     * @param pageable   objecte de paginació (page, size, sort)
     * @return una {@link Page} de {@link Product}
     */
    /*Page<Product> findBySupplierIdAndIsActiveTrue(Long supplierId, Pageable pageable);*/

    /**
     * Cerca i filtra productes actius segons diversos criteris opcionals.
     * <p>
     * Si un paràmetre és {@code null}, no s’aplica el filtre corresponent.
     * </p>
     *
     * @param q         query lliure de cerca (pot ser null)
     * @param name         nom parcial o complet del producte (pot ser null)
     * @param category     categoria del producte (pot ser null)
     * @param supplierUuid UUID del proveïdor (pot ser null)
     * @param pageable     configuració de paginació
     * @return pàgina de productes que compleixen els criteris especificats
     */
    /*@Query("""
    SELECT p FROM Product p
    WHERE p.isActive = true
      AND (
            :q IS NULL
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(p.category) LIKE LOWER(CONCAT('%', :q, '%'))
          )
      AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:category IS NULL OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')))
      AND (:supplierUuid IS NULL OR p.supplier.uuid = :supplierUuid)
    """)
    Page<Product> searchProducts(
            @Param("q") String q,
            @Param("name") String name,
            @Param("category") String category,
            @Param("supplierUuid") String supplierUuid,
            Pageable pageable);*/

    /**
     * Cerca avançada de productes amb múltiples filtres.
     * Inclou tots els camps disponibles per a una cerca completa.
     *
     * @param name el nom a cercar (opcional, cerca parcial)
     * @param description la descripció a cercar (opcional, cerca parcial)
     * @param category la categoria a cercar (opcional, cerca parcial)
     * @param isActive l'estat d'activitat (opcional)
     * @param pageable informació de paginació
     * @return pàgina de productes que compleixen els criteris
     */
    @Query("SELECT p FROM Product p WHERE p.supplier.id = :supplierId AND " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:description IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
            "(:category IS NULL OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%'))) AND " +
            "(:unit IS NULL OR LOWER(p.unit) = LOWER(:unit)) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:isActive IS NULL OR p.isActive = :isActive)")
    Page<Product> findProductsBySupplierWithFilter(Long supplierId,
                                            @Param("name") String name,
                                            @Param("description") String description,
                                            @Param("category") String category,
                                            @Param("unit") String unit,
                                            @Param("minPrice") BigDecimal minPrice,
                                            @Param("maxPrice") BigDecimal maxPrice,
                                            @Param("isActive") Boolean isActive,
                                            Pageable pageable);

    /**
     * Cerca bàsica de productes d'un proveïdor n múltiples camps de text amb paginació.
     * Cerca en: name, description, category, forma simultània.
     *
     * @param searchText el text a cercar (pot ser null per obtenir tots)
     * @param pageable informació de paginació
     * @return pàgina de productes
     */
    @Query("SELECT p FROM Product p WHERE p.supplier.id = :supplierId AND  " +
            "(:searchText IS NULL OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<Product> findProductsBySupplierWithSearch(Long supplierId, @Param("searchText") String searchText, Pageable pageable);

}