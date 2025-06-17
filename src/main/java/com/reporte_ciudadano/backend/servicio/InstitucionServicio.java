package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.repositorio.InstitucionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstitucionServicio {

    @Autowired
    private InstitucionRepositorio repositorio;

    public List<Institucion> listar() {
        return repositorio.findAll();
    }

    public Optional<Institucion> obtenerPorId(Long id) {
        return repositorio.findById(id);
    }

    public Institucion guardar(Institucion institucion) {
        return repositorio.save(institucion);
    }

    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }

}
