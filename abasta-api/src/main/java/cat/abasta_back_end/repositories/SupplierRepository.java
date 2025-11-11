package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
     * @param pageable informació de paginació
     * @return pàgina de proveïdors que compleixen els criteris
     */
    @Query("SELECT s FROM Supplier s WHERE s.company.id = :companyId AND s.isActive = true AND " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:contactName IS NULL OR LOWER(s.contactName) LIKE LOWER(CONCAT('%', :contactName, '%'))) AND " +
            "(:email IS NULL OR LOWER(s.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:phone IS NULL OR LOWER(s.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) AND " +
            "(:address IS NULL OR LOWER(s.address) LIKE LOWER(CONCAT('%', :address, '%')))")
    Page<Supplier> findByCompanyIdAndCriteriaActive(
            @Param("companyId") Long companyId,
            @Param("name") String name,
            @Param("contactName") String contactName,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("address") String address,
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
    @Query("SELECT s FROM Supplier s WHERE s.company.id = :companyId AND s.isActive = true AND " +
            "(:searchText IS NULL OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.contactName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.phone) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.address) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<Supplier> findByCompanyIdAndMultipleFieldsContainingActive(
            @Param("companyId") Long companyId,
            @Param("searchText") String searchText,
            Pageable pageable);

    /**
     * Obté tots els proveïdors actius d'una empresa amb paginació.
     *
     * <p>Aquest mètode utilitza les convencions de nomenclatura de Spring Data JPA
     * per generar automàticament la query SQL corresponent. Filtra únicament els
     * proveïdors que tenen l'estat {@code isActive = true}, excloent automàticament
     * aquells que han estat marcats com eliminats mitjançant soft delete.</p>
     *
     * <p>La query generada automàticament és equivalent a:
     * <pre>
     * SELECT s FROM Supplier s
     * WHERE s.company.uuid = :companyUuid
     *   AND s.isActive = true
     * ORDER BY [ordenació especificada al Pageable]
     * </pre>
     * </p>
     *
     * <p>Aquest mètode és especialment útil per:
     * <ul>
     *   <li>Llistar tots els proveïdors operatius d'una empresa</li>
     *   <li>Implementar funcionalitat de soft delete (només mostrar actius)</li>
     *   <li>Millorar l'experiència d'usuari ocultant proveïdors eliminats</li>
     *   <li>Mantenir la integritat referencial mentre s'oculten dades "eliminades"</li>
     * </ul>
     * </p>
     *
     * <p><strong>Seguretat i aïllament de dades:</strong><br>
     * El filtratge per {@code companyUuid} garanteix que cada empresa només pugui
     * accedir als seus propis proveïdors, proporcionant aïllament automàtic de dades
     * en entorns multi-tenant.</p>
     *
     * <p><strong>Exemples d'ús:</strong>
     * <pre>
     * // Primera pàgina amb 10 elements, ordenats per nom
     * Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
     * Page&lt;Supplier&gt; activeSuppliers = repository.findByCompanyUuidAndIsActiveTrue(
     *     "123e4567-e89b-12d3-a456-426614174000", pageable);
     *
     * // Segona pàgina amb 20 elements, ordenats per data de creació descendent
     * Pageable pageable = PageRequest.of(1, 20, Sort.by("createdAt").descending());
     * Page&lt;Supplier&gt; suppliers = repository.findByCompanyUuidAndIsActiveTrue(
     *     companyUuid, pageable);
     * </pre>
     * </p>
     *
     * @param companyUuid l'identificador UUID únic de l'empresa. No pot ser {@code null}
     *                   ni una cadena buida. Ha de correspondre amb un UUID vàlid
     *                   d'una empresa existent al sistema
     * @param pageable   informació de paginació i ordenació. Inclou el número de pàgina,
     *                  mida de pàgina i criteris d'ordenació. No pot ser {@code null}
     * @return una {@link Page} amb els proveïdors actius de l'empresa especificada.
     *         La pàgina conté el contingut sol·licitat segons els paràmetres de paginació,
     *         més metadades sobre el total d'elements, número de pàgines, etc.
     *         Si no es troben proveïdors actius, retorna una pàgina buida
     * @throws org.springframework.dao.InvalidDataAccessApiUsageException si els paràmetres
     *         són invàlids (per exemple, si {@code pageable} és {@code null})
     * @since 2.0
     * @see Pageable
     * @see Page
     * @see org.springframework.data.domain.PageRequest
     * @see org.springframework.data.domain.Sort
     */
    Page<Supplier> findByCompanyUuidAndIsActiveTrue(String companyUuid, Pageable pageable);
}