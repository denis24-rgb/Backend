//package com.reporte_ciudadano.backend.modelo;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//@Entity
//@Table(name = "imagen_reporte")
//@Data
//public class ImagenReporte {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String url;
//
//    @ManyToOne
//    @JoinColumn(name = "reporte_id", nullable = false)
//    private Reporte reporte;
//}
