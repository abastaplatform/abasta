package cat.abasta_back_end.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    /**
     * Data inicial del període seleccionat
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataInicial;

    /**
     * Data final del període seleccionat
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataFinal;

    /**
     * Nombre de comandes
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private int totalComandes;

    /**
     * Despesa total de comandes
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

