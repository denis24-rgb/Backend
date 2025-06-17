package com.reporte_ciudadano.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "instituciones")
@Data
public class Institucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usernombre;
    private String contrasena;
    private String telefono;
    private String direccion;
    private String correo;

    @Column(name = "nombre_institucion")
    private String nombreInstitucion;
}
