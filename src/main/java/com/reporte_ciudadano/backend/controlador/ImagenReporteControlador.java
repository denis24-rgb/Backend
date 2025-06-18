//package com.reporte_ciudadano.backend.controlador;
//
//
//
//import com.reporte_ciudadano.backend.modelo.ImagenReporte;
//import com.reporte_ciudadano.backend.modelo.Reporte;
//import com.reporte_ciudadano.backend.servicio.ImagenReporteServicio;
//import com.reporte_ciudadano.backend.servicio.ReporteServicio;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/imagenes")
//public class ImagenReporteControlador {
//
//    @Autowired
//    private ImagenReporteServicio imagenServicio;
//
//    @Autowired
//    private ReporteServicio reporteServicio;
//
//    // Mostrar imágenes de un reporte específico
//    @GetMapping("/reporte/{reporteId}")
//    public String verImagenesPorReporte(@PathVariable Long reporteId, Model model) {
//        List<ImagenReporte> imagenes = imagenServicio.listarPorReporte(reporteId);
//        model.addAttribute("imagenes", imagenes);
//        model.addAttribute("reporte", reporteServicio.obtenerPorId(reporteId).orElse(new Reporte()));
//        return "imagenes-reporte";
//    }
//
//    // Formulario de subida manual de evidencias (solo URL en esta versión)
//    @GetMapping("/nueva/{reporteId}")
//    public String mostrarFormularioSubida(@PathVariable Long reporteId, Model model) {
//        ImagenReporte imagen = new ImagenReporte();
//        Reporte reporte = reporteServicio.obtenerPorId(reporteId).orElse(null);
//        imagen.setReporte(reporte);
//
//        model.addAttribute("imagen", imagen);
//        return "formulario-imagen";
//    }
//
//    @PostMapping("/guardar")
//    public String guardarImagen(@ModelAttribute ImagenReporte imagen) {
//        imagenServicio.guardar(imagen);
//        return "redirect:/imagenes/reporte/" + imagen.getReporte().getId();
//    }
//
//    @GetMapping("/eliminar/{id}")
//    public String eliminarImagen(@PathVariable Long id) {
//        Long reporteId = imagenServicio.obtenerPorId(id).get().getReporte().getId();
//        imagenServicio.eliminar(id);
//        return "redirect:/imagenes/reporte/" + reporteId;
//    }
//}
