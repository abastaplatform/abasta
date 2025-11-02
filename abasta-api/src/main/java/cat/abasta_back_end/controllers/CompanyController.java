package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.ApiResponseDTO;
import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyRequestDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.exceptions.BadRequestException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
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

    /**
     * Obté la informació d'una empresa pel seu identificador UUID.
     * Aquest endpoint retorna les dades completes de l'empresa identificada
     * pel UUID proporcionat en el path.
     *
     * @param uuid l'identificador únic (UUID) de l'empresa a recuperar
     * @return ResponseEntity amb ApiResponseDTO que conté la informació de l'empresa (CompanyResponseDTO)
     * i un missatge de confirmació. Codi HTTP 200 (OK) si l'empresa existeix.
     * @throws ResourceNotFoundException si no existeix cap empresa amb l'UUID proporcionat
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponseDTO<CompanyResponseDTO>> getCompanyByUuid(@PathVariable String uuid) {
        CompanyResponseDTO company = companyService.getCompanyByUuid(uuid);
        return ResponseEntity.ok(ApiResponseDTO.success(company, "Empresa recuperada exitosament"));
    }

    /**
     * Actualitza la informació d'una empresa existent.
     * Permet modificar les dades d'una empresa identificada pel seu UUID.
     * Tots els camps del DTO són validats abans de processar l'actualització.
     *
     * @param uuid l'identificador únic (UUID) de l'empresa a actualitzar
     * @param companyRequestDTO objecte amb les noves dades de l'empresa (validat amb @Valid)
     * @return ResponseEntity amb ApiResponseDTO que conté la informació actualitzada de l'empresa
     * i un missatge de confirmació. Codi HTTP 200 (OK) si l'actualització és correcta.
     * @throws ResourceNotFoundException si no existeix cap empresa amb l'UUID proporcionat
     * @throws BadRequestException si les dades proporcionades no són vàlides o hi ha errors de validació
     */
    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponseDTO<CompanyResponseDTO>> updateCompany(
            @PathVariable String uuid,
            @Valid @RequestBody CompanyRequestDTO companyRequestDTO) {
        CompanyResponseDTO updated = companyService.updateCompany(uuid, companyRequestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(updated, "Empresa actualitzada exitosament"));
    }

}