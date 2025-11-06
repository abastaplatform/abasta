package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per gestionar les peticions de cerca bàsica de proveïdors per nom.
 * Conté els paràmetres necessaris per realitzar cerques simples amb paginació i ordenació.
 *
 * <p>Aquest DTO s'utilitza en l'endpoint de cerca bàsica quan es vol filtrar proveïdors
 * de l'empresa de l'usuari autenticat, proporcionant funcionalitat de paginació i ordenació.
 * És la versió simplificada de cerca que cobreix els casos d'ús més comuns.</p>
 *
 * <p>Característiques principals:
 * <ul>
 *   <li><strong>Cerca automàtica per empresa</strong> - El companyUuid s'extreu de l'usuari autenticat</li>
 *   <li><strong>Filtre per nom (opcional)</strong> - Permet filtrar o mostrar tots els proveïdors</li>
 *   <li><strong>Paginació completa</strong> - Control de pàgina i mida</li>
 *   <li><strong>Ordenació flexible</strong> - Per qualsevol camp amb direcció configurable</li>
 * </ul>
 * </p>
 *
 * <p><strong>Comportament del filtratge per nom:</strong>
 * <ul>
 *   <li>Si <code>name</code> és null o buit → retorna tots els proveïdors de l'empresa</li>
 *   <li>Si <code>name</code> té valor → filtra proveïdors que continguin aquest text</li>
 *   <li>La cerca utilitza LIKE amb comodins (%nom%) i és insensible a majúscules</li>
 *   <li><strong>IMPORTANT:</strong> Tots els proveïdors a la base de dades tenen nom (NOT NULL)</li>
 *   <li>Aquest camp només és opcional com a <em>filtre de cerca</em>, no com a valor d'entitat</li>
 * </ul>
 * </p>
 *
 * <p>Les validacions implementades garanteixen que:
 * <ul>
 *   <li>El filtre de nom és opcional (pot ser null per mostrar tots els proveïdors)</li>
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
 * <strong>2. Cercar proveïdors que continguin "Catalunya":</strong>
 * <pre>
 * GET /api/suppliers/search?name=Catalunya&page=0&size=10&sortBy=name&sortDir=asc
 * </pre>
 * </p>
 *
 * <p><strong>Seguretat:</strong>
 * El companyUuid s'extreu automàticament de l'usuari autenticat, garantint que
 * cada usuari només pugui cercar proveïdors de la seva pròpia empresa.
 * </p>
 *
 * @author Enrique Pérez
 * @version 3.0
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
     * Nom del proveïdor a cercar (cerca parcial opcional, insensible a majúscules).
     */
    private String name;

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