package cat.abasta_back_end.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utilitat per gestionar tokens JWT (JSON Web Tokens).
 * Proporciona funcionalitats per generar, validar i extreure informació dels tokens JWT.
 *
 * <p>Utilitza l'algorisme HS512 per signar els tokens amb una clau secreta generada
 * automàticament. Els tokens tenen una validesa d'1 hora per defecte.</p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 */
@Component
public class JwtUtil {

    /**
     * Clau secreta per signar els tokens JWT.
     * Generada automàticament amb l'algorisme HS512.
     */
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * Temps d'expiració del token en mil·lisegons (1 hora = 3600000 ms).
     */
    private final long jwtExpiration = 3600000;

    /**
     * Genera un nou token JWT per a l'usuari especificat.
     * El token conté el nom d'usuari com a subject i té una validesa d'1 hora.
     *
     * @param username el nom d'usuari per al qual es genera el token
     * @return el token JWT generat com a String
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

    /**
     * Extreu el nom d'usuari (subject) d'un token JWT.
     *
     * @param token el token JWT del qual extreure el nom d'usuari
     * @return el nom d'usuari contingut en el token
     * @throws JwtException si el token no és vàlid o no es pot parsejar
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Verifica si un token JWT ha expirat.
     *
     * @param token el token JWT a verificar
     * @return true si el token ha expirat, false en cas contrari
     * @throws JwtException si el token no és vàlid o no es pot parsejar
     */
    public boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    /**
     * Obté la data d'expiració d'un token JWT.
     *
     * @param token el token JWT del qual obtenir la data d'expiració
     * @return la data d'expiració del token
     * @throws JwtException si el token no és vàlid o no es pot parsejar
     */
    public Date getExpirationDateFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    /**
     * Valida un token JWT verificant que el nom d'usuari coincideix i que no ha expirat.
     *
     * @param token el token JWT a validar
     * @param username el nom d'usuari esperat en el token
     * @return true si el token és vàlid i correspon a l'usuari, false en cas contrari
     */
    public boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}