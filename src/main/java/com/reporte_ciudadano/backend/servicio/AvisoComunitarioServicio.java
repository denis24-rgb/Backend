package com.reporte_ciudadano.backend.servicio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reporte_ciudadano.backend.modelo.AvisoComunitario;
import com.reporte_ciudadano.backend.repositorio.AvisoComunitarioRepositorio;

@Service
public class AvisoComunitarioServicio {

    @Autowired
    private AvisoComunitarioRepositorio repo;

    public AvisoComunitario guardar(AvisoComunitario aviso) {
        return repo.save(aviso);
    }

    public List<AvisoComunitario> listarVigentes() {
        return repo.findByFechaEliminacionAfterOrderByFechaCreacionDesc(LocalDateTime.now());
    }

    public void eliminarVencidos() {
        List<AvisoComunitario> vencidos = repo.findAll().stream()
                .filter(a -> a.getFechaEliminacion().isBefore(LocalDateTime.now()))
                .toList();
        repo.deleteAll(vencidos);
    }
}
