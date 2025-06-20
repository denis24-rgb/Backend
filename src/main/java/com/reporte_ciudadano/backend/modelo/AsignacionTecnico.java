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

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;

    @ManyToOne
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    @ManyToOne
    @JoinColumn(name = "tecnico_id", nullable = false)
    private UsuarioInstitucional tecnico;

    @ManyToOne
    @JoinColumn(name = "asignador_id")
    private UsuarioInstitucional asignador;

    @Column(name = "comentario_tecnico", columnDefinition = "text")
    private String comentarioTecnico;

    @Column(name = "imagen_trabajo", columnDefinition = "text")
    private String imagenTrabajo;

    @Column(name = "fecha_finalizacion")
    private LocalDateTime fechaFinalizacion;

    @PrePersist
    public void prePersist() {
        this.fechaAsignacion = LocalDateTime.now();
    }
}
