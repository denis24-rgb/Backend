package com.reporte_ciudadano.backend.dto;

import com.reporte_ciudadano.backend.modelo.Notificacion;
import lombok.Data;

@Data
public class NotificacionDTO {
    private Long id;
    private String mensaje;
    private String fecha;
    private boolean leido;
    private String tipoReporte;
    private String descripcion;

    public NotificacionDTO(Notificacion notificacion) {
        this.id = notificacion.getId();
        this.mensaje = notificacion.getMensaje();
        this.fecha = notificacion.getFecha().toString();
        this.leido = notificacion.isLeido();

        if (notificacion.getReporte() != null) {
            this.tipoReporte = notificacion.getReporte().getTipoReporte() != null
                    ? notificacion.getReporte().getTipoReporte().getNombre()
                    : "Tipo no disponible";
            this.descripcion = notificacion.getReporte().getDescripcion();
        } else {
            // Validamos si es un aviso comunitario
            if (notificacion.getMensaje() != null &&
                    notificacion.getMensaje().toLowerCase().contains("aviso comunitario")) {
                this.tipoReporte = null;
                this.descripcion = null;
            } else {
                this.tipoReporte = "Sin reporte";
                this.descripcion = "Sin detalles";
            }
        }
    }
}
