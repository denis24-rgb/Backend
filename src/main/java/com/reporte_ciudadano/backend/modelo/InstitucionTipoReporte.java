package com.reporte_ciudadano.backend.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "institucion_tipo_reporte", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "institucion_id", "tipo_reporte_id","categoria_reporte_id" })
})
@Data
public class InstitucionTipoReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;

    @ManyToOne
    @JoinColumn(name = "tipo_reporte_id", nullable = false)
    private TipoReporte tipoReporte;

    @ManyToOne
    @JoinColumn(name = "categoria_reporte_id", nullable = false)
    private CategoriaReporte categoriaReporte;
}
