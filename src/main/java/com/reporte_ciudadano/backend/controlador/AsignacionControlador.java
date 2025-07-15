package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.AsignacionTecnico;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.modelo.RolInstitucional;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.servicio.AsignacionTecnicoServicio;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.servicio.TipoReporteServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/asignaciones")
public class AsignacionControlador {
    @Autowired
    private TipoReporteServicio tipoReporteServicio;
    @Autowired
    private AsignacionTecnicoServicio asignacionServicio;

    @Autowired
    private ReporteServicio reporteServicio;

    @Autowired
    private UsuarioInstitucionalServicio usuarioServicio;

    @GetMapping
    public String listarAsignaciones(Model model) {
        model.addAttribute("asignaciones", asignacionServicio.listarTodas());
        return "asignaciones"; // asignaciones.html
    }

    @GetMapping("/nueva")
    public String mostrarFormularioAsignacion(Model model) {
        model.addAttribute("asignacion", new AsignacionTecnico());

        List<UsuarioInstitucional> tecnicos = usuarioServicio.listarTodos()
                .stream()
                .filter(u -> u.getRol() == RolInstitucional.TECNICO)
                .collect(Collectors.toList());

        model.addAttribute("tecnicos", tecnicos);
        model.addAttribute("reportes", reporteServicio.listarTodos());

        return "formulario-asignacion";
    }

    @PostMapping("/guardar")
    public String guardarAsignacion(@RequestParam("reporte.id") Long reporteId,
                                    @RequestParam("tecnico.id") Long tecnicoId,
                                    @RequestParam("asignador.id") Long asignadorId) {
        AsignacionTecnico asignacion = new AsignacionTecnico();
        asignacion.setFechaAsignacion(LocalDateTime.now());
        asignacion.setReporte(new Reporte(reporteId));
        asignacion.setTecnico(new UsuarioInstitucional(tecnicoId));
        asignacion.setAsignador(new UsuarioInstitucional(asignadorId));

        asignacionServicio.guardar(asignacion);
        return "redirect:/reportes";
    }


    @GetMapping("/eliminar/{id}")
    public String eliminarAsignacion(@PathVariable Long id) {
        asignacionServicio.eliminar(id);
        return "redirect:/asignaciones";
    }

    @GetMapping("/reportes")
    public String verAsignacionReportes(Model model, java.security.Principal principal) {

        UsuarioInstitucional usuario = usuarioServicio.obtenerPorCorreo(principal.getName()).orElse(null);

        List<UsuarioInstitucional> tecnicos;

        if (usuario.getRol() == RolInstitucional.SUPERADMIN) {
            tecnicos = usuarioServicio.listarTodos().stream()
                    .filter(u -> u.getRol() == RolInstitucional.TECNICO)
                    .collect(Collectors.toList());
        } else {
            Long institucionId = usuario.getInstitucion().getId();
            tecnicos = usuarioServicio.listarTodos().stream()
                    .filter(u -> u.getRol() == RolInstitucional.TECNICO &&
                            u.getInstitucion().getId().equals(institucionId))
                    .collect(Collectors.toList());
        }

        Map<Long, Integer> conteoAsignaciones = new HashMap<>();
        for (UsuarioInstitucional tecnico : tecnicos) {
            int cantidad = asignacionServicio.listarPorTecnico(tecnico.getId()).size();
            conteoAsignaciones.put(tecnico.getId(), cantidad);
        }

        model.addAttribute("tecnicos", tecnicos);
        model.addAttribute("conteoAsignaciones", conteoAsignaciones);
        model.addAttribute("tecnicoSeleccionado", null);
        model.addAttribute("asignaciones", new ArrayList<>());
        model.addAttribute("enProceso", 0);
        model.addAttribute("resueltos", 0);
        model.addAttribute("cerrados", 0);
        model.addAttribute("tecnicoSeleccionadoId", -1L);

        return "asignacion_reportes";
    }


    @GetMapping("/reportes/{tecnicoId}")
    public String verAsignacionesPorTecnico(@PathVariable Long tecnicoId, Model model, java.security.Principal principal) {

        UsuarioInstitucional usuario = usuarioServicio.obtenerPorCorreo(principal.getName()).orElse(null);

        List<UsuarioInstitucional> tecnicos;

        if (usuario.getRol() == RolInstitucional.SUPERADMIN) {
            tecnicos = usuarioServicio.listarTodos().stream()
                    .filter(u -> u.getRol() == RolInstitucional.TECNICO)
                    .collect(Collectors.toList());
        } else {
            Long institucionId = usuario.getInstitucion().getId();
            tecnicos = usuarioServicio.listarTodos().stream()
                    .filter(u -> u.getRol() == RolInstitucional.TECNICO &&
                            u.getInstitucion().getId().equals(institucionId))
                    .collect(Collectors.toList());
        }

        // Obtener el técnico seleccionado
        UsuarioInstitucional tecnicoSeleccionado = usuarioServicio.obtenerPorId(tecnicoId).orElse(null);

        if (tecnicoSeleccionado == null) {
            return "redirect:/asignaciones/reportes";
        }

        // Obtener asignaciones de ese técnico
        List<AsignacionTecnico> asignaciones = asignacionServicio.listarPorTecnico(tecnicoId);

        // Contar estados directamente sobre las asignaciones
        long enProceso = asignaciones.stream()
                .filter(a -> a.getReporte().getEstado().equalsIgnoreCase("en proceso"))
                .count();

        long resueltos = asignaciones.stream()
                .filter(a -> a.getReporte().getEstado().equalsIgnoreCase("resuelto"))
                .count();

        long cerrados = asignaciones.stream()
                .filter(a -> a.getReporte().getEstado().equalsIgnoreCase("cerrado"))
                .count();

        // 🔧 Contar asignaciones por técnico
        Map<Long, Integer> conteoAsignaciones = new HashMap<>();
        for (UsuarioInstitucional tecnico : tecnicos) {
            int cantidad = asignacionServicio.listarPorTecnico(tecnico.getId()).size();
            conteoAsignaciones.put(tecnico.getId(), cantidad);
        }

        model.addAttribute("tecnicos", tecnicos);
        model.addAttribute("conteoAsignaciones", conteoAsignaciones);
        model.addAttribute("tecnicoSeleccionado", tecnicoSeleccionado);
        model.addAttribute("asignaciones", asignaciones);
        model.addAttribute("enProceso", enProceso);
        model.addAttribute("resueltos", resueltos);
        model.addAttribute("cerrados", cerrados);
        model.addAttribute("tecnicoSeleccionadoId", tecnicoId);
        var tipos = tipoReporteServicio.listarTodos();
        model.addAttribute("tipos", tipos);

        String color = "#2B2D30FF"; // color por defecto

        if (usuario.getInstitucion() != null && usuario.getInstitucion().getColorPrimario() != null && !usuario.getInstitucion().getColorPrimario().isEmpty()) {
            color = usuario.getInstitucion().getColorPrimario();
        }

        model.addAttribute("colorInstitucion", color);

        String nombreInstitucion = (usuario.getInstitucion() != null)
                ? usuario.getInstitucion().getNombre()
                : "Superadministrador";

        model.addAttribute("nombreInstitucion", nombreInstitucion);
        return "asignacion_reportes";
    }


    @PostMapping("/tomar")
    public String tomarReporte(@RequestParam Long reporteId,
                               @RequestParam Long tecnicoId,
                               Model model) {

        boolean ok = asignacionServicio.tomarReporte(reporteId, tecnicoId);

        if (ok) {
            model.addAttribute("mensaje", "Reporte tomado correctamente");
        } else {
            model.addAttribute("error", "No se puede tomar el reporte. Verifica el estado o la asignación.");
        }

        return "redirect:/asignaciones/reportes";
    }

}
