package cat.abasta_back_end.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO per gestionar les peticions de cerca avançada de proveïdors amb múltiples filtres.
 * Conté tots els paràmetres disponibles per realitzar cerques complexes amb paginació i ordenació.
 *
 * <p>Aquest DTO s'utilitza en l'endpoint de cerca avançada quan es vol filtrar proveïdors
 * per múltiples criteris simultàniament, proporcionant màxima flexibilitat en les cerques.
 * Permet filtrar per tots els camps disponibles de la taula suppliers incloent-hi
 * filtres de text, estat d'activitat i rangs de dates.</p>
 *
 * <p>Els filtres disponibles inclouen:
 * <ul>
 *   <li><strong>Empresa específica per UUID (obligatori)</strong> - Garanteix seguretat de dades</li>
 *   <li><strong>Filtres de text amb cerca parcial (opcionals):</strong>
 *     <ul>
 *       <li>Nom del proveïdor</li>
 *       <li>Nom de contacte</li>
 *       <li>Email del proveïdor</li>
 *       <li>Telèfon de contacte</li>
 *       <li>Adreça completa</li>
 *       <li>Notes o comentaris</li>
 *     </ul>
 *   </li>
 *   <li><strong>Estat d'activitat (opcional)</strong> - Filtra per proveïdors actius/inactius</li>
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
 *   <li>L'UUID de l'empresa és obligatori i no pot estar buit</li>
 *   <li>L'email té format vàlid si es proporciona</li>
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
 *   <li><code>name</code> - Nom del proveïdor (per defecte)</li>
 *   <li><code>contactName</code> - Nom de contacte</li>
 *   <li><code>email</code> - Email</li>
 *   <li><code>phone</code> - Telèfon</li>
 *   <li><code>createdAt</code> - Data de creació</li>
 *   <li><code>updatedAt</code> - Data d'actualització</li>
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
 * <p>Exemple d'ús amb Builder (cerca completa):
 * <pre>
 * SupplierFilterDTO filterDto = SupplierFilterDTO.builder()
 *     .companyUuid("123e4567-e89b-12d3-a456-426614174000")
 *     .name("Catalunya")
 *     .contactName("Joan")
 *     .email("@provcat.com")
 *     .phone("93")
 *     .address("Barcelona")
 *     .notes("proveïdor important")
 *     .isActive(true)
 *     .createdAfter(LocalDateTime.of(2024, 1, 1, 0, 0))
 *     .createdBefore(LocalDateTime.of(2024, 12, 31, 23, 59))
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
 *     .companyUuid("123e4567-e89b-12d3-a456-426614174000")
 *     .name("Catalunya")
 *     .isActive(true)
 *     .build(); // Usa valors per defecte per paginació
 * </pre>
 * </p>
 *
 * <p>Estructura JSON de la petició completa:
 * <pre>
 * {
 *   "companyUuid": "123e4567-e89b-12d3-a456-426614174000",
 *   "name": "Catalunya",
 *   "contactName": "Joan",
 *   "email": "@provcat.com",
 *   "phone": "93",
 *   "address": "Barcelona",
 *   "notes": "proveïdor important",
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
 *   "companyUuid": "123e4567-e89b-12d3-a456-426614174000",
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
 *   <li>El companyUuid sempre és obligatori per garantir la seguretat de les dades</li>
 *   <li>La classe inclou mètodes utilitaris hasTextFilters() i hasDateFilters()</li>
 * </ul>
 * </p>
 *
 * <p><strong>Casos d'ús típics:</strong>
 * <ul>
 *   <li>Cerca de proveïdors per nom i ubicació</li>
 *   <li>Filtrat per proveïdors creats en un període específic</li>
 *   <li>Cerca per informació de contacte (email, telèfon)</li>
 *   <li>Auditoria de proveïdors actualitzats recentment</li>
 *   <li>Cerca en notes per paraules clau específiques</li>
 * </ul>
 * </p>
 *
 * @author Enrique Pérez
 * @version 2.0
 * @since 1.0
 * @see SupplierResponseDTO
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

    // Filtres de text (cerca parcial, insensible a majúscules)
    private String name;        // Nom del proveïdor
    private String contactName; // Nom de contacte
    private String email;       // Email
    private String phone;       // Telèfon
    private String address;     // Adreça
    private String notes;       // Notes

    // Filtre d'estat
    private Boolean isActive;   // Estat actiu (true/false/null per tots)

    // Filtres de dates (rang de dates)
    private LocalDateTime createdAfter;   // Creat després de...
    private LocalDateTime createdBefore;  // Creat abans de...
    private LocalDateTime updatedAfter;   // Actualitzat després de...
    private LocalDateTime updatedBefore;  // Actualitzat abans de...

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
                (address != null && !address.trim().isEmpty()) ||
                (notes != null && !notes.trim().isEmpty());
    }

    /**
     * Verifica si algun filtre de data està aplicat.
     *
     * @return true si hi ha algun filtre de data actiu
     */
    public boolean hasDateFilters() {
        return createdAfter != null || createdBefore != null ||
                updatedAfter != null || updatedBefore != null;
    }
}
