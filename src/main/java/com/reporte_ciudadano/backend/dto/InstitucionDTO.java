package com.reporte_ciudadano.backend.dto;

import com.reporte_ciudadano.backend.modelo.Institucion;
import lombok.Data;

@Data
public class InstitucionDTO {
    private Long id;
    private String nombre;
    private String zona;
    private String correoInstitucional;
    private boolean activo;

    public InstitucionDTO(Institucion institucion) {
        this.id = institucion.getId();
        this.nombre = institucion.getNombre();
        this.zona = institucion.getZona();
        this.correoInstitucional = institucion.getCorreoInstitucional();
        this.activo = institucion.isActivo();
    }
}
