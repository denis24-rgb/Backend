package com.reporte_ciudadano.backend.servicio;


import com.reporte_ciudadano.backend.modelo.CategoriaReporte;
import com.reporte_ciudadano.backend.repositorio.CategoriaReporteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaReporteServicio {

    @Autowired
    private CategoriaReporteRepositorio repo;

    public List<CategoriaReporte> listarTodas() {
        return repo.findAll();
    }

    public Optional<CategoriaReporte> obtenerPorId(Long id) {
        return repo.findById(id);
    }

    public CategoriaReporte guardar(CategoriaReporte categoria) {
        return repo.save(categoria);
    }
}
