package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.*;


/**
 * Interfície que defineix les operacions de negoci per a la gestió d'informes i estadístiques.
 * <p>
 * Aquesta capa actua com a contracte entre el controlador i la implementació del servei,
 * definint les funcionalitats disponibles relacionades amb la obtenció d'informació per informes i estadístiques.
 * </p>
 *
 * @author Daniel Garcia
 * @author Enrique Pérez
 * @since 1.0
 */
public interface ReportService {


    /**
     * Obté informació de comandes l'últim mes.
     *
     * @return La informació corresponent com a {@link DashboardResponseDTO}.
     */
    DashboardResponseDTO dashboardInfo();

    /**
     * Obté informació de comandes / proveïdors / productes
     *
     * @param dto periode de temps
     * @return La informació corresponent com a {@link ReportGlobalResponseDTO}.
     */
    ReportGlobalResponseDTO globalInfo(PeriodRequestDTO dto);

    /**
     * Genera un pdf amb les dades del report demanat
     *
     * @param dto amb la informació del report
     * @return byte[] pdf
     */
    byte[] generateGlobalInfoPDF(ReportGlobalResponseDTO dto);

}
