package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.SupplierRequestDTO;
import cat.abasta_back_end.dto.SupplierResponseDTO;
import cat.abasta_back_end.dto.SupplierFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Activa o desactiva un proveïdor.
     *
     * @param uuid l'UUID del proveïdor
     * @param isActive l'estat d'activitat a establir
     * @return el proveïdor amb l'estat actualitzat
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si el proveïdor no existeix
     */
    SupplierResponseDTO toggleSupplierStatus(String uuid, Boolean isActive);

    /**
     * Obté tots els proveïdors actius de l'empresa de l'usuari autenticat amb paginació.
     *
     * <p>Aquest mètode implementa el patró de soft delete, retornant únicament els proveïdors
     * que tenen l'estat {@code isActive = true}, ocultant aquells que han estat "eliminats"
     * del sistema mitjançant la funcionalitat de soft delete. Proporciona una vista neta
     * i operativa dels proveïdors disponibles per a l'usuari.</p>
     *
     * <p><strong>Funcionalitat principal:</strong>
     * <ul>
     *   <li>Extracció automàtica del {@code companyUuid} des del context de seguretat</li>
     *   <li>Filtratge automàtic només de proveïdors actius ({@code isActive = true})</li>
     *   <li>Suport complet de paginació i ordenació</li>
     *   <li>Transformació automàtica d'entitats a DTOs de resposta</li>
     *   <li>Aïllament total de dades per empresa (multi-tenant)</li>
     * </ul>
     * </p>
     *
     * <p><strong>Seguretat i autorització:</strong><br>
     * El mètode utilitza getCompanyUuidFromAuthenticatedUser() per extreure
     * automàticament l'identificador de l'empresa des de l'usuari actualment autenticat
     * mitjançant Spring Security. Això garanteix que cada usuari només pugui accedir
     * als proveïdors de la seva pròpia empresa, proporcionant seguretat automàtica
     * en entorns multi-tenant.</p>
     *
     * <p><strong>Transaccionalitat i rendiment:</strong><br>
     * El mètode està marcat com {@code @Transactional(readOnly = true)}, optimitzant
     * el rendiment per a operacions de només lectura i garantint consistència de dades
     * durant la consulta. La configuració de només lectura permet a l'ORM aplicar
     * optimitzacions específiques per a consultes.</p>
     *
     * <p><strong>Transformació de dades:</strong><br>
     * Utilitza el patró de mapejat funcional amb {@code Page.map(this::mapToResponseDTO)}
     * per convertir eficientment les entitats Supplier a objectes
     * {@link SupplierResponseDTO}, mantenint tota la informació de paginació intacta.</p>
     *
     * <p><strong>Casos d'ús típics:</strong>
     * <ul>
     *   <li>Llistat principal de proveïdors en interfícies d'usuari</li>
     *   <li>Funcionalitat de navegació per pàgines en aplicacions web</li>
     *   <li>APIs RESTful que necessiten paginació per a grans volums de dades</li>
     *   <li>Dashboards i panells de control empresarials</li>
     * </ul>
     * </p>
     *
     * <p><strong>Exemples d'ús des del controlador:</strong>
     * <pre>
     * // Obtenir primera pàgina amb 10 proveïdors, ordenats per nom
     * Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
     * Page&lt;SupplierResponseDTO&gt; result = supplierService.getAllSuppliersPaginated(pageable);
     *
     * // Obtenir segona pàgina amb 20 proveïdors, ordenats per data de creació
     * Pageable pageable = PageRequest.of(1, 20, Sort.by("createdAt").descending());
     * Page&lt;SupplierResponseDTO&gt; result = supplierService.getAllSuppliersPaginated(pageable);
     *
     * // Accés a metadades de paginació
     * int totalPages = result.getTotalPages();
     * long totalElements = result.getTotalElements();
     * boolean hasNext = result.hasNext();
     * </pre>
     * </p>
     *
     * <p><strong>Gestió d'errors:</strong><br>
     * El mètode pot llançar ResourceNotFoundException si l'usuari autenticat
     * no existeix al sistema o no té una empresa assignada. Aquesta validació es
     * realitza dins del mètode getCompanyUuidFromAuthenticatedUser().</p>
     *
     * <p><strong>Consideracions de rendiment:</strong>
     * <ul>
     *   <li>És recomanable limitar la mida de pàgina per evitar sobrecàrrega de memòria</li>
     *   <li>L'ús d'índexs compostos en {@code company_uuid} i {@code is_active} millora significativament el rendiment</li>
     *   <li>La transformació DTO es realitza de manera lazy per optimitzar la memòria</li>
     * </ul>
     * </p>
     *
     * @param pageable configuració de paginació i ordenació. Inclou el número de pàgina
     *                (començant per 0), mida de pàgina, i criteris d'ordenació opcionals.
     *                No pot ser {@code null}.
     * @return una {@link Page} de {@link SupplierResponseDTO} amb els proveïdors actius
     *         de l'empresa de l'usuari autenticat. Inclou tant el contingut de la pàgina
     *         com les metadades de paginació (total d'elements, número de pàgines, etc.).
     *         Si no hi ha proveïdors actius, retorna una pàgina buida però vàlida
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si l'usuari autenticat no existeix al sistema
     *                                 o no té una empresa assignada
     * @throws IllegalArgumentException si el paràmetre {@code pageable} és {@code null}
     * @throws org.springframework.security.access.AccessDeniedException si l'usuari no està
     *                                                                  correctament autenticat
     * @since 2.0
     * @see SupplierResponseDTO
     * @see Pageable
     * @see Page
     * @see org.springframework.data.domain.PageRequest
     * @see org.springframework.transaction.annotation.Transactional
     */
    Page<SupplierResponseDTO> getAllSuppliersPaginated(Pageable pageable);

    /**
     * Cerca bàsica de proveïdors per text en múltiples camps de l'empresa de l'usuari autenticat.
     * Cerca simultàniament en: name, contactName, email, phone i address.
     * Utilitza el context de Spring Security per identificar l'usuari.
     *
     * @param text el text a cercar (pot ser null per obtenir tots)
     * @param pageable informació de paginació
     * @return pàgina de proveïdors que compleixen el criteri
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si l'usuari no existeix o no té empresa assignada
     */
    Page<SupplierResponseDTO> searchSuppliersByText(String text, Pageable pageable);

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