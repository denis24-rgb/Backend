package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.InstitucionTipoReporte;
import com.reporte_ciudadano.backend.modelo.TipoReporte;
import com.reporte_ciudadano.backend.repositorio.InstitucionTipoReporteRepositorio;
import com.reporte_ciudadano.backend.repositorio.TipoReporteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public boolean eliminar(Long id, String rutaIconos) {
        // Verifica si el tipo de reporte está siendo usado en algún reporte
        if (tipoRepo.estaUsadoEnReportes(id)) {
            return false; // No se elimina, está en uso
        }

        // Elimina todas las relaciones con instituciones
        List<InstitucionTipoReporte> relaciones = institucionTipoReporteRepositorio.findByTipoReporteId(id);
        institucionTipoReporteRepositorio.deleteAll(relaciones);

        // Busca el tipo de reporte
        Optional<TipoReporte> tipoOpt = tipoRepo.findById(id);
        if (tipoOpt.isPresent()) {
            TipoReporte tipo = tipoOpt.get();

            // Elimina el ícono del disco si existe
            try {
                Path ruta = Paths.get(rutaIconos, tipo.getIcono());
                Files.deleteIfExists(ruta);
            } catch (Exception e) {
                System.err.println("No se pudo eliminar el ícono: " + e.getMessage());
            }

            // Elimina el tipo de reporte
            tipoRepo.deleteById(id);
            return true;
        }

        return false; // No encontrado
    }

    public TipoReporte buscarPorId(Long id) {
        return tipoRepo.findById(id).orElse(null);
    }

    public Optional<TipoReporte> obtenerPorId(Long id) {
        return tipoRepo.findById(id);
    }

    public List<TipoReporte> listarPorCategoria(Long categoriaId) {
        return institucionTipoReporteRepositorio.findTiposReportePorCategoria(categoriaId);

    }

}
