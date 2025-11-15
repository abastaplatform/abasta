package cat.abasta_back_end.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.validation.*;
import java.math.BigDecimal;
import java.util.Set;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaris per {@link OrderItemRequestDTO}.
 *
 * <p>
 * Verifica:
 * <ul>
 *     <li>Funcionament del builder i getters.</li>
 *     <li>Validació de camps amb anotacions Jakarta Validation.</li>
 *     <li>Mètodes equals, hashCode i toString generats per Lombok.</li>
 * </ul>
 * </p>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@DisplayName("OrderItemRequestDTO Tests")
class OrderItemRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Builder crea correctament el DTO")
    void builder_createsDTO() {
        OrderItemRequestDTO dto = OrderItemRequestDTO.builder()
                .productUuid("prod-123")
                .quantity(new BigDecimal("3"))
                .notes("Observació")
                .build();

        assertThat(dto.getProductUuid()).isEqualTo("prod-123");
        assertThat(dto.getQuantity()).isEqualTo(new BigDecimal("3"));
        assertThat(dto.getNotes()).isEqualTo("Observació");
    }

    @Test
    @DisplayName("Validació falla si el productUuid és nul o buit")
    void validation_fails_onInvalidProductUuid() {
        OrderItemRequestDTO dto = OrderItemRequestDTO.builder()
                .productUuid("")
                .quantity(new BigDecimal("1"))
                .build();

        Set<ConstraintViolation<OrderItemRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Validació falla si la quantitat és nul·la")
    void validation_fails_onNullQuantity() {
        OrderItemRequestDTO dto = OrderItemRequestDTO.builder()
                .productUuid("prod-1")
                .quantity(null)
                .build();

        Set<ConstraintViolation<OrderItemRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("equals i hashCode funcionen correctament")
    void equalsHashCode() {
        OrderItemRequestDTO a = OrderItemRequestDTO.builder()
                .productUuid("p1")
                .quantity(new BigDecimal("2"))
                .build();

        OrderItemRequestDTO b = OrderItemRequestDTO.builder()
                .productUuid("p1")
                .quantity(new BigDecimal("2"))
                .build();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("toString genera informació")
    void toString_notEmpty() {
        OrderItemRequestDTO dto = OrderItemRequestDTO.builder()
                .productUuid("x")
                .quantity(new BigDecimal("1"))
                .build();

        assertThat(dto.toString()).contains("productUuid");
    }
}
