//// NUEVO CÓDIGO LISETH - Controlador del Técnico
//package com.reporte_ciudadano.backend.controlador;
//
//
//import com.reporte_ciudadano.backend.dto.ActualizacionTecnico;
//import com.reporte_ciudadano.backend.modelo.Reporte;
//import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
//import com.reporte_ciudadano.backend.servicio.ReporteServicio;
//import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@Controller
//@RequiredArgsConstructor
//public class TecnicoControlador {
//
//    private final ReporteServicio reporteServicio;
//    private final UsuarioInstitucionalServicio usuarioServicio;
//
//    @GetMapping("/tecnico")
//    public String vistaTecnico(
//            @RequestParam(name = "estado", required = false) String estado,
//            Model model,
//            HttpSession session) {
//
//        Long tecnicoId = (Long) session.getAttribute("usuarioId");
//        if (tecnicoId == null) return "redirect:/login";
//
//        Optional<UsuarioInstitucional> tecnicoOptional = usuarioServicio.obtenerPorId(tecnicoId);
//        if (tecnicoOptional.isEmpty()) return "redirect:/login";
//
//        UsuarioInstitucional tecnico = tecnicoOptional.get();
//        List<Reporte> reportes = (estado != null && !estado.isEmpty())
//                ? reporteServicio.listarPorTecnicoYEstado(tecnicoId, estado)
//                : reporteServicio.listarPorTecnico(tecnicoId);
//
//        // Estados REALES en la base de datos: RECIBIDO, EN_PROCESO, FINALIZADO
//        long totalPendientes = reporteServicio.contarPorTecnicoYEstado(tecnicoId, "RECIBIDO");
//        long totalProceso = reporteServicio.contarPorTecnicoYEstado(tecnicoId, "EN_PROCESO");
//        long totalFinalizado = reporteServicio.contarPorTecnicoYEstado(tecnicoId, "FINALIZADO");
//
//        model.addAttribute("tecnico", tecnico);
//        model.addAttribute("reportes", reportes);
//        model.addAttribute("estadoSeleccionado", estado);
//        model.addAttribute("totalPendientes", totalPendientes);
//        model.addAttribute("totalProceso", totalProceso);
//        model.addAttribute("totalFinalizado", totalFinalizado);
//
//        return "tecnico";
//    }
//
//    @PostMapping("/tecnico/actualizar-reporte")
//    @ResponseBody
//    public ResponseEntity<String> actualizarReporteTecnico(
//            @RequestParam("reporteId") Long reporteId,
//            @RequestParam("nuevoEstado") String nuevoEstado,
//            @RequestParam(value = "comentario", required = false) String comentario
//    ) {
//        try {
//            ActualizacionTecnico dto = new ActualizacionTecnico();
//            dto.setReporteId(reporteId);
//            dto.setNuevoEstado(nuevoEstado);
//            dto.setComentario(comentario);
//
//            boolean actualizado = reporteServicio.actualizarPorTecnico(dto);
//            return ResponseEntity.ok(actualizado ? "ok" : "error");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
//        }
//    }
//
//    // Endpoint para obtener reportes por estado (AJAX)
//    @GetMapping("/tecnico/reportes")
//    @ResponseBody
//    public List<Reporte> obtenerReportesPorEstado(@RequestParam String estado, HttpSession session) {
//        Long tecnicoId = (Long) session.getAttribute("usuarioId");
//        return reporteServicio.listarPorTecnicoYEstado(tecnicoId, estado);
//    }
//
//    @GetMapping("/tecnico/reportes/contar")
//    @ResponseBody
//    public Map<String, Long> contarReportesPorEstado(HttpSession session) {
//        Long tecnicoId = (Long) session.getAttribute("usuarioId");
//
//        Map<String, Long> conteos = new HashMap<>();
//        conteos.put("RECIBIDO", reporteServicio.contarPorTecnicoYEstado(tecnicoId, "RECIBIDO"));
//        conteos.put("EN PROCESO", reporteServicio.contarPorTecnicoYEstado(tecnicoId, "EN PROCESO")); // ✔ clave y valor iguales al frontend y base
//        conteos.put("FINALIZADO", reporteServicio.contarPorTecnicoYEstado(tecnicoId, "RESUELTO")); // ✔ clave que espera el frontend
//        return conteos;
//    }
//
//    // Endpoint para filtro dinámico por JS
//    @GetMapping("/tecnico/filtrar")
//    @ResponseBody
//    public List<Reporte> filtrarReportesPorEstado(@RequestParam String estado, HttpSession session) {
//        Long tecnicoId = (Long) session.getAttribute("usuarioId");
//        return (tecnicoId == null) ? List.of() : reporteServicio.listarPorTecnicoYEstado(tecnicoId, estado);
//    }
//
//    @PostMapping("/tecnico/tomar-reporte")
//    public String tomarReporte(
//            @RequestParam Long reporteId,
//            HttpSession session,
//            RedirectAttributes attrs
//    ) {
//        Long tecnicoId = (Long) session.getAttribute("usuarioId");
//
//        boolean exito = reporteServicio.tomarReporte(reporteId, tecnicoId);
//
//        if (exito) {
//            attrs.addFlashAttribute("exito", "Has tomado el reporte.");
//        } else {
//            attrs.addFlashAttribute("error", "No se pudo tomar el reporte.");
//        }
//
//        return "redirect:/tecnico";
//    }
//
//}
//// FIN NUEVO CÓDIGO LISETH
