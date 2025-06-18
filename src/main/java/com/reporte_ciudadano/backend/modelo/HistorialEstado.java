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

    // 🔗 Relación con el reporte al que pertenece el historial
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    // 📄 Estado anterior antes del cambio
    @Column(name = "estado_anterior", nullable = false, length = 50)
    private String estadoAnterior;

    // 📄 Estado nuevo después del cambio
    @Column(name = "estado_nuevo", nullable = false, length = 50)
    private String estadoNuevo;

    // 🕒 Fecha automática de cambio
    @CreationTimestamp
    @Column(name = "fecha_cambio", nullable = false, updatable = false)
    private LocalDateTime fechaCambio;
}
