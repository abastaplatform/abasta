package cat.abasta_back_end;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests d'integració simplificats per la classe principal de l'aplicació.
 * Verifica que l'aplicació Spring Boot es carregui correctament.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("AbastaBackEndApplication Tests")
class AbastaBackEndApplicationTest {

    @Test
    @DisplayName("Context de l'aplicació hauria de carregar-se correctament")
    void contextLoads() {
        // Aquest test verifica que el context de Spring Boot es carregui sense errors
        // Si hi ha problemes de configuració, beans mal configurats, etc., aquest test fallarà
    }

    @Test
    @DisplayName("Mètode main hauria d'executar-se sense errors")
    void main_ShouldRunWithoutErrors() {
        // Given & When & Then
        assertThatCode(() -> AbastaBackEndApplication.main(new String[]{}))
                .doesNotThrowAnyException();
    }
}