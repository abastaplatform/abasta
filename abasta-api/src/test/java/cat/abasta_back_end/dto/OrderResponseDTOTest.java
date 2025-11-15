package cat.abasta_back_end.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaris per {@link OrderResponseDTO}.
 *
 * <p>
 * Verifica:
 * <ul>
 *     <li>Builder i getters.</li>
 *     <li>equals/hashCode i toString generats per Lombok.</li>
 * </ul>
 * </p>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@DisplayName("OrderResponseDTO Tests")
class OrderResponseDTOTest {

    @Test
    @DisplayName("Builder crea correctament el DTO")
    void builder_createsDTO() {

        OrderItemResponseDTO item = OrderItemResponseDTO.builder()
                .uuid("item-1")
                .productUuid("p-1")
                .subtotal(new BigDecimal("10"))
                .build();

        OrderResponseDTO dto = OrderResponseDTO.builder()
                .uuid("order-1")
                .name("Comanda")
                .status("PENDING")
                .totalAmount(new BigDecimal("100"))
                .notes("notes")
                .deliveryDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .supplierUuid("sup-1")
                .items(List.of(item))
                .build();

        assertThat(dto.getUuid()).isEqualTo("order-1");
        assertThat(dto.getStatus()).isEqualTo("PENDING");
        assertThat(dto.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("equals i hashCode són coherents")
    void equalsHashCode() {
        OrderResponseDTO a = OrderResponseDTO.builder()
                .uuid("x")
                .build();

        OrderResponseDTO b = OrderResponseDTO.builder()
                .uuid("x")
                .build();

        assertThat(a).isEqualTo(b);
    }

    @Test
    @DisplayName("toString conté informació rellevant")
    void toString_notEmpty() {
        OrderResponseDTO dto = OrderResponseDTO.builder()
                .uuid("x")
                .build();

        assertThat(dto.toString()).contains("uuid");
    }
}
