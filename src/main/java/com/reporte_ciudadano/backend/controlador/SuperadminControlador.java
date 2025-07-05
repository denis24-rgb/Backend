package com.reporte_ciudadano.backend.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.reporte_ciudadano.backend.dto.FormularioInstitucionAdmin;
import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.repositorio.InstitucionRepositorio;
import com.reporte_ciudadano.backend.repositorio.UsuarioInstitucionalRepositorio;
import com.reporte_ciudadano.backend.servicio.InstitucionServicio;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/panel/superadmin")
@PreAuthorize("hasRole('SUPERADMIN')")
public class SuperadminControlador {
    @Autowired
    private InstitucionServicio institucionServicio;

    @Autowired
    private UsuarioInstitucionalServicio usuarioServicio;

    @Autowired
    private ReporteServicio reporteServicio;
    @Autowired
    private InstitucionRepositorio institucionRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioInstitucionalRepositorio usuarioRepo;

    @GetMapping
    public String mostrarPanel(Model model) throws JsonProcessingException {
        // Formulario para nueva institución
        FormularioInstitucionAdmin form = new FormularioInstitucionAdmin();
        form.setInstitucion(new Institucion());
        form.setAdmin(new UsuarioInstitucional());
        model.addAttribute("formulario", form);
        model.addAttribute("institucionesLista", institucionRepo.findAll());

        // Estadísticas
        model.addAttribute("instituciones", institucionServicio.contarInstituciones());
        model.addAttribute("usuarios", usuarioServicio.contarUsuarios());
        model.addAttribute("reportes", reporteServicio.contarReportes());

        // Reportes con ubicación
        List<Reporte> listaReportes = reporteServicio.obtenerConUbicacion();

        // Map para enviar solo datos necesarios al JS
        List<Map<String, Object>> reportesMap = listaReportes.stream().map(r -> {
            Map<String, Object> reporteMap = new HashMap<>();
            reporteMap.put("id", r.getId());
            reporteMap.put("descripcion", r.getDescripcion());
            reporteMap.put("estado", r.getEstado()); // Ahora es String directamente
            reporteMap.put("fechaReporte", r.getFechaReporte() != null ? r.getFechaReporte().toString() : null);
            reporteMap.put("hora", r.getHora() != null ? r.getHora().toString() : null);
            reporteMap.put("ubicacion", r.getUbicacion());

            // Relacionales, si están mapeadas como @ManyToOne puedes hacer esto:
            if (r.getInstitucion() != null) {
                reporteMap.put("institucion", r.getInstitucion().getNombre());
            }

            if (r.getTipoReporte() != null) {
                Map<String, Object> tipo = new HashMap<>();
                tipo.put("nombre", r.getTipoReporte().getNombre());
                tipo.put("icono", r.getTipoReporte().getIcono());
                reporteMap.put("tipoReporte", tipo);
            }

            return reporteMap;
        }).toList();

        // Convertir a JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String reportesJSON = mapper.writeValueAsString(reportesMap);

        model.addAttribute("reportesJSON", reportesJSON);

        return "panel/superadmin";
    }

    @GetMapping("/crear-institucion")
    public String mostrarFormulario(Model model) {
        FormularioInstitucionAdmin form = new FormularioInstitucionAdmin();
        form.setInstitucion(new Institucion());
        form.setAdmin(new UsuarioInstitucional());

        model.addAttribute("formulario", form);
        model.addAttribute("instituciones", institucionRepo.findAllByOrderByNombreAsc());

        return "panel/crear_institucion";
    }

    @PostMapping("/crear-institucion")
    public String crearInstitucion(@ModelAttribute("formulario") FormularioInstitucionAdmin formulario,
            BindingResult result,
            Model model) {

        Institucion nueva = formulario.getInstitucion();

        // Validar duplicados por nombre
        if (institucionRepo.existsByNombre(nueva.getNombre())) {
            result.rejectValue("institucion.nombre", "error.formulario", "Ya existe una institución con ese nombre.");
        }

        // Validar duplicados por correo
        if (institucionRepo.existsByCorreoInstitucional(nueva.getCorreoInstitucional())) {
            result.rejectValue("institucion.correoInstitucional", "error.formulario",
                    "Ya existe una institución con ese correo.");
        }

        // Si hay errores, devolver a la vista con los mensajes por campo
        if (result.hasErrors()) {
            model.addAttribute("formulario", formulario);
            model.addAttribute("instituciones", institucionRepo.findAll());
            return "panel/crear_institucion";
        }

        // Convertir zona y tipo de servicio a mayúsculas
        nueva.setZona(nueva.getZona().toUpperCase());
        // nueva.setTipoServicio(nueva.getTipoServicio().toUpperCase());

        // Guardar
        nueva.setActivo(true);
        institucionRepo.save(nueva);
        return "redirect:/panel/superadmin/crear-institucion?exito=true";
    }

    @PostMapping("/eliminar-institucion")
    public String eliminarInstitucion(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Optional<Institucion> institucion = institucionRepo.findById(id);
        if (institucion.isPresent()) {
            institucionRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Institución eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "No se encontró la institución.");
        }
        return "redirect:/panel/superadmin/crear-institucion";
    }

    @PostMapping("/editar-institucion")
    public String editarInstitucion(@RequestParam Long id,
            @RequestParam String nombre,
            @RequestParam String zona,
            @RequestParam String correoInstitucional,
            RedirectAttributes redirectAttributes) {
        Optional<Institucion> optInst = institucionRepo.findById(id);

        if (optInst.isPresent()) {
            Institucion inst = optInst.get();
            inst.setNombre(nombre);
            inst.setZona(zona);
            inst.setCorreoInstitucional(correoInstitucional);
            // inst.setTipoServicio(tipoServicio);
            institucionRepo.save(inst); // ✔️ Actualiza la misma institución

            redirectAttributes.addFlashAttribute("mensaje", "Institución actualizada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "No se encontró la institución.");
        }

        return "redirect:/panel/superadmin/crear-institucion";
    }

    @GetMapping("/usuarios")
    public String mostrarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioServicio.listarTodos());
        return "usuarios"; // O "panel/usuarios" si lo pones en subcarpeta
    }
    @GetMapping("/reportes")
    public String verReportes(Model model) {
        List<Reporte> reportes = reporteServicio.listarTodos();
        model.addAttribute("reportes", reportes);
        return "reportes";
    }


}
