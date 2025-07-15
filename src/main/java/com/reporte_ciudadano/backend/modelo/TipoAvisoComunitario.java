package com.reporte_ciudadano.backend.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipos_aviso_comunitario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoAvisoComunitario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String icono;
}
