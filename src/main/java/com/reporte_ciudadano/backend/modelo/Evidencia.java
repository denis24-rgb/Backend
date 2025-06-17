package com.reporte_ciudadano.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "evidencias")
@Data
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporte_id")
    private Reporte reporte;

    private String tipoEvidencia;

    private String url;
}
