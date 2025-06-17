package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialEstadoRepositorio extends JpaRepository<HistorialEstado, Long> {
    List<HistorialEstado> findByReporteIdOrderByFechaCambioAsc(Long reporteId);
}