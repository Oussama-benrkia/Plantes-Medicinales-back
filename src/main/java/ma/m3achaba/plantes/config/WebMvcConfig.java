package ma.m3achaba.plantes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Flutter web typically runs on localhost:3000 or other ports
                // Flutter mobile uses different endpoints
                .allowedOrigins(
                        "http://localhost:4200",   // Angular local development
                        "http://localhost:3000"  // Flutter web local

                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}