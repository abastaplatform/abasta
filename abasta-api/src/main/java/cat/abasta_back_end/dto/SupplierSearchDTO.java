package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per gestionar les peticions de cerca simple de proveïdors per l'UUID d'empresa i nom.
 * Conté els paràmetres necessaris per realitzar cerques per nom amb paginació i ordenació.
 *
 * <p>Aquest DTO s'utilitza en l'endpoint de cerca simple quan es vol filtrar proveïdors
 * únicament per nom, proporcionant també opcions de paginació i ordenació.</p>
 *
 * <p>Les validacions implementades garanteixen que:
 * <ul>
 *   <li>L'UUID de l'empresa és obligatori i no pot estar buit</li>
 *   <li>El nom de cerca és obligatori i no pot estar buit</li>
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
 * SupplierSearchDTO searchDto = SupplierSearchDTO.builder()
 *     .companyUuid
 *     .name("Proveïdors")
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
 *   "name": "Proveïdors",
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
public class SupplierSearchDTO {

    /**
     * UUID de l'empresa per filtrar proveïdors.
     * Només es mostraran proveïdors d'aquesta empresa.
     */
    @NotBlank(message = "L'UUID de l'empresa és obligatori")
    private String companyUuid;

    /**
     * Nom del proveïdor a cercar (cerca parcial, insensible a majúscules).
     * És obligatori per a la cerca.
     */
    @NotBlank(message = "El nom de cerca és obligatori")
    private String name;

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