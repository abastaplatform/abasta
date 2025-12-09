package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.services.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test unitari per al ReportController.
 * <p>
 * Aquest test valida els endpoints REST relacionats amb informes i estadístiques .
 * S'utilitza MockMvc per simular les peticions HTTP i es mockeja
 * el servei ReportService per evitar dependències de base de dades.
 * </p>
 *
 * @autor: Daniel Garcia
 * @version: 1.0
 */
@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(reportController)
                .setControllerAdvice() // si añadís GlobalExceptionHandler, añadirlo aquí
                .build();
    }

    /**
     * Comprova l'endpoint per rebre informació al dashboard
     * @throws Exception excepció
     */
    @Test
    @DisplayName("GET /api/reports/dashboard → retorna dades correctes")
    void dashboardInfo_success() throws Exception {

        DashboardResponseDTO dto = DashboardResponseDTO.builder()
                .totalComandes(5)
                .comandesPendents(2)
                .despesaComandes(BigDecimal.valueOf(150))
                .build();

        when(reportService.dashboardInfo()).thenReturn(dto);

        mockMvc().perform(get("/api/reports/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Comprovar estructura d'ApiResponseDTO
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Informació per dashboard correcta."))

                // Comprovar dades internes
                .andExpect(jsonPath("$.data.totalComandes").value(5))
                .andExpect(jsonPath("$.data.comandesPendents").value(2))
                .andExpect(jsonPath("$.data.despesaComandes").value(150));

        verify(reportService).dashboardInfo();
    }

    /**
     * Comprova l'endpoint per rebre informació global
     * @throws Exception excepció
     */
    @Test
    @DisplayName("GET /api/reports/global → retorna dades correctes")
    void globalInfo_success() throws Exception {

        ReportGlobalResponseDTO dto = ReportGlobalResponseDTO.builder()
                .totalComandes(10)
                .despesaTotal(BigDecimal.valueOf(250))
                .build();

        when(reportService.globalInfo(any())).thenReturn(dto);

        mockMvc().perform(get("/api/reports/global")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Informació per el report correcta."))
                .andExpect(jsonPath("$.data.totalComandes").value(10))
                .andExpect(jsonPath("$.data.despesaTotal").value(250));

        verify(reportService).globalInfo(any(PeriodRequestDTO.class));
    }

    /**
     * Comprova l'endpoint per generar pdf de l'informe
     * @throws Exception excepció
     *
     */
    @Test
    @DisplayName("GET /api/reports/global/pdf → retorna un PDF correctament")
    void globalInfoPDF_success() throws Exception {

        ReportGlobalResponseDTO dto = ReportGlobalResponseDTO.builder()
                .totalComandes(3)
                .despesaTotal(BigDecimal.valueOf(44))
                .build();

        byte[] fakePdf = "FAKE_PDF_BYTES".getBytes();

        when(reportService.globalInfo(any())).thenReturn(dto);
        when(reportService.generateGlobalInfoPDF(dto)).thenReturn(fakePdf);

        mockMvc().perform(get("/api/reports/global/pdf")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))

                // retorn de dades
                .andExpect(content().bytes(fakePdf))

                // Headers
                .andExpect(header().string("Content-Disposition",
                        "inline; filename=\"Report_Global_Abasta_de_2025_01_01_a_2025_01_31.pdf\""));

        verify(reportService).globalInfo(any(PeriodRequestDTO.class));
        verify(reportService).generateGlobalInfoPDF(dto);
    }

}