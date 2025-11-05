package cat.abasta_back_end.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) per a rebre les dades necessàries per crear un nou producte.
 * <p>
 * Aquest DTO s’utilitza per validar i encapsular la informació enviada pel client abans
 * de crear el registre corresponent a la base de dades.
 * </p>
 *
 * <p>Es correspon amb l’estructura de la taula <strong>products</strong>:</p>
 * <ul>
 *   <li><code>supplier_id</code> → Identificador del proveïdor (obligatori).</li>
 *   <li><code>category</code> → Categoria del producte (opcional).</li>
 *   <li><code>name</code> → Nom del producte (obligatori).</li>
 *   <li><code>description</code> → Descripció opcional.</li>
 *   <li><code>price</code> → Preu del producte (obligatori, DECIMAL(10,2)).</li>
 *   <li><code>unit</code> → Unitat de mesura (opcional).</li>
 *   <li><code>image_url</code> → Enllaç a la imatge del producte (opcional).</li>
 * </ul>
 *
 * <p>Els camps <code>uuid</code>, <code>is_active</code>, <code>created_at</code> i <code>updated_at</code>
 * són gestionats automàticament pel sistema i no formen part d’aquest DTO.</p>
 *
 * @author Daniel Garcia
 * @version 1.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    /**
     * Identificador del proveïdor associat al producte.
     * Ha de correspondre a un proveïdor existent a la base de dades.
     */
    @NotNull(message = "El proveïdor és obligatori")
    private Long supplierId;

    /**
     * Categoria a la qual pertany el producte (ex: Fruites, Begudes...).
     * Camp opcional.
     */
    @Size(max = 255, message = "La categoria no pot superar els 255 caràcters")
    private String category;

    /**
     * Nom del producte.
     * No pot estar buit ni superar els 255 caràcters.
     */
    @NotBlank(message = "El nom del producte és obligatori")
    @Size(max = 255, message = "El nom no pot superar els 255 caràcters")
    private String name;

    /**
     * Descripció del producte.
     * Camp opcional que pot contenir informació addicional.
     */
    @Size(max = 5000, message = "La descripció no pot superar els 5000 caràcters")
    private String description;

    /**
     * Preu del producte.
     * Ha de ser un valor positiu amb com a màxim 10 dígits (8 enters i 2 decimals).
     */
    @NotNull(message = "El preu és obligatori")
    @DecimalMin(value = "0.00", inclusive = true, message = "El preu no pot ser negatiu")
    @Digits(integer = 8, fraction = 2, message = "El preu ha de tenir com a màxim 8 dígits enters i 2 decimals")
    private BigDecimal price;

    /**
     * Unitat de mesura del producte (ex: kg, litres, unitats, caixes...).
     * Camp opcional segons la base de dades.
     */
    @Size(max = 50, message = "La unitat no pot superar els 50 caràcters")
    private String unit;

    /**
     * URL de la imatge del producte.
     * Camp opcional, però si es proporciona ha de ser un enllaç vàlid.
     */
    @Size(max = 500, message = "La ruta o nom de la imatge no pot superar els 500 caràcters")
    /*
    - Activar si el camp és una url per comprovar mitjançant patró.
    @Pattern(
            regexp = "^(https?://.*)?$",
            message = "La URL de la imatge ha de ser un enllaç vàlid (http o https)"
    )*/
    private String imageUrl;
}

