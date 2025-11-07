// Actualitzar el SupplierSearchDTO.java canviant el camp "name" per "searchText":

package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per gestionar les peticions de cerca bàsica de proveïdors en múltiples camps.
 * Conté els paràmetres necessaris per realitzar cerques simples amb paginació i ordenació.
 *
 * <p>Aquest DTO s'utilitza en l'endpoint de cerca bàsica quan es vol filtrar proveïdors
 * de l'empresa de l'usuari autenticat, proporcionant funcionalitat de paginació i ordenació.
 * És la versió simplificada de cerca que cobreix els casos d'ús més comuns.</p>
 *
 * <p>Característiques principals:
 * <ul>
 *   <li><strong>Cerca automàtica per empresa</strong> - El companyUuid s'extreu de l'usuari autenticat</li>
 *   <li><strong>Cerca en múltiples camps</strong> - Filtra en name, contactName, email, phone i address</li>
 *   <li><strong>Paginació completa</strong> - Control de pàgina i mida</li>
 *   <li><strong>Ordenació flexible</strong> - Per qualsevol camp amb direcció configurable</li>
 * </ul>
 * </p>
 *
 * <p><strong>Comportament del filtratge per text:</strong>
 * <ul>
 *   <li>Si <code>searchText</code> és null o buit → retorna tots els proveïdors de l'empresa</li>
 *   <li>Si <code>searchText</code> té valor → filtra proveïdors que continguin aquest text en:</li>
 *   <li>  - <strong>Nom de l'empresa</strong> (name)</li>
 *   <li>  - <strong>Nom de contacte</strong> (contactName)</li>
 *   <li>  - <strong>Email</strong> (email)</li>
 *   <li>  - <strong>Telèfon</strong> (phone)</li>
 *   <li>  - <strong>Adreça</strong> (address)</li>
 *   <li>La cerca utilitza LIKE amb comodins (%text%) i és insensible a majúscules</li>
 *   <li>Busca amb operador OR entre tots els camps (si el text apareix en qualsevol camp)</li>
 * </ul>
 * </p>
 *
 * <p>Les validacions implementades garanteixen que:
 * <ul>
 *   <li>El text de cerca és opcional (pot ser null per mostrar tots els proveïdors)</li>
 *   <li>La pàgina ha de ser un valor no negatiu (0 o superior)</li>
 *   <li>La mida de pàgina ha de ser com a mínim 1</li>
 *   <li>El camp d'ordenació és obligatori</li>
 *   <li>La direcció d'ordenació només pot ser 'asc' o 'desc'</li>
 * </ul>
 * </p>
 *
 * <p>Exemples pràctics:
 * <strong>1. Llistar tots els proveïdors de l'empresa de l'usuari:</strong>
 * <pre>
 * GET /api/suppliers/search
 * </pre>
 *
 * <strong>2. Cercar proveïdors que continguin "Barcelona" en qualsevol camp:</strong>
 * <pre>
 * GET /api/suppliers/search?searchText=Barcelona&page=0&size=10&sortBy=name&sortDir=asc
 * </pre>
 *
 * <strong>3. Cercar per email:</strong>
 * <pre>
 * GET /api/suppliers/search?searchText=@gmail.com
 * </pre>
 *
 * <strong>4. Cercar per telèfon:</strong>
 * <pre>
 * GET /api/suppliers/search?searchText=93
 * </pre>
 * </p>
 *
 * <p><strong>Seguretat:</strong>
 * El companyUuid s'extreu automàticament de l'usuari autenticat, garantint que
 * cada usuari només pugui cercar proveïdors de la seva pròpia empresa.
 * </p>
 *
 * @author Enrique Pérez
 * @version 4.0
 * @since 1.0
 * @see SupplierFilterDTO
 * @see SupplierResponseDTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierSearchDTO {

    /**
     * Text a cercar en múltiples camps del proveïdor (cerca parcial opcional, insensible a majúscules).
     * Cerca simultàniament en: name, contactName, email, phone i address.
     */
    private String searchText;

    /**
     * Número de pàgina a obtenir (basat en 0).
     * Per defecte és 0 (primera pàgina).
     */
    @Min(value = 0, message = "El número de pàgina ha de ser 0 o superior")
    @Builder.Default
    private int page = 0;

    /**
     * Nombre d'elements per pàgina.
     * Per defecte són 10 elements.
     */
    @Min(value = 1, message = "La mida de pàgina ha de ser 1 o superior")
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
    @Pattern(regexp = "asc|desc", message = "La direcció d'ordenació ha de ser 'asc' o 'desc'")
    @Builder.Default
    private String sortDir = "asc";
}