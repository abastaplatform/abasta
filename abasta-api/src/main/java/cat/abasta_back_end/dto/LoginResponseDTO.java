package cat.abasta_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) per a la resposta d'autenticació després d'un login exitós.
 * Conté el token JWT d'autenticació, el tipus de token i la informació bàsica de l'usuari.
 *
 * <p>Aquest DTO s'envia com a resposta quan un usuari inicia sessió correctament
 * a l'aplicació. El token JWT s'utilitzarà en les peticions posteriors per autenticar
 * l'usuari.</p>
 *
 * <p>Exemple d'ús típic:
 * <pre>
 * LoginResponseDTO response = LoginResponseDTO.builder()
 *     .token(jwtToken)
 *     .user(userResponseDTO)
 *     .build();
 * </pre>
 * </p>
 *
 * <p>Les anotacions de Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor, @Builder)
 * generen automàticament:
 * <ul>
 *   <li>Getters i setters per a tots els camps</li>
 *   <li>Mètodes equals(), hashCode() i toString()</li>
 *   <li>Constructor sense paràmetres</li>
 *   <li>Constructor amb tots els paràmetres</li>
 *   <li>Patró Builder per a la construcció fluent d'objectes</li>
 * </ul>
 * </p>
 *
 * <p>Estructura JSON de la resposta:
 * <pre>
 * {
 *   "token": "eyJhbGciOiJIUzUxMiJ9...",
 *   "type": "Bearer",
 *   "user": {
 *     "id": 1,
 *     "email": "user@example.com",
 *     "firstName": "Joan",
 *     "lastName": "Garcia",
 *     ...
 *   }
 * }
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 * @see UserResponseDTO
 * @see cat.abasta_back_end.security.JwtUtil
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    /**
     * Token JWT d'autenticació generat per a l'usuari.
     * Aquest token s'utilitza per autenticar les peticions posteriors a l'API.
     *
     * <p>El token conté informació codificada sobre l'usuari i té una data d'expiració.
     * El client ha d'incloure aquest token a la capçalera Authorization de les peticions HTTP
     * en el format: "Bearer {token}".</p>
     *
     * <p>Exemple de token JWT:
     * <pre>eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjQw...</pre>
     * </p>
     */
    private String token;

    /**
     * Tipus de token d'autenticació.
     * Per defecte és "Bearer", que indica que s'utilitza un Bearer Token segons l'estàndard OAuth 2.0.
     *
     * <p>L'anotació @Builder.Default assegura que aquest camp tingui el valor "Bearer"
     * quan es construeix l'objecte amb el patró Builder, encara que no s'especifiqui explícitament.</p>
     *
     * <p>Aquest camp indica al client com ha d'enviar el token en les peticions posteriors:
     * <pre>Authorization: Bearer {token}</pre>
     * </p>
     */
    @Builder.Default
    private String type = "Bearer";

    /**
     * Informació bàsica de l'usuari que ha iniciat sessió.
     * Conté dades com l'identificador, nom, cognoms, email, rol i estat de verificació.
     *
     * <p>Aquest objecte permet al client tenir accés immediat a la informació de l'usuari
     * sense haver de fer una petició addicional després del login.</p>
     *
     * <p>No inclou informació delicada com la contrasenya, només dades que són segures
     * per enviar al client.</p>
     *
     * @see UserResponseDTO
     */
    private UserResponseDTO user;
}