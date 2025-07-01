package com.reporte_ciudadano.backend.configuraciones;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Para imágenes de la carpeta static/imagenes
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations(
                        "classpath:/static/imagenes/",
                        "file:/C:/imagenes-trabajos/",
                        "file:/C:/evidencias/"
                );
    }
}
