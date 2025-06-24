package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.AsignacionTecnico;
import com.reporte_ciudadano.backend.servicio.AsignacionTecnicoServicio;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reportes")
public class ReporteWebControlador {

    @Autowired
    private ReporteServicio reporteServicio;

    @Autowired
    private UsuarioInstitucionalServicio usuarioServicio;

    @Autowired
    private AsignacionTecnicoServicio asignacionServicio;

    @GetMapping
    public String verReportes(Model model, Principal principal) {
        String correo = principal.getName();
        var usuario = usuarioServicio.obtenerPorCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        var reportes = usuario.getRol().name().equals("SUPERADMIN")
                ? reporteServicio.listarTodos()
                : reporteServicio.listarPorInstitucion(usuario.getInstitucion().getId());

        var tecnicos = usuario.getRol().name().equals("SUPERADMIN")
                ? usuarioServicio.listarTodosTecnicos()
                : usuarioServicio.listarTecnicosPorInstitucion(usuario.getInstitucion().getId());

        Map<Long, AsignacionTecnico> asignaciones = reportes.stream()
                .map(r -> asignacionServicio.obtenerPorReporte(r.getId()).orElse(null))
                .filter(a -> a != null)
                .collect(Collectors.toMap(
                        a -> a.getReporte().getId(),
                        a -> a
                ));

        model.addAttribute("usuario", usuario);
        model.addAttribute("reportes", reportes);
        model.addAttribute("tecnicos", tecnicos);
        model.addAttribute("asignaciones", asignaciones);
        model.addAttribute("estados", List.of("recibido", "en proceso", "resuelto", "cerrado"));

        var tipos = reportes.stream()
                .map(r -> r.getTipoReporte().getNombre())
                .distinct()
                .toList();
        model.addAttribute("tipos", tipos);

        return "reportes";
    }

    @PostMapping("/guardar-estado")
    public String cambiarEstado(@RequestParam("id") Long id,
                                @RequestParam("estado") String estado,
                                RedirectAttributes redirect) {
        try {
            reporteServicio.cambiarEstado(id, estado);
            redirect.addFlashAttribute("mensajeExito", "Estado actualizado correctamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensajeError", "Error al actualizar el estado: " + e.getMessage());
        }
        return "redirect:/reportes";
    }

    @PostMapping("/eliminar")
    public String eliminarReporte(@RequestParam Long reporteId, RedirectAttributes redirect) {
        try {
            reporteServicio.eliminarPorId(reporteId);
            redirect.addFlashAttribute("mensajeExito", "Reporte eliminado correctamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensajeError", "Error al eliminar el reporte: " + e.getMessage());
        }
        return "redirect:/reportes";
    }

    @PostMapping("/{id}/cerrar")
    public String cerrarPorOperador(@PathVariable Long id, RedirectAttributes redirect) {
        boolean ok = reporteServicio.cerrarReportePorOperador(id);
        if (ok) {
            redirect.addFlashAttribute("mensajeExito", "🔒 Reporte cerrado exitosamente.");
        } else {
            redirect.addFlashAttribute("mensajeError", "No se puede cerrar este reporte.");
        }
        return "redirect:/reportes";
    }
}
