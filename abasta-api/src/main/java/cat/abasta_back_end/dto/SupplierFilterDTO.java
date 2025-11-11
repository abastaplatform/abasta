package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per gestionar les peticions de cerca avançada de proveïdors amb múltiples filtres.
 * Conté tots els paràmetres disponibles per realitzar cerques complexes amb paginació i ordenació.
 *
 * <p>Aquest DTO s'utilitza en l'endpoint de cerca avançada quan es vol filtrar proveïdors
 * de l'empresa de l'usuari autenticat per múltiples criteris simultàniament, proporcionant
 * màxima flexibilitat en les cerques. Permet filtrar pels camps disponibles de la
 * taula suppliers incloent-hi filtres de text</p>
 *
 * <p>Els filtres disponibles inclouen:
 * <ul>
 *   <li><strong>Empresa automàtica</strong> - El companyUuid s'extreu de l'usuari autenticat</li>
 *   <li><strong>Filtres de text amb cerca parcial (opcionals):</strong>
 *     <ul>
 *       <li>Nom del proveïdor</li>
 *       <li>Nom de contacte</li>
 *       <li>Email del proveïdor</li>
 *       <li>Telèfon de contacte</li>
 *       <li>Adreça completa</li>
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
 *   <li>La direcció d'ordenació només pot ser 'asc' o 'desc'</li>
 * </ul>
 * </p>
 *
 * <p>Camps d'ordenació disponibles:
 * <ul>
 *   <li><code>name</code> - Nom del proveïdor (per defecte)</li>
 *   <li><code>contactName</code> - Nom de contacte</li>
 *   <li><code>email</code> - Email</li>
 *   <li><code>phone</code> - Telèfon</li>
 *   <li><code>createdAt</code> - Data de creació</li>
 *   <li><code>updatedAt</code> - Data d'actualització</li>
 * </ul>
 * </p>
 *
 * <p>Exemple d'ús amb Builder (cerca completa):
 * <pre>
 * SupplierFilterDTO filterDto = SupplierFilterDTO.builder()
 *     .name("Catalunya")
 *     .contactName("Joan")
 *     .email("@provcat.com")
 *     .phone("93")
 *     .address("Barcelona")
 *     .page(0)
 *     .size(20)
 *     .sortBy("name")
 *     .sortDir("asc")
 *     .build();
 * </pre>
 * </p>
 *
 * <p>Exemple d'ús amb Builder (cerca simple):
 * <pre>
 * SupplierFilterDTO filterDto = SupplierFilterDTO.builder()
 *     .name("Catalunya")
 *     .build(); // Usa valors per defecte per paginació.
 * </pre>
 * </p>
 *
 * <p>Estructura JSON de la petició completa:
 * <pre>
 * {
 *   "name": "Catalunya",
 *   "contactName": "Joan",
 *   "email": "@provcat.com",
 *   "phone": "93",
 *   "address": "Barcelona",
 *   "page": 0,
 *   "size": 20,
 *   "sortBy": "name",
 *   "sortDir": "asc"
 * }
 * </pre>
 * </p>
 *
 * <p><strong>Notes d'implementació:</strong>
 * <ul>
 *   <li>Tots els filtres de text utilitzen cerca parcial insensible a majúscules</li>
 *   <li>El companyUuid s'extreu automàticament de l'usuari per garantir seguretat</li>
 *   <li>La classe inclou mètode utilitari hasTextFilters()</li>
 * </ul>
 * </p>
 *
 * <p><strong>Seguretat:</strong>
 * El companyUuid s'extreu automàticament de l'usuari autenticat, garantint que
 * cada usuari només pugui filtrar proveïdors de la seva pròpia empresa.
 * </p>
 *
 * @author Enrique Pérez
 * @version 3.0
 * @since 1.0
 * @see SupplierResponseDTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierFilterDTO {

    // Filtres de text (cerca parcial, insensible a majúscules)
    private String name;        // Nom del proveïdor
    private String contactName; // Nom de contacte
    private String email;       // Email
    private String phone;       // Telèfon
    private String address;     // Adreça

    // Paginació
    @Min(value = 0, message = "El número de pàgina ha de ser 0 o superior")
    @Builder.Default
    private int page = 0;

    @Min(value = 1, message = "La mida de pàgina ha de ser 1 o superior")
    @Builder.Default
    private int size = 10;

    // Ordenació (camps disponibles: name, contactName, email, phone, createdAt, updatedAt)
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
                (contactName != null && !contactName.trim().isEmpty()) ||
                (email != null && !email.trim().isEmpty()) ||
                (phone != null && !phone.trim().isEmpty()) ||
                (address != null && !address.trim().isEmpty());
    }

}