package com.reporte_ciudadano.backend.dto;

import lombok.Data;

@Data
public class ActualizacionTecnico {
    private Long reporteId;
    private String nuevoEstado; // Por ejemplo: "EN_PROCESO" o "FINALIZADO"
    private String comentario;  // Comentario del técnico
}
