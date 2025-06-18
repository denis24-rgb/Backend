package com.reporte_ciudadano.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "asignacion_tecnico")
@Data
public class AsignacionTecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    @ManyToOne
    @JoinColumn(name = "tecnico_id", nullable = false)
    private UsuarioInstitucional tecnico;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;
}
