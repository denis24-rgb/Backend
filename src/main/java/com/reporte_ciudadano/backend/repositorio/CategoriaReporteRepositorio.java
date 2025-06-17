package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.CategoriaReporte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaReporteRepositorio extends JpaRepository<CategoriaReporte, Long> {
}
