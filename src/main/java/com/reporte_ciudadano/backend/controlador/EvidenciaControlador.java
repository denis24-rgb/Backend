package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.configuraciones.RutaProperties;
import com.reporte_ciudadano.backend.modelo.Evidencia;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.repositorio.EvidenciaRepositorio;
import com.reporte_ciudadano.backend.repositorio.ReporteRepositorio;
import com.reporte_ciudadano.backend.servicio.EvidenciaServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.security.Principal;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Path;
import java.nio.file.Paths;
@RestController
@RequestMapping("/api/evidencias")
@CrossOrigin(origins = "*")
public class EvidenciaControlador {

    private final EvidenciaServicio servicio;

    @Autowired
    private RutaProperties ruta; // ✅ Ruta dinámica

    @Autowired
    private EvidenciaRepositorio evidenciaRepositorio;

    @Autowired
    private ReporteRepositorio reporteRepositorio;

    public EvidenciaControlador(EvidenciaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Evidencia> listar() {
        return servicio.listarTodas();
    }

    @PostMapping
    public Evidencia crear(@RequestBody Evidencia evidencia) {
        return servicio.guardar(evidencia);
    }

    @GetMapping("/{id}")
    public Evidencia obtenerPorId(@PathVariable Long id) {
        return servicio.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }

    @PostMapping("/subir")
    public ResponseEntity<?> subirEvidencia(
            @RequestParam("reporte_id") Long reporteId,
            @RequestParam("tipo_evidencia") String tipo,
            @RequestPart("archivo") MultipartFile archivo,
            Principal principal) {

        try {
            if (archivo == null || archivo.isEmpty()) {
                return ResponseEntity.badRequest().body("❌ Archivo no recibido o vacío");
            }

            Reporte reporte = reporteRepositorio.findById(reporteId).orElse(null);
            if (reporte == null) {
                return ResponseEntity.badRequest().body("Reporte no encontrado");
            }

            String uploadDir = ruta.getEvidencias(); // ✅ Ruta por perfil
            File directorio = new File(uploadDir);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            String originalFilename = archivo.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                return ResponseEntity.badRequest().body("Nombre de archivo inválido");
            }

            String nombreArchivo = UUID.randomUUID() + "_" +
                    StringUtils.cleanPath(originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_"));
            String rutaArchivo = uploadDir + nombreArchivo;

            archivo.transferTo(new File(rutaArchivo));

            Evidencia evidencia = new Evidencia();
            evidencia.setReporte(reporte);
            evidencia.setTipoEvidencia(tipo);
            evidencia.setUrl(nombreArchivo);

            evidenciaRepositorio.save(evidencia);

            return ResponseEntity.ok("✅ Evidencia subida con éxito");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al guardar el archivo: " + e.getMessage());
        }
    }

    @GetMapping("/ver/{nombreArchivo:.+}")
    public ResponseEntity<Resource> verEvidencia(@PathVariable String nombreArchivo) {
        try {
            String uploadDir = ruta.getEvidencias(); // ✅ Ruta dinámica
            Path rutaArchivo = Paths.get(uploadDir).resolve(nombreArchivo).normalize();
            Resource recurso = new UrlResource(rutaArchivo.toUri());

            if (!recurso.exists() || !recurso.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                    .contentType(MediaTypeFactory.getMediaType(recurso).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(recurso);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
