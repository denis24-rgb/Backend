package com.reporte_ciudadano.backend.dto;

import com.reporte_ciudadano.backend.modelo.Reporte;
import lombok.Data;

@Data
public class ReporteDetalleDTO {
    private Long id;
    private String descripcion;
    private String estado;
    private String fechaReporte;
    private String hora;
    private String ubicacion;
    private String imagenNombre;
    private String tipoReporte;
    private String usuario;

    public ReporteDetalleDTO(Reporte r) {
        this.id = r.getId();
        this.descripcion = r.getDescripcion();
        this.estado = r.getEstado();
        this.fechaReporte = r.getFechaReporte().toString();
        this.hora = r.getHora().toString();
        this.ubicacion = r.getUbicacion();
        this.tipoReporte = r.getTipoReporte().getNombre();
        this.imagenNombre = r.getTipoReporte().getIcono();
        this.usuario = r.getUsuario() != null ? r.getUsuario().getNombre() : "Anónimo";
    }
}