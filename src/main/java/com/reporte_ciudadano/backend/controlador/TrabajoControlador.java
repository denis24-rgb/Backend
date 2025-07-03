package com.reporte_ciudadano.backend.controlador;

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

    private static final String CARPETA_TRABAJOS = "/opt/reporte_ciudadano/imagenes-trabajos/";

    @GetMapping("/ver/{nombreArchivo:.+}")
    public ResponseEntity<Resource> verImagenTrabajo(@PathVariable String nombreArchivo) {
        try {
            Path ruta = Paths.get(CARPETA_TRABAJOS + nombreArchivo);
            Resource recurso = new UrlResource(ruta.toUri());

            if (!recurso.exists() || !recurso.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Puedes ajustar según el tipo de imagen real
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(recurso);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
