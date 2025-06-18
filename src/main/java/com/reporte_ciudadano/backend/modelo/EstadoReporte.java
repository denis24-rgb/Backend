//package com.reporte_ciudadano.backend.modelo;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import jakarta.persistence.*;
//import lombok.Data;
//import java.util.List;
//
//@Entity
//@Table(name = "estado_reporte")
//@Data
//public class EstadoReporte {
//
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String nombre;
//
//    @OneToMany(mappedBy = "estado")
//    @JsonIgnoreProperties("estado") //  evita la recursión
//    private List<Reporte> reportes;
//}
