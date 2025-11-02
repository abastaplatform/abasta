package cat.abasta_back_end.dto;

import cat.abasta_back_end.entities.Company;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) per gestionar les peticions de creació i actualització d'empreses.
 * Conté tota la informació necessària per crear o modificar una empresa al sistema.
 *
 * <p>Aquest DTO s'utilitza en els endpoints de registre i actualització d'empreses,
 * incloent-hi validacions per assegurar la integritat de les dades rebudes.</p>
 *
 * <p>Les validacions implementades garanteixen que:
 * <ul>
 *   <li>El nom i el NIF/CIF són obligatoris i no poden estar buits</li>
 *   <li>Els camps de text respecten les longituds màximes definides</li>
 *   <li>L'email té un format vàlid si es proporciona</li>
 *   <li>Tots els camps compleixen amb les restriccions de mida</li>
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
 * CompanyRequestDTO company = CompanyRequestDTO.builder()
 *     .name("Abasta SL")
 *     .taxId("B12345678")
 *     .email("info@abasta.com")
 *     .phone("932123456")
 *     .address("Carrer Example 123")
 *     .city("Barcelona")
 *     .postalCode("08001")
 *     .status(Company.CompanyStatus.PENDING)
 *     .build();
 * </pre>
 * </p>
 *
 * <p>Estructura JSON de la petició:
 * <pre>
 * {
 *   "name": "Abasta SL",
 *   "taxId": "B12345678",
 *   "email": "info@abasta.com",
 *   "phone": "932123456",
 *   "address": "Carrer Example 123",
 *   "city": "Barcelona",
 *   "postalCode": "08001",
 *   "status": "PENDING"
 * }
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequestDTO {

    /**
     * Nom de l'empresa.
     * Camp obligatori amb una longitud màxima de 255 caràcters.
     */
    @NotBlank(message = "El nom és obligatori")
    @Size(max = 255, message = "El nom no pot excedir de 255 caràcters")
    private String name;

    /**
     * NIF o CIF de l'empresa.
     * Identificador fiscal únic, obligatori, amb una longitud màxima de 50 caràcters.
     */
    @NotBlank(message = "El NIF/CIF és obligatori")
    @Size(max = 50, message = "El NIF/CIF no pot excedir de 50 caràcters")
    private String taxId;

    /**
     * Adreça de correu electrònic de l'empresa.
     * Camp opcional que ha de tenir un format d'email vàlid si es proporciona.
     */
    @Email(message = "L'email ha de ser vàlid")
    private String email;

    /**
     * Número de telèfon de contacte de l'empresa.
     * Camp opcional amb una longitud màxima de 50 caràcters.
     */
    @Size(max = 50, message = "El telèfon no pot excedir de 50 caràcters")
    private String phone;

    /**
     * Adreça postal completa de l'empresa.
     * Camp opcional sense límit de longitud específic.
     */
    private String address;

    /**
     * Ciutat on està ubicada l'empresa.
     * Camp opcional amb una longitud màxima de 100 caràcters.
     */
    @Size(max = 100, message = "La ciutat no pot excedir de 100 caràcters")
    private String city;

    /**
     * Codi postal de l'adreça de l'empresa.
     * Camp opcional amb una longitud màxima de 20 caràcters.
     */
    @Size(max = 20, message = "El codi postal no pot excedir de 20 caràcters")
    private String postalCode;

    /**
     * Estat actual de l'empresa (PENDING, ACTIVE, INACTIVE).
     * Camp opcional que indica si l'empresa està pendent de verificació,
     * activa o inactiva al sistema.
     */
    private Company.CompanyStatus status;
}