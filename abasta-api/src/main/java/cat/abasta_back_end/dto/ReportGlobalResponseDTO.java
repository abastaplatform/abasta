package cat.abasta_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object (DTO) per retornar la informació de comandes, proveïdors i productes segons periode.
 * <p>
 * Aquest DTO encapsula les dades de les comandes, proveïdors i productes en un periode determinant.
 *
 * <p>Aquest DTO s’utilitza principalment com a resposta en operacions REST per recuperar informació de comandes als proveïdors i els productes demanats.</p>
 *
 * @author Daniel Garcia
 * @author Enrique Pérez
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportGlobalResponseDTO {

    // --- Resum del període ---
    /**
     * Nombre de comandes
     */
    private int totalComandes;

    /**
     * Despesa total de comandes
     */
    private BigDecimal despesaTotal;

    /**
     * Mitjana de comanda
     */
    private BigDecimal comandaMitjana;

    /**
     * DTO amb la informació de proveïdors
     */
    private List<DespesaPerProveidorDTO> despesaProveidors;

    /**
     * DTO amb la informació de productes
     */
    private List<ProducteTopDTO> topProductes;
}

