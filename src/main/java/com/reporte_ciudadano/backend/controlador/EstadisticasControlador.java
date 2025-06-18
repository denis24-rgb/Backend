package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EstadisticasControlador {

    @Autowired
    private ReporteServicio reporteServicio;

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Model model) {
        long pendientes = reporteServicio.contarPorEstado("recibido");
        long revision = reporteServicio.contarPorEstado("en proceso");
        long resueltos = reporteServicio.contarPorEstado("resuelto");
        long cerrados = reporteServicio.contarPorEstado("cerrado");

        long total = pendientes + revision + resueltos + cerrados;

        model.addAttribute("recibido", pendientes);
        model.addAttribute("en proceso", revision);
        model.addAttribute("resueltos", resueltos);
        model.addAttribute("cerrado", cerrados);
        model.addAttribute("total", total);

        return "estadisticas";
    }

}
