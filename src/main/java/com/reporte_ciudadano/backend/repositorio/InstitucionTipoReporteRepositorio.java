package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.modelo.InstitucionTipoReporte;
import com.reporte_ciudadano.backend.modelo.TipoReporte;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstitucionTipoReporteRepositorio extends JpaRepository<InstitucionTipoReporte, Long> {
    boolean existsByInstitucionIdAndTipoReporteId(Long institucionId, Long tipoReporteId);

    @Query("SELECT i FROM Institucion i JOIN InstitucionTipoReporte itr ON i.id = itr.institucion.id WHERE itr.tipoReporte.id = :tipoReporteId")
    List<Institucion> findInstitucionesPorTipoReporteId(@Param("tipoReporteId") Long tipoReporteId);

    @Query("SELECT itr.institucion FROM InstitucionTipoReporte itr WHERE itr.tipoReporte.id = :tipoId")
    List<Institucion> findInstitucionesPorTipoReporte(@Param("tipoId") Long tipoId);

    List<InstitucionTipoReporte> findByInstitucionId(Long institucionId);

    List<InstitucionTipoReporte> findByCategoriaReporteId(Long categoriaId);

    @Query("SELECT itr.tipoReporte FROM InstitucionTipoReporte itr WHERE itr.categoriaReporte.id = :categoriaId")
    List<TipoReporte> findTiposReportePorCategoria(@Param("categoriaId") Long categoriaId);
    Optional<InstitucionTipoReporte> findByTipoReporteId(Long tipoReporteId);

}
