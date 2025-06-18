package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.AsignacionTecnico;
import com.reporte_ciudadano.backend.repositorio.AsignacionTecnicoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AsignacionTecnicoServicio {

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
}
