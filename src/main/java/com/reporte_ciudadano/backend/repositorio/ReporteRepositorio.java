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
    long countByTipoReporteId(Long tipoReporteId);

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
    @Query("SELECT COUNT(r) FROM Reporte r GROUP BY FUNCTION('TO_CHAR', r.fechaReporte, 'Mon') ORDER BY MIN(r.fechaReporte)")
    List<Long> contarPorCadaMes();

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.institucion.id = :institucionId GROUP BY FUNCTION('TO_CHAR', r.fechaReporte, 'Mon') ORDER BY MIN(r.fechaReporte)")
    List<Long> contarPorCadaMesPorInstitucion(@Param("institucionId") Long institucionId);


    // Conteo por estado e institución
    @Query("SELECT COUNT(r) FROM Reporte r WHERE LOWER(r.estado) = LOWER(:estado) AND r.institucion.id = :institucionId")
    long contarPorEstadoEInstitucion(@Param("estado") String estado, @Param("institucionId") Long institucionId);

    // Nombres de tipos de reporte por institución
    @Query("SELECT DISTINCT r.tipoReporte.nombre FROM Reporte r WHERE r.institucion.id = :institucionId")
    List<String> obtenerNombresTiposPorInstitucion(@Param("institucionId") Long institucionId);

    // Conteo por tipo de reporte e institución
    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.tipoReporte.nombre = :nombreTipo AND r.institucion.id = :institucionId")
    long contarPorTipoEInstitucion(@Param("nombreTipo") String nombreTipo, @Param("institucionId") Long institucionId);

    // Conteo por tipo (usado en bucle)
    @Query("SELECT r.tipoReporte.nombre, COUNT(r) FROM Reporte r WHERE r.institucion.id = :institucionId GROUP BY r.tipoReporte.nombre")
    List<Object[]> contarPorTipoAgrupado(@Param("institucionId") Long institucionId);

    // Meses (formato abreviado) con reportes por institución
    @Query("SELECT TO_CHAR(r.fechaReporte, 'Mon') FROM Reporte r WHERE r.institucion.id = :institucionId GROUP BY TO_CHAR(r.fechaReporte, 'Mon') ORDER BY MIN(r.fechaReporte)")
    List<String> obtenerMesesConFormatoPorInstitucion(@Param("institucionId") Long institucionId);

    // Conteo por mes
    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.institucion.id = :institucionId GROUP BY TO_CHAR(r.fechaReporte, 'Mon') ORDER BY MIN(r.fechaReporte)")
    List<Long> contarPorMesEInstitucion(@Param("institucionId") Long institucionId);

    // Últimos días recientes
    @Query("SELECT TO_CHAR(r.fechaReporte, 'DD-MM') FROM Reporte r WHERE r.institucion.id = :institucionId ORDER BY r.fechaReporte DESC LIMIT 7")
    List<String> obtenerUltimosDias(@Param("institucionId") Long institucionId);

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.institucion.id = :institucionId GROUP BY TO_CHAR(r.fechaReporte, 'DD-MM') ORDER BY MIN(r.fechaReporte) DESC")
    List<Long> contarUltimosDias(@Param("institucionId") Long institucionId);

    // Rango de fechas
    @Query("SELECT TO_CHAR(r.fechaReporte, 'DD-MM') FROM Reporte r WHERE r.fechaReporte BETWEEN :inicio AND :fin AND r.institucion.id = :institucionId GROUP BY r.fechaReporte ORDER BY r.fechaReporte")
    List<String> obtenerDiasEnRango(@Param("inicio") LocalDate inicio,
                                    @Param("fin") LocalDate fin,
                                    @Param("institucionId") Long institucionId);

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.fechaReporte BETWEEN :inicio AND :fin AND r.institucion.id = :institucionId GROUP BY r.fechaReporte ORDER BY r.fechaReporte")
    List<Long> contarPorDiaEnRango(@Param("inicio") LocalDate inicio,
                                   @Param("fin") LocalDate fin,
                                   @Param("institucionId") Long institucionId);

}
