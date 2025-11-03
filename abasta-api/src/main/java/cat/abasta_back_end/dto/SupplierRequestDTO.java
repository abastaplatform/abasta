package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO (Data Transfer Object) per gestionar les peticions de creació i actualització de proveïdors.
 * Conté tota la informació necessària per crear o modificar un proveïdor al sistema.
 *
 * <p>Aquest DTO s'utilitza en els endpoints de creació i actualització de proveïdors,
 * incloent-hi validacions exhaustives per assegurar la integritat de les dades rebudes.</p>
 *
 * <p>Les validacions implementades garanteixen que:
 * <ul>
 *   <li>L'UUID de l'empresa és obligatori i ha de ser un valor vàlid</li>
 *   <li>El nom del proveïdor és obligatori i no pot superar els 255 caràcters</li>
 *   <li>L'email té un format vàlid si es proporciona</li>
 *   <li>Tots els camps de text respecten les longituds màximes definides</li>
 *   <li>El camp isActive té un valor per defecte de true si no s'especifica</li>
 * </ul>
 * </p>
 *
 * <p>Les anotacions de Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor, @Builder)
 * generen automàticament:
 * <ul>
 *   <li>Getters i setters per a tots els camps</li>
 *   <li>Mètodes equals(), hashCode() i toString()</li>
 *   <li>Constructor sense paràmetres</li>
 *   <li>Constructor amb tots els paràmetres</li>
 *   <li>Patró Builder per a la construcció fluent d'objectes</li>
 * </ul>
 * </p>
 *
 * <p>Exemple d'ús amb Builder:
 * <pre>
 * SupplierRequestDTO supplier = SupplierRequestDTO.builder()
 *     .companyUuid("123e4567-e89b-12d3-a456-426614174000")
 *     .name("Proveïdors Catalunya SL")
 *     .contactName("Joan Martínez")
 *     .email("joan@provcat.com")
 *     .phone("938765432")
 *     .address("Av. Diagonal 123, Barcelona")
 *     .notes("Proveïdor de materials de construcció")
 *     .isActive(true)
 *     .build();
 * </pre>
 * </p>
 *
 * <p>Estructura JSON de la petició:
 * <pre>
 * {
 *   "companyUuid": "123e4567-e89b-12d3-a456-426614174000",
 *   "name": "Proveïdors Catalunya SL",
 *   "contactName": "Joan Martínez",
 *   "email": "joan@provcat.com",
 *   "phone": "938765432",
 *   "address": "Av. Diagonal 123, Barcelona",
 *   "notes": "Proveïdor de materials de construcció",
 *   "isActive": true
 * }
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequestDTO {

    /**
     * UUID de l'empresa associada.
     * És obligatori per a tots els proveïdors.
     */
    @NotBlank(message = "L'UUID de l'empresa és obligatori")
    private String companyUuid;

    /**
     * Nom de l'empresa proveïdora.
     * Ha de tenir entre 1 i 255 caràcters.
     */
    @NotBlank(message = "El nom del proveïdor és obligatori")
    @Size(max = 255, message = "El nom no pot superar els 255 caràcters")
    private String name;

    /**
     * Nom de la persona de contacte.
     * Pot ser nul, però si s'especifica no pot superar els 255 caràcters.
     */
    @Size(max = 255, message = "El nom de contacte no pot superar els 255 caràcters")
    private String contactName;

    /**
     * Adreça de correu electrònic del proveïdor.
     * Ha de tenir un format vàlid si s'especifica.
     */
    @Email(message = "L'adreça de correu electrònic ha de tenir un format vàlid")
    @Size(max = 255, message = "L'email no pot superar els 255 caràcters")
    private String email;

    /**
     * Número de telèfon del proveïdor.
     * Pot ser nul, però si s'especifica no pot superar els 50 caràcters.
     */
    @Size(max = 50, message = "El telèfon no pot superar els 50 caràcters")
    private String phone;

    /**
     * Adreça física del proveïdor.
     * Pot contenir text llarg.
     */
    private String address;

    /**
     * Notes addicionals sobre el proveïdor.
     * Pot contenir text llarg.
     */
    private String notes;

    /**
     * Estat d'activitat del proveïdor.
     * Per defecte és true si no s'especifica.
     */
    @Builder.Default
    private Boolean isActive = true;
}