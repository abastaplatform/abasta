package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static jakarta.xml.bind.DatatypeConverter.parseDate;

/**
 * Controlador REST per gestionar les operacions relacionades amb els informes i estadístiques.
 * <p>
 * Aquest controlador s'encarrega de rebre les peticions HTTP provinents del client
 * i delegar la seva execució al servei {@link ReportService}.
 * </p>
 *
 * <p>Exposa els endpoints principals del recurs <strong>/api/reports</strong>.</p>
 * <p>Ofereix els endpoints:</p>
 * <ul>
 *     <li>GET /dashboard (sense paràmetres)</li>
 * </ul>
 *
 * @author Daniel Garcia
 * @author Enrique Pérez
 * @version 1.0
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    /** Servei encarregat de la lògica de negoci dels reports . */
    private final ReportService reportService;

    /**
     * Report amb dades de comandes de l'últim mes
     *
     * @return {@link DashboardResponseDTO} amb les dades de les comandes
     */
    @GetMapping("dashboard")
    public ResponseEntity<ApiResponseDTO<DashboardResponseDTO>> dashboardInfo(){

        // Servei
        DashboardResponseDTO dashboard = reportService.dashboardInfo();

        // Retorn
        return ResponseEntity.ok(ApiResponseDTO.success(dashboard, "Informació per dashboard correcta."));
    }

    /**
     * Report amb dades de comandes, proveïdors i productes en un periode de temps
     *
     * @param startDate data inicial del periode
     * @param endDate data final del periode
     * @return {@link ReportGlobalResponseDTO} amb diferents dades
     */
    @GetMapping("global")
    public ResponseEntity<ApiResponseDTO<ReportGlobalResponseDTO>> globalInfo(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {

        LocalDateTime start = parseDate(startDate,false);
        LocalDateTime end = parseDate(endDate,true);

        // Creem DTO a partir dels paràmetres
        PeriodRequestDTO dto = PeriodRequestDTO.builder().dataInicial(start).dataFinal(end).build();

        // Servei
        ReportGlobalResponseDTO report = reportService.globalInfo(dto);

        // Retorn
        return ResponseEntity.ok(ApiResponseDTO.success(report, "Informació per el report correcta."));
    }

    /**
     * PDF : Report amb dades de comandes, proveïdors i productes en un periode de temps
     *
     * @param startDate data inicial del periode
     * @param endDate data final del periode
     * @return {@link ReportGlobalResponseDTO} amb diferents dades
     */
    @GetMapping("global/pdf")
    public ResponseEntity<byte[]> globalInfoPDF(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {

        LocalDateTime start = parseDate(startDate,false);
        LocalDateTime end = parseDate(endDate,true);

        // Creem DTO a partir dels paràmetres
        PeriodRequestDTO dto = PeriodRequestDTO.builder().dataInicial(start).dataFinal(end).build();

        // Servei
        ReportGlobalResponseDTO report = reportService.globalInfo(dto);

        byte[] pdfBytes = reportService.generateGlobalInfoPDF(report);

        // Retorn
        //return ResponseEntity.ok(ApiResponseDTO.success(report, "Informació per el report correcta."));
        // Devolver PDF como respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline")
                .filename("report.pdf").build());

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    /**
     * Fa el canvi de format String a LocalDateTime
     * Si només es data, permet ajustar inicio o fi de dia
     *
     * @param dateStr la data com a string
     * @param endOfDay cert si volem que sigui final o fals si és inici
     * @return LocalDateTime data retornada en format correcte
     */
    private LocalDateTime parseDate(String dateStr, boolean endOfDay) {
        if (dateStr == null || dateStr.isBlank()) {
            throw new ResourceNotFoundException("Format de data no vàlid");
        }

        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,              // 2025-12-07T14:23:59
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"), // 2025-12-07 14:23:59
                DateTimeFormatter.ISO_LOCAL_DATE                    // 2025-12-07
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                if (formatter == DateTimeFormatter.ISO_LOCAL_DATE) {
                    LocalDate date = LocalDate.parse(dateStr, formatter);
                    return endOfDay ? date.atTime(LocalTime.MAX) : date.atStartOfDay();
                }
                return LocalDateTime.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Provem els següents parsers
            }
        }

        // Si cap parser ha funcionat
        throw new ResourceNotFoundException("Format de data no vàlid");
    }

}