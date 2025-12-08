package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.entities.*;
import cat.abasta_back_end.exceptions.BadRequestException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.OrderRepository;
import cat.abasta_back_end.repositories.ProductRepository;
import cat.abasta_back_end.repositories.UserRepository;
import cat.abasta_back_end.services.ReportService;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Implementació de la interfície {@link ReportService}.
 * <p>
 * Aquesta classe conté la lògica de negoci per recuperar la informació de diferents
 * entitats i generar informes i estadístiques tractant les dades i enviant els DTO amb
 * la informació.
 * </p>
 *
 * <p>Inclou:</p>
 * <ul>
 *   <li>Dashboard: Informació de les comandes l'últim mes..</li>
 * </ul>
 *
 * @author Daniel Garcia
 * @author Enrique Pérez
 * @since 1.0
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    /**
     * Constructor amb injecció de dependències.
     *
     * @param productRepository  repositori de productes
     * @param orderRepository repositori de comandes
     */
    public ReportServiceImpl(ProductRepository productRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public DashboardResponseDTO dashboardInfo() {

        // Recuperem informació de l'usuari i la companyia
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("Usuari no trobat: " + username));
        Long companyId = user.getCompany().getId();

        // Calculem el període (últim mes)
        LocalDateTime currentDate = LocalDateTime.now();
        YearMonth currentYearMonth = YearMonth.from(currentDate);
        LocalDateTime startDate = currentYearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = currentYearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        // Recuperar llistat de comandes
        List<Order> orders = orderRepository.getOrdersByCompanyIdAndPeriodWithoutOrderItems(companyId, startDate, endDate);

        // Recuperem només les actives
        List<Order> activeOrders = orders.stream().filter(o -> EnumSet.of(Order.OrderStatus.PENDING,Order.OrderStatus.SENT,Order.OrderStatus.CONFIRMED,Order.OrderStatus.COMPLETED).contains(o.getStatus())).toList();

        // Calculem el total de comandes
        int totalOrders = activeOrders.size();

        // Calculem l'import total
        BigDecimal totalAmount = activeOrders.stream().map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculem els pendents
        int pendingOrders = (int) activeOrders.stream().filter(o -> o.getStatus() == Order.OrderStatus.PENDING).count();

        // Retornar el resultat com a DTO
        return mapToDashboardResponseDTO(totalOrders,totalAmount,pendingOrders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ReportGlobalResponseDTO globalInfo(PeriodRequestDTO dto) {

        // Recuperem informació de l'usuari i la companyia
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("Usuari no trobat: " + username));
        Long companyId = user.getCompany().getId();

        // Recuperar llistat de comandes
        List<Order> orders = orderRepository.getOrdersByCompanyIdAndPeriodWithOrderItems(companyId, dto.getDataInicial(), dto.getDataFinal());

        // Recuperem només les actives
        List<Order> activeOrders = orders.stream().filter(o -> EnumSet.of(Order.OrderStatus.PENDING,Order.OrderStatus.SENT,Order.OrderStatus.CONFIRMED,Order.OrderStatus.COMPLETED).contains(o.getStatus())).toList();

        // Calculem el total de comandes
        int totalComandes = activeOrders.size();

        // Despesa total
        BigDecimal despesaTotal = activeOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .map(item -> item.getUnitPrice().multiply(item.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Comanda mitjana
        BigDecimal comandaMitjana = totalComandes > 0
                ? despesaTotal.divide(BigDecimal.valueOf(totalComandes), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Despesa per proveïdor
        Map<Supplier, List<Order>> ordersBySupplier = activeOrders.stream().collect(Collectors.groupingBy(Order::getSupplier));

        List<DespesaPerProveidorDTO> despesaPerProveidor = ordersBySupplier.entrySet().stream()
                .map(entry -> {
                    Supplier supplier = entry.getKey();
                    List<Order> supplierOrders = entry.getValue();

                    // Nombre de comandes del proveïdor
                    int numComandes = supplierOrders.size();

                    // Despesa total del proveïdor
                    BigDecimal totalSupplier = supplierOrders.stream()
                            .flatMap(o -> o.getItems().stream())
                            .map(i -> i.getUnitPrice().multiply(i.getQuantity()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, RoundingMode.HALF_UP);

                    // % respecte el total del període
                    BigDecimal percentatge = despesaTotal.compareTo(BigDecimal.ZERO) > 0
                            ? totalSupplier.multiply(BigDecimal.valueOf(100))
                            .divide(despesaTotal, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return new DespesaPerProveidorDTO(
                            supplier.getName(),
                            numComandes,
                            totalSupplier,
                            percentatge
                    );
                })
                .toList();

        // Agrupem tots els items per producte
        Map<Product, List<OrderItem>> itemsPerProduct = activeOrders.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(OrderItem::getProduct));

        // Convertim a llista de DTO + càlculs
        List<ProducteTopDTO> topProductes = itemsPerProduct.entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    List<OrderItem> items = entry.getValue();

                    // Quantitat total
                    BigDecimal quantitatTotal = items.stream()
                            .map(OrderItem::getQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Despesa total
                    BigDecimal despesaProducte = items.stream()
                            .map(i -> i.getUnitPrice().multiply(i.getQuantity()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, RoundingMode.HALF_UP);

                    return new ProducteTopDTO(
                            product.getName(),
                            quantitatTotal,
                            despesaProducte
                    );
                })
                // Ordenem pels més demanats
                .sorted(Comparator.comparing(ProducteTopDTO::getQuantitatTotal).reversed())
                // Top 10
                .limit(10)
                .toList();

        //Retornar el resultat com a DTO
        return mapToReportResponseDTO(
                dto.getDataInicial(),
                dto.getDataFinal(),
                totalComandes,
                despesaTotal,
                comandaMitjana,
                despesaPerProveidor,
                topProductes
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public byte[] generateGlobalInfoPDF(ReportGlobalResponseDTO report) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Logo Abasta
            Image img = Image.getInstance(getClass().getResource("/images/logo_pdf.png"));
            img.scaleToFit(100,100);
            img.setAlignment(Image.ALIGN_RIGHT);


            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Logo al document
            document.add(img);

            // Fonts
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLUE);
            Font subtitleFont = new Font(Font.HELVETICA, 16, Font.BOLD, Color.BLACK);

            // Títol
            Paragraph title = new Paragraph("Report Global", titleFont);
            document.add(title);

            // Periode
            document.add(new Paragraph("\n"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            document.add(new Paragraph("Període: de " + report.getDataInicial().format(formatter) + " a " + report.getDataFinal().format(formatter) ));

            // Informació general
            document.add(new Paragraph("\n"));
            Paragraph subtitleGlobal = new Paragraph("Resum global", subtitleFont);
            document.add(subtitleGlobal);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total comandes: " + report.getTotalComandes()));
            DecimalFormat df = new DecimalFormat("#0.00");
            String despesaTotalFormatada = df.format(report.getDespesaTotal());
            document.add(new Paragraph("Despesa total: " + despesaTotalFormatada));
            document.add(new Paragraph("Comanda mitjana: " + report.getComandaMitjana()));
            document.add(new Paragraph("\n"));

            // Tabla de proveïdors
            document.add(new Paragraph("\n"));
            Paragraph subtitle2 = new Paragraph("Despesa proveïdors", subtitleFont);
            document.add(subtitle2);
            document.add(new Paragraph("\n"));
            PdfPTable table = new PdfPTable(4);
            table.addCell("Proveïdor");
            table.addCell("Num Comandes");
            table.addCell("Despesa Total");
            table.addCell("% del total");
            for (var p : report.getDespesaProveidors()) {
                table.addCell(p.getProveidor());
                table.addCell(String.valueOf(p.getNumComandes()));
                table.addCell(p.getDespesaTotal().toString());
                table.addCell(p.getPercentatge().toString());
            }
            document.add(table);

            // Taula de productes top
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));
            Paragraph subtitle1 = new Paragraph("Top Productes", subtitleFont);
            document.add(subtitle1);
            document.add(new Paragraph("\n"));
            PdfPTable prodTable = new PdfPTable(3);
            prodTable.addCell("Nom Producte");
            prodTable.addCell("Quantitat Total");
            prodTable.addCell("Despesa Total");
            for (var p : report.getTopProductes()) {
                prodTable.addCell(p.getNomProducte());
                prodTable.addCell(p.getQuantitatTotal().toString());
                prodTable.addCell(p.getDespesaTotal().toString());
            }
            document.add(prodTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BadRequestException("Error generant PDF" + e.getMessage());
        }
    }

    /**
     * Converteix una sèrie d'informació en un {@link DashboardResponseDTO}.
     *
     * @param totalOrders total de comandes
     * @param totalAmount total de despesa
     * @param pendingOrders comandes pendents
     * @return DTO amb la informació del dashboard creat
     */
    private DashboardResponseDTO mapToDashboardResponseDTO(int totalOrders,BigDecimal totalAmount,int pendingOrders) {
        return DashboardResponseDTO.builder()
                .totalComandes(totalOrders)
                .despesaComandes(totalAmount)
                .comandesPendents(pendingOrders)
                .build();
    }

    /**
     * Converteix una sèrie d'informació en un {@link ReportGlobalResponseDTO}.
     *
     * @return DTO amb la informació global creada
     */
    private ReportGlobalResponseDTO mapToReportResponseDTO(LocalDateTime dataInicial, LocalDateTime dataFinal, int totalComandes, BigDecimal despesaTotal, BigDecimal comandaMitjana, List<DespesaPerProveidorDTO> despesaProveidors, List<ProducteTopDTO> topProductes
    ) {
        return ReportGlobalResponseDTO.builder()
                .dataInicial(dataInicial)
                .dataFinal(dataFinal)
                .totalComandes(totalComandes)
                .despesaTotal(despesaTotal)
                .comandaMitjana(comandaMitjana)
                .despesaProveidors(despesaProveidors)
                .topProductes(topProductes)
                .build();
    }

}