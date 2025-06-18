package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.ImagenReporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagenReporteRepositorio extends JpaRepository<ImagenReporte, Long> {
    List<ImagenReporte> findByReporteId(Long reporteId);
}
