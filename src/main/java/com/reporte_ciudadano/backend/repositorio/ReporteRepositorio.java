package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Reporte;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReporteRepositorio extends JpaRepository<Reporte, Long> {
    List<Reporte> findByUsuarioId(Long usuarioId);

}