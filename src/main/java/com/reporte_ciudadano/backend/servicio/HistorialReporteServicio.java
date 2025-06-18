package com.reporte_ciudadano.backend.servicio;


import com.reporte_ciudadano.backend.modelo.HistorialReporte;
import com.reporte_ciudadano.backend.repositorio.HistorialReporteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistorialReporteServicio {

    @Autowired
    private HistorialReporteRepositorio historialRepo;

    public List<HistorialReporte> listarTodos() {
        return historialRepo.findAll();
    }

    public List<HistorialReporte> listarPorReporte(Long reporteId) {
        return historialRepo.findByReporteId(reporteId);
    }

    public Optional<HistorialReporte> obtenerPorId(Long id) {
        return historialRepo.findById(id);
    }

    public HistorialReporte guardar(HistorialReporte historial) {
        return historialRepo.save(historial);
    }

    public void eliminar(Long id) {
        historialRepo.deleteById(id);
    }
}
