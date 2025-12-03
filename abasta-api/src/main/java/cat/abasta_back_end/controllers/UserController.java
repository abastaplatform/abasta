package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST per a la gestió integral d'usuaris a través d'endpoints HTTP.
 * Proporciona una API completa per administrar usuaris amb operacions CRUD i cerca avançada.
 *
 * <p>Aquest controlador exposa tots els endpoints necessaris per gestionar usuaris,
 * seguint les millors pràctiques de REST i proporcionant respostes estandarditzades.
 * Inclou múltiples nivells de cerca des de bàsica fins avançada amb tots els filtres disponibles.</p>
 *
 * <p>Endpoints disponibles:
 * <ul>
 *   <li>GET /api/users/ - Usuaris d'una empresa</li>
 *   <li>GET /api/users/{uuid} - Obtenir usuari per UUID</li>
 *   <li>GET /api/users/search - Cerca bàsica per text</li>
 *   <li>GET /api/users/filter - Cerca avançada amb tots els filtres</li>
 *   <li>POST /api/users - Crear nou usuari</li>
 *   <li>PUT /api/users/{uuid} - Actualitzar usuari existent</li>
 *   <li>PATCH /api/users/{uuid}/status - Canviar estat actiu/inactiu</li>
 *   <li>PATCH /api/users/{uuid}/change-password - Canviar contrasenya</li>
 *   <li>DELETE /api/users/{uuid} - Eliminar usuari (soft delete)</li>
 * </ul>
 * </p>
 *
 * <p>Nivells de cerca implementats:
 * <ul>
 *   <li><strong>Cerca bàsica (/search):</strong> Cerca per qualsevol camp</li>
 *   <li><strong>Cerca avançada (/filter):</strong> Filtres per tots els camps</li>
 * </ul>
 * </p>
 *
 * <p>Filtres disponibles en cerca avançada:
 * <ul>
 *   <li><strong>Filtres de text:</strong> email, firstName, lastName, phone</li>
 * </ul>
 * </p>
 *
 * <p>Funcionalitats de paginació i ordenació:
 * <ul>
 *   <li>Paginació automàtica amb paràmetres page i size</li>
 *   <li>Ordenació configurable amb sortBy i sortDir</li>
 *   <li>Camps d'ordenació: email, firstName, lastName, phone, createdAt, updatedAt</li>
 *   <li>Valors per defecte raonables (page=0, size=10, sortBy="email", sortDir="asc")</li>
 *   <li>Validació de paràmetres amb Bean Validation</li>
 * </ul>
 * </p>
 *
 * <p>Seguretat implementada:
 * <ul>
 *   <li>Un usuari només pot veure usuaris de la seva empresa</li>
 *   <li>Només usuaris amb rol ADMIN poden gestionar usuaris</li>
 *   <li>No es pot manipular el companyUuid des del client</li>
 *   <li>Control d'accés basat en rols</li>
 * </ul>
 * </p>
 *
 * @author Enrique Pérez
 * @version 2.0
 * @see UserService
 * @see UserRegistrationDTO
 * @see UserRequestDTO
 * @see UserResponseDTO
 * @see UserSearchDTO
 * @see UserFilterDTO
 * @since 1.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Obté tots els usuaris de l'empresa de l'usuari autenticat.
     * El companyUuid s'extreu automàticament de l'usuari.
     *
     * <p>Aquest endpoint retorna la llista completa d'usuaris de l'empresa
     * de l'usuari autenticat, amb paginació. Només usuaris amb rol ADMIN
     * poden accedir a aquest endpoint.</p>
     *
     * <p>Exemple d'ús:
     * <pre>
     * GET /api/users
     * Authorization: Bearer {token}
     * </pre>
     * </p>
     *
     * @param searchDTO paràmetres de paginació
     * @return resposta amb la llista d'usuaris
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<UserResponseDTO>>> getAllUsers(
            @Valid UserSearchDTO searchDTO) {
        Sort sort = searchDTO.getSortDir().equalsIgnoreCase("desc") ?
                Sort.by(searchDTO.getSortBy()).descending() :
                Sort.by(searchDTO.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), sort);

        Page<UserResponseDTO> users = userService.getAllUsersPaginated(pageable);
        // Convertir Page a PagedResponseDTO per evitar warning de serialització
        PagedResponseDTO<UserResponseDTO> pagedResponse = PagedResponseDTO.of(users);

        return ResponseEntity.ok(
                ApiResponseDTO.success(pagedResponse, "Usuaris de l'empresa obtinguts correctament"));
    }

    /**
     * Cerca bàsica d'usuaris per text en múltiples camps amb paginació.
     * El companyUuid s'obté automàticament de l'usuari autenticat.
     *
     * <p>Aquest endpoint permet cercar usuaris de l'empresa de l'usuari filtrant
     * simultàniament en múltiples camps: email, firstName, lastName i phone.
     * Si no s'especifica text, retorna tots els usuaris actius.</p>
     *
     * <p>Camps de cerca inclosos:
     * <ul>
     *   <li><strong>Email</strong> (email)</li>
     *   <li><strong>Nom</strong> (firstName)</li>
     *   <li><strong>Cognoms</strong> (lastName)</li>
     *   <li><strong>Telèfon</strong> (phone)</li>
     * </ul>
     * </p>
     *
     * <p>Exemples d'ús:
     * <pre>
     * GET /api/users/search?searchText=John&page=0&size=10&sortBy=email&sortDir=asc
     * GET /api/users/search?searchText=@gmail.com
     * GET /api/users/search?searchText=555
     * </pre>
     * </p>
     *
     * @param searchDTO paràmetres de cerca (Spring els mapeja automàticament des dels query params)
     * @return resposta amb la pàgina d'usuaris trobats
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<UserResponseDTO>>> searchUsersByText(
            @Valid UserSearchDTO searchDTO) {

        Sort sort = searchDTO.getSortDir().equalsIgnoreCase("desc") ?
                Sort.by(searchDTO.getSortBy()).descending() :
                Sort.by(searchDTO.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), sort);

        Page<UserResponseDTO> users = userService.searchUsersByText(
                searchDTO.getSearchText(), pageable);

        // Convertir Page a PagedResponseDTO per evitar warning de serialització
        PagedResponseDTO<UserResponseDTO> pagedResponse = PagedResponseDTO.of(users);

        return ResponseEntity.ok(
                ApiResponseDTO.success(pagedResponse, "Cerca bàsica d'usuaris completada"));
    }

    /**
     * Cerca avançada d'usuaris amb múltiples filtres.
     * El companyUuid s'obté automàticament de l'usuari autenticat.
     *
     * <p>Aquest endpoint permet filtrar usuaris de l'empresa de l'usuari utilitzant
     * tots els camps disponibles, incloent-hi filtres de text.</p>
     *
     * <p>Filtres disponibles:
     * <ul>
     *   <li><strong>Text:</strong> email, firstName, lastName, phone</li>
     * </ul>
     * </p>
     *
     * <p>Exemple d'ús complet:
     * <pre>
     * GET /api/users/filter?email=john&firstName=John&lastName=Doe
     *     &phone=555&page=0&size=10&sortBy=email&sortDir=asc
     * </pre>
     * </p>
     *
     * <p>Exemple d'ús mínim:
     * <pre>
     * GET /api/users/filter?email=john
     * </pre>
     * </p>
     *
     * @param filterDTO paràmetres de filtratge (Spring els mapeja automàticament des dels query params)
     * @return resposta amb la pàgina d'usuaris filtrats
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<UserResponseDTO>>> filterUsers(
            @Valid UserFilterDTO filterDTO) {

        Sort sort = filterDTO.getSortDir().equalsIgnoreCase("desc") ?
                Sort.by(filterDTO.getSortBy()).descending() :
                Sort.by(filterDTO.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);

        Page<UserResponseDTO> users = userService.searchUsersWithFilters(
                filterDTO, pageable);

        // Convertir Page a PagedResponseDTO per evitar warning de serialització
        PagedResponseDTO<UserResponseDTO> pagedResponse = PagedResponseDTO.of(users);

        String message = String.format("Cerca avançada completada. Filtres aplicats: text=%s",
                filterDTO.hasTextFilters());

        return ResponseEntity.ok(
                ApiResponseDTO.success(pagedResponse, message));
    }

    /**
     * Obté un usuari pel seu UUID.
     *
     * @param uuid l'UUID de l'usuari
     * @return resposta amb les dades de l'usuari
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUserByUuid(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid) {
        UserResponseDTO user = userService.getUserByUuid(uuid);
        return ResponseEntity.ok(ApiResponseDTO.success(user, "Usuari obtingut correctament"));
    }

    /**
     * Registra un nou usuari al sistema.
     * <p>
     * Aquest endpoint permet crear un nou compte d'usuari amb les dades
     * proporcionades. Les dades són validades automàticament mitjançant
     * les anotacions de validació de Jakarta Bean Validation.
     * </p>
     *
     * @param registrationDTO les dades de registre de l'usuari. No pot ser null
     * i ha de complir les validacions definides.
     * @return ResponseEntity amb codi d'estat 201 (CREATED) i un objecte
     * ApiResponseDTO que conté les dades de l'usuari creat i un missatge de confirmació.
     */
    @PostMapping()
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> registerUser(
            @Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserResponseDTO created = userService.registerUser(registrationDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(created, "Usuari registrat correctament"));
    }

    /**
     * Actualitza les dades d'un usuari existent.
     * <p>
     * Permet modificar l'email, nom, cognoms, telèfon, rol i estat actiu
     * de l'usuari. Les dades són validades abans de l'actualització.
     * </p>
     *
     * @param uuid identificador únic de l'usuari a actualitzar.
     * @param userRequestDTO DTO amb les noves dades de l'usuari.
     * @return ResponseEntity amb codi d'estat 200 (OK) i un objecte
     * ApiResponseDTO que conté les dades de l'usuari actualitzat.
     */
    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> updateUser(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid,
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updated = userService.updateUser(uuid, userRequestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(updated, "Usuari actualitzat correctament"));
    }

    /**
     * Canvia l'estat actiu/inactiu d'un usuari.
     *
     * @param uuid identificador únic de l'usuari.
     * @param isActive nou estat d'activació ({@code true} per actiu, {@code false} per inactiu).
     * @return ResponseEntity amb codi d'estat 200 (OK) i un objecte
     * ApiResponseDTO que conté les dades de l'usuari amb l'estat actualitzat.
     */
    @PatchMapping("/{uuid}/status")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> changeUserStatus(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid,
            @RequestParam Boolean isActive) {
        UserResponseDTO updated = userService.changeUserStatus(uuid, isActive);
        return ResponseEntity.ok(ApiResponseDTO.success(updated, "Estat de l'usuari actualitzat correctament"));
    }

    /**
     * Canvia la contrasenya d'un usuari.
     * <p>
     * Requereix la contrasenya actual per verificar la identitat de l'usuari
     * abans de permetre el canvi.
     * </p>
     *
     * @param uuid identificador únic de l'usuari.
     * @param passwordChangeDTO DTO amb la contrasenya actual i la nova contrasenya.
     * @return ResponseEntity amb codi d'estat 200 (OK) i un objecte
     * ApiResponseDTO amb missatge de confirmació.
     */
    @PatchMapping("/{uuid}/change-password")
    public ResponseEntity<ApiResponseDTO<Void>> changePassword(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid,
            @Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        userService.changePassword(uuid, passwordChangeDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Contrasenya canviada correctament"));
    }

    /**
     * Elimina un usuari del sistema.
     * <p>
     * Realitza una eliminació lògica (soft delete) de l'usuari,
     * mantenint les dades a la base de dades però marcant-lo com eliminat.
     * </p>
     *
     * @param uuid identificador únic de l'usuari a eliminar.
     * @return ResponseEntity amb codi d'estat 200 (OK) i un objecte
     * ApiResponseDTO amb missatge de confirmació.
     */
    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteUser(
            @PathVariable @NotBlank(message = "L'UUID no pot estar buit") String uuid) {
        userService.deleteUser(uuid);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Usuari eliminat correctament"));
    }
}