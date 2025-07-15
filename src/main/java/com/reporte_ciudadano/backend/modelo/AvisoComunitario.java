package com.reporte_ciudadano.backend.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "aviso_comunitario")
@Getter
@Setter
public class AvisoComunitario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "tipo_aviso_id") // Este será el nombre de la columna en la tabla
    private TipoAvisoComunitario tipoAviso;

    private String descripcion;

    private String imagenUrl;

    private String ubicacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_eliminacion")
    private LocalDateTime fechaEliminacion;

}
