package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.configuraciones.RutaProperties;
import com.reporte_ciudadano.backend.modelo.TipoAvisoComunitario;
import com.reporte_ciudadano.backend.servicio.TipoAvisoComunitarioServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-aviso-comunitario")
@CrossOrigin(origins = "*")
public class TipoAvisoComunitarioControlador {

    private final TipoAvisoComunitarioServicio servicio;
    private final RutaProperties rutaProperties;

    public TipoAvisoComunitarioControlador(
            TipoAvisoComunitarioServicio servicio,
            RutaProperties rutaProperties
    ) {
        this.servicio = servicio;
        this.rutaProperties = rutaProperties;
    }

    @GetMapping
    public List<TipoAvisoComunitario> listarTodos() {
        return servicio.listarTodos();
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearTipoAviso(
            @RequestParam("nombre") String nombre,
            @RequestParam("icono") MultipartFile icono) {

        try {
            // Obtener ruta dinámica desde application.properties o application.yml
            String rutaIcono = rutaProperties.getIconos(); //
            String nombreArchivo = icono.getOriginalFilename();
            Path rutaCompleta = Paths.get(rutaIcono + nombreArchivo);

            // Guardar archivo en disco
            Files.write(rutaCompleta, icono.getBytes());

            // Guardar en BD solo el nombre del archivo
            TipoAvisoComunitario nuevo = new TipoAvisoComunitario();
            nuevo.setNombre(nombre);
            nuevo.setIcono(nombreArchivo);

            servicio.guardar(nuevo);
            return ResponseEntity.ok("Tipo de aviso creado correctamente");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el ícono");
        }
    }
}