package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositori JPA per a la gestió de l'accés a dades de proveïdors.
 * Proporciona mètodes per a les operacions CRUD i consultes avançades sobre l'entitat Supplier.
 *
 * <p>Aquest repositori estén JpaRepository per obtenir automàticament les operacions
 * bàsiques de base de dades i defineix mètodes personalitzats per a consultes específiques
 * del domini de proveïdors.</p>
 *
 * <p>Les funcionalitats proporcionades inclouen:
 * <ul>
 *   <li>Operacions CRUD bàsiques heretades de JpaRepository</li>
 *   <li>Cerques per UUID, nom i estat actiu</li>
 *   <li>Consultes amb paginació per optimitzar el rendiment</li>
 *   <li>Validacions d'existència per evitar duplicats</li>
 *   <li>Consultes personalitzades amb @Query per cerques complexes</li>
 * </ul>
 * </p>
 *
 * <p>Els mètodes de cerca utilitzen nomenclatura estàndard de Spring Data JPA:
 * <ul>
 *   <li>findBy*: per cerques que retornen entitats</li>
 *   <li>existsBy*: per validacions d'existència</li>
 *   <li>*IgnoreCase: per cerques insensibles a majúscules/minúscules</li>
 *   <li>*Containing: per cerques de text parcial</li>
 * </ul>
 * </p>
 *
 * <p>Exemple d'ús en un servei:
 * <pre>
 * {@literal @}Autowired
 * private SupplierRepository supplierRepository;
 *
 * // Cercar proveïdors actius d'una empresa
 * List&lt;Supplier&gt; activeSuppliers = supplierRepository
 *     .findByCompanyUuidAndIsActive(companyUuid, true);
 *
 * // Cercar amb filtres múltiples i paginació
 * Page&lt;Supplier&gt; suppliers = supplierRepository
 *     .findSuppliersWithFilters(companyUuid, "acme", null, true, pageable);
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 1.0
 * @see Supplier
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
     * Verifica si existeix un proveïdor amb el nom especificat en una empresa per UUID.
     *
     * @param companyUuid l'UUID de l'empresa
     * @param name el nom del proveïdor
     * @return true si existeix, false altrament
     */
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.company.uuid = :companyUuid AND LOWER(s.name) = LOWER(:name)")
    boolean existsByCompanyUuidAndNameIgnoreCase(@Param("companyUuid") String companyUuid, @Param("name") String name);

}