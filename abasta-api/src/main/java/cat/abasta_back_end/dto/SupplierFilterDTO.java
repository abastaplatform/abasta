package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per gestionar les peticions de cerca avançada de proveïdors amb múltiples filtres.
 * Conté tots els paràmetres disponibles per realitzar cerques complexes amb paginació i ordenació.
 *
 * <p>Aquest DTO s'utilitza en l'endpoint de cerca avançada quan es vol filtrar proveïdors
 * per múltiples criteris simultàniament, proporcionant màxima flexibilitat en les cerques.</p>
 *
 * <p>Els filtres disponibles inclouen:
 * <ul>
 *   <li>Empresa específica per UUID (obligatori)</li>
 *   <li>Nom del proveïdor amb cerca parcial (opcional)</li>
 *   <li>Email del proveïdor amb cerca parcial (opcional)</li>
 *   <li>Estat d'activitat (actiu/inactiu) (opcional)</li>
 *   <li>Paràmetres de paginació i ordenació</li>
 * </ul>
 * </p>
 *
 * <p>Les validacions implementades garanteixen que:
 * <ul>
 *   <li>L'email té format vàlid si es proporciona</li>
 *   <li>La pàgina ha de ser un valor no negatiu</li>
 *   <li>La mida de pàgina ha de ser com a mínim 1</li>
 *   <li>Els camps d'ordenació tenen valors per defecte raonables</li>
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
 * SupplierFilterDTO filterDto = SupplierFilterDTO.builder()
 *     .companyUuid("123e4567-e89b-12d3-a456-426614174000")
 *     .name("Catalunya")
 *     .email("@provcat.com")
 *     .isActive(true)
 *     .page(0)
 *     .size(20)
 *     .sortBy("name")
 *     .sortDir("asc")
 *     .build();
 * </pre>
 * </p>
 *
 * <p>Estructura JSON de la petició:
 * <pre>
 * {
 *   "companyUuid": "123e4567-e89b-12d3-a456-426614174000",
 *   "name": "Catalunya",
 *   "email": "@provcat.com",
 *   "isActive": true,
 *   "page": 0,
 *   "size": 20,
 *   "sortBy": "name",
 *   "sortDir": "asc"
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
public class SupplierFilterDTO {

    /**
     * UUID de l'empresa per filtrar proveïdors.
     * Només es mostraran proveïdors d'aquesta empresa.
     */
    @NotBlank(message = "L'UUID de l'empresa és obligatori")
    private String companyUuid;

    /**
     * Nom del proveïdor a cercar (cerca parcial, insensible a majúscules).
     * Si es proporciona, es filtrarà per noms que continguin aquest text.
     */
    private String name;

    /**
     * Email del proveïdor a cercar (cerca parcial, insensible a majúscules).
     * Si es proporciona, es filtrarà per emails que continguin aquest text.
     */
    @Email(message = "L'email ha de tenir un format vàlid")
    private String email;

    /**
     * Estat d'activitat dels proveïdors a mostrar.
     * Si és true, només proveïdors actius. Si és false, només inactius.
     * Si és null, es mostren tots independentment de l'estat.
     */
    private Boolean isActive;

    /**
     * Número de pàgina a obtenir (basat en 0).
     * Per defecte és 0 (primera pàgina).
     */
    @Min(value = 0, message = "La pàgina ha de ser >= 0")
    @Builder.Default
    private int page = 0;

    /**
     * Nombre d'elements per pàgina.
     * Per defecte són 10 elements.
     */
    @Min(value = 1, message = "La mida ha de ser >= 1")
    @Builder.Default
    private int size = 10;

    /**
     * Camp pel qual ordenar els resultats.
     * Per defecte s'ordena per nom.
     */
    @Builder.Default
    private String sortBy = "name";

    /**
     * Direcció de l'ordenació: "asc" per ascendent, "desc" per descendent.
     * Per defecte és ascendent.
     */
    @Builder.Default
    private String sortDir = "asc";
}