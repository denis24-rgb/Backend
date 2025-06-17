package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Institucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitucionRepositorio extends JpaRepository<Institucion, Long> {
}
