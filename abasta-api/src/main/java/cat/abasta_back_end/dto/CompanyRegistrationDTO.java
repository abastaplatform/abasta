package cat.abasta_back_end.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pel registre complet d'una nova empresa amb usuari administrador.
 * Utilitzant el procés de registre públic d'empresa a la plataforma.
 * Conté les dades de l'empresa com les dades del primer usuari administrador, creat automàticament.
 *
 * @author Dani Garcia
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRegistrationDTO {

    // ========== Dades de l'empresa ==========

    /** Nom comercial de l'empresa */
    @NotBlank(message = "El nom de l'empresa és obligatori")
    @Size(max = 255, message = "El nom no pot excedir 255 caràcters")
    private String companyName;

    /** CIF o NIF d'identificació fiscal de l'empresa */
    @NotBlank(message = "El CIF/NIF és obligatori")
    @Size(max = 20, message = "El CIF no pot excedir 20 caràcters")
    private String taxId;

    /** Email de contacte de l'empresa */
    @Email(message = "L'email de l'empresa ha de ser vàlid")
    private String companyEmail;

    /** Telèfon de contacte de l'empresa */
    @Size(max = 50, message = "El telèfon no pot excedir 50 caràcters")
    private String companyPhone;

    /** Adreça física de l'empresa */
    private String companyAddress;

    /** Ciutat on s'ubica l'empresa */
    private String companyCity;

    /** Codi postal de l'empresa */
    private String companyPostalCode;

    // ========== Dades de l'administrador ==========

    /**
     * Email de l'usuari administrador.
     * S'utilitzarà per al login i per enviar l'email de verificació.
     */
    @NotBlank(message = "L'email de l'administrador és obligatori")
    @Email(message = "L'email ha de ser vàlid")
    private String adminEmail;

    /**
     * Contrasenya de l'administrador.
     * Ha de contenir un mínim de 8 caràcters amb una majúscula, una minúscula, un número i un caràcter especial.
     */
    @NotBlank(message = "La contrasenya és obligatòria")
    @Size(min = 8, message = "La contrasenya ha de tenir un mínim de 8 caràcters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).*$",
            message = "La contrasenya ha de contenir un mínim d'una majúscula, una minúscula, un número i un caràcter especial")
    private String adminPassword;

    /** Nom de l'usuari administrador */
    @NotBlank(message = "El nom de l'administrador és obligatori")
    @Size(max = 100, message = "El nom no pot excedir 100 caràcters")
    private String adminFirstName;

    /** Cognom de l'usuari administrador */
    @NotBlank(message = "El cognom és obligatori")
    @Size(max = 100, message = "El cognom no pot excedir 100 caràcters")
    private String adminLastName;

    /** Telèfon de contacte de l'administrador (opcional) */
    @Size(max = 50, message = "El telèfon no pot excedir 50 caràcters")
    private String adminPhone;
}