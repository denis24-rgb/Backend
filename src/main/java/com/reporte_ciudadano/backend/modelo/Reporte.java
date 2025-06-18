package com.reporte_ciudadano.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "reporte")
@Data
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relaciones con otras entidades
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "institucion_id")
    private Institucion institucion;

    @ManyToOne
    @JoinColumn(name = "tipo_reporte_id")
    private TipoReporte tipoReporte;

    // Otros campos
    private String descripcion;
    private String ubicacion;

    @CreationTimestamp
    @Column(name = "fecha_reporte", updatable = false)
    private LocalDate fechaReporte;

    @CreationTimestamp
    @Column(name = "hora", updatable = false)
    private LocalTime hora;

    private String estado = "recibido";

    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialEstado> historialEstados;

}
