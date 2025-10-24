package cat.abasta_back_end.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuració de seguretat de l'aplicació Spring Security.
 * Defineix les polítiques de seguretat, CORS, autenticació JWT i autorització d'endpoints.
 *
 * <p>Configura una política de sessions stateless adequada per a APIs REST amb JWT,
 * permet endpoints públics per a operacions d'autenticació i registre, i protegeix
 * la resta d'endpoints requerint autenticació.</p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Proporciona un bean de PasswordEncoder per encriptar contrasenyes.
     * Utilitza BCrypt com a algorisme d'encriptació, que és segur i recomanat
     * per a l'emmagatzematge de contrasenyes.
     *
     * @return una instància de BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura CORS (Cross-Origin Resource Sharing) per permetre peticions des de dominis específics.
     * Permet peticions des de localhost:5173 i localhost:3000 (entorns de desenvolupament frontend).
     *
     * @return la configuració de CORS per a l'aplicació
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173*", "http://localhost:3000*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Configura la cadena de filtres de seguretat de Spring Security.
     * Defineix quins endpoints són públics i quins requereixen autenticació,
     * estableix la política de sessions stateless i afegeix el filtre JWT.
     *
     * @param http l'objecte HttpSecurity per configurar la seguretat
     * @return la cadena de filtres de seguretat configurada
     * @throws Exception si es produeix un error durant la configuració
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Swagger endpoints - públics
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        // Auth endpoints - públics
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/verify-email").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/resend-verification").permitAll()

                        // Companies - públics per al registre
                        .requestMatchers(HttpMethod.POST, "/api/companies/register").permitAll()

                        // Tots els altres endpoints requereixen autenticació
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}