package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EstadisticasControlador {

    @Autowired
    private ReporteServicio reporteServicio;

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Model model) {
        // Estadísticas por Estado
        long recibidos = reporteServicio.contarPorEstado("recibido");
        long enProceso = reporteServicio.contarPorEstado("en proceso");
        long resueltos = reporteServicio.contarPorEstado("resuelto");
        long cerrados = reporteServicio.contarPorEstado("cerrado");

        model.addAttribute("recibidos", recibidos);
        model.addAttribute("enProceso", enProceso);
        model.addAttribute("resueltos", resueltos);
        model.addAttribute("cerrados", cerrados);

        // Estadísticas por Tipo de Reporte
        List<String> tipos = reporteServicio.obtenerNombresTiposReporte();
        List<Long> cantidadesTipos = reporteServicio.contarReportesPorTipo();

        model.addAttribute("tipos", tipos);
        model.addAttribute("cantidadesTipos", cantidadesTipos);

        // Evolución de Reportes por Mes
        List<String> meses = reporteServicio.obtenerMesesConFormato();
        List<Long> reportesMes = reporteServicio.contarReportesPorMes();

        model.addAttribute("meses", meses);
        model.addAttribute("reportesMes", reportesMes);

        return "estadisticas";
    }
    @GetMapping("/api/estadisticas/rango")
    @ResponseBody
    public Map<String, Object> obtenerEstadisticasPorRango(
            @RequestParam String inicio,
            @RequestParam String fin) {

        LocalDate fechaInicio = LocalDate.parse(inicio);
        LocalDate fechaFin = LocalDate.parse(fin);

        List<String> dias = reporteServicio.obtenerDiasEnRango(fechaInicio, fechaFin);
        List<Long> cantidades = reporteServicio.contarReportesPorDiaEnRango(fechaInicio, fechaFin);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("labels", dias);
        respuesta.put("valores", cantidades);

        return respuesta;
    }

}
