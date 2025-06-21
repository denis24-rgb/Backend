package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.Evidencia;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.repositorio.EvidenciaRepositorio;
import com.reporte_ciudadano.backend.repositorio.ReporteRepositorio;
import com.reporte_ciudadano.backend.servicio.EvidenciaServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.security.Principal; // ⬅️ importante

@RestController
@RequestMapping("/api/evidencias")
@CrossOrigin(origins = "*")
public class EvidenciaControlador {

    private final EvidenciaServicio servicio;

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

    private static final String UPLOAD_DIR = "C:/evidencias/";

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

            String correo = principal.getName();
            System.out.println("Usuario autenticado: " + correo);

            File directorio = new File(UPLOAD_DIR);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            String originalFilename = archivo.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                return ResponseEntity.badRequest().body("Nombre de archivo inválido");
            }

            String nombreArchivo = UUID.randomUUID() + "_" +
                    StringUtils.cleanPath(originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_"));
            String rutaArchivo = UPLOAD_DIR + nombreArchivo;

            archivo.transferTo(new File(rutaArchivo));

            Evidencia evidencia = new Evidencia();
            evidencia.setReporte(reporte);
            evidencia.setTipoEvidencia(tipo);
            evidencia.setUrl(nombreArchivo); // Guardar solo el nombre del archivo

            evidenciaRepositorio.save(evidencia);

            return ResponseEntity.ok("✅ Evidencia subida con éxito");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al guardar el archivo: " + e.getMessage());
        }
    }

}
