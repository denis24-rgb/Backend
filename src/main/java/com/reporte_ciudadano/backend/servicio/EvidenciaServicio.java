package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.Evidencia;
import com.reporte_ciudadano.backend.repositorio.EvidenciaRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvidenciaServicio {

    private final EvidenciaRepositorio repositorio;

    public EvidenciaServicio(EvidenciaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Evidencia> listarTodas() {
        return repositorio.findAll();
    }

    public Evidencia guardar(Evidencia evidencia) {
        return repositorio.save(evidencia);
    }

    public Evidencia buscarPorId(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }
    public Evidencia buscarPrimeraImagenPorReporte(Long reporteId) {
        return repositorio.findTopByReporteIdAndTipoEvidencia(reporteId, "imagen");
    }
    public List<Evidencia> listarPorReporte(Long reporteId) {
        return repositorio.findByReporteId(reporteId);
    }

}
