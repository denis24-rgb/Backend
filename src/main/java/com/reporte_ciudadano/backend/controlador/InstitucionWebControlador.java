package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.configuraciones.RutaProperties;
import com.reporte_ciudadano.backend.dto.FormularioInstitucionAdmin;
import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.repositorio.InstitucionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/panel/superadmin")
@PreAuthorize("hasRole('SUPERADMIN')")
public class InstitucionWebControlador {

    @Autowired
    private InstitucionRepositorio institucionRepo;

    @Autowired
    private RutaProperties rutaProperties;

    // Mostrar formulario principal
    @GetMapping("/crear-institucion")
    public String mostrarFormulario(@RequestParam(name = "id", required = false) Long id, Model model) {
        FormularioInstitucionAdmin form = new FormularioInstitucionAdmin();

        if (id != null) {
            institucionRepo.findById(id).ifPresent(form::setInstitucion);
        } else {
            form.setInstitucion(new Institucion());
        }

        model.addAttribute("formulario", form);
        model.addAttribute("instituciones", institucionRepo.findAllByOrderByNombreAsc());

        return "panel/crear_institucion";
    }


    // Crear nueva institución
    @PostMapping("/crear-institucion")
    public String crearInstitucion(@ModelAttribute("formulario") FormularioInstitucionAdmin formulario,
                                   @RequestParam("logoFile") MultipartFile archivo,
                                   BindingResult result,
                                   Model model) {

        Institucion nueva = formulario.getInstitucion();

        // Validar duplicados
        if (institucionRepo.existsByNombre(nueva.getNombre())) {
            result.rejectValue("institucion.nombre", "error.formulario", "Ya existe una institución con ese nombre.");
        }

        if (institucionRepo.existsByCorreoInstitucional(nueva.getCorreoInstitucional())) {
            result.rejectValue("institucion.correoInstitucional", "error.formulario",
                    "Ya existe una institución con ese correo.");
        }

        if (result.hasErrors()) {
            model.addAttribute("formulario", formulario);
            model.addAttribute("instituciones", institucionRepo.findAllByOrderByNombreAsc());
            return "panel/crear_institucion";
        }

        // Guardar imagen
        if (!archivo.isEmpty()) {
            String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            String ruta = rutaProperties.getIconosInstituciones() + File.separator + nombreArchivo;
            try {
                archivo.transferTo(new File(ruta));
                nueva.setLogo(nombreArchivo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        nueva.setZona(nueva.getZona().toUpperCase());
        nueva.setActivo(true);
        institucionRepo.save(nueva);

        return "redirect:/panel/superadmin/crear-institucion?exito=true";
    }

    @PostMapping("/eliminar-institucion")
    public String eliminarInstitucion(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Optional<Institucion> institucion = institucionRepo.findById(id);

        if (institucion.isPresent()) {
            try {
                // Eliminar logo si existe
                if (institucion.get().getLogo() != null) {
                    File archivo = new File(rutaProperties.getIconosInstituciones() + File.separator + institucion.get().getLogo());
                    if (archivo.exists()) {
                        archivo.delete();
                    }
                }

                institucionRepo.deleteById(id);
                redirectAttributes.addFlashAttribute("mensaje", "Institución eliminada correctamente.");

            } catch (DataIntegrityViolationException e) {
                redirectAttributes.addFlashAttribute("errorMsg", "No se puede eliminar: la institución tiene tipos de reporte asignados.");
            }

        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "No se encontró la institución.");
        }

        return "redirect:/panel/superadmin/crear-institucion";
    }

    @PostMapping("/editar-institucion")
    public String editarInstitucion(@ModelAttribute("formulario") FormularioInstitucionAdmin formulario,
                                    @RequestParam("logoFile") MultipartFile archivo,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {

        Institucion inst = formulario.getInstitucion();

        Optional<Institucion> optInst = institucionRepo.findById(inst.getId());
        if (optInst.isPresent()) {
            Institucion entidad = optInst.get();
            entidad.setNombre(inst.getNombre());
            entidad.setZona(inst.getZona().toUpperCase());
            entidad.setCorreoInstitucional(inst.getCorreoInstitucional());
            entidad.setColorPrimario(inst.getColorPrimario());

            if (!archivo.isEmpty()) {
                // Eliminar logo anterior si existe
                if (entidad.getLogo() != null) {
                    File anterior = new File(rutaProperties.getIconosInstituciones() + File.separator + entidad.getLogo());
                    if (anterior.exists()) {
                        anterior.delete();
                    }
                }

                // Guardar nuevo logo
                String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
                String ruta = rutaProperties.getIconosInstituciones() + File.separator + nombreArchivo;
                try {
                    archivo.transferTo(new File(ruta));
                    entidad.setLogo(nombreArchivo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            institucionRepo.save(entidad);
            redirectAttributes.addFlashAttribute("mensaje", "Institución actualizada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "No se encontró la institución.");
        }

        return "redirect:/panel/superadmin/crear-institucion";
    }

    @GetMapping("/editar-institucion")
    public String mostrarFormularioEditar(@RequestParam Long id, Model model) {
        Optional<Institucion> optInst = institucionRepo.findById(id);
        if (optInst.isPresent()) {
            FormularioInstitucionAdmin form = new FormularioInstitucionAdmin();
            form.setInstitucion(optInst.get());
            model.addAttribute("formulario", form);
            model.addAttribute("modoEdicion", true);
            model.addAttribute("instituciones", institucionRepo.findAllByOrderByNombreAsc());
            return "panel/crear_institucion";
        } else {
            return "redirect:/panel/superadmin/crear-institucion";
        }
    }
}
