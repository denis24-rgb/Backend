package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.TipoReporte;
import com.reporte_ciudadano.backend.repositorio.TipoReporteRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoReporteServicio {

    private final TipoReporteRepositorio repositorio;

    public TipoReporteServicio(TipoReporteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<TipoReporte> listarTodos() {
        return repositorio.findAll();
    }

    public TipoReporte guardar(TipoReporte tipo) {
        return repositorio.save(tipo);
    }

    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }

    public TipoReporte buscarPorId(Long id) {
        return repositorio.findById(id).orElse(null);
    }
}
