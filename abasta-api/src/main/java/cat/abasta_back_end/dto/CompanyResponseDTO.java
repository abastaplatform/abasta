package cat.abasta_back_end.dto;

import cat.abasta_back_end.entities.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO de resposta amb la informació completa d'una empresa.
 * Utilitzat per retornar dades d'empresa en las respostes de l'API, excloent-hi informació sensible i relacions complexes de l'entitat.
 *
 * @author Dani Garcia
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponseDTO {

    /** Identificador únic númeric de l'empresa */
    private Long id;

    /** Identificador UUID de l'empresa, utilitzat per l'identificació pública */
    private String uuid;

    /** Nom comercial de l'empresa */
    private String name;

    /** NIF o CIF d'identificació fiscal */
    private String taxId;

    /** Email de contacte de l'empresa */
    private String email;

    /** Telèfon de contacte */
    private String phone;

    /** Adreça física completa */
    private String address;

    /** Ciutat d'ubicació */
    private String city;

    /** Codi postal */
    private String postalCode;

    /** Estat actual de l'empresa (ACTIVE, INACTIVE, SUSPENDED) */
    private Company.CompanyStatus status;

    /** Data i hora de creació del registre */
    private LocalDateTime createdAt;

    /** Data i hora de l'última modificació */
    private LocalDateTime updatedAt;
}