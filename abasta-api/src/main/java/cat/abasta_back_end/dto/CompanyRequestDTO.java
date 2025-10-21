package cat.abasta_back_end.dto;

import cat.abasta_back_end.entities.Company;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per crear o actualitzar dades d'una empresa existent.
 * A diferència de CompanyRegistrationDTO, aquest DTO s'utilitz per operacions de gestió interna d'empreses ja registrades, sense incloure dades de l'usuari administrador.
 *
 * @author Dani Garcia
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequestDTO {

    /** Nom comercial de l'empresa */
    @NotBlank(message = "El nom és obligatori")
    @Size(max = 255, message = "El nom no pot excedir 255 caràcters")
    private String name;

    /** NIF o CIF d'identificació fiscal de l'empresa */
    @NotBlank(message = "El NIF/CIF és obligatori")
    @Size(max = 50, message = "El NIF/CIF no pot excedir 50 caràcters")
    private String taxId;

    /** Email de contacte de l'empresa */
    @NotBlank(message = "L'email és obligatori")
    @Email(message = "L'email ha de ser vàlid")
    private String email;

    /** Telèfon de contacte de l'empresa (opcional) */
    @Size(max = 50, message = "El telèfon no pot excedir 50 caràcters")
    private String phone;

    /** Adreça física de l'empresa */
    private String address;

    /** Ciutat on s'ubica l'empresa */
    @Size(max = 100, message = "La ciutat no pot excedir 100 caràcters")
    private String city;

    /** Codi postal de l'empresa */
    @Size(max = 20, message = "El codi postal no pot excedir 20 caràcters")
    private String postalCode;

    /** Estat de l'empresa (ACTIVE, INACTIVE, SUSPENDED) */
    private Company.CompanyStatus status;
}