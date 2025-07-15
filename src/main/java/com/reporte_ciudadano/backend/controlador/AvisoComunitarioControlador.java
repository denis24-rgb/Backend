package com.reporte_ciudadano.backend.controlador;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.reporte_ciudadano.backend.configuraciones.RutaProperties;
import com.reporte_ciudadano.backend.modelo.AvisoComunitario;
import com.reporte_ciudadano.backend.modelo.Notificacion;
import com.reporte_ciudadano.backend.modelo.TipoAvisoComunitario;
import com.reporte_ciudadano.backend.modelo.Usuario;
import com.reporte_ciudadano.backend.repositorio.NotificacionRepositorio;
import com.reporte_ciudadano.backend.repositorio.TipoAvisoComunitarioRepositorio;
import com.reporte_ciudadano.backend.repositorio.UsuarioRepositorio;
import com.reporte_ciudadano.backend.servicio.AvisoComunitarioServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/avisos")
public class AvisoComunitarioControlador {

    @Autowired
    private AvisoComunitarioServicio servicio;

    @Autowired
    private UsuarioRepositorio usuarioRepo;

    @Autowired
    private TipoAvisoComunitarioRepositorio tipoAvisoRepo;

    @Autowired
    private NotificacionRepositorio notificacionRepositorio;

    private final RutaProperties rutaProperties;

    @Autowired
    public AvisoComunitarioControlador(RutaProperties rutaProperties) {
        this.rutaProperties = rutaProperties;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crearAviso(
            Principal principal,
            @RequestParam(value = "ubicacion", required = false) String ubicacion,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("tipoAvisoId") Long tipoAvisoId,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try {
            String correo = principal.getName();
            System.out.println("📨 Correo extraído del token: " + correo);

            Usuario usuario = usuarioRepo.findByCorreo(correo).orElse(null);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado.");
            }

            AvisoComunitario aviso = new AvisoComunitario();
            aviso.setDescripcion(descripcion);
            aviso.setUsuario(usuario);

            TipoAvisoComunitario tipo = tipoAvisoRepo.findById(tipoAvisoId).orElse(null);
            if (tipo == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo de aviso no encontrado.");
            }
            aviso.setTipoAviso(tipo);

            if (imagen != null && !imagen.isEmpty()) {
                String nombreArchivo = UUID.randomUUID() + "_" + imagen.getOriginalFilename();
                String rutaBase = rutaProperties.getAvisos(); // ← Ruta dinámica
                Path ruta = Paths.get(rutaBase + nombreArchivo);
                Files.createDirectories(ruta.getParent());
                Files.write(ruta, imagen.getBytes());

                // URL pública de acceso
                aviso.setImagenUrl("/imagenes/avisos/" + nombreArchivo);
            }

            if (ubicacion != null && !ubicacion.isBlank()) {
                aviso.setUbicacion(ubicacion);
            }

            aviso.setFechaCreacion(LocalDateTime.now());
            aviso.setFechaEliminacion(LocalDateTime.now().plusDays(15));
            servicio.guardar(aviso);

            Notificacion notificacion = new Notificacion(
                    "📢 Tu aviso fue publicado correctamente.\n📝 Descripción: " + aviso.getDescripcion(),
                    usuario,
                    LocalDateTime.now());

            notificacionRepositorio.save(notificacion);

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
