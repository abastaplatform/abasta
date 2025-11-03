package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.SupplierRequestDTO;
import cat.abasta_back_end.dto.SupplierResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfície del servei de negoci per a la gestió integral de proveïdors.
 * Defineix les operacions disponibles per administrar proveïdors dins del sistema.
 *
 * <p>Aquest servei actua com a capa intermèdia entre els controladors REST i la capa
 * de persistència, encapsulant tota la lògica de negoci relacionada amb els proveïdors.</p>
 *
 * <p>Les responsabilitats del servei inclouen:
 * <ul>
 *   <li>Gestió completa del cicle de vida dels proveïdors (CRUD)</li>
 *   <li>Validacions de negoci i integritat de dades</li>
 *   <li>Transformació entre entitats i DTOs</li>
 *   <li>Aplicació de regles de negoci específiques</li>
 *   <li>Gestió de relacions amb entitats Company</li>
 *   <li>Operacions de cerca i filtratge avançat</li>
 * </ul>
 * </p>
 *
 * <p>Les operacions de cerca poden utilitzar diversos criteris:
 * <ul>
 *   <li>Per identificador únic (UUID)</li>
 *   <li>Per empresa associada (UUID)</li>
 *   <li>Per nom (cerca parcial i insensible a majúscules)</li>
 *   <li>Per estat d'activitat</li>
 *   <li>Combinació de múltiples filtres</li>
 *   <li>Amb suport de paginació i ordenació</li>
 * </ul>
 * </p>
 *
 * <p>Validacions implementades automàticament:
 * <ul>
 *   <li>Verificació d'existència de l'empresa associada</li>
 *   <li>Unicitat del nom de proveïdor dins de la mateixa empresa</li>
 *   <li>Validació de formats (email, telèfon, etc.)</li>
 *   <li>Comprovació de límits de longitud de camps</li>
 * </ul>
 * </p>
 *
 * <p>Gestió d'excepcions personalitzades:
 * <ul>
 *   <li>{@link cat.abasta_back_end.exceptions.ResourceNotFoundException}: quan no es troba un recurs</li>
 *   <li>{@link cat.abasta_back_end.exceptions.DuplicateResourceException}: quan es viola la unicitat</li>
 *   <li>{@link cat.abasta_back_end.exceptions.BadRequestException}: per dades invàlides</li>
 * </ul>
 * </p>
 *
 * <p>Exemple d'implementació en un controlador:
 * <pre>
 * {@literal @}Autowired
 * private SupplierService supplierService;
 *
 * {@literal @}PostMapping
 * public ResponseEntity&lt;ApiResponseDTO&lt;SupplierResponseDTO&gt;&gt; createSupplier(
 *         {@literal @}Valid {@literal @}RequestBody SupplierRequestDTO request) {
 *     SupplierResponseDTO supplier = supplierService.createSupplier(request);
 *     return ResponseEntity.ok(ApiResponseDTO.success(supplier, "Proveïdor creat"));
 * }
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 1.0
 * @see SupplierRequestDTO
 * @see SupplierResponseDTO
 * @see cat.abasta_back_end.entities.Supplier
 */
public interface SupplierService {

    /**
     * Crea un nou proveïdor.
     *
     * @param supplierRequestDTO les dades del proveïdor a crear
     * @return el proveïdor creat amb totes les seves dades
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si l'empresa no existeix
     * @throws cat.abasta_back_end.exceptions.DuplicateResourceException si ja existeix un proveïdor amb el mateix nom a l'empresa
     */
    SupplierResponseDTO createSupplier(SupplierRequestDTO supplierRequestDTO);

    /**
     * Obté un proveïdor pel seu UUID.
     *
     * @param uuid l'UUID del proveïdor
     * @return les dades del proveïdor
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si el proveïdor no existeix
     */
    SupplierResponseDTO getSupplierByUuid(String uuid);

    /**
     * Actualitza un proveïdor existent.
     *
     * @param uuid l'UUID del proveïdor a actualitzar
     * @param supplierRequestDTO les noves dades del proveïdor
     * @return el proveïdor actualitzat
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si el proveïdor no existeix
     * @throws cat.abasta_back_end.exceptions.DuplicateResourceException si el nou nom ja existeix a l'empresa
     */
    SupplierResponseDTO updateSupplier(String uuid, SupplierRequestDTO supplierRequestDTO);

    /**
     * Obté tots els proveïdors d'una empresa específica.
     *
     * @param companyUuid l'UUID de l'empresa
     * @return llista de proveïdors de l'empresa
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si l'empresa no existeix
     */
    List<SupplierResponseDTO> getSuppliersByCompanyUuid(String companyUuid);

    /**
     * Activa o desactiva un proveïdor.
     *
     * @param uuid l'UUID del proveïdor
     * @param isActive l'estat d'activitat a establir
     * @return el proveïdor amb l'estat actualitzat
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si el proveïdor no existeix
     */
    SupplierResponseDTO toggleSupplierStatus(String uuid, Boolean isActive);
}