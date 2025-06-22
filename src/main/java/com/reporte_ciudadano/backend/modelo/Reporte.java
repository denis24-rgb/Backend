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

    @Column(nullable = false)
    private String estado; // "Recibido", "En Proceso", etc.

    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialEstado> historialEstados;

    // PARA PODER MOSTRAR LA IMAGEN DE EVIDENCIA EN EL DTO REPORTEDETALLE
    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evidencia> evidencias;

    // ✅ Constructor vacío necesario para JPA
    public Reporte() {
    }

    // ✅ Constructor con ID útil para formularios o referencias
    public Reporte(Long id) {
        this.id = id;
    }
}
