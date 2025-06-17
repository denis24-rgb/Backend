package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.modelo.InstitucionTipoReporte;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstitucionTipoReporteRepositorio extends JpaRepository<InstitucionTipoReporte, Long> {
    boolean existsByInstitucionIdAndTipoReporteId(Long institucionId, Long tipoReporteId);

    @Query("SELECT i FROM Institucion i JOIN InstitucionTipoReporte itr ON i.id = itr.institucion.id WHERE itr.tipoReporte.id = :tipoReporteId")
    List<Institucion> findInstitucionesPorTipoReporteId(@Param("tipoReporteId") Long tipoReporteId);

    @Query("SELECT itr.institucion FROM InstitucionTipoReporte itr WHERE itr.tipoReporte.id = :tipoId")
    List<Institucion> findInstitucionesPorTipoReporte(@Param("tipoId") Long tipoId);

}
