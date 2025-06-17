package com.reporte_ciudadano.backend.repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reporte_ciudadano.backend.modelo.AvisoComunitario;

public interface AvisoComunitarioRepositorio extends JpaRepository<AvisoComunitario, Long> {

    List<AvisoComunitario> findByFechaEliminacionAfterOrderByFechaCreacionDesc(LocalDateTime ahora);
}
