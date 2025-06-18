package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.Evidencia;
import com.reporte_ciudadano.backend.modelo.HistorialEstado;
import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.modelo.TipoReporte;
import com.reporte_ciudadano.backend.modelo.Usuario;
import com.reporte_ciudadano.backend.repositorio.EvidenciaRepositorio;
import com.reporte_ciudadano.backend.servicio.ReporteServicio;
import com.reporte_ciudadano.backend.dto.ReporteDetalleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteControlador {
    @Autowired
    private EvidenciaRepositorio evidenciaRepositorio;

    private final ReporteServicio servicio;

    public ReporteControlador(ReporteServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/por-usuario/{usuarioId}")
    public ResponseEntity<List<Reporte>> obtenerReportesPorUsuario(@PathVariable Long usuarioId) {
        List<Reporte> reportes = servicio.buscarPorUsuarioId(usuarioId);
        return ResponseEntity.ok(reportes);
    }

    @GetMapping
    public List<Reporte> listar() {
        return servicio.listarTodos();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            // ✅ Imprimir todo el JSON recibido para depuración
            System.out.println("========= JSON RECIBIDO =========");
            body.forEach((clave, valor) -> System.out.println(clave + ": " + valor));
            System.out.println("==================================");

            // Extraer campos del JSON manualmente
            Long usuarioId = Long.valueOf(body.get("usuario_id").toString());
            Long institucionId = Long.valueOf(body.get("institucion_id").toString());
            Long tipoReporteId = Long.valueOf(body.get("tipo_reporte_id").toString());
            String descripcion = body.get("descripcion").toString();
            String ubicacion = body.get("ubicacion").toString();

            // Crear entidades básicas con solo el ID
            Usuario usuario = new Usuario();
            usuario.setId(usuarioId);

            Institucion institucion = new Institucion();
            institucion.setId(institucionId);

            TipoReporte tipoReporte = new TipoReporte();
            tipoReporte.setId(tipoReporteId);

            // Construir objeto Reporte completo
            Reporte reporte = new Reporte();
            reporte.setUsuario(usuario);
            reporte.setInstitucion(institucion);
            reporte.setTipoReporte(tipoReporte);
            reporte.setDescripcion(descripcion);
            reporte.setUbicacion(ubicacion);

            // ✅ Confirmar objeto armado
            System.out.println("OBJETO REPORTE CREADO:");
            System.out.println("Usuario ID: " + usuarioId);
            System.out.println("Institución ID: " + institucionId);
            System.out.println("TipoReporte ID: " + tipoReporteId);
            System.out.println("Descripción: " + descripcion);
            System.out.println("Ubicación: " + ubicacion);

            Reporte creado = servicio.guardar(reporte);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            e.printStackTrace(); // 👈 Muestra stacktrace completo en consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el reporte: " + e.getMessage());
        }
    }

//    @PutMapping("/{id}/estado")
//    public Reporte cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
//        return servicio.cambiarEstado(id, nuevoEstado);
//    }

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
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/{id}/detalle")
    public ResponseEntity<?> obtenerReporteConEvidencia(@PathVariable Long id) {
        Reporte reporte = servicio.buscarPorId(id);
        if (reporte == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reporte no encontrado");
        }

        ReporteDetalleDTO dto = new ReporteDetalleDTO(reporte);

        Evidencia evidencia = evidenciaRepositorio.findTopByReporteIdAndTipoEvidencia(id, "imagen");
        if (evidencia != null && evidencia.getUrl() != null) {
            String ruta = evidencia.getUrl();
            String soloNombre = Paths.get(ruta).getFileName().toString();
            dto.setImagenNombre(soloNombre);

        }

        return ResponseEntity.ok(dto);
    }

}
