package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST per a la gestió d'usuaris.
 * <p>
 * Proporciona endpoints per al registre i gestió d'usuaris del sistema Abasta.
 * Tots els endpoints retornen respostes en format JSON amb l'estructura
 * ApiResponseDTO per mantenir la consistència de les respostes de l'API.
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Obté un usuari pel seu identificador UUID.
     *
     * @param uuid identificador únic de l'usuari a cercar.
     * @return ResponseEntity amb codi d'estat 200 (OK) i un objecte
     * ApiResponseDTO que conté les dades de l'usuari trobat.
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUserByUuid(@PathVariable String uuid) {
        UserResponseDTO user = userService.getUserByUuid(uuid);
        return ResponseEntity.ok(ApiResponseDTO.success(user, "Usuari recuperat correctament"));
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
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
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
            @PathVariable String uuid,
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
            @PathVariable String uuid,
            @RequestParam Boolean isActive) {
        UserResponseDTO updated = userService.changeUserStatus(uuid, isActive);
        return ResponseEntity.ok(ApiResponseDTO.success(updated, "Estat de l'usuari actualitzat"));
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
            @PathVariable String uuid,
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
    public ResponseEntity<ApiResponseDTO<Void>> deleteUser(@PathVariable String uuid) {
        userService.deleteUser(uuid);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Usuari eliminat correctament"));
    }
}