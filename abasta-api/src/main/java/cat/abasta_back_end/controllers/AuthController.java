package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST per a la gestió de l'autenticació i recuperació de comptes d'usuari.
 * Proporciona els endpoints necessaris per iniciar sessió, restablir contrasenyes
 * i verificar adreces de correu electrònic.
 *
 * <p>Tots els endpoints retornen un objecte {@link ApiResponseDTO} que encapsula la resposta
 * amb el missatge i les dades corresponents.</p>
 *
 * @author Dani Garcia
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /** Servei responsable de la lògica d'autenticació i gestió d'usuaris */
    private final UserService userService;

    /**
     * Endpoint per iniciar sessió d'un usuari registrat.
     *
     * @param loginDTO Dades d'accés (email i contrasenya)
     * @return Resposta amb el token JWT i la informació bàsica de l'usuari
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginDTO) {
        LoginResponseDTO response = userService.login(loginDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(response, "Inici de sessió correcte"));
    }

    /**
     * Endpoint per sol·licitar la recuperació de contrasenya.
     * Envia un correu electrònic amb un enllaç o token per restablir-la.
     *
     * @param requestDTO Objecte amb l'email de l'usuari que vol recuperar la contrasenya
     * @return Missatge confirmant l'enviament del correu de recuperació
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseDTO<Void>> forgotPassword(@Valid @RequestBody PasswordResetRequestDTO requestDTO) {
        userService.requestPasswordReset(requestDTO.getEmail());
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Correu de recuperació enviat correctament"));
    }

    /**
     * Endpoint per restablir la contrasenya d'un usuari mitjançant un token vàlid.
     *
     * @param passwordResetDTO Dades del token i la nova contrasenya
     * @return Missatge indicant que la contrasenya s'ha restablert correctament
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO<Void>> resetPassword(@Valid @RequestBody PasswordResetDTO passwordResetDTO) {
        userService.resetPassword(passwordResetDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Contrasenya restablerta correctament"));
    }

    /**
     * Endpoint per verificar l'adreça de correu electrònic d'un usuari nou registrat.
     *
     * @param verificationDTO Token de verificació enviat al correu de l'usuari
     * @return Missatge confirmant la verificació del compte
     */
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponseDTO<Void>> verifyEmail(@Valid @RequestBody EmailVerificationDTO verificationDTO) {
        userService.verifyEmail(verificationDTO.getToken());
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Correu electrònic verificat correctament. Ja pots iniciar sessió."));
    }

    /**
     * Endpoint per reenviar el correu de verificació en cas que l'usuari no l'hagi rebut.
     *
     * @param requestDTO Objecte amb l'email de l'usuari
     * @return Missatge confirmant que s'ha reenviat el correu de verificació
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponseDTO<Void>> resendVerification(@Valid @RequestBody PasswordResetRequestDTO requestDTO) {
        userService.resendVerificationEmail(requestDTO.getEmail());
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Correu de verificació reenviat correctament"));
    }
}
