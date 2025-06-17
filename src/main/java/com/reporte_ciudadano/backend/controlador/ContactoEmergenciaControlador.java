package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.ContactoEmergencia;
import com.reporte_ciudadano.backend.servicio.ContactoEmergenciaServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contactos")
public class ContactoEmergenciaControlador {

    private final ContactoEmergenciaServicio servicio;

    public ContactoEmergenciaControlador(ContactoEmergenciaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<ContactoEmergencia> obtenerTodos() {
        return servicio.listarTodos();
    }

    @PostMapping
    public ContactoEmergencia crear(@RequestBody ContactoEmergencia contacto) {
        return servicio.guardar(contacto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }

    @GetMapping("/{id}")
    public ContactoEmergencia obtenerPorId(@PathVariable Long id) {
        return servicio.buscarPorId(id);
    }

    @GetMapping("/tipo/{tipo}")
    public List<ContactoEmergencia> obtenerPorTipo(@PathVariable String tipo) {
        return servicio.listarPorTipo(tipo);
    }

}
