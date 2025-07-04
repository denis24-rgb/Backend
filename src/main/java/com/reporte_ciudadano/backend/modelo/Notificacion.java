package com.reporte_ciudadano.backend.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notificaciones")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mensaje;

    private LocalDateTime fecha;

    @Column(nullable = false)
    private boolean leido = false;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // ✅ SIN @JsonManagedReference

    @ManyToOne
    @JoinColumn(name = "reporte_id")
    @JsonIgnore // ✅ Esto evita la recursividad infinita
    private Reporte reporte;

    public Notificacion(String mensaje, Usuario usuario, LocalDateTime fecha) {
        this.mensaje = mensaje;
        this.usuario = usuario;
        this.fecha = fecha;
        this.leido = false;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }
}
