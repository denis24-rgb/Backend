package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.TipoReporte;
import com.reporte_ciudadano.backend.repositorio.InstitucionTipoReporteRepositorio;
import com.reporte_ciudadano.backend.repositorio.TipoReporteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoReporteServicio {

    @Autowired
    private TipoReporteRepositorio tipoRepo;

    @Autowired
    private InstitucionTipoReporteRepositorio institucionTipoReporteRepositorio;

    public TipoReporteServicio(TipoReporteRepositorio repositorio) {
        this.tipoRepo = repositorio;
    }

    public List<TipoReporte> listarTodos() {
        return tipoRepo.findAll();
    }

    public TipoReporte guardar(TipoReporte tipo) {
        return tipoRepo.save(tipo);
    }

    public void eliminar(Long id) {
        tipoRepo.deleteById(id);
    }

    public TipoReporte buscarPorId(Long id) {
        return tipoRepo.findById(id).orElse(null);
    }

    public Optional<TipoReporte> obtenerPorId(Long id) {
        return tipoRepo.findById(id);
    }

    public List<TipoReporte> listarPorCategoria(Long categoriaId) {
        return institucionTipoReporteRepositorio.findTiposReportePorCategoria(categoriaId);

    public boolean existeNombre(String nombre) {
        return tipoRepo.existsByNombre(nombre);

    }

}
