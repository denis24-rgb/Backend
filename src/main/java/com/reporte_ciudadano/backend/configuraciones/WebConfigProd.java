package com.reporte_ciudadano.backend.configuraciones;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("prod")  // Solo se activa en el servidor
public class WebConfigProd implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations(
                        "file:/opt/reporte_ciudadano/imagenes-trabajos/",
                        "file:/opt/reporte_ciudadano/evidencias/",
                        "file:/opt/reporte_ciudadano/iconos-tipo-reporte/",
                        "file:/opt/reporte_ciudadano/avisos-comunitarios/"
                );
    }
}
