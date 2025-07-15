package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.TipoReporte;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TipoReporteRepositorio extends JpaRepository<TipoReporte, Long> {

    Optional<TipoReporte> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    @Query("SELECT COUNT(r) > 0 FROM Reporte r WHERE r.tipoReporte.id = :tipoId")
    boolean estaUsadoEnReportes(@Param("tipoId") Long tipoId);

}
