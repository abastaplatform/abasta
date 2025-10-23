package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.ApiResponseDTO;
import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.services.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST per la gestió d'empreses.
 * Proporciona endpoints per crear, registrar, consultar, actualitzar i eliminar empreses registrades a la plataforma.
 *
 * @author Dani Garcia
 * @version 1.0
 */
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Registra una nova empresa amb l'usuari administrador inicial.
     * Aquest endpoint s'utilitza durant el procés de registre públic de noves empreses.
     * Crea l'empresa i un usuari administrador associat, enviant un email de verificació.
     *
     * @param registrationDTO Dades de l'empresa i l'usuari administrador a crear
     * @return ResponseEntity amb les dades de l'empresa creada i missatge de verificació d'email
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> registerCompanyWithAdmin(
            @Valid @RequestBody CompanyRegistrationDTO registrationDTO) {

        CompanyResponseDTO company = companyService.registerCompanyWithAdmin(registrationDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("company", company);
        response.put("message", "Empresa y administrador registrats exitosament. " +
                "Si us plau, verifica l'email de l'administrador per activar el compte.");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(response, "Registre completat exitosament"));
    }

}