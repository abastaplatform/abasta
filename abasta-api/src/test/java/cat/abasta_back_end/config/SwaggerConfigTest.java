package cat.abasta_back_end.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
    }

    @Test
    void testAbastaAPI_ShouldReturnConfiguredOpenAPI() {
        // Act
        OpenAPI openAPI = swaggerConfig.abastaAPI();

        // Assert
        assertNotNull(openAPI, "L'objecte OpenAPI no hauria de ser null");
        assertNotNull(openAPI.getInfo(), "La secció Info no hauria de ser null");
        assertEquals("Abasta Back-End API", openAPI.getInfo().getTitle());
        assertEquals("1.0", openAPI.getInfo().getVersion());

        assertNotNull(openAPI.getServers(), "La llista de servidors no hauria de ser null");
        assertFalse(openAPI.getServers().isEmpty(), "Ha d'existir almenys un servidor configurat");

        boolean hasSecurityScheme = openAPI.getComponents()
                .getSecuritySchemes()
                .containsKey("Bearer Authentication");

        assertTrue(hasSecurityScheme, "Ha d'existir un esquema de seguretat Bearer Authentication");

        SecurityScheme scheme = openAPI.getComponents()
                .getSecuritySchemes()
                .get("Bearer Authentication");

        assertEquals("bearer", scheme.getScheme());
        assertEquals("JWT", scheme.getBearerFormat());
        assertEquals(SecurityScheme.Type.HTTP, scheme.getType());
    }
}
