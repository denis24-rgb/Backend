package com.reporte_ciudadano.backend.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tipos_reporte")
@Data
public class TipoReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_tipo")
    private String nombreTipo;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @JsonBackReference
    private CategoriaReporte categoria;

}