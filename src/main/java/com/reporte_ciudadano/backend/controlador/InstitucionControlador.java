package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.dto.InstitucionDTO;
import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.repositorio.InstitucionTipoReporteRepositorio;
import com.reporte_ciudadano.backend.servicio.InstitucionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.Thymeleaf;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/instituciones")
@CrossOrigin(origins = "*")
public class InstitucionControlador {

    @Autowired
    private InstitucionServicio institucionServicio;

    @Autowired
    private InstitucionTipoReporteRepositorio institucionTipoReporteRepositorio;

    // ✅ API REST para obtener todas las instituciones
    @GetMapping
    public List<Institucion> listar() {
        return institucionServicio.listarTodas();
    }

    @GetMapping("/{id}")
    public Optional<Institucion> obtenerPorId(@PathVariable Long id) {
        return institucionServicio.obtenerPorId(id);
    }

    @PostMapping
    public Institucion crear(@RequestBody Institucion institucion) {
        return institucionServicio.guardar(institucion);
    }

    @PutMapping("/{id}")
    public Institucion actualizar(@PathVariable Long id, @RequestBody Institucion nuevaInstitucion) {

        return institucionServicio.obtenerPorId(id).map(institucion -> {
            institucion.setNombre(nuevaInstitucion.getNombre());
            institucion.setCorreoInstitucional(nuevaInstitucion.getCorreoInstitucional());
            institucion.setZona(nuevaInstitucion.getZona());
            institucion.setActivo(nuevaInstitucion.isActivo());
            return institucionServicio.guardar(institucion);
        }).orElseGet(() -> {
            nuevaInstitucion.setId(id);
            return institucionServicio.guardar(nuevaInstitucion);

        });
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        institucionServicio.eliminar(id);
    }

    @GetMapping("/por-tipo")
    public ResponseEntity<?> obtenerInstitucionPorTipo(@RequestParam Long tipoId, Principal principal) {
        // ✅ Extraer correo desde el token JWT
        String correo = principal.getName();
        System.out.println("🔒 Solicitud realizada por: " + correo);

        // 🔎 Buscar instituciones asociadas al tipo de reporte
        List<Institucion> instituciones = institucionTipoReporteRepositorio.findInstitucionesPorTipoReporte(tipoId);

        if (instituciones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontraron instituciones para este tipo de reporte");
        }

        // ✅ Convertir a DTO
        List<InstitucionDTO> dtos = instituciones.stream()
                .map(InstitucionDTO::new)
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // / ✅ Vista con Thymeleaf para listar instituciones
    @GetMapping("/panel/superadmin")
    public String listarInstituciones(Model model) {
        model.addAttribute("instituciones", institucionServicio.listarTodas());
        return "instituciones"; // templates/instituciones.html
    }

    // Mostrar formulario de nueva institución
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("institucion", new Institucion());
        return "formulario-institucion";
    }

    // Guardar institución
    @PostMapping("/guardar")
    public String guardarInstitucion(@ModelAttribute Institucion institucion) {
        institucionServicio.guardar(institucion);
        return "redirect:/instituciones";
    }

    // Eliminar institución
    @GetMapping("/eliminar/{id}")
    public String eliminarInstitucion(@PathVariable Long id) {
        institucionServicio.eliminar(id);
        return "redirect:/instituciones";
    }
}
