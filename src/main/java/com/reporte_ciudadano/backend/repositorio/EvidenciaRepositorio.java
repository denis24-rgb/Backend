package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenciaRepositorio extends JpaRepository<Evidencia, Long> {
    Evidencia findTopByReporteIdAndTipoEvidencia(Long reporteId, String tipoEvidencia);
    List<Evidencia> findByReporteId(Long reporteId);

}
