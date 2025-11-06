package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.SupplierRequestDTO;
import cat.abasta_back_end.dto.SupplierResponseDTO;
import cat.abasta_back_end.dto.SupplierFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfície del servei de negoci per a la gestió integral de proveïdors.
 * Defineix les operacions disponibles per administrar proveïdors dins del sistema.
 *
 * <p>Aquest servei actua com a capa intermèdia entre els controladors REST i la capa
 * de persistència, encapsulant tota la lògica de negoci relacionada amb els proveïdors.
 * S'ha actualitzat per suportar cerca avançada amb extracció automàtica del companyUuid.</p>
 *
 * <p>Les responsabilitats del servei inclouen:
 * <ul>
 *   <li>Gestió completa del cicle de vida dels proveïdors (CRUD)</li>
 *   <li>Validacions de negoci i integritat de dades</li>
 *   <li>Transformació entre entitats i DTOs</li>
 *   <li>Aplicació de regles de negoci específiques</li>
 *   <li>Gestió de relacions amb entitats Company</li>
 *   <li>Operacions de cerca i filtratge avançat amb múltiples criteris</li>
 *   <li>Extracció automàtica del companyUuid des de l'usuari autenticat</li>
 * </ul>
 * </p>
 *
 * <p>Les operacions de cerca poden utilitzar diversos criteris:
 * <ul>
 *   <li>Per identificador únic (UUID)</li>
 *   <li>Per empresa associada (extret automàticament de l'usuari)</li>
 *   <li>Per nom (cerca parcial i insensible a majúscules)</li>
 *   <li>Per informació de contacte (email, telèfon)</li>
 *   <li>Per ubicació (adreça)</li>
 *   <li>Per notes o comentaris</li>
 *   <li>Per estat d'activitat</li>
 *   <li>Per rangs de dates (creació, actualització)</li>
 *   <li>Combinació de múltiples filtres simultàniament</li>
 *   <li>Amb suport de paginació i ordenació per qualsevol camp</li>
 * </ul>
 * </p>
 *
 * <p>Validacions implementades automàticament:
 * <ul>
 *   <li>Verificació d'existència de l'empresa associada</li>
 *   <li>Unicitat del nom de proveïdor dins de la mateixa empresa</li>
 *   <li>Validació de formats (email, telèfon, etc.)</li>
 *   <li>Comprovació de límits de longitud de camps</li>
 *   <li>Seguretat: l'usuari només pot accedir a proveïdors de la seva empresa</li>
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
 * @author Enrique Pérez
 * @version 3.0
 * @since 1.0
 * @see SupplierRequestDTO
 * @see SupplierResponseDTO
 * @see SupplierFilterDTO
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

    /**
     * Cerca proveïdors d'una empresa per nom amb paginació.
     * Utilitza filtres bàsics per empresa i nom.
     *
     * @param companyUuid l'UUID de l'empresa (obligatori)
     * @param name el nom a cercar (cerca parcial, insensible a majúscules, pot ser null)
     * @param pageable informació de paginació i ordenació
     * @return pàgina de proveïdors de l'empresa que coincideixen amb el nom
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si l'empresa no existeix
     */
    Page<SupplierResponseDTO> searchSuppliersByCompanyAndName(String companyUuid, String name, Pageable pageable);

    /**
     * Obté tots els proveïdors de l'empresa de l'usuari autenticat.
     * Utilitza el context de Spring Security per identificar l'usuari.
     *
     * @return llista de proveïdors de l'empresa de l'usuari autenticat
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si l'usuari no existeix o no té empresa assignada
     */
    List<SupplierResponseDTO> getAllSuppliers();

    /**
     * Cerca proveïdors per nom de l'empresa de l'usuari autenticat amb paginació.
     * Utilitza el context de Spring Security per identificar l'usuari.
     *
     * @param name el nom a cercar (pot ser null per obtenir tots)
     * @param pageable informació de paginació
     * @return pàgina de proveïdors que compleixen el criteri
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si l'usuari no existeix o no té empresa assignada
     */
    Page<SupplierResponseDTO> searchSuppliersByName(String name, Pageable pageable);

    /**
     * Cerca avançada amb filtres per l'empresa de l'usuari autenticat.
     * Utilitza el context de Spring Security per identificar l'usuari.
     *
     * @param filterDTO paràmetres de filtratge
     * @param pageable informació de paginació
     * @return pàgina de proveïdors filtrats
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si l'usuari no existeix o no té empresa assignada
     */
    Page<SupplierResponseDTO> searchSuppliersWithFilters(SupplierFilterDTO filterDTO, Pageable pageable);

}