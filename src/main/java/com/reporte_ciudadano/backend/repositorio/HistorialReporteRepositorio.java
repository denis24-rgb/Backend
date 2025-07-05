package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.HistorialReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistorialReporteRepositorio extends JpaRepository<HistorialReporte, Long> {
    List<HistorialReporte> findByReporteId(Long reporteId);
    @Modifying
    @Query("DELETE FROM HistorialReporte h WHERE h.reporte.id = :reporteId")
    void eliminarPorReporteId(@Param("reporteId") Long reporteId);

}
