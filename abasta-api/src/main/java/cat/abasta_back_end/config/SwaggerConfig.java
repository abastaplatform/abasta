package cat.abasta_back_end.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuració de Swagger/OpenAPI per a la documentació automàtica de l'API REST.
 * Proporciona una interfície web interactiva per explorar i provar tots els endpoints
 * de l'aplicació amb suport per a autenticació JWT.
 *
 * <p>Swagger UI està disponible a: <code>http://localhost:8080/swagger-ui.html</code></p>
 * <p>Documentació OpenAPI JSON: <code>http://localhost:8080/v3/api-docs</code></p>
 *
 * <p>Aquesta configuració inclou:
 * <ul>
 *   <li>Informació general de l'API (títol, versió, descripció)</li>
 *   <li>Configuració de servidors (local, producció)</li>
 *   <li>Esquema de seguretat JWT amb suport per autorització</li>
 *   <li>Informació de contacte i llicència</li>
 *   <li>Documentació d'autenticació i flux típic d'ús</li>
 * </ul>
 * </p>
 *
 * <p><strong>Com utilitzar Swagger UI:</strong></p>
 * <ol>
 *   <li>Accedeix a http://localhost:8080/swagger-ui.html</li>
 *   <li>Fes login utilitzant l'endpoint POST /api/auth/login</li>
 *   <li>Copia el token JWT de la resposta</li>
 *   <li>Clica el botó "Authorize" (icona de cadenat) a la part superior</li>
 *   <li>Enganxa el token (sense el prefix 'Bearer ')</li>
 *   <li>Ara pots provar tots els endpoints protegits</li>
 * </ol>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 * @see OpenAPI
 * @see SecurityScheme
 */
@Configuration
public class SwaggerConfig {

    /**
     * URL del frontend de l'aplicació configurada a application.properties.
     * S'utilitza per configurar CORS i referències a la documentació.
     * Valor per defecte: http://localhost:5173 (entorn de desenvolupament amb Vite/React)
     */
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    /**
     * Configura l'objecte OpenAPI amb tota la informació necessària per generar
     * la documentació automàtica de l'API.
     *
     * <p>Aquest mètode configura:
     * <ul>
     *   <li><strong>Esquema de seguretat JWT:</strong> Defineix com s'ha d'enviar el token
     *       d'autenticació en les peticions (capçalera Authorization amb Bearer token)</li>
     *   <li><strong>Servidors:</strong> Llista dels servidors disponibles (local, producció)</li>
     *   <li><strong>Informació de l'API:</strong> Metadades com títol, versió, descripció,
     *       contacte i llicència</li>
     *   <li><strong>Requeriments de seguretat:</strong> Indica que els endpoints protegits
     *       requereixen autenticació JWT</li>
     * </ul>
     * </p>
     *
     * <p><strong>Esquema de seguretat JWT:</strong></p>
     * <ul>
     *   <li>Tipus: HTTP Bearer Authentication</li>
     *   <li>Format: JWT (JSON Web Token)</li>
     *   <li>Ubicació: Capçalera HTTP "Authorization"</li>
     *   <li>Format de la capçalera: <code>Authorization: Bearer {token}</code></li>
     * </ul>
     *
     * <p><strong>Descripció de l'API:</strong></p>
     * La descripció inclou:
     * <ul>
     *   <li>Característiques principals del sistema</li>
     *   <li>Instruccions pas a pas per autenticar-se</li>
     *   <li>Flux típic d'ús de l'API</li>
     * </ul>
     *
     * <p><strong>Nota:</strong> La configuració utilitza format Markdown per a la descripció,
     * que es renderitza correctament a la interfície de Swagger UI.</p>
     *
     * @return objecte OpenAPI completament configurat amb esquemes de seguretat,
     *         informació de l'API i servidors
     * @see OpenAPI
     * @see SecurityScheme
     * @see Info
     * @see Server
     */
    @Bean
    public OpenAPI abastaAPI() {
        // Configurar l'esquema de seguretat JWT
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("Introdueix el token JWT (sense 'Bearer ')");

        // Requeriment de seguretat
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");

        // Servidor local
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Servidor de desenvolupament local");

        // Informació de l'API
        Info info = new Info()
                .title("Abasta Back-End API")
                .version("1.0")
                .description("""
                    API REST per a la gestió d'empreses i usuaris de la plataforma Abasta.
                    
                    ## Característiques principals:
                    - 🏢 Gestió completa d'empreses
                    - 👥 Gestió d'usuaris amb rols (ADMIN, USER)
                    - 🔐 Autenticació JWT
                    - ✉️ Verificació d'email
                    - 🔑 Recuperació de contrasenya
                    
                    ## Autenticació:
                    1. Fes login a `/api/auth/login` amb email i contrasenya
                    2. Copia el token JWT de la resposta
                    3. Fes clic al botó "Authorize" (🔓) a dalt a la dreta
                    4. Enganxa el token (sense 'Bearer ') i fes clic a "Authorize"
                    5. Ara pots provar tots els endpoints protegits
                    
                    ## Flux típic:
                    1. Registrar empresa i admin: `POST /api/companies/register`
                    2. Verificar email: `POST /api/auth/verify-email`
                    3. Login: `POST /api/auth/login`
                    4. Utilitzar el token per accedir als endpoints protegits
                    """)
                .contact(new Contact()
                        .name("Equip Abasta")
                        .email("abasta.platform@gmail.com")
                        .url("https://deveps.ddns.net/abasta"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .addSecurityItem(securityRequirement)
                .schemaRequirement("Bearer Authentication", securityScheme);
    }
}