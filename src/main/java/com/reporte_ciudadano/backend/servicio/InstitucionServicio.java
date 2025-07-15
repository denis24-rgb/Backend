package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.repositorio.InstitucionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstitucionServicio {

    @Autowired
    private InstitucionRepositorio institucionRepositorio;

    public List<Institucion> listarTodas() {
        return institucionRepositorio.findAll();
    }

    public Optional<Institucion> obtenerPorId(Long id) {
        return institucionRepositorio.findById(id);
    }

    public Institucion guardar(Institucion institucion) {
        return institucionRepositorio.save(institucion);
    }

    public void eliminar(Long id) {
        institucionRepositorio.deleteById(id);
    }

    public long contarInstituciones() {
        return institucionRepositorio.count();
    }

    public boolean existeNombreOCorreo(String nombre, String correo) {
        return institucionRepositorio.existsByNombre(nombre) ||
                institucionRepositorio.existsByCorreoInstitucional(correo);
    }
    public List<Institucion> obtenerTodas() {
        return institucionRepositorio.findAll();
    }
}
