package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.HistorialReporte;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.servicio.HistorialReporteServicio;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/historial")
public class HistorialReporteControlador {

    @Autowired
    private HistorialReporteServicio historialServicio;

    @Autowired
    private ReporteServicio reporteServicio;

    @Autowired
    private UsuarioInstitucionalServicio usuarioServicio;

    @GetMapping("/reporte/{reporteId}")
    public String verHistorialPorReporte(@PathVariable Long reporteId, Model model) {
        List<HistorialReporte> historial = historialServicio.listarPorReporte(reporteId);
        model.addAttribute("historial", historial);
        model.addAttribute("reporte", reporteServicio.obtenerPorId(reporteId).orElse(new Reporte()));
        return "historial-reporte";
    }

    @GetMapping("/nuevo/{reporteId}")
    public String mostrarFormularioHistorial(@PathVariable Long reporteId, Model model) {
        HistorialReporte h = new HistorialReporte();
        h.setReporte(reporteServicio.obtenerPorId(reporteId).orElse(null));
        model.addAttribute("historial", h);
        model.addAttribute("usuarios", usuarioServicio.listarTodos());
        return "formulario-historial";
    }

    @PostMapping("/guardar")
    public String guardarHistorial(@ModelAttribute

    HistorialReporte historial) {
        historial.setFechaHora(LocalDateTime.now());
        historialServicio.guardar(historial);
        return "redirect:/historial/reporte/" + historial.getReporte().getId();
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarHistorial(@PathVariable Long id) {
        Long reporteId = historialServicio.obtenerPorId(id).get().getReporte().getId();
        historialServicio.eliminar(id);
        return "redirect:/historial/reporte/" + reporteId;
    }
}
