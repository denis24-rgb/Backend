package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.dto.ActualizacionTecnico;
import com.reporte_ciudadano.backend.modelo.AsignacionTecnico;
import com.reporte_ciudadano.backend.modelo.Evidencia;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.servicio.AsignacionTecnicoServicio;
import com.reporte_ciudadano.backend.servicio.EvidenciaServicio;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class TecnicoControlador {
    private final ReporteServicio reporteServicio;
    private final UsuarioInstitucionalServicio usuarioServicio;
    private final EvidenciaServicio evidenciaServicio;


    @Autowired
    private AsignacionTecnicoServicio asignacionTecnicoServicio;

    @GetMapping("/tecnico")
    public String vistaTecnico(
            @RequestParam(name = "estado", required = false) String estado,
            Model model,
            HttpSession session) {

        Long tecnicoId = (Long) session.getAttribute("usuarioId");
        String rol = (String) session.getAttribute("usuarioRol");

        if (tecnicoId == null || !"TECNICO".equals(rol)) {
            return "redirect:/login";
        }

        Optional<UsuarioInstitucional> tecnicoOptional = usuarioServicio.obtenerPorId(tecnicoId);
        if (tecnicoOptional.isEmpty()) return "redirect:/login";

        UsuarioInstitucional tecnico = tecnicoOptional.get();

        List<AsignacionTecnico> asignaciones = (estado != null && !estado.isBlank())
                ? asignacionTecnicoServicio.listarPorAsignacionYEstado(tecnicoId, estado)
                : asignacionTecnicoServicio.listarPorTecnico(tecnicoId);

        List<Map<String, Object>> reportesMap = asignaciones.stream().map(asig -> {
            Reporte r = asig.getReporte();
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("descripcion", r.getDescripcion());
            map.put("ubicacion", r.getUbicacion());
            map.put("fechaReporte", r.getFechaReporte());
            map.put("estado", r.getEstado());
            return map;
        }).toList();

        long totalPendientes = asignacionTecnicoServicio.contarPendientes(tecnicoId);
        long totalProceso = asignacionTecnicoServicio.contarEnProceso(tecnicoId);
        long totalFinalizado = asignacionTecnicoServicio.contarResueltos(tecnicoId);
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("reportes", reportesMap);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("totalPendientes", totalPendientes);
        model.addAttribute("totalProceso", totalProceso);
        model.addAttribute("totalFinalizado", totalFinalizado);

        return "tecnico";
    }

    @PostMapping("/tecnico/tomar-reporte")
    public String tomarReporte(
            @RequestParam Long reporteId,
            HttpSession session,
            RedirectAttributes attrs) {

        Long tecnicoId = (Long) session.getAttribute("usuarioId");

        boolean exito = asignacionTecnicoServicio.tomarReporte(reporteId, tecnicoId);

        if (exito) {
            attrs.addFlashAttribute("exito", "Has tomado el reporte.");
        } else {
            attrs.addFlashAttribute("error", "No se pudo tomar el reporte.");
        }

        return "redirect:/tecnico";
    }

    @PostMapping("/tecnico/actualizar-reporte")
    @ResponseBody
    public ResponseEntity<String> actualizarReporteTecnico(
            @RequestParam("reporteId") Long reporteId,
            @RequestParam("comentario") String comentario,
            @RequestParam(value = "imagenTrabajo", required = false) MultipartFile imagenTrabajo) {

        try {
            String nombreImagen = null;

            if (imagenTrabajo != null && !imagenTrabajo.isEmpty()) {
                String nombreUnico = UUID.randomUUID() + "_" + imagenTrabajo.getOriginalFilename();
                String rutaCarpeta = "C:/imagenes-trabajos/"; // O la carpeta que uses en tu servidor
                imagenTrabajo.transferTo(new java.io.File(rutaCarpeta + nombreUnico));
                nombreImagen = nombreUnico;
            }

            boolean actualizado = asignacionTecnicoServicio.finalizarReportePorTecnico(reporteId, comentario, nombreImagen);
            return ResponseEntity.ok(actualizado ? "ok" : "error");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }


    @GetMapping("/tecnico/reportes")
    @ResponseBody
    public List<Map<String, Object>> obtenerReportesPorEstado(@RequestParam String estado, HttpSession session) {
        Long tecnicoId = (Long) session.getAttribute("usuarioId");

        return asignacionTecnicoServicio.listarPorAsignacionYEstado(tecnicoId, estado).stream().map(asig -> {
            Reporte r = asig.getReporte();

            Evidencia evidencia = evidenciaServicio.buscarPrimeraImagenPorReporte(r.getId());
            String urlImagen = evidencia != null ? evidencia.getUrl() : null;

            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("descripcion", r.getDescripcion());
            map.put("ubicacion", r.getUbicacion());
            map.put("fechaReporte", r.getFechaReporte());
            map.put("estado", r.getEstado());

            Map<String, Object> tipoReporteMap = new HashMap<>();
            tipoReporteMap.put("nombre", r.getTipoReporte().getNombre());
            tipoReporteMap.put("icono", r.getTipoReporte().getIcono());
            map.put("tipoReporte", tipoReporteMap);

            map.put("fechaAsignacion", asig.getFechaAsignacion());
            map.put("asignadoPor", asig.getAsignador() != null ? asig.getAsignador().getNombre() : "Sin asignador");
            map.put("imagen", urlImagen);
            map.put("imagenTrabajo", asig.getImagenTrabajo()); // imagen del trabajo realizado
            return map;
        }).toList();
    }


    @GetMapping("/tecnico/reportes/contar")
    @ResponseBody
    public Map<String, Long> contarReportesPorEstado(HttpSession session) {
        Long tecnicoId = (Long) session.getAttribute("usuarioId");

        Map<String, Long> conteos = new HashMap<>();
        conteos.put("recibido", asignacionTecnicoServicio.contarPendientes(tecnicoId));
        conteos.put("en proceso", asignacionTecnicoServicio.contarEnProceso(tecnicoId));
        conteos.put("resuelto", asignacionTecnicoServicio.contarResueltos(tecnicoId));

        return conteos;
    }

    @GetMapping("/tecnico/filtrar")
    @ResponseBody
    public List<Map<String, Object>> filtrarReportesPorEstado(@RequestParam String estado, HttpSession session) {
        Long tecnicoId = (Long) session.getAttribute("usuarioId");

        if (tecnicoId == null) return List.of();

        return asignacionTecnicoServicio.listarPorAsignacionYEstado(tecnicoId, estado).stream().map(asig -> {
            Reporte r = asig.getReporte();
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("descripcion", r.getDescripcion());
            map.put("ubicacion", r.getUbicacion());
            map.put("fechaReporte", r.getFechaReporte());
            map.put("estado", r.getEstado());
            return map;
        }).toList();
    }
}
