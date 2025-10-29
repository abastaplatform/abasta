package cat.abasta_back_end.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitaris simplificats per a la classe JwtUtil.
 * Verifica la generació, validació i processament de tokens JWT.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("JwtUtil Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testUsername;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        testUsername = "test@abasta.com";
    }

    @Test
    @DisplayName("Hauria de generar un token JWT vàlid")
    void generateToken_ShouldCreateValidToken() {
        // When
        String token = jwtUtil.generateToken(testUsername);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT té 3 parts separades per punts
    }

    @Test
    @DisplayName("Hauria d'extreure el username correctament del token")
    void getUsernameFromToken_ShouldReturnCorrectUsername() {
        // Given
        String token = jwtUtil.generateToken(testUsername);

        // When
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        // Then
        assertThat(extractedUsername).isEqualTo(testUsername);
    }

    @Test
    @DisplayName("Hauria de validar correctament un token vàlid")
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        // Given
        String token = jwtUtil.generateToken(testUsername);

        // When
        boolean isValid = jwtUtil.validateToken(token, testUsername);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Hauria de retornar false per un token amb username incorrecte")
    void validateToken_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        // Given
        String token = jwtUtil.generateToken(testUsername);
        String differentUsername = "different@abasta.com";

        // When
        boolean isValid = jwtUtil.validateToken(token, differentUsername);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Hauria de retornar false per un token malformat")
    void validateToken_ShouldReturnFalse_WhenTokenIsMalformed() {
        // Given
        String invalidToken = "token.malformat.invalid";

        // When
        boolean isValid = jwtUtil.validateToken(invalidToken, testUsername);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Hauria d'obtenir la data d'expiració del token")
    void getExpirationDateFromToken_ShouldReturnExpirationDate() {
        // Given
        String token = jwtUtil.generateToken(testUsername);
        Date now = new Date();

        // When
        Date expirationDate = jwtUtil.getExpirationDateFromToken(token);

        // Then
        assertThat(expirationDate).isNotNull();
        assertThat(expirationDate).isAfter(now);
    }

    @Test
    @DisplayName("Un token recent no hauria d'estar expirat")
    void isTokenExpired_ShouldReturnFalse_WhenTokenIsRecent() {
        // Given
        String token = jwtUtil.generateToken(testUsername);

        // When
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("Hauria de llançar excepció per token null")
    void getUsernameFromToken_ShouldThrowException_WhenTokenIsNull() {
        // When & Then
        assertThatThrownBy(() -> jwtUtil.getUsernameFromToken(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Tokens generats haurien de tenir l'estructura JWT correcta")
    void generateToken_ShouldHaveCorrectJwtStructure() {
        String token1 = jwtUtil.generateToken(testUsername);
        String token2 = jwtUtil.generateToken("other@abasta.com");

        // Verificar estructura JWT (3 partes)
        assertThat(token1.split("\\.")).hasSize(3);
        assertThat(token2.split("\\.")).hasSize(3);

        // Tokens para usuarios diferentes son diferentes
        assertThat(token1).isNotEqualTo(token2);

        // Usernames correctos
        assertThat(jwtUtil.getUsernameFromToken(token1)).isEqualTo(testUsername);
        assertThat(jwtUtil.getUsernameFromToken(token2)).isEqualTo("other@abasta.com");
    }
}