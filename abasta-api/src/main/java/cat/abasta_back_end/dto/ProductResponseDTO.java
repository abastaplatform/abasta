package cat.abasta_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) per retornar la informació d’un producte.
 * <p>
 * Aquest DTO encapsula les dades que s’envien des del backend al client
 * després de crear, consultar o actualitzar un producte.
 * </p>
 *
 * <p>Es correspon amb la taula <strong>products</strong> i inclou els camps principals:</p>
 * <ul>
 *   <li><code>id</code> → Identificador intern del producte.</li>
 *   <li><code>uuid</code> → Identificador únic universal.</li>
 *   <li><code>supplierId</code> → ID del proveïdor associat.</li>
 *   <li><code>category</code> → Categoria del producte.</li>
 *   <li><code>name</code> → Nom del producte.</li>
 *   <li><code>description</code> → Descripció del producte.</li>
 *   <li><code>price</code> → Preu del producte (DECIMAL(10,2)).</li>
 *   <li><code>unit</code> → Unitat de mesura (kg, litres, etc.).</li>
 *   <li><code>imageUrl</code> → Nom o ruta de la imatge associada.</li>
 *   <li><code>isActive</code> → Estat del producte (actiu o inactiu).</li>
 *   <li><code>createdAt</code> → Data i hora de creació del registre.</li>
 *   <li><code>updatedAt</code> → Data i hora de l’última modificació.</li>
 * </ul>
 *
 * <p>Aquest DTO s’utilitza principalment com a resposta en operacions REST
 * com ara <strong>crear producte</strong> o <strong>consultar llistes de productes</strong>.</p>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    /** Identificador únic universal (UUID) del producte. */
    private String uuid;

    /** Dades básiques del proveïdor relacionat. */
    private ProductSupplierResponseDTO supplier;

    /** Categoria del producte (ex: Fruites, Begudes...). */
    private String category;

    /** Nom del producte. */
    private String name;

    /** Descripció detallada del producte. */
    private String description;

    /** Preu del producte (DECIMAL(10,2)). */
    private BigDecimal price;

    /** Unitat de mesura (ex: kg, litres, unitats, caixes...). */
    private String unit;

    /** Nom o ruta de la imatge associada al producte. */
    private String imageUrl;

    /** Indica si el producte està actiu (TRUE) o inactiu (FALSE). */
    private Boolean isActive;

    /** Data i hora en què es va crear el producte. */
    private LocalDateTime createdAt;

    /** Data i hora de l’última actualització del producte. */
    private LocalDateTime updatedAt;
}
