package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.repositorio.InstitucionTipoReporteRepositorio;
import com.reporte_ciudadano.backend.servicio.InstitucionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.Thymeleaf;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<?> obtenerInstitucionPorTipo(@RequestParam Long tipoId) {
        List<Institucion> instituciones = institucionTipoReporteRepositorio.findInstitucionesPorTipoReporteId(tipoId);

        if (instituciones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontraron instituciones para este tipo de reporte");
        }

        // Puedes retornar la primera si es 1 a 1, o toda la lista si permite varias
        return ResponseEntity.ok(instituciones.get(0)); // o simplemente: return ResponseEntity.ok(instituciones);
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
