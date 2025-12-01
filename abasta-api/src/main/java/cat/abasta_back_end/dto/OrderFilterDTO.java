package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) per gestionar les peticions de llistat de comandes amb cerca avançada amb múltiples filtres.
 * Conté els paràmetres necessaris per realitzar llista amb cerques complexes, paginació i ordenació.
 *
 * <p>Aquest DTO s'utilitza en l'endpoint de filtre de comandes i conté les dades de paginació i ordenació i filtratge.</p>
 *
 * <p>Els filtres disponibles inclouen:
 * <ul>
 *   <li><strong>Uuid proveïdor (opcional)</strong></li>
 *   <li><strong>Uuid usuari (opcional)</strong></li>
 *   <li><strong>Filtres de text amb cerca parcial (opcionals):</strong>
 *     <ul>
 *       <li>Nom de la comanda</li>
 *       <li>Notes</li>
 *     </ul>
 *   </li>
 *   <li><strong>Estat de la comanda (opcional)</strong></li>
 *   <li><strong>Filtres de dates per rangs (opcionals):</strong>
 *     <ul>
 *       <li>Data d'entrega (des de / fins a)</li>
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
 *   <li><code>createdAt</code> - Data de creació</li>
 *   <li><code>updatedAt</code> - Data d'actualització</li>
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
public class OrderFilterDTO {

    /** Identificador de la comanda */
    private String orderUuid;

    /**
     * Identificador únic de proveïdor
     */
    private String supplierUuid;

    /**
     * Identificador únic de l'usuari
     */
    private String userUuid;

    /**
     * Text a cercar en múltiples camps de la comanda (cerca parcial opcional, insensible a majúscules).
     * Cerca simultàniament en: name / notes
     */
    private String searchText;
    private String name;
    private String notes;

    /**
     * Estat de la comanda
     */
    private String status;

    /**
     * Preu mínim de la comanda (opcional)
     */
    @Min(value = 0, message = "El preu mínim de la comandano pot ser negatiu")
    private BigDecimal minAmount;

    /**
     * Preu màxim de la comanda (opcional)
     */
    @Min(value = 0, message = "El preu màxim de la comanda no pot ser negatiu")
    private BigDecimal maxAmount;

    // --- Rangs de dates ---
    /** Rang de dates d'entrega */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDateTo;

    /** Rang de dates de creació */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAtFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAtTo;

    /** Rang de dates de modificació */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAtFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAtTo;

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

    /**
     * Verifica si algun filtre de text està aplicat.
     *
     * @return true si hi ha algun filtre de text actiu
     */
    public boolean hasTextFilters() {
        return (name != null && !name.trim().isEmpty()) || (notes != null && !notes.trim().isEmpty());
    }

}