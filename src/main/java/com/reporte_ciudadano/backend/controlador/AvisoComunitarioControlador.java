package com.reporte_ciudadano.backend.controlador;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.reporte_ciudadano.backend.modelo.AvisoComunitario;
import com.reporte_ciudadano.backend.modelo.Notificacion;
import com.reporte_ciudadano.backend.modelo.Usuario;
import com.reporte_ciudadano.backend.repositorio.NotificacionRepositorio;
import com.reporte_ciudadano.backend.repositorio.UsuarioRepositorio;
import com.reporte_ciudadano.backend.servicio.AvisoComunitarioServicio;

@RestController
@RequestMapping("/api/avisos")
public class AvisoComunitarioControlador {

    @Autowired
    private AvisoComunitarioServicio servicio;

    @Autowired
    private UsuarioRepositorio usuarioRepo;

    @Autowired
    private NotificacionRepositorio notificacionRepositorio;

    private final String rutaImagenes = "src/main/resources/static/imagenes/avisos/";

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crearAviso(
            @RequestParam("usuario_id") Long usuarioId,
            @RequestParam(value = "ubicacion", required = false) String ubicacion,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try {
            if (usuarioId == null) {
                return ResponseEntity.badRequest().body("usuario_id es obligatorio.");
            }

            System.out.println("🧪 usuarioId recibido: " + usuarioId);

            Usuario usuario = usuarioRepo.findById(usuarioId).orElse(null);

            if (usuario == null) {
                System.out.println("❌ Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado.");
            }

            System.out.println("✅ Usuario encontrado: " + usuario.getCorreo());

            AvisoComunitario aviso = new AvisoComunitario();
            aviso.setDescripcion(descripcion);
            aviso.setUsuario(usuario);

            if (imagen != null && !imagen.isEmpty()) {
                String nombreArchivo = UUID.randomUUID() + "_" + imagen.getOriginalFilename();
                Path ruta = Paths.get(rutaImagenes + nombreArchivo);
                Files.createDirectories(ruta.getParent());
                Files.write(ruta, imagen.getBytes());
                aviso.setImagenUrl("/imagenes/avisos/" + nombreArchivo);
            }
            if (ubicacion != null && !ubicacion.isBlank()) {
                aviso.setUbicacion(ubicacion);
            }

            aviso.setFechaCreacion(LocalDateTime.now());
            servicio.guardar(aviso); // guardar primero el aviso

            // ✅ Notificación para el usuario
            Notificacion notificacion = new Notificacion(
                    "📢 Tu aviso fue publicado correctamente.\n📝 Descripción: " + aviso.getDescripcion(),
                    aviso.getUsuario(),
                    LocalDateTime.now());

            notificacionRepositorio.save(notificacion);

            // ✅ Ahora sí, devolver respuesta
            return ResponseEntity.ok(aviso);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar aviso: " + e.getMessage());
        }

    }

    @GetMapping
    public List<AvisoComunitario> listarAvisos() {
        return servicio.listarVigentes();
    }
}
