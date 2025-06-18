package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.TipoReporte;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoReporteRepositorio extends JpaRepository<TipoReporte, Long> {
    List<TipoReporte> findByCategoriaId(Long categoriaId);
    Optional<TipoReporte> findByNombre(String nombre);
    boolean existsByNombre(String nombre);

}
