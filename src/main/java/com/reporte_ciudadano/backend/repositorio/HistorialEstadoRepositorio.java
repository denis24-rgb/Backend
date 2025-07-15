package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistorialEstadoRepositorio extends JpaRepository<HistorialEstado, Long> {
    List<HistorialEstado> findByReporteIdOrderByFechaCambioAsc(Long reporteId);
    @Modifying
    @Query("DELETE FROM HistorialEstado he WHERE he.reporte.id = :reporteId")
    void eliminarPorReporteId(@Param("reporteId") Long reporteId);

}