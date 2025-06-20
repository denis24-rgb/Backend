package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.AsignacionTecnico;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.modelo.RolInstitucional;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.servicio.AsignacionTecnicoServicio;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/asignaciones")
public class AsignacionControlador {

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

        List<Reporte> reportes;
        List<UsuarioInstitucional> tecnicos;

        if (usuario.getRol() == RolInstitucional.SUPERADMIN) {
            // 🔓 SUPERADMIN ve todos los reportes
            reportes = reporteServicio.listarPorEstados("en proceso", "resuelto", "cerrado");
            tecnicos = usuarioServicio.listarTodos()
                    .stream()
                    .filter(u -> u.getRol() == RolInstitucional.TECNICO)
                    .collect(Collectors.toList());
        } else {
            // 🔐 Técnicos o administradores ven solo su institución
            Long institucionId = usuario.getInstitucion().getId();

            reportes = reporteServicio.listarPorEstadosYInstitucion(institucionId, "en proceso", "resuelto", "cerrado");

            tecnicos = usuarioServicio.listarTodos()
                    .stream()
                    .filter(u -> u.getRol() == RolInstitucional.TECNICO &&
                            u.getInstitucion().getId().equals(institucionId))
                    .collect(Collectors.toList());
        }

        model.addAttribute("reportes", reportes);
        model.addAttribute("tecnicos", tecnicos);

        return "asignacion_reportes"; // nombre del HTML

    }

}
