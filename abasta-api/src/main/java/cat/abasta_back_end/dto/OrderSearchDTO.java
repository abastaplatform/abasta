package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) per gestionar les peticions de llistat de comandes.
 * Conté els paràmetres necessaris per realitzar llista amb paginació i ordenació.
 *
 * <p>Aquest DTO s'utilitza en l'endpoint de llistat de comandes i conté les dades de paginació i ordenació.</p>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchDTO {

    /**
     * Identificador únic de proveïdor
     */
    private String supplierUuid;

    /**
     * Text a cercar en múltiples camps de la comanda (cerca parcial opcional, insensible a majúscules).
     * Cerca simultàniament en: name
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