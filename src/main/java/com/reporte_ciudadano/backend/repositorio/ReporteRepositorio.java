package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReporteRepositorio extends JpaRepository<Reporte, Long> {

    List<Reporte> findByUsuarioId(Long usuarioId);

    @Query("SELECT r FROM Reporte r WHERE r.estado IN :estados")
    List<Reporte> findByEstadoIn(@Param("estados") List<String> estados);

    @Query("SELECT r FROM Reporte r WHERE r.institucion.id = :institucionId AND r.estado IN :estados")
    List<Reporte> findByInstitucionIdAndEstadoIn(@Param("institucionId") Long institucionId, @Param("estados") List<String> estados);

    List<Reporte> findByUbicacionIsNotNull();

    long countByEstadoIgnoreCase(String estado);

    long countByEstadoIgnoreCaseAndInstitucionId(String estado, Long institucionId);

    List<Reporte> findByInstitucionIdAndUbicacionIsNotNull(Long institucionId);

    int countByInstitucionIdAndEstado(Long institucionId, String estado);

    int countByInstitucionIdAndEstadoNot(Long institucionId, String estado);

    List<Reporte> findByInstitucionId(Long institucionId);

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.institucion.id = :institucionId")
    int contarPorInstitucion(@Param("institucionId") Long institucionId);

    @Query("SELECT r.tipoReporte.nombre FROM Reporte r GROUP BY r.tipoReporte.nombre")
    List<String> obtenerNombresTiposConCantidad();

    @Query("SELECT COUNT(r) FROM Reporte r GROUP BY r.tipoReporte.nombre")
    List<Long> contarPorCadaTipo();

    @Query("SELECT r.tipoReporte.nombre FROM Reporte r WHERE r.institucion.id = :institucionId GROUP BY r.tipoReporte.nombre")
    List<String> obtenerNombresTiposConCantidadPorInstitucion(@Param("institucionId") Long institucionId);

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.institucion.id = :institucionId GROUP BY r.tipoReporte.nombre")
    List<Long> contarPorCadaTipoPorInstitucion(@Param("institucionId") Long institucionId);

    @Query("SELECT FUNCTION('TO_CHAR', r.fechaReporte, 'Mon') FROM Reporte r GROUP BY FUNCTION('TO_CHAR', r.fechaReporte, 'Mon') ORDER BY MIN(r.fechaReporte)")
    List<String> obtenerMesesConFormato();

    @Query("SELECT FUNCTION('TO_CHAR', r.fechaReporte, 'Mon') FROM Reporte r WHERE r.institucion.id = :institucionId GROUP BY FUNCTION('TO_CHAR', r.fechaReporte, 'Mon') ORDER BY MIN(r.fechaReporte)")
    List<String> obtenerMesesConFormatoPorInstitucion(@Param("institucionId") Long institucionId);

    @Query("SELECT COUNT(r) FROM Reporte r GROUP BY FUNCTION('TO_CHAR', r.fechaReporte, 'Mon') ORDER BY MIN(r.fechaReporte)")
    List<Long> contarPorCadaMes();

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.institucion.id = :institucionId GROUP BY FUNCTION('TO_CHAR', r.fechaReporte, 'Mon') ORDER BY MIN(r.fechaReporte)")
    List<Long> contarPorCadaMesPorInstitucion(@Param("institucionId") Long institucionId);

    @Query(value = """
    SELECT TO_CHAR(fecha_reporte, 'DD/MM') AS dia
    FROM reporte
    WHERE fecha_reporte BETWEEN :inicio AND :fin
    GROUP BY dia
    ORDER BY MIN(fecha_reporte)
    """, nativeQuery = true)
    List<String> obtenerDiasEnRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query(value = """
    SELECT TO_CHAR(fecha_reporte, 'DD/MM') AS dia
    FROM reporte
    WHERE fecha_reporte BETWEEN :inicio AND :fin AND institucion_id = :institucionId
    GROUP BY dia
    ORDER BY MIN(fecha_reporte)
    """, nativeQuery = true)
    List<String> obtenerDiasEnRangoPorInstitucion(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, @Param("institucionId") Long institucionId);

    @Query(value = """
    SELECT COUNT(*)
    FROM reporte
    WHERE fecha_reporte BETWEEN :inicio AND :fin
    GROUP BY TO_CHAR(fecha_reporte, 'DD/MM')
    ORDER BY MIN(fecha_reporte)
    """, nativeQuery = true)
    List<Long> contarReportesPorDiaEnRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query(value = """
    SELECT COUNT(*)
    FROM reporte
    WHERE fecha_reporte BETWEEN :inicio AND :fin AND institucion_id = :institucionId
    GROUP BY TO_CHAR(fecha_reporte, 'DD/MM')
    ORDER BY MIN(fecha_reporte)
    """, nativeQuery = true)
    List<Long> contarReportesPorDiaEnRangoPorInstitucion(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, @Param("institucionId") Long institucionId);
}
