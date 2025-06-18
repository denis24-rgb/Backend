package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.TipoReporte;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoReporteRepositorio extends JpaRepository<TipoReporte, Long> {

    Optional<TipoReporte> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
