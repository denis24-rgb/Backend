package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.TipoAvisoComunitario;
import com.reporte_ciudadano.backend.repositorio.TipoAvisoComunitarioRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoAvisoComunitarioServicio {

    private final TipoAvisoComunitarioRepositorio repositorio;

    public TipoAvisoComunitarioServicio(TipoAvisoComunitarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<TipoAvisoComunitario> listarTodos() {
        return repositorio.findAll();
    }

    public TipoAvisoComunitario guardar(TipoAvisoComunitario tipo) {
        return repositorio.save(tipo);
    }

    public TipoAvisoComunitario buscarPorId(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    public void eliminarPorId(Long id) {
        repositorio.deleteById(id);
    }
}
