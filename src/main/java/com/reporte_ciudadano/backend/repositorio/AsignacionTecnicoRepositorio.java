package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.AsignacionTecnico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsignacionTecnicoRepositorio extends JpaRepository<AsignacionTecnico, Long> {
    List<AsignacionTecnico> findByTecnicoId(Long tecnicoId);
    Optional<AsignacionTecnico> findByReporteIdAndTecnicoId(Long reporteId, Long tecnicoId);
    long countByTecnicoIdAndReporte_Estado(Long tecnicoId, String estado);

    List<AsignacionTecnico> findByTecnicoIdAndReporte_Estado(Long tecnicoId, String estado);
    Optional<AsignacionTecnico> findByReporteId(Long reporteId);
}
