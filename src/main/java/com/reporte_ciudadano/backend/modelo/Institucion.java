package com.reporte_ciudadano.backend.modelo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "institucion")
@Data
public class Institucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(name = "correo_institucional", nullable = false, unique = true)
    private String correoInstitucional;

    @Column(nullable = false)
    private String zona;

    @Column(nullable = false)
    private boolean activo;

    // NUEVOS CAMPOS PERSONALIZADOS
    @Column(name = "color_primario")
    private String colorPrimario; // Ejemplo: "#28a745"

    @Column(name = "logo")
    private String logo; // Ejemplo: "logo_cre.png"

    // Relaciones

    @OneToMany(mappedBy = "institucion")
    @JsonManagedReference
    private List<UsuarioInstitucional> usuarios;

    @OneToMany(mappedBy = "institucion")
    @JsonManagedReference(value = "institucion-reportes")
    private List<Reporte> reportes;
}
