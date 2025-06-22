package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.Evidencia;
import com.reporte_ciudadano.backend.modelo.HistorialEstado;
import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.modelo.TipoReporte;
import com.reporte_ciudadano.backend.modelo.Usuario;
import com.reporte_ciudadano.backend.repositorio.EvidenciaRepositorio;
import com.reporte_ciudadano.backend.repositorio.UsuarioRepositorio;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.dto.HistorialDTO;
import com.reporte_ciudadano.backend.dto.ReporteDetalleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteControlador {
    @Autowired
    private EvidenciaRepositorio evidenciaRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    private final ReporteServicio servicio;

    public ReporteControlador(ReporteServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/por-usuario/{usuarioId}")
    public ResponseEntity<List<ReporteDetalleDTO>> obtenerReportesPorUsuario(@PathVariable Long usuarioId) {
        List<Reporte> reportes = servicio.buscarPorUsuarioId(usuarioId);
        List<ReporteDetalleDTO> dtoList = reportes.stream()
                .map(ReporteDetalleDTO::new)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        List<Reporte> reportes = servicio.listarTodos();
        List<ReporteDetalleDTO> dtos = reportes.stream()
                .map(ReporteDetalleDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body, Principal principal) {
        try {

            String correo = principal.getName(); // JWT te da el correo

            Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Long institucionId = Long.valueOf(body.get("institucion_id").toString());
            Long tipoReporteId = Long.valueOf(body.get("tipo_reporte_id").toString());
            String descripcion = body.get("descripcion").toString();
            String ubicacion = body.get("ubicacion").toString();

            Institucion institucion = new Institucion();
            institucion.setId(institucionId);

            TipoReporte tipoReporte = new TipoReporte();
            tipoReporte.setId(tipoReporteId);

            Reporte reporte = new Reporte();
            reporte.setUsuario(usuario); // ✅ usuario completo
            reporte.setInstitucion(institucion);
            reporte.setTipoReporte(tipoReporte);
            reporte.setDescripcion(descripcion);
            reporte.setUbicacion(ubicacion);

            // ✅ Guardar y retornar
            Reporte creado = servicio.guardar(reporte);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("id", creado.getId()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el reporte: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    public Reporte cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        return servicio.cambiarEstado(id, nuevoEstado);
    }

    @GetMapping("/{id}")
    public Reporte obtenerPorId(@PathVariable Long id) {
        return servicio.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<?> obtenerHistorial(@PathVariable Long id) {
        List<HistorialEstado> historial = servicio.obtenerHistorialPorReporte(id);
        if (historial.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró historial para el reporte");
        }

        List<HistorialDTO> historialDTO = historial.stream()
                .map(HistorialDTO::new)
                .toList();

        return ResponseEntity.ok(historialDTO);
    }

    @GetMapping("/{id}/detalle")
    @PreAuthorize("isAuthenticated()") // 🔐 Asegura que solo usuarios autenticados accedan
    public ResponseEntity<?> obtenerReporteConEvidencia(@PathVariable Long id, Authentication authentication) {
        String correo = authentication.getName(); // ← Se extrae desde el token JWT
        System.out.println("📌 Acceso desde: " + correo);

        Reporte reporte = servicio.buscarPorId(id);
        if (reporte == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reporte no encontrado");
        }

        ReporteDetalleDTO dto = new ReporteDetalleDTO(reporte);
        // Mantener el icono (ya viene del tipoReporte, por ejemplo)
        dto.setImagenNombre(reporte.getTipoReporte().getIcono());

        Evidencia evidencia = evidenciaRepositorio.findTopByReporteIdAndTipoEvidencia(id, "imagen");
        if (evidencia != null && evidencia.getUrl() != null) {
            String ruta = evidencia.getUrl();
            String soloNombre = Paths.get(ruta).getFileName().toString();
            dto.setEvidenciaNombre(soloNombre);
        }

        return ResponseEntity.ok(dto);
    }

}
