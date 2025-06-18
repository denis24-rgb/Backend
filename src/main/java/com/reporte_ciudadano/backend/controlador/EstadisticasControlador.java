//package com.reporte_ciudadano.backend.controlador;
//
//import com.reporte_ciudadano.backend.servicio.ReporteServicio;
//import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@Controller
//public class EstadisticasControlador {
//
//    @Autowired
//    private UsuarioInstitucionalServicio usuarioServicio;
//
//    @Autowired
//    private ReporteServicio reporteServicio;
//
//    @GetMapping("/estadisticas")
//    public String mostrarEstadisticas(Model model) {
//        long pendientes = reporteServicio.contarPorEstado("Pendiente");
//        long revision = reporteServicio.contarPorEstado("En Revisión");
//        long resueltos = reporteServicio.contarPorEstado("Resuelto");
//        long cerrados = reporteServicio.contarPorEstado("Cerrado");
//
//        long total = pendientes + revision + resueltos + cerrados;
//
//        model.addAttribute("pendientes", pendientes);
//        model.addAttribute("revision", revision);
//        model.addAttribute("resueltos", resueltos);
//        model.addAttribute("cerrados", cerrados);
//        model.addAttribute("total", total);
//
//        return "estadisticas";
//    }
//
//}
