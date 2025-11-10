package cat.abasta_back_end.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Permet servir arxius des de carpeta  /img/productes/
        String uploadPath = Paths.get("img/productes/").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/img/productes/**")
                .addResourceLocations(uploadPath);
    }
}
