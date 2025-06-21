package com.reporte_ciudadano.backend.dto;

import com.reporte_ciudadano.backend.modelo.HistorialEstado;
import lombok.Data;

@Data
public class HistorialDTO {
    private String estadoAnterior;
    private String estadoNuevo;
    private String fechaCambio;
    private String hora;

    public HistorialDTO(HistorialEstado h) {
        this.estadoAnterior = h.getEstadoAnterior();
        this.estadoNuevo = h.getEstadoNuevo();
        this.fechaCambio = h.getFechaCambio().toLocalDate().toString();
        this.hora = h.getFechaCambio().toLocalTime().toString();
    }
}
