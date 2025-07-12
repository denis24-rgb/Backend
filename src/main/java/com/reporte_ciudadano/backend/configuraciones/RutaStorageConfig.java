package com.reporte_ciudadano.backend.configuraciones;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RutaStorageConfig {

    @Value("${ruta.evidencias}")
    private String rutaEvidencias;

    @Value("${ruta.iconos}")
    private String rutaIconos;

    @Value("${ruta.trabajos}")
    private String rutaTrabajos;

    @Value("${ruta.avisos}")
    private String rutaAvisos;
}
