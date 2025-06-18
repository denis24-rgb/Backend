package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Institucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstitucionRepositorio extends JpaRepository<Institucion, Long> {
    boolean existsByNombre(String nombre);
    boolean existsByCorreoInstitucional(String correoInstitucional);
    List<Institucion> findAllByOrderByNombreAsc();
}
