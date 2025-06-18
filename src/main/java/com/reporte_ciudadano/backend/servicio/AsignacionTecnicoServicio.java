package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.AsignacionTecnico;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.repositorio.AsignacionTecnicoRepositorio;
import com.reporte_ciudadano.backend.repositorio.ReporteRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AsignacionTecnicoServicio {
    @Autowired
    private ReporteRepositorio reporteRepo;

    @Autowired
    private AsignacionTecnicoRepositorio asignacionRepo;

    public List<AsignacionTecnico> listarTodas() {
        return asignacionRepo.findAll();
    }

    public Optional<AsignacionTecnico> obtenerPorId(Long id) {
        return asignacionRepo.findById(id);
    }

    public List<AsignacionTecnico> listarPorTecnico(Long tecnicoId) {
        return asignacionRepo.findByTecnicoId(tecnicoId);
    }

    public AsignacionTecnico guardar(AsignacionTecnico asignacion) {
        return asignacionRepo.save(asignacion);
    }

    public void eliminar(Long id) {
        asignacionRepo.deleteById(id);
    }
    @Transactional
    public boolean tomarReporte(Long reporteId, Long tecnicoId) {
        Optional<AsignacionTecnico> asignacionOpt = asignacionRepo.findByReporteIdAndTecnicoId(reporteId, tecnicoId);
        if (asignacionOpt.isEmpty()) return false;

        Reporte reporte = asignacionOpt.get().getReporte();
        if (!"recibido".equalsIgnoreCase(reporte.getEstado())) return false;

        reporte.setEstado("en proceso");

        reporteRepo.save(reporte);
        return true;
    }
    public List<Reporte> listarPorTecnicoYEstado(Long tecnicoId, String estadoNombre) {
        return asignacionRepo.findByTecnicoIdAndReporte_Estado(tecnicoId, estadoNombre)
                .stream()
                .map(AsignacionTecnico::getReporte)
                .toList();
    }
    public long contarPorTecnicoYEstado(Long tecnicoId, String estadoNombre) {
        return asignacionRepo.countByTecnicoIdAndReporte_Estado(tecnicoId, estadoNombre);
    }


}
