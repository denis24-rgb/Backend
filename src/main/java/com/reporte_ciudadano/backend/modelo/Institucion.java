package com.reporte_ciudadano.backend.modelo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "institucion")
@Data
public class Institucion {


//    private String usernombre;
//    private String contrasena;
//    private String telefono;
      //private String direccion;
//    private String correo;

//    @Column(name = "nombre_institucion")
//    private String nombreInstitucion;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)//denis cambia lo necesaio para usar esto en vez de nombreInstitucion
    private String nombre;

    @Column(name = "correo_institucional", nullable = false, unique = true)
    private String correoInstitucional;

    @Column(nullable = false)
    private String zona;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "institucion")
    @JsonManagedReference
    private List<UsuarioInstitucional> usuarios;

    @OneToMany(mappedBy = "institucion")
    @JsonManagedReference(value = "institucion-reportes")
    private List<Reporte> reportes;
}
