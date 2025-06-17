package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.Usuario;
import com.reporte_ciudadano.backend.servicio.EmailService;
import com.reporte_ciudadano.backend.servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioServicio.listarUsuarios();
    }

    @GetMapping("/{id}")
    public Optional<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioServicio.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioServicio.eliminarUsuario(id);
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarCorreo(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        try {
            usuarioServicio.crearUsuarioConToken(correo);
            return ResponseEntity.ok("Correo de verificación enviado a " + correo);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/confirmar")
    public ModelAndView confirmarCorreo(@RequestParam("token") String token) {
        System.out.println("Token recibido para confirmar: " + token);

        if (usuarioServicio.confirmarUsuario(token)) {
            Optional<Usuario> usuarioOpt = usuarioServicio.buscarPorToken(token);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                ModelAndView modelAndView = new ModelAndView("confirmacion");
                modelAndView.addObject("correo", usuario.getCorreo()); // <-- Aquí pasamos el correo
                return modelAndView;
            }
        }
        return new ModelAndView("error404");
    }

    @PostMapping("/completar")
    public ResponseEntity<?> completarDatos(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        String nombre = request.get("nombre");
        String telefono = request.get("telefono");
        String direccion = request.get("direccion");

        Usuario usuario = usuarioServicio.completarDatos(correo, nombre, telefono, direccion);
        Map<String, Object> response = new HashMap<>();
        response.put("correo", usuario.getCorreo());
        response.put("tokenSesion", usuario.getTokenSesion());
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/existe")
    public ResponseEntity<?> verificarCorreo(@RequestParam("correo") String correo) {
        Optional<Usuario> usuarioOpt = usuarioServicio.buscarPorCorreo(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.isConfirmado()) {
                Map<String, Object> response = new HashMap<>();
                response.put("confirmado", true);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("confirmado", false);
                return ResponseEntity.ok(response);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    @PostMapping("/login-dispositivo")
    public ResponseEntity<?> loginDesdeOtroDispositivo(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");

        Optional<Usuario> usuarioOpt = usuarioServicio.buscarPorCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Correo no encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        if (!usuario.isConfirmado()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Correo no confirmado.");
        }

        // Generar nuevo token
        String nuevoToken = UUID.randomUUID().toString();
        usuario.setTokenVerificacion(nuevoToken);
        usuarioServicio.guardarUsuario(usuario);

        // Enviar correo de verificación
        emailService.enviarCorreoLoginNuevoDispositivo(correo, nuevoToken);

        // ✅ Aquí devolvemos los datos necesarios al frontend
        return ResponseEntity.ok(Map.of(
                "id", usuario.getId(),
                "correo", usuario.getCorreo()));
    }

    @GetMapping("/confirmar-login")
    public ModelAndView confirmarLoginDesdeOtroDispositivo(@RequestParam("token") String token) {
        Optional<Usuario> usuarioOpt = usuarioServicio.confirmarLoginDesdeToken(token);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            ModelAndView view = new ModelAndView("nuevo-dispositivo-confirmado");
            view.addObject("correo", usuario.getCorreo());
            view.addObject("tokenSesion", usuario.getTokenSesion()); // ENVIAR TOKEN
            return view;
        }

        return new ModelAndView("error404");
    }

    @GetMapping("/token-sesion")
    public ResponseEntity<?> obtenerTokenSesion(@RequestParam("correo") String correo) {
        Optional<Usuario> usuarioOpt = usuarioServicio.buscarPorCorreo(correo);
        if (usuarioOpt.isPresent()) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("tokenSesion", usuarioOpt.get().getTokenSesion());
            return ResponseEntity.ok(respuesta);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
    }

    @GetMapping("/por-correo")
    public ResponseEntity<?> obtenerUsuarioPorCorreo(@RequestParam("correo") String correo) {
        Optional<Usuario> usuarioOpt = usuarioServicio.buscarPorCorreo(correo);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", usuario.getId());
            datos.put("correo", usuario.getCorreo());
            return ResponseEntity.ok(datos);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario datosActualizados) {
        Optional<Usuario> usuarioOpt = usuarioServicio.buscarPorId(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setNombre(datosActualizados.getNombre());
            usuario.setTelefono(datosActualizados.getTelefono());
            usuario.setDireccion(datosActualizados.getDireccion());
            usuarioServicio.guardarUsuario(usuario);
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
    }

}
