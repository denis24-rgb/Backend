package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.dto.NotificacionDTO;
import com.reporte_ciudadano.backend.modelo.Notificacion;
import com.reporte_ciudadano.backend.modelo.Usuario;
import com.reporte_ciudadano.backend.servicio.NotificacionServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioServicio;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/notificaciones")
@RequiredArgsConstructor
public class NotificacionControlador {

    private final NotificacionServicio notificacionServicio;
    private final UsuarioServicio usuarioServicio;

    @GetMapping
    public List<NotificacionDTO> obtenerNotificaciones(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioServicio.obtenerPorId(usuarioId);

        // Convertir las notificaciones en DTOs
        return notificacionServicio.obtenerPorUsuario(usuario)
                .stream()
                .map(NotificacionDTO::new)
                .toList();
    }

    @PutMapping("/{notificacionId}/leida")
    public ResponseEntity<String> marcarComoLeida(
            @PathVariable Long usuarioId,
            @PathVariable Long notificacionId) {

        Usuario usuario = usuarioServicio.obtenerPorId(usuarioId);

        Notificacion notificacion = notificacionServicio.obtenerPorId(notificacionId);

        if (!notificacion.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("❌ No tienes permiso para modificar esta notificación");
        }

        notificacion.setLeido(true);
        notificacionServicio.guardar(notificacion);

        return ResponseEntity.ok("✅ Notificación marcada como leída");
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarNotificaciones(
            @RequestBody List<Long> idsNotificaciones,
            Principal principal) {

        // Obtener usuario desde el correo que viene en el token
        Usuario usuario = usuarioServicio.obtenerPorCorreo(principal.getName());

        // Filtrar solo notificaciones que pertenecen al usuario y están en la lista
        List<Notificacion> paraEliminar = notificacionServicio.obtenerPorUsuario(usuario)
                .stream()
                .filter(noti -> idsNotificaciones.contains(noti.getId()))
                .toList();

        if (!paraEliminar.isEmpty()) {
            notificacionServicio.eliminarTodas(paraEliminar);
            return ResponseEntity.ok("✅ Notificaciones eliminadas correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("❌ No se encontraron notificaciones para eliminar");
        }
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<Integer> contarNoLeidas(Principal principal) {
        Usuario usuario = usuarioServicio.obtenerPorCorreo(principal.getName());

        int cantidad = (int) notificacionServicio.obtenerPorUsuario(usuario).stream()
                .filter(n -> !n.isLeido())
                .count();

        return ResponseEntity.ok(cantidad);
    }

}
