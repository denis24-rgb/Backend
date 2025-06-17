package com.reporte_ciudadano.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estados")
@Data
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    @Column(name = "estado_anterior")
    private String estadoAnterior;

    @Column(name = "estado_nuevo")
    private String estadoNuevo;

    @CreationTimestamp
    @Column(name = "fecha_cambio", updatable = false)
    private LocalDateTime fechaCambio;
}
