package com.reporte_ciudadano.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "contactos_emergencia")
@Data
public class ContactoEmergencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_contacto")
    private String nombreContacto;

    @Column(name = "tipo_contacto")
    private String tipoContacto;

    private String telefono;

    private String direccion;
    @Column(name = "latitud")
    private Double lat;

    @Column(name = "longitud")
    private Double lng;

}
