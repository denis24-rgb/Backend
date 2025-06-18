package com.reporte_ciudadano.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "categoria_reporte")
@Data
public class CategoriaReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @OneToMany(mappedBy = "categoriaReporte")
    private List<InstitucionTipoReporte> tiposAsignados;
}
