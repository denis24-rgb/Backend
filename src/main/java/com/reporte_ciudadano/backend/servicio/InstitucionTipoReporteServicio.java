package com.reporte_ciudadano.backend.servicio;


import com.reporte_ciudadano.backend.modelo.InstitucionTipoReporte;
import com.reporte_ciudadano.backend.repositorio.InstitucionTipoReporteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstitucionTipoReporteServicio {

    @Autowired
    private InstitucionTipoReporteRepositorio repo;
    @Autowired
    private InstitucionTipoReporteRepositorio institucionTipoReporteRepositorio;

    public InstitucionTipoReporte guardar(InstitucionTipoReporte entidad) {
        return repo.save(entidad);
    }

    public List<InstitucionTipoReporte> listarTodos() {
        return repo.findAll();
    }

    public List<InstitucionTipoReporte> listarPorInstitucion(Long institucionId) {
        return repo.findByInstitucionId(institucionId);
    }

    public List<InstitucionTipoReporte> listarPorCategoria(Long categoriaId) {
        return repo.findByCategoriaReporteId(categoriaId);
    }
    public InstitucionTipoReporte buscarPorTipoReporteId(Long tipoReporteId) {
        List<InstitucionTipoReporte> lista = repo.findByTipoReporteId(tipoReporteId);
        return lista.isEmpty() ? null : lista.get(0);
    }

}
