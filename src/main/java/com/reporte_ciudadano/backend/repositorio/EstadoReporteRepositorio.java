package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.EstadoReporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoReporteRepositorio extends JpaRepository<EstadoReporte, Long> {
    // NUEVO CÓDIGO LISETH - Buscar estado por nombre
    Optional<EstadoReporte> findByNombre(String nombre);


}

