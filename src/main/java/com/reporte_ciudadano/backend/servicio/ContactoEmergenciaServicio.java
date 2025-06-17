package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.ContactoEmergencia;
import com.reporte_ciudadano.backend.repositorio.ContactoEmergenciaRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactoEmergenciaServicio {

    private final ContactoEmergenciaRepositorio repositorio;

    public ContactoEmergenciaServicio(ContactoEmergenciaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<ContactoEmergencia> listarTodos() {
        return repositorio.findAll();
    }

    public ContactoEmergencia guardar(ContactoEmergencia contacto) {
        return repositorio.save(contacto);
    }

    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }

    public ContactoEmergencia buscarPorId(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    public List<ContactoEmergencia> listarPorTipo(String tipo) {
        return repositorio.findByTipoContacto(tipo);
    }
}
