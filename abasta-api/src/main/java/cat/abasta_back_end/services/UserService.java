package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.exceptions.BadRequestException;
import cat.abasta_back_end.exceptions.DuplicateResourceException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfície del servei de negoci per a la gestió integral d'usuaris.
 * Defineix les operacions disponibles per administrar usuaris dins del sistema.
 *
 * <p>Aquest servei actua com a capa intermèdia entre els controladors REST i la capa
 * de persistència, encapsulant tota la lògica de negoci relacionada amb els usuaris.
 * S'ha actualitzat per suportar cerca avançada amb extracció automàtica del companyUuid.</p>
 *
 * <p>Les responsabilitats del servei inclouen:
 * <ul>
 *   <li>Gestió completa del cicle de vida dels usuaris (CRUD)</li>
 *   <li>Autenticació i gestió de sessions amb JWT</li>
 *   <li>Validacions de negoci i integritat de dades</li>
 *   <li>Transformació entre entitats i DTOs</li>
 *   <li>Aplicació de regles de negoci específiques</li>
 *   <li>Gestió de relacions amb entitats Company</li>
 *   <li>Operacions de cerca i filtratge avançat amb múltiples criteris</li>
 *   <li>Restabliment segur de contrasenyes amb tokens temporals</li>
 *   <li>Verificació d'emails amb activació automàtica d'empreses</li>
 *   <li>Extracció automàtica del companyUuid des de l'usuari autenticat</li>
 * </ul>
 * </p>
 *
 * <p>Les operacions de cerca poden utilitzar diversos criteris:
 * <ul>
 *   <li>Per identificador únic (UUID)</li>
 *   <li>Per empresa associada (extret automàticament de l'usuari)</li>
 *   <li>Per email (cerca parcial i insensible a majúscules)</li>
 *   <li>Per nom i cognoms (cerca parcial)</li>
 *   <li>Per telèfon</li>
 *   <li>Per rol d'usuari (ADMIN, USER)</li>
 *   <li>Per estat d'activitat (actiu/inactiu)</li>
 *   <li>Per estat de verificació d'email</li>
 *   <li>Combinació de múltiples filtres simultàniament</li>
 *   <li>Amb suport de paginació i ordenació per qualsevol camp</li>
 * </ul>
 * </p>
 *
 * <p>Validacions implementades automàticament:
 * <ul>
 *   <li>Verificació d'existència de l'empresa associada</li>
 *   <li>Unicitat de l'email dins del sistema</li>
 *   <li>Validació de formats (email, telèfon, contrasenya)</li>
 *   <li>Comprovació de límits de longitud de camps</li>
 *   <li>Validació de tokens de verificació i restabliment</li>
 *   <li>Seguretat: l'usuari només pot accedir a usuaris de la seva empresa</li>
 *   <li>Control d'accés basat en rols (només ADMIN pot gestionar usuaris)</li>
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
 * @version 2.0
 * @since 2025
 * @see UserRegistrationDTO
 * @see UserRequestDTO
 * @see UserResponseDTO
 * @see UserFilterDTO
 * @see cat.abasta_back_end.entities.User
 */
public interface UserService {

    /**
     * Autentica un usuari a la plataforma mitjançant les seves credencials (correu electrònic i la contrasenya).
     * Si les credencials són correctes, retorna un token o la informació necessària per mantenir la sessió iniciada.
     *
     * @param loginDTO l'objecte que conté les credencials d'inici de sessió (correu electrònic i contrasenya)
     * @return un {@link LoginResponseDTO} amb la informació de l'usuari autenticat i/o el token de sessió
     */
    LoginResponseDTO login(LoginRequestDTO loginDTO);

    /**
     * Inicia el procés de restabliment de contrasenya per a un usuari.
     * Genera un token de restabliment i envia un correu electrònic amb les instruccions.
     *
     * @param email l'adreça de correu electrònic de l'usuari que vol restablir la contrasenya
     * @throws ResourceNotFoundException si no es troba cap usuari amb l'email especificat
     */
    void requestPasswordReset(String email);

    /**
     * Restableix la contrasenya d'un usuari utilitzant un token de restabliment vàlid.
     *
     * @param passwordResetDTO l'objecte que conté el token i la nova contrasenya
     * @throws BadRequestException si el token és invàlid o ha expirat
     */
    void resetPassword(PasswordResetDTO passwordResetDTO);

    /**
     * Verifica l'adreça de correu electrònic d'un usuari utilitzant un token de verificació.
     * Si l'usuari és un administrador d'empresa, també activa l'empresa associada.
     *
     * @param token el token de verificació d'email
     * @throws BadRequestException si el token és invàlid o ha expirat
     */
    void verifyEmail(String token);

    /**
     * Reenvia el correu electrònic de verificació a un usuari.
     * Genera un nou token de verificació abans d'enviar el correu.
     *
     * @param email l'adreça de correu electrònic de l'usuari
     * @throws ResourceNotFoundException si no es troba cap usuari amb l'email especificat
     * @throws BadRequestException si l'email ja està verificat
     */
    void resendVerificationEmail(String email);

    /**
     * Registra un nou usuari al sistema.
     * <p>
     * Aquest mètode crea un nou compte d'usuari associat a l'empresa de l'usuari
     * autenticat.
     * Només els usuaris amb rol d'administrador poden registrar un nou usuari.
     * El procés inclou:
     * </p>
     * <ul>
     *   <li>Validació que el rol sigui administrador</li>
     *   <li>Validació que l'email no estigui duplicat</li>
     *   <li>Verificació de l'existència de l'empresa</li>
     *   <li>Generació d'un token de verificació vàlid per 24 hores</li>
     *   <li>Encriptació de la contrasenya</li>
     *   <li>Enviament d'un correu electrònic de verificació</li>
     * </ul>
     * <p>
     * L'usuari creat romandrà inactiu fins que verifiqui el seu correu electrònic
     * mitjançant el token generat.
     * </p>
     *
     * @param registrationDTO les dades de registre de l'usuari. No pot ser null
     * @return UserResponseDTO amb les dades de l'usuari creat
     * @throws DuplicateResourceException si ja existeix un usuari amb l'email proporcionat
     * @throws ResourceNotFoundException si l'empresa associada no existeix
     * @throws BadRequestException si l'usuari autenticat no té rol d'administrador
     */
    UserResponseDTO registerUser(UserRegistrationDTO registrationDTO);

    /**
     * Obté tots els usuaris no eliminats de l'empresa de l'usuari autenticat amb paginació.
     *
     * <p>Aquest mètode implementa el patró de soft delete, retornant únicament els usuaris
     * que no han estat marcats com a eliminats ({@code isDeleted = false}), incloent tant
     * usuaris actius com inactius. Proporciona una vista completa dels usuaris disponibles
     * per a l'administrador de l'empresa.</p>
     *
     * <p><strong>Funcionalitat principal:</strong>
     * <ul>
     *   <li>Extracció automàtica del {@code companyUuid} des del context de seguretat</li>
     *   <li>Filtratge automàtic només d'usuaris no eliminats ({@code isDeleted = false})</li>
     *   <li>Inclou usuaris actius i inactius</li>
     *   <li>Suport complet de paginació i ordenació</li>
     *   <li>Transformació automàtica d'entitats a DTOs de resposta</li>
     *   <li>Aïllament total de dades per empresa (multi-tenant)</li>
     *   <li>Control d'accés: només ADMIN pot accedir</li>
     * </ul>
     * </p>
     *
     * <p><strong>Seguretat i autorització:</strong><br>
     * El mètode utilitza getCompanyUuidFromAuthenticatedUser() per extreure
     * automàticament l'identificador de l'empresa des de l'usuari actualment autenticat
     * mitjançant Spring Security. A més, valida que l'usuari tingui rol d'ADMIN.
     * Això garanteix que cada administrador només pugui accedir als usuaris de la seva
     * pròpia empresa.</p>
     *
     * <p><strong>Exemples d'ús des del controlador:</strong>
     * <pre>
     * // Obtenir primera pàgina amb 10 usuaris, ordenats per email
     * Pageable pageable = PageRequest.of(0, 10, Sort.by("email").ascending());
     * Page&lt;UserResponseDTO&gt; result = userService.getAllUsersPaginated(pageable);
     *
     * // Obtenir segona pàgina amb 20 usuaris, ordenats per data de creació
     * Pageable pageable = PageRequest.of(1, 20, Sort.by("createdAt").descending());
     * Page&lt;UserResponseDTO&gt; result = userService.getAllUsersPaginated(pageable);
     * </pre>
     * </p>
     *
     * @param pageable configuració de paginació i ordenació. No pot ser {@code null}
     * @return una {@link Page} de {@link UserResponseDTO} amb els usuaris no eliminats
     *         de l'empresa de l'usuari autenticat
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si l'usuari autenticat no existeix
     * @throws cat.abasta_back_end.exceptions.BadRequestException si l'usuari no té rol d'ADMIN
     * @see UserResponseDTO
     * @see Pageable
     * @see Page
     */
    Page<UserResponseDTO> getAllUsersPaginated(Pageable pageable);

    /**
     * Obté un usuari pel seu identificador UUID.
     * Només els usuaris amb rol d'administrador poden accedir a aquesta informació.
     *
     * @param uuid identificador únic de l'usuari a cercar
     * @return DTO amb les dades de l'usuari trobat
     * @throws ResourceNotFoundException si no existeix cap usuari amb l'UUID especificat
     * @throws BadRequestException si l'usuari autenticat no té rol d'administrador
     */
    UserResponseDTO getUserByUuid(String uuid);

    /**
     * Cerca bàsica d'usuaris per text en múltiples camps de l'empresa de l'usuari autenticat.
     * Cerca simultàniament en: email, firstName, lastName i phone.
     * Utilitza el context de Spring Security per identificar l'usuari.
     * Retorna només usuaris actius i no eliminats.
     *
     * <p>Aquest mètode proporciona una cerca ràpida i flexible que permet trobar usuaris
     * introduint qualsevol fragment de text que pugui coincidir amb els seus camps principals.
     * És ideal per implementar funcionalitats de cerca en temps real en interfícies d'usuari.</p>
     *
     * <p><strong>Camps de cerca:</strong>
     * <ul>
     *   <li><strong>email</strong>: cerca parcial en l'adreça de correu electrònic</li>
     *   <li><strong>firstName</strong>: cerca parcial en el nom de l'usuari</li>
     *   <li><strong>lastName</strong>: cerca parcial en els cognoms</li>
     *   <li><strong>phone</strong>: cerca parcial en el número de telèfon</li>
     * </ul>
     * </p>
     *
     * <p><strong>Comportament:</strong>
     * <ul>
     *   <li>La cerca és insensible a majúscules i minúscules</li>
     *   <li>Retorna usuaris si el text coincideix amb QUALSEVOL dels camps</li>
     *   <li>Sempre filtra per: {@code isDeleted = false} i {@code isActive = true}</li>
     *   <li>Si {@code text} és {@code null}, retorna tots els usuaris actius</li>
     * </ul>
     * </p>
     *
     * @param text el text a cercar (pot ser null per obtenir tots els usuaris actius)
     * @param pageable informació de paginació
     * @return pàgina d'usuaris que compleixen el criteri
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si l'usuari no existeix o no té empresa assignada
     * @see UserResponseDTO
     * @see Pageable
     * @see Page
     */
    Page<UserResponseDTO> searchUsersByText(String text, Pageable pageable);

    /**
     * Cerca avançada amb filtres múltiples per l'empresa de l'usuari autenticat.
     * Utilitza el context de Spring Security per identificar l'usuari.
     * Retorna només usuaris no eliminats.
     *
     * <p>Aquest mètode permet realitzar cerques molt específiques combinant diversos
     * criteris simultàniament. A diferència de la cerca per text, aquí cada filtre
     * s'aplica al seu camp específic, permetent cerques més precises.</p>
     *
     * <p><strong>Filtres disponibles (tots opcionals):</strong>
     * <ul>
     *   <li><strong>email</strong>: cerca parcial específica en l'email</li>
     *   <li><strong>firstName</strong>: cerca parcial específica en el nom</li>
     *   <li><strong>lastName</strong>: cerca parcial específica en els cognoms</li>
     *   <li><strong>phone</strong>: cerca parcial específica en el telèfon</li>
     *   <li><strong>isActive</strong>: filtre usuaris actius o inactius</li>
     * </ul>
     * </p>
     *
     * <p><strong>Comportament dels filtres:</strong>
     * <ul>
     *   <li>Tots els filtres són opcionals (poden ser {@code null})</li>
     *   <li>Els filtres s'apliquen amb operador AND (tots han de complir-se)</li>
     *   <li>La cerca és insensible a majúscules i minúscules</li>
     *   <li>Sempre filtra per: {@code isDeleted = false}</li>
     * </ul>
     * </p>
     *
     * <p><strong>Exemples d'ús:</strong>
     * <pre>
     * // Cercar usuaris amb email "john" i cognoms "Doe"
     * UserFilterDTO filter = new UserFilterDTO();
     * filter.setEmail("john");
     * filter.setLastName("Doe");
     * Page&lt;UserResponseDTO&gt; users = userService.searchUsersWithFilters(filter, pageable);
     *
     * // Cercar només per telèfon
     * UserFilterDTO filter = new UserFilterDTO();
     * filter.setPhone("555");
     * Page&lt;UserResponseDTO&gt; users = userService.searchUsersWithFilters(filter, pageable);
     * </pre>
     * </p>
     *
     * @param filterDTO paràmetres de filtratge (tots opcionals)
     * @param pageable informació de paginació
     * @return pàgina d'usuaris filtrats
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si l'usuari no existeix o no té empresa assignada
     * @see UserFilterDTO
     * @see UserResponseDTO
     * @see Pageable
     * @see Page
     */
    Page<UserResponseDTO> searchUsersWithFilters(UserFilterDTO filterDTO, Pageable pageable);

    /**
     * Actualitza les dades d'un usuari existent.
     * <p>
     * Permet modificar l'email, nom, cognoms, telèfon, rol i estat actiu de l'usuari.
     * Valida que el nou email no estigui ja en ús per un altre usuari.
     * Els camps rol i estat actiu només s'actualitzen si es proporcionen valors no nuls.
     * Només els usuaris amb rol d'administrador poden actualitzar les dades de l'usuari.
     * </p>
     *
     * @param uuid identificador únic de l'usuari a actualitzar
     * @param userRequestDTO DTO amb les noves dades de l'usuari
     * @return DTO amb les dades de l'usuari actualitzat
     * @throws ResourceNotFoundException si no existeix cap usuari amb l'UUID especificat
     * @throws DuplicateResourceException si el nou email ja està registrat per un altre usuari
     * @throws BadRequestException si l'usuari autenticat no té rol d'administrador
     */
    UserResponseDTO updateUser(String uuid, UserRequestDTO userRequestDTO);

    /**
     * Canvia l'estat actiu/inactiu d'un usuari.
     * Només els usuaris amb rol d'administrador poden canviar l'estat.
     *
     * @param uuid identificador únic de l'usuari
     * @param isActive nou estat d'activació ({@code true} per actiu, {@code false} per inactiu)
     * @return DTO amb les dades de l'usuari amb l'estat actualitzat
     * @throws ResourceNotFoundException si no existeix cap usuari amb l'UUID especificat
     * @throws BadRequestException si l'usuari autenticat no té rol d'administrador
     */
    UserResponseDTO changeUserStatus(String uuid, Boolean isActive);

    /**
     * Canvia la contrasenya d'un usuari.
     * <p>
     * Requereix verificació de la contrasenya actual abans de permetre el canvi.
     * La nova contrasenya s'emmagatzema codificada.
     * </p>
     *
     * @param uuid identificador únic de l'usuari
     * @param passwordChangeDTO DTO amb la contrasenya actual i la nova contrasenya
     * @throws ResourceNotFoundException si no existeix cap usuari amb l'UUID especificat
     * @throws BadRequestException si la contrasenya actual proporcionada no és correcta
     */
    void changePassword(String uuid, PasswordChangeDTO passwordChangeDTO);

    /**
     * Elimina un usuari de forma lògica (soft delete).
     * Només els usuaris amb rol d'administrador poden eliminar usuaris.
     *
     * <p>
     * L'usuari no s'elimina físicament de la base de dades,
     * sinó que es marca com a eliminat per mantenir la integritat referencial.
     * </p>
     *
     * @param uuid identificador únic de l'usuari a eliminar
     * @throws ResourceNotFoundException si no existeix cap usuari amb l'UUID especificat
     * @throws BadRequestException si l'usuari autenticat no té rol d'administrador
     */
    void deleteUser(String uuid);
}