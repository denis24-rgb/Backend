package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.AsignacionTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AsignacionTecnicoRepositorio extends JpaRepository<AsignacionTecnico, Long> {
    List<AsignacionTecnico> findByTecnicoId(Long tecnicoId);
    Optional<AsignacionTecnico> findByReporteIdAndTecnicoId(Long reporteId, Long tecnicoId);
    List<AsignacionTecnico> findByTecnicoIdAndReporte_Estado(Long tecnicoId, String estado);
    Optional<AsignacionTecnico> findByReporteId(Long reporteId);
    long countByTecnicoIdAndReporte_EstadoIgnoreCase(Long tecnicoId, String estado);
    long countByTecnicoIdAndReporte_EstadoIgnoreCaseAndFechaFinalizacionIsNotNull(Long tecnicoId, String estado);
    long countByTecnicoIdAndReporte_EstadoIgnoreCaseAndFechaFinalizacionIsNull(Long tecnicoId, String estado);
    @Query("SELECT a FROM AsignacionTecnico a JOIN FETCH a.reporte r LEFT JOIN FETCH r.evidencias WHERE a.tecnico.id = :tecnicoId")
    List<AsignacionTecnico> listarPorTecnicoConEvidencias(@Param("tecnicoId") Long tecnicoId);

}
