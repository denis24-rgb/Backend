package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Reporte;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReporteRepositorio extends JpaRepository<Reporte, Long> {
    List<Reporte> findByUsuarioId(Long usuarioId);

    // Buscar reportes por múltiples estados
    @Query("SELECT r FROM Reporte r WHERE r.estado IN :estados")
    List<Reporte> findByEstadoIn(@Param("estados") List<String> estados);

    // Buscar por institución y estados
    @Query("SELECT r FROM Reporte r WHERE r.institucion.id = :institucionId AND r.estado IN :estados")
    List<Reporte> findByInstitucionIdAndEstadoIn(@Param("institucionId") Long institucionId,
            @Param("estados") List<String> estados);

    // Reportes asignados a un técnico
    List<Reporte> findByAsignacionTecnicoId(Long tecnicoId);

    // Reportes por técnico y estado
    List<Reporte> findByAsignacionTecnicoIdAndEstado(Long tecnicoId, String estado);

    // Contar por técnico y estado
    long countByAsignacionTecnicoIdAndEstado(Long tecnicoId, String estado);

    List<Reporte> findByLatitudIsNotNullAndLongitudIsNotNull();

    long countByEstadoIgnoreCase(String estado);

    List<Reporte> findByInstitucionIdAndUbicacionIsNotNull(Long institucionId);

    int countByInstitucionIdAndEstado(Long institucionId, String estado);

    int countByInstitucionIdAndEstadoNot(Long institucionId, String estado);

}