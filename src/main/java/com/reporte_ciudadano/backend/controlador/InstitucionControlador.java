package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.dto.InstitucionDTO;
import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.repositorio.InstitucionTipoReporteRepositorio;
import com.reporte_ciudadano.backend.servicio.InstitucionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // ✅ Obtener todas las instituciones
    @GetMapping
    public List<Institucion> listar() {
        return institucionServicio.listarTodas();
    }

    // ✅ Obtener institución por ID
    @GetMapping("/{id}")
    public Optional<Institucion> obtenerPorId(@PathVariable Long id) {
        return institucionServicio.obtenerPorId(id);
    }

    // ✅ Crear nueva institución
    @PostMapping
    public Institucion crear(@RequestBody Institucion institucion) {
        return institucionServicio.guardar(institucion);
    }

    // ✅ Actualizar institución existente
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

    // ✅ Eliminar institución
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        institucionServicio.eliminar(id);
    }

    // ✅ Obtener instituciones por tipo de reporte
    @GetMapping("/por-tipo")
    public ResponseEntity<?> obtenerInstitucionPorTipo(@RequestParam Long tipoId, Principal principal) {
        String correo = principal.getName();
        System.out.println("🔒 Solicitud realizada por: " + correo);

        List<Institucion> instituciones = institucionTipoReporteRepositorio.findInstitucionesPorTipoReporte(tipoId);

        if (instituciones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontraron instituciones para este tipo de reporte");
        }

        List<InstitucionDTO> dtos = instituciones.stream()
                .map(InstitucionDTO::new)
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
