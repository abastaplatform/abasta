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
     * Registra un nou usuari al sistema.
     * <p>
     * Aquest endpoint permet crear un nou compte d'usuari amb les dades
     * proporcionades. Les dades són validades automàticament mitjançant
     * les anotacions de validació de Jakarta Bean Validation.
     * </p>
     *
     * @param registrationDTO les dades de registre de l'usuari. No pot ser null
     *                        i ha de complir les validacions definides
     * @return ResponseEntity amb codi d'estat 201 (CREATED) i un objecte
     *         ApiResponseDTO que conté les dades de l'usuari creat i un
     *         missatge de confirmació.
     */
    @PostMapping()
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserResponseDTO created = userService.registerUser(registrationDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(created, "Usuari registrat correctament"));
    }

}