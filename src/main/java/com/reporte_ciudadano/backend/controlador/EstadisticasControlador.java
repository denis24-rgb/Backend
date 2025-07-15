package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.RolInstitucional;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.servicio.InstitucionServicio;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class EstadisticasControlador {

    @Autowired
    private ReporteServicio reporteServicio;

    @Autowired
    private UsuarioInstitucionalServicio usuarioServicio;

    @Autowired
    private InstitucionServicio institucionServicio;

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(@RequestParam(required = false) Long institucionId,
                                      Principal principal,
                                      Model model) {

        // Obtener usuario logueado
        UsuarioInstitucional usuario = usuarioServicio.obtenerPorCorreoComoUsuario(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean esSuperadmin = usuario.getRol() == RolInstitucional.SUPERADMIN;

        List<Institucion> instituciones = null;
        Institucion institucion;

        if (esSuperadmin) {
            instituciones = institucionServicio.obtenerTodas();

            // Si no seleccionó una institución específica, se toma la primera por defecto (opcional)
            if (institucionId == null && !instituciones.isEmpty()) {
                institucionId = instituciones.get(0).getId();
            }

            institucion = institucionServicio.obtenerPorId(institucionId)
                    .orElseThrow(() -> new RuntimeException("Institución no encontrada"));
        } else {
            institucion = usuario.getInstitucion();
            institucionId = institucion.getId();
        }

        // Establecer color institucional
        String color = institucion.getColorPrimario();
        if (color == null || color.isEmpty()) {
            color = "#0d6efd"; // color por defecto si no está definido
        }

        // Estadísticas por Estado
        long recibidos = reporteServicio.contarPorEstadoEInstitucion("recibido", institucionId);
        long enProceso = reporteServicio.contarPorEstadoEInstitucion("en proceso", institucionId);
        long resueltos = reporteServicio.contarPorEstadoEInstitucion("resuelto", institucionId);
        long cerrados = reporteServicio.contarPorEstadoEInstitucion("cerrado", institucionId);

        // Estadísticas por Tipo de Reporte
        List<String> tipos = reporteServicio.obtenerNombresTiposPorInstitucion(institucionId);
        List<Long> cantidadesTipos = reporteServicio.contarReportesPorTipoEInstitucion(institucionId);

        // Evolución de Reportes por Mes
        List<String> meses = reporteServicio.obtenerMesesConFormatoPorInstitucion(institucionId);
        List<Long> reportesMes = reporteServicio.contarReportesPorMesEInstitucion(institucionId);

        // Para gráfica por día
        List<String> dias = reporteServicio.obtenerUltimosDiasPorInstitucion(institucionId);
        List<Long> reportesDia = reporteServicio.contarReportesPorDiaRecienteEInstitucion(institucionId);

        // Agregar atributos al modelo
        model.addAttribute("recibidos", recibidos);
        model.addAttribute("enProceso", enProceso);
        model.addAttribute("resueltos", resueltos);
        model.addAttribute("cerrados", cerrados);

        model.addAttribute("tipos", tipos);
        model.addAttribute("cantidadesTipos", cantidadesTipos);

        model.addAttribute("meses", meses);
        model.addAttribute("reportesMes", reportesMes);

        model.addAttribute("dias", dias);
        model.addAttribute("reportesDia", reportesDia);

        model.addAttribute("instituciones", instituciones);
        model.addAttribute("institucionId", institucionId);
        model.addAttribute("esSuperadmin", esSuperadmin);
        model.addAttribute("colorInstitucion", color); // color dinámico

        return "estadisticas";
    }

    @GetMapping("/api/estadisticas/rango")
    @ResponseBody
    public Map<String, Object> obtenerEstadisticasPorRango(@RequestParam String inicio,
                                                           @RequestParam String fin,
                                                           @RequestParam Long institucionId) {

        LocalDate fechaInicio = LocalDate.parse(inicio);
        LocalDate fechaFin = LocalDate.parse(fin);

        List<String> dias = reporteServicio.obtenerDiasEnRangoPorInstitucion(fechaInicio, fechaFin, institucionId);
        List<Long> cantidades = reporteServicio.contarReportesPorDiaEnRangoEInstitucion(fechaInicio, fechaFin, institucionId);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("labels", dias);
        respuesta.put("valores", cantidades);

        return respuesta;
    }
}
