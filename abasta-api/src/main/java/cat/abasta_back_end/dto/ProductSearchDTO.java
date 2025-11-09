package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) per gestionar les peticions de cerca bàsica de productes en múltiples camps.
 * Conté els paràmetres necessaris per realitzar cerques simples amb paginació i ordenació.
 *
 * <p>Aquest DTO s'utilitza en l'endpoint de cerca bàsica quan es vol filtrar productes
 * d'un proveïdor proporcionant funcionalitat de paginació i ordenació.
 * És la versió simplificada de cerca que cobreix els casos d'ús més comuns.</p>
 *
 * <p>Característiques principals:
 * <ul>
 *   <li><strong>Cerca automàtica per proveïdor</strong></li>
 *   <li><strong>Cerca en múltiples camps</strong> - Filtra en name, description, category</li>
 *   <li><strong>Paginació completa</strong> - Control de pàgina i mida</li>
 *   <li><strong>Ordenació flexible</strong> - Per qualsevol camp amb direcció configurable</li>
 * </ul>
 * </p>
 *
 * <p><strong>Comportament del filtratge per text:</strong>
 * <ul>
 *   <li>Si <code>searchText</code> és null o buit → retorna tots els productes del proveïdor</li>
 *   <li>Si <code>searchText</code> té valor → filtra productes que continguin aquest text en:</li>
 *   <li>  - <strong>Nom del producte</strong> (name)</li>
 *   <li>  - <strong>Descripció del producte</strong> (description)</li>
 *   <li>  - <strong>Categoria del producte</strong> (category)</li>
 *   <li>La cerca utilitza LIKE amb comodins (%text%) i és insensible a majúscules</li>
 *   <li>Busca amb operador OR entre tots els camps (si el text apareix en qualsevol camp)</li>
 * </ul>
 * </p>
 *
 * <p>Les validacions implementades garanteixen que:
 * <ul>
 *   <li>El text de cerca és opcional (pot ser null per mostrar tots els productes)</li>
 *   <li>La pàgina ha de ser un valor no negatiu (0 o superior)</li>
 *   <li>La mida de pàgina ha de ser com a mínim 1</li>
 *   <li>El camp d'ordenació és obligatori</li>
 *   <li>La direcció d'ordenació només pot ser 'asc' o 'desc'</li>
 * </ul>
 * </p>
 *
 * <p>Exemples pràctics:
 * <strong>1. Llistar tots els productes d'un proveïdor:</strong>
 * <pre>
 * GET /api/products/search/supplier/uuidsupplier
 * </pre>
 *
 * <strong>2. Cercar productes que continguin "aigua" en qualsevol camp:</strong>
 * <pre>
 * GET /api/products/search/supplier/uuidsupplier?searchText=Aigua&page=0&size=10&sortBy=name&sortDir=asc
 * </pre>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchDTO {

    /**
     * Text a cercar en múltiples camps del producte (cerca parcial opcional, insensible a majúscules).
     * Cerca simultàniament en: name, description i category.
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