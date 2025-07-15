package com.reporte_ciudadano.backend.controlador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.reporte_ciudadano.backend.dto.FormularioInstitucionAdmin;
import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.repositorio.InstitucionRepositorio;
import com.reporte_ciudadano.backend.servicio.InstitucionServicio;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/panel/superadmin")
@PreAuthorize("hasRole('SUPERADMIN')")
public class SuperadminControlador {

    @Autowired
    private InstitucionServicio institucionServicio;

    @Autowired
    private UsuarioInstitucionalServicio usuarioServicio;

    @Autowired
    private ReporteServicio reporteServicio;

    @Autowired
    private InstitucionRepositorio institucionRepo;

    @GetMapping
    public String mostrarPanel(Model model) throws JsonProcessingException {
        // Formulario para nueva institución
        FormularioInstitucionAdmin form = new FormularioInstitucionAdmin();
        form.setInstitucion(new Institucion());
        form.setAdmin(new UsuarioInstitucional());
        model.addAttribute("formulario", form);
        model.addAttribute("institucionesLista", institucionRepo.findAll());

        // Estadísticas
        model.addAttribute("instituciones", institucionServicio.contarInstituciones());
        model.addAttribute("usuarios", usuarioServicio.contarUsuarios());
        model.addAttribute("reportes", reporteServicio.contarReportes());

        // Reportes con ubicación
        List<Reporte> listaReportes = reporteServicio.obtenerConUbicacion();

        // Mapa reducido para JS
        List<Map<String, Object>> reportesMap = listaReportes.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", r.getId());
            m.put("descripcion", r.getDescripcion());
            m.put("estado", r.getEstado());
            m.put("fechaReporte", r.getFechaReporte() != null ? r.getFechaReporte().toString() : null);
            m.put("hora", r.getHora() != null ? r.getHora().toString() : null);
            m.put("ubicacion", r.getUbicacion());
            if (r.getInstitucion() != null) m.put("institucion", r.getInstitucion().getNombre());
            if (r.getTipoReporte() != null) {
                Map<String, Object> tipo = new HashMap<>();
                tipo.put("nombre", r.getTipoReporte().getNombre());
                tipo.put("icono", r.getTipoReporte().getIcono());
                m.put("tipoReporte", tipo);
            }
            return m;
        }).toList();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String reportesJSON = mapper.writeValueAsString(reportesMap);

        model.addAttribute("reportesJSON", reportesJSON);

        return "panel/superadmin";
    }

    @GetMapping("/usuarios")
    public String mostrarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioServicio.listarTodos());
        return "usuarios";
    }

    @GetMapping("/reportes")
    public String verReportes(Model model) {
        List<Reporte> reportes = reporteServicio.listarTodos();
        model.addAttribute("reportes", reportes);
        return "reportes";
    }

}
