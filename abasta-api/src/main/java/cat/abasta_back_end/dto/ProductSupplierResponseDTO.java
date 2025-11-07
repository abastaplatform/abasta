package cat.abasta_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) per retornar la informació básica d'un proveïdor associat a un producte.
 * <p>
 * Aquest DTO encapsula les dades que s’envien des del backend al client
 * </p>
 *
 * <p>Es correspon amb la taula <strong>suppliers</strong> i inclou els camps reduïts:</p>
 * <ul>
 *   <li><code>uuid</code> → Identificador únic universal.</li>
 *   <li><code>name</code> → Nom del proveïdor.</li>
 * </ul>
 *
 * <p>Aquest DTO s’utilitza principalment com a resposta en operacions REST
 * on la relació amb el proveïdor requereix l'enviament d'informació adicional.</p>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSupplierResponseDTO {

    /** Identificador únic universal (UUID) del proveïdor. */
    private String uuid;

    /** Nom del proveïdor */
    private String name;

}
