package com.reporte_ciudadano.backend.controlador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.modelo.RolInstitucional;
import com.reporte_ciudadano.backend.servicio.AsignacionTecnicoServicio;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class InicioControlador {

    @Autowired
    private UsuarioInstitucionalServicio usuarioInstitucionalServicio;

    @Autowired
    private ReporteServicio reporteServicio;
    @Autowired
    private AsignacionTecnicoServicio asignacionTecnicoServicio;

    @GetMapping("/")
    public String redirigirInicio() {
        return "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String mostrarInicio(Model model, Principal principal) throws JsonProcessingException {
        UsuarioInstitucional usuario = usuarioInstitucionalServicio.obtenerPorCorreo(principal.getName()).orElse(null);
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

            // Técnico asignado
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

        model.addAttribute("reportesJSON", reportesJSON);
        model.addAttribute("tecnicos", tecnicosMap);
        model.addAttribute("usuario", usuario);
        model.addAttribute("reportesActivos", reportesActivos);
        model.addAttribute("asignacionesPendientes", asignacionesPendientes);
        model.addAttribute("usuariosActivos", usuariosActivos);
        String color = institucion.getColorPrimario();
        if (color == null || color.isEmpty()) {
            color = "#0d6efd";
        }
        model.addAttribute("colorInstitucion", color);

        model.addAttribute("institucion", institucion);
        return "inicio-admin";
    }



    @GetMapping("/redirigir")
    public String redirigirSegunRol(Principal principal, HttpSession session) {
        UsuarioInstitucional usuario = usuarioInstitucionalServicio.obtenerPorCorreo(principal.getName()).orElse(null);
        if (usuario == null) return "redirect:/login?error";

        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioRol", usuario.getRol().name());

        return switch (usuario.getRol()) {
            case ADMINISTRADOR -> "redirect:/inicio";
            case OPERADOR -> "redirect:/panel/operador";
            case TECNICO -> "redirect:/tecnico";
            case SUPERADMIN -> "redirect:/panel/superadmin";
            default -> "redirect:/login?error";
        };
    }


}
