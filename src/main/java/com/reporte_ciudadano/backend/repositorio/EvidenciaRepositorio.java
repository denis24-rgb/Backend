package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvidenciaRepositorio extends JpaRepository<Evidencia, Long> {
    Evidencia findTopByReporteIdAndTipoEvidencia(Long reporteId, String tipoEvidencia);
    List<Evidencia> findByReporteId(Long reporteId);
    @Modifying
    @Query("DELETE FROM Evidencia e WHERE e.reporte.id = :reporteId")
    void eliminarPorReporteId(@Param("reporteId") Long reporteId);
}
