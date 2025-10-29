package cat.abasta_back_end.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris simplificats per a la classe JwtAuthenticationFilter.
 * Verifica el processament de peticions HTTP i l'establiment del context de seguretat.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("JwtAuthenticationFilter Tests")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String validToken;
    private String testUsername;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        testUsername = "test@abasta.com";
    }

    @Test
    @DisplayName("Hauria de processar petició sense token correctament")
    void doFilterInternal_ShouldContinueFilter_WhenNoToken() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Hauria de processar petició amb token vàlid")
    void doFilterInternal_ShouldSetAuthentication_WhenTokenIsValid() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + validToken;
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn(testUsername);
        when(jwtUtil.validateToken(validToken, testUsername)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(testUsername);
    }

    @Test
    @DisplayName("Hauria de rebutjar token invàlid")
    void doFilterInternal_ShouldNotSetAuthentication_WhenTokenIsInvalid() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + validToken;
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn(testUsername);
        when(jwtUtil.validateToken(validToken, testUsername)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Hauria de gestionar header Authorization malformat")
    void doFilterInternal_ShouldIgnoreMalformedHeader() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtUtil, never()).getUsernameFromToken(any());
    }

    @Test
    @DisplayName("Hauria de gestionar excepció en l'extracció del username")
    void doFilterInternal_ShouldHandleException_WhenExtractingUsername() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + validToken;
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.getUsernameFromToken(validToken)).thenThrow(new RuntimeException("Token malformat"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("No hauria de sobreescriure autenticació existent")
    void doFilterInternal_ShouldNotOverrideExistingAuthentication() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + validToken;
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn(testUsername);

        // Simular que ja hi ha una autenticació
        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "existing@abasta.com", null, java.util.Collections.emptyList()));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(any(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("existing@abasta.com");
    }
}