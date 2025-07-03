package com.reporte_ciudadano.backend.controlador;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.*;

@RestController
@RequestMapping("/archivos")
@CrossOrigin(origins = "*") // Importante para que Flutter acceda
public class ArchivoControlador {

    private final Path rutaEvidencias = Paths.get("/opt/reporte_ciudadano/evidencias"); // Ruta VPS

    @GetMapping("/{nombreArchivo:.+}")
    public ResponseEntity<Resource> verArchivo(@PathVariable String nombreArchivo) {
        try {
            Path archivo = rutaEvidencias.resolve(nombreArchivo).normalize();
            Resource recurso = new UrlResource(archivo.toUri());

            if (!recurso.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(archivo);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(recurso);

        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}