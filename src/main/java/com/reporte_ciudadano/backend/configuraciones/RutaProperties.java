package com.reporte_ciudadano.backend.configuraciones;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ruta")
public class RutaProperties {
    private String evidencias;
    private String iconos;
    private String trabajos;
    private String avisos;
    private String iconosInstituciones;
}
