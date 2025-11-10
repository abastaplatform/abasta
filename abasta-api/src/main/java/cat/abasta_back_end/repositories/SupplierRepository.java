package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    /**
     * Cerca un proveïdor pel seu UUID.
     *
     * @param uuid l'UUID del proveïdor a cercar
     * @return un Optional que conté el proveïdor si es troba
     */
    Optional<Supplier> findByUuid(String uuid);

    /**
     * Verifica si existeix un proveïdor amb el nom especificat en una empresa per UUID,
     * excloent un proveïdor específic (útil per actualitzacions).
     *
     * @param companyUuid l'UUID de l'empresa
     * @param name el nom del proveïdor
     * @param supplierUuid l'UUID del proveïdor a excloure
     * @return true si existeix, false altrament
     */
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.company.uuid = :companyUuid AND LOWER(s.name) = LOWER(:name) AND s.uuid != :supplierUuid")
    boolean existsByCompanyUuidAndNameIgnoreCaseAndUuidNot(@Param("companyUuid") String companyUuid, @Param("name") String name, @Param("supplierUuid") String supplierUuid);

    /**
     * Cerca avançada de proveïdors amb múltiples filtres.
     * Inclou tots els camps disponibles per a una cerca completa.
     *
     * @param companyId l'identificador de l'empresa
     * @param name el nom a cercar (opcional, cerca parcial)
     * @param contactName el nom de contacte a cercar (opcional, cerca parcial)
     * @param email l'email a cercar (opcional, cerca parcial)
     * @param phone el telèfon a cercar (opcional, cerca parcial)
     * @param address l'adreça a cercar (opcional, cerca parcial)
     * @param isActive l'estat d'activitat (opcional)
     * @param pageable informació de paginació
     * @return pàgina de proveïdors que compleixen els criteris
     */
    @Query("SELECT s FROM Supplier s WHERE " +
            "(s.company.id = :companyId) AND " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:contactName IS NULL OR LOWER(s.contactName) LIKE LOWER(CONCAT('%', :contactName, '%'))) AND " +
            "(:email IS NULL OR LOWER(s.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:phone IS NULL OR LOWER(s.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) AND " +
            "(:address IS NULL OR LOWER(s.address) LIKE LOWER(CONCAT('%', :address, '%'))) AND " +
            "(:isActive IS NULL OR s.isActive = :isActive)")
    Page<Supplier> findSuppliersWithFilters(@Param("companyId") Long companyId,
                                                    @Param("name") String name,
                                                    @Param("contactName") String contactName,
                                                    @Param("email") String email,
                                                    @Param("phone") String phone,
                                                    @Param("address") String address,
                                                    @Param("isActive") Boolean isActive,
                                                    Pageable pageable);

    /**
     * Cerca bàsica de proveïdors d'una empresa en múltiples camps de text amb paginació.
     * Cerca en: name, contactName, email, phone i address de forma simultània.
     *
     * @param companyId l'identificador de l'empresa
     * @param searchText el text a cercar (pot ser null per obtenir tots)
     * @param pageable informació de paginació
     * @return pàgina de proveïdors
     */
    @Query("SELECT s FROM Supplier s WHERE s.company.id = :companyId AND " +
            "(:searchText IS NULL OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.contactName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.phone) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.address) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<Supplier> findByCompanyIdAndMultipleFieldsContaining(@Param("companyId") Long companyId,
                                                              @Param("searchText") String searchText,
                                                              Pageable pageable);
}