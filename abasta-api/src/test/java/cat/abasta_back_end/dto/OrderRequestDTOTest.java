package cat.abasta_back_end.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaris per {@link OrderRequestDTO}.
 *
 * <p>
 * Verifica:
 * <ul>
 *     <li>Builder.</li>
 *     <li>Validació de camps obligatoris.</li>
 *     <li>equals/hashCode i toString de Lombok.</li>
 * </ul>
 * </p>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@DisplayName("OrderRequestDTO Tests")
class OrderRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Builder construeix correctament")
    void builder_createsDTO() {

        OrderItemRequestDTO item = OrderItemRequestDTO.builder()
                .productUuid("p-1")
                .quantity(java.math.BigDecimal.ONE)
                .build();

        OrderRequestDTO dto = OrderRequestDTO.builder()
                .name("Comanda Test")
                .supplierUuid("sup-123")
                .notes("notes")
                .deliveryDate(LocalDate.now())
                .items(List.of(item))
                .build();

        assertThat(dto.getName()).isEqualTo("Comanda Test");
        assertThat(dto.getSupplierUuid()).isEqualTo("sup-123");
        assertThat(dto.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Validació falla si el nom és buit")
    void validation_fails_onBlankName() {
        OrderRequestDTO dto = OrderRequestDTO.builder()
                .name("")
                .supplierUuid("x")
                .items(List.of())
                .build();

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    @DisplayName("Validació falla si el supplierUuid és buit")
    void validation_fails_onBlankSupplier() {
        OrderRequestDTO dto = OrderRequestDTO.builder()
                .name("ok")
                .supplierUuid("")
                .items(List.of())
                .build();

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    @DisplayName("Validació falla si items és nul")
    void validation_fails_onNullItems() {
        OrderRequestDTO dto = OrderRequestDTO.builder()
                .name("ok")
                .supplierUuid("x")
                .items(null)
                .build();

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    @DisplayName("toString funciona")
    void toString_notEmpty() {
        OrderRequestDTO dto = OrderRequestDTO.builder()
                .name("x")
                .supplierUuid("y")
                .items(List.of())
                .build();

        assertThat(dto.toString()).contains("name");
    }
}
