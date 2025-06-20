package com.reporte_ciudadano.backend.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario_institucional")
@Data
public class UsuarioInstitucional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre; // nombre completo del usuario

    @Column(nullable = false, unique = true)
    private String usuario; // nombre de usuario visible/sistema

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private boolean activo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolInstitucional rol;

    @ManyToOne
    @JoinColumn(name = "institucion_id", nullable = false)
    @JsonBackReference
    private Institucion institucion;

    // ✅ Constructor vacío obligatorio para JPA
    public UsuarioInstitucional() {
    }

    // ✅ Constructor con ID útil para asignaciones
    public UsuarioInstitucional(Long id) {
        this.id = id;
    }
}
