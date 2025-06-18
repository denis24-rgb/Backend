package com.reporte_ciudadano.backend.controlador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
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

    @GetMapping("/")
    public String redirigirInicio() {
        return "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String mostrarInicio(Model model, Principal principal) throws JsonProcessingException {
        // Obtener el usuario actual
        UsuarioInstitucional usuario = usuarioInstitucionalServicio.obtenerPorCorreo(principal.getName()).orElse(null);
        Institucion institucion = usuario.getInstitucion();

        // Obtener reportes con ubicación solo de su institución
        List<Reporte> listaReportes = reporteServicio.obtenerConUbicacionPorInstitucion(institucion.getId());

        // 🔧 Obtener técnicos de la institución
        List<UsuarioInstitucional> tecnicos = usuarioInstitucionalServicio
                .listarTecnicosPorInstitucion(institucion.getId());

        // Convertir a estructura simplificada para el frontend
        List<Map<String, Object>> reportesMap = listaReportes.stream().map(r -> {
            Map<String, Object> reporteMap = new HashMap<>();

            reporteMap.put("id", r.getId());
            reporteMap.put("descripcion", r.getDescripcion());
            reporteMap.put("estado", r.getEstado()); // Ahora es String directamente
            reporteMap.put("fechaReporte", r.getFechaReporte() != null ? r.getFechaReporte().toString() : null);
            reporteMap.put("hora", r.getHora() != null ? r.getHora().toString() : null);
            reporteMap.put("ubicacion", r.getUbicacion());

            // Relacionales, si están mapeadas como @ManyToOne puedes hacer esto:
            if (r.getInstitucion() != null) {
                reporteMap.put("institucion", r.getInstitucion().getNombre());
            }

            if (r.getTipoReporte() != null) {
                Map<String, Object> tipo = new HashMap<>();
                tipo.put("nombre", r.getTipoReporte().getNombre());
                tipo.put("icono", r.getTipoReporte().getIcono());
                reporteMap.put("tipoReporte", tipo);
            }

            return reporteMap;
        }).toList();

        // Convertir lista a JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String reportesJSON = mapper.writeValueAsString(reportesMap);

        // Agregar al modelo para que Thymeleaf lo inyecte
        model.addAttribute("reportesJSON", reportesJSON);
        List<Map<String, Object>> tecnicosMap = tecnicos.stream().map(t -> {
            Map<String, Object> tecnicoMap = new HashMap<>();
            tecnicoMap.put("id", t.getId());
            tecnicoMap.put("nombre", t.getNombre());
            return tecnicoMap;
        }).toList();

        model.addAttribute("tecnicos", tecnicosMap);
        // ✅ ahora sí
        // 👉 Indicadores para las tarjetas del panel
        int reportesActivos = reporteServicio.contarReportesActivosPorInstitucion(institucion.getId());
        int asignacionesPendientes = reporteServicio.contarAsignacionesPendientesPorInstitucion(institucion.getId());
        int usuariosActivos = usuarioInstitucionalServicio.contarUsuariosActivosPorInstitucion(institucion.getId());

        model.addAttribute("reportesActivos", reportesActivos);
        model.addAttribute("asignacionesPendientes", asignacionesPendientes);
        model.addAttribute("usuariosActivos", usuariosActivos);

        return "inicio-admin";
    }
}
