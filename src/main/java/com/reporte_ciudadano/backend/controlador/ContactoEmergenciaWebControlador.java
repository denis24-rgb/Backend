package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.ContactoEmergencia;
import com.reporte_ciudadano.backend.repositorio.ContactoEmergenciaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/superadmin/contactos")
public class ContactoEmergenciaWebControlador {

    @Autowired
    private ContactoEmergenciaRepositorio contactoRepositorio;

    @GetMapping
    public String listarContactos(Model model) {
        model.addAttribute("contactos", contactoRepositorio.findAll());
        return "panel/contactos";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ContactoEmergencia contacto) {

        // Si viene con un ID, actualiza. Si no, crea uno nuevo.
        if (contacto.getId() != null) {
            Optional<ContactoEmergencia> contactoExistente = contactoRepositorio.findById(contacto.getId());
            if (contactoExistente.isPresent()) {
                ContactoEmergencia existente = contactoExistente.get();
                existente.setNombreContacto(contacto.getNombreContacto());
                existente.setTipoContacto(contacto.getTipoContacto());
                existente.setTelefono(contacto.getTelefono());
                existente.setDireccion(contacto.getDireccion());
                existente.setLat(contacto.getLat());
                existente.setLng(contacto.getLng());
                contactoRepositorio.save(existente);
            } else {
                // Si no se encuentra por id, lo guarda como nuevo
                contactoRepositorio.save(contacto);
            }
        } else {
            contactoRepositorio.save(contacto);
        }

        return "redirect:/superadmin/contactos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        contactoRepositorio.deleteById(id);
        return "redirect:/superadmin/contactos";
    }
}