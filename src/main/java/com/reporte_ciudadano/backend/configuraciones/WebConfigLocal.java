package com.reporte_ciudadano.backend.configuraciones;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("local")
public class WebConfigLocal implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations(
                        "file:///C:/imagenes-trabajos/",
                        "file:///C:/evidencias/",
                        "file:///C:/iconos-tipo-reporte/",
                        "file:///C:/avisos-comunitarios/"
                );
    }
}
