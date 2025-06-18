package com.reporte_ciudadano.backend.dto;


import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import lombok.Data;

@Data
public class FormularioInstitucionAdmin {
    private Institucion institucion;
    private UsuarioInstitucional admin;
}
