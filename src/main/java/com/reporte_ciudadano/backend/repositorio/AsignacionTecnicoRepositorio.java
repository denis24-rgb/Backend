package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.AsignacionTecnico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignacionTecnicoRepositorio extends JpaRepository<AsignacionTecnico, Long> {
    List<AsignacionTecnico> findByTecnicoId(Long tecnicoId);
}
