package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.TipoAvisoComunitario;
import com.reporte_ciudadano.backend.servicio.TipoAvisoComunitarioServicio;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-aviso-comunitario")
@CrossOrigin(origins = "*")
public class TipoAvisoComunitarioControlador {

    private final TipoAvisoComunitarioServicio servicio;

    public TipoAvisoComunitarioControlador(TipoAvisoComunitarioServicio servicio) {
        this.servicio = servicio;
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
            // Ruta donde se guardará el ícono
            String rutaIcono = "C:/iconos-tipo-reporte/";
            String nombreArchivo = icono.getOriginalFilename();
            Path rutaCompleta = Paths.get(rutaIcono + nombreArchivo);

            // Guardar el archivo físicamente
            Files.write(rutaCompleta, icono.getBytes());

            // Crear y guardar el tipo de avisosw
            TipoAvisoComunitario nuevo = new TipoAvisoComunitario();
            nuevo.setNombre(nombre);
            nuevo.setIcono(nombreArchivo); // Solo el nombre del archivo

            servicio.guardar(nuevo);
            return ResponseEntity.ok("Tipo de aviso creado correctamente");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el ícono");
        }
    }

}
