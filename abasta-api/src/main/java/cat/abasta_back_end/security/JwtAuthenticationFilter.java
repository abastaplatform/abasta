package cat.abasta_back_end.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Filtre d'autenticació JWT que intercepta cada petició HTTP per validar tokens JWT.
 * Estén OncePerRequestFilter per garantir que s'executa una sola vegada per petició.
 *
 * <p>Aquest filtre extreu el token JWT de la capçalera Authorization, el valida i estableix
 * el context de seguretat de Spring Security si el token és vàlid.</p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Mètode principal del filtre que processa cada petició HTTP.
     * Extreu el token JWT de la capçalera Authorization, el valida i estableix
     * l'autenticació en el context de seguretat de Spring si el token és vàlid.
     *
     * @param request la petició HTTP rebuda
     * @param response la resposta HTTP a enviar
     * @param filterChain la cadena de filtres a continuar
     * @throws ServletException si es produeix un error en el processament del servlet
     * @throws IOException si es produeix un error d'entrada/sortida
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Extreu el token del header Authorization
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                System.out.println("Error al extraure username del token: " + e.getMessage());
            }
        }

        // Valida el token y estableix el context de seguretat
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(token, username)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}