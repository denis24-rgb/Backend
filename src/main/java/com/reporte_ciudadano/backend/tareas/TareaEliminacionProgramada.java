package com.reporte_ciudadano.backend.tareas;

import com.reporte_ciudadano.backend.servicio.AvisoComunitarioServicio;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TareaEliminacionProgramada {

    private final AvisoComunitarioServicio servicio;

    public TareaEliminacionProgramada(AvisoComunitarioServicio servicio) {
        this.servicio = servicio;
    }

    // ⏰ Ejecutar todos los días a las 2:00 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void ejecutarEliminacionAutomatica() {
        System.out.println("⏰ Ejecutando limpieza de avisos comunitarios vencidos...");
        servicio.eliminarVencidos();
    }
}
