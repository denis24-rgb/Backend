package com.reporte_ciudadano.backend.controlador;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/trabajos")
@CrossOrigin(origins = "*")
public class TrabajoControlador {

    @Value("${ruta.trabajos}")
    private String rutaTrabajos;

    @GetMapping("/ver/{nombreArchivo:.+}")
    public ResponseEntity<Resource> verImagenTrabajo(@PathVariable String nombreArchivo) {
        try {
            Path ruta = Paths.get(rutaTrabajos).resolve(nombreArchivo);
            Resource recurso = new UrlResource(ruta.toUri());

            if (!recurso.exists() || !recurso.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Detectar tipo MIME automáticamente
            MediaType mediaType = MediaType.IMAGE_JPEG;
            if (nombreArchivo.toLowerCase().endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                    .contentType(mediaType)
                    .body(recurso);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
