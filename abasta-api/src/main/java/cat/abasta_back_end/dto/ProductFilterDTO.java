package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) per gestionar les peticions de cerca avançada de productes amb múltiples filtres.
 * Conté tots els paràmetres disponibles per realitzar cerques complexes amb paginació i ordenació.
 *
 * <p>Aquest DTO s'utilitza en l'endpoint de cerca avançada quan es vol filtrar productes
 * del proveïdor per múltiples criteris simultàniament, proporcionant
 * màxima flexibilitat en les cerques. Permet filtrar per tots els camps disponibles de la
 * taula products incloent-hi filtres de text, estat d'activitat i rangs de dates.</p>
 *
 * <p>Els filtres disponibles inclouen:
 * <ul>
 *   <li><strong>Uuid proveïdor</strong> - implícit en endpoint</li>
 *   <li><strong>Filtres de text amb cerca parcial (opcionals):</strong>
 *     <ul>
 *       <li>Nom del producte</li>
 *       <li>Descripció del producte</li>
 *       <li>Categoria del producte</li>
 *     </ul>
 *   </li>
 *   <li><strong>Estat d'activitat (opcional)</strong> - Filtra per productes actius/inactius</li>
 *   <li><strong>Filtres de dates per rangs (opcionals):</strong>
 *     <ul>
 *       <li>Data de creació (des de / fins a)</li>
 *       <li>Data d'actualització (des de / fins a)</li>
 *     </ul>
 *   </li>
 *   <li><strong>Paràmetres de paginació i ordenació</strong></li>
 * </ul>
 * </p>
 *
 * <p>Les validacions implementades garanteixen que:
 * <ul>
 *   <li>La pàgina ha de ser un valor no negatiu (0 o superior)</li>
 *   <li>La mida de pàgina ha de ser com a mínim 1</li>
 *   <li>El camp d'ordenació és obligatori</li>
 *   <li>La direcció d'ordenació només pot ser 'asc' o 'desc'</li>
 *   <li>Les dates segueixen el format ISO LocalDateTime</li>
 * </ul>
 * </p>
 *
 * <p>Camps d'ordenació disponibles:
 * <ul>
 *   <li><code>name</code> - Nom del producte (per defecte)</li>
 *   <li><code>description</code> - Descripció del producte</li>
 *   <li><code>category</code> - Categoria del producte</li>
 *   <li><code>createdAt</code> - Data de creació</li>
 *   <li><code>updatedAt</code> - Data d'actualització</li>
 * </ul>
 * </p>
 *
 * *
 * <p>Estructura JSON de la petició completa:
 * <pre>
 * {
 *   "name": "Catalunya",
 *   "description": "Joan",
 *   "category": "@provcat.com",
 *   "isActive": true,
 *   "createdAfter": "2024-01-01T00:00:00",
 *   "createdBefore": "2024-12-31T23:59:59",
 *   "updatedAfter": "2024-06-01T00:00:00",
 *   "updatedBefore": "2024-12-31T23:59:59",
 *   "page": 0,
 *   "size": 20,
 *   "sortBy": "name",
 *   "sortDir": "asc"
 * }
 * </pre>
 * </p>
 *
 * <p>Estructura JSON de la petició mínima:
 * <pre>
 * {
 *   "sortBy": "name",
 *   "sortDir": "asc"
 * }
 * </pre>
 * </p>
 *
 * <p><strong>Notes d'implementació:</strong>
 * <ul>
 *   <li>Tots els filtres de text utilitzen cerca parcial insensible a majúscules</li>
 *   <li>Els filtres de dates permeten rangs oberts (només 'des de' o només 'fins a')</li>
 *   <li>Si no s'especifica isActive, es mostren tant proveïdors actius com inactius</li>
 *   <li>La classe inclou mètodes utilitaris hasTextFilters() i hasDateFilters()</li>
 * </ul>
 * </p>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterDTO {

    /**
     * Identificador únic de proveïdor
     */
    private String supplierUuid;

    // Filtres de text (cerca parcial, insensible a majúscules
    private String name;        // Nom del producte
    private String description; // Descripció del producte
    private String category; // Categoria del producte

    // Preu mínim del producte (opcional)
    @Min(value = 0, message = "El preu mínim no pot ser negatiu")
    private BigDecimal minPrice;

    // Preu màxim del producte (opcional)
    @Min(value = 0, message = "El preu màxim no pot ser negatiu")
    private BigDecimal maxPrice;

    // Volum del producte (ex: 1, 2, 3)
    private BigDecimal volume;

    // Unitat de mesura del producte (ex: kg, l, cl...)
    private String unit;

    // Filtre d'estat
    private Boolean isActive;   // Estat actiu (true/false/null per tots)

    // Paginació
    @Min(value = 0, message = "El número de pàgina ha de ser 0 o superior")
    @Builder.Default
    private int page = 0;

    @Min(value = 1, message = "La mida de pàgina ha de ser 1 o superior")
    @Builder.Default
    private int size = 10;

    // Ordenació (camps disponibles: name, category, price, unit, createdAt, updatedAt)
    @Builder.Default
    private String sortBy = "name";

    @Pattern(regexp = "asc|desc", message = "La direcció d'ordenació ha de ser 'asc' o 'desc'")
    @Builder.Default
    private String sortDir = "asc";

    /**
     * Verifica si algun filtre de text està aplicat.
     *
     * @return true si hi ha algun filtre de text actiu
     */
    public boolean hasTextFilters() {
        return (name != null && !name.trim().isEmpty()) ||
                (description != null && !description.trim().isEmpty()) ||
                (category != null && !category.trim().isEmpty());
    }

}