package com.reporte_ciudadano.backend.controlador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.servicio.AsignacionTecnicoServicio;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@PreAuthorize("hasRole('OPERADOR')")
public class OperadorControlador {

    private final UsuarioInstitucionalServicio usuarioInstitucionalServicio;
    private final ReporteServicio reporteServicio;
    private final AsignacionTecnicoServicio asignacionTecnicoServicio;

    public OperadorControlador(UsuarioInstitucionalServicio usuarioInstitucionalServicio,
                               ReporteServicio reporteServicio,
                               AsignacionTecnicoServicio asignacionTecnicoServicio) {
        this.usuarioInstitucionalServicio = usuarioInstitucionalServicio;
        this.reporteServicio = reporteServicio;
        this.asignacionTecnicoServicio = asignacionTecnicoServicio;
    }

    @GetMapping("/panel/operador")
    public String mostrarPanelOperador(Model model, Principal principal) throws JsonProcessingException {
        UsuarioInstitucional usuario = usuarioInstitucionalServicio.obtenerPorCorreo(principal.getName()).orElse(null);
        if (usuario == null) return "redirect:/login?error";

        Institucion institucion = usuario.getInstitucion();

        List<Reporte> listaReportes = reporteServicio.obtenerConUbicacionPorInstitucion(institucion.getId());
        List<UsuarioInstitucional> tecnicos = usuarioInstitucionalServicio.listarTecnicosPorInstitucion(institucion.getId());

        List<Map<String, Object>> reportesMap = listaReportes.stream().map(r -> {
            Map<String, Object> reporteMap = new HashMap<>();
            reporteMap.put("id", r.getId());
            reporteMap.put("descripcion", r.getDescripcion());
            reporteMap.put("estado", r.getEstado());
            reporteMap.put("fechaReporte", r.getFechaReporte() != null ? r.getFechaReporte().toString() : null);
            reporteMap.put("hora", r.getHora() != null ? r.getHora().toString() : null);
            reporteMap.put("ubicacion", r.getUbicacion());

            if (r.getInstitucion() != null) {
                reporteMap.put("institucion", r.getInstitucion().getNombre());
            }

            if (r.getTipoReporte() != null) {
                Map<String, Object> tipo = new HashMap<>();
                tipo.put("nombre", r.getTipoReporte().getNombre());
                tipo.put("icono", r.getTipoReporte().getIcono());
                reporteMap.put("tipoReporte", tipo);
            }

            asignacionTecnicoServicio.buscarPorReporteId(r.getId()).ifPresent(asignacion -> {
                Map<String, Object> tecnicoMap = new HashMap<>();
                tecnicoMap.put("id", asignacion.getTecnico().getId());
                tecnicoMap.put("nombre", asignacion.getTecnico().getNombre());
                reporteMap.put("tecnico", tecnicoMap);
            });

            return reporteMap;
        }).toList();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String reportesJSON = mapper.writeValueAsString(reportesMap);

        List<Map<String, Object>> tecnicosMap = tecnicos.stream().map(t -> {
            Map<String, Object> tecnicoMap = new HashMap<>();
            tecnicoMap.put("id", t.getId());
            tecnicoMap.put("nombre", t.getNombre());
            return tecnicoMap;
        }).toList();

        int reportesActivos = reporteServicio.contarReportesActivosPorInstitucion(institucion.getId());
        int asignacionesPendientes = reporteServicio.contarAsignacionesPendientesPorInstitucion(institucion.getId());
        int usuariosActivos = usuarioInstitucionalServicio.contarUsuariosActivosPorInstitucion(institucion.getId());
        int totalReportes = reporteServicio.contarTotalReportesPorInstitucion(institucion.getId());

        model.addAttribute("reportesJSON", reportesJSON);
        model.addAttribute("tecnicos", tecnicosMap);
        model.addAttribute("usuario", usuario);
        model.addAttribute("reportesActivos", reportesActivos);
        model.addAttribute("asignacionesPendientes", asignacionesPendientes);
        model.addAttribute("usuariosActivos", usuariosActivos);
        model.addAttribute("totalReportes", totalReportes);

        String color = institucion.getColorPrimario();
        if (color == null || color.isEmpty()) {
            color = "#0d6efd";
        }

        model.addAttribute("colorInstitucion", color);
        model.addAttribute("institucion", institucion);

        return "operador";
    }
}
