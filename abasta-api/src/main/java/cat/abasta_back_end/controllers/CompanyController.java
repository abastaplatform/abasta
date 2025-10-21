package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.ApiResponseDTO;
import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyRequestDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.services.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
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
     * Crea una nova empresa a la plataforma.
     *
     * @param companyRequestDTO Dades de l'empresa a crear
     * @return ResponseEntity amb l'empresa creada i codi HTTP 201
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<CompanyResponseDTO>> createCompany(@Valid @RequestBody CompanyRequestDTO companyRequestDTO) {
        CompanyResponseDTO created = companyService.createCompany(companyRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(created, "Empresa creada exitosament"));
    }

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
     * Obté totes les empreses registrades.
     *
     * @return ResponseEntity amb el llistat de totes les empreses
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CompanyResponseDTO>>> getAllCompanies() {
        List<CompanyResponseDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(ApiResponseDTO.success(companies, "Empreses recuperades exitosament"));
    }

    /**
     * Obté una empresa per el seu identificador numéric.
     *
     * @param id Identificador únic de l'empresa
     * @return ResponseEntity amb les dades de l'empresa
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CompanyResponseDTO>> getCompanyById(@PathVariable Long id) {
        CompanyResponseDTO company = companyService.getCompanyById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(company, "Empresa recuperada exitosament"));
    }

    /**
     * Obté una empresa per el seu UUID.
     *
     * @param uuid Identificador UUID de l'empresa
     * @return ResponseEntity amb les dades de l'empresa
     */
    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<ApiResponseDTO<CompanyResponseDTO>> getCompanyByUuid(@PathVariable String uuid) {
        CompanyResponseDTO company = companyService.getCompanyByUuid(uuid);
        return ResponseEntity.ok(ApiResponseDTO.success(company, "Empresa recuperada exitosament"));
    }

    /**
     * Obté totes les empreses filtrades per estat.
     *
     * @param status Estat de les empreses a consultar (ACTIVE, INACTIVE, SUSPENDED)
     * @return ResponseEntity amb el llistat d'empreses que coincideixen amb l'estat
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponseDTO<List<CompanyResponseDTO>>> getCompaniesByStatus(@PathVariable Company.CompanyStatus status) {
        List<CompanyResponseDTO> companies = companyService.getCompaniesByStatus(status);
        return ResponseEntity.ok(ApiResponseDTO.success(companies, "Empreses recuperades exitosament"));
    }

    /**
     * Actualitza les dades d'una empresa existent.
     *
     * @param id Identificador de l'empresa a actualitzar
     * @param companyRequestDTO Noves dades de l'empresa
     * @return ResponseEntity amb l'empresa actualitzada
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CompanyResponseDTO>> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyRequestDTO companyRequestDTO) {
        CompanyResponseDTO updated = companyService.updateCompany(id, companyRequestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(updated, "Empresa actualitzada exitosament"));
    }

    /**
     * Canvia l'estat d'una empresa (ACTIVE, INACTIVE, SUSPENDED).
     *
     * @param id Identificador de l'empresa
     * @param status Nou estat a assignar
     * @return ResponseEntity amb l'empresa amb l'estat actualitzat
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponseDTO<CompanyResponseDTO>> changeCompanyStatus(
            @PathVariable Long id,
            @RequestParam Company.CompanyStatus status) {
        CompanyResponseDTO updated = companyService.changeCompanyStatus(id, status);
        return ResponseEntity.ok(ApiResponseDTO.success(updated, "Estat de l'empresa actualitzat"));
    }

    /**
     * Elimina una empresa de la plataforma.
     *
     * @param id Identificador de l'empresa a eliminar
     * @return ResponseEntity amb confirmació de l'eliminació
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Empresa eliminada exitosament"));
    }
}