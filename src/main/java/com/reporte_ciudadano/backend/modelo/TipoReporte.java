package com.reporte_ciudadano.backend.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "tipos_reporte")
@Data
public class TipoReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private String icono;

    // Relación con los reportes individuales
    @OneToMany(mappedBy = "tipoReporte")
    @JsonIgnoreProperties("tipoReporte") // evita recursión infinita
    private List<Reporte> reportes;

    // Nueva relación con institución y categoría (tabla intermedia)
    @OneToMany(mappedBy = "tipoReporte")
    @JsonManagedReference
    private List<InstitucionTipoReporte> asignaciones;
}