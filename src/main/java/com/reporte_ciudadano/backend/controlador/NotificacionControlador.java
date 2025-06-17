package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.Notificacion;
import com.reporte_ciudadano.backend.modelo.Usuario;
import com.reporte_ciudadano.backend.servicio.NotificacionServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioServicio;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/notificaciones")
@RequiredArgsConstructor
public class NotificacionControlador {

    private final NotificacionServicio notificacionServicio;
    private final UsuarioServicio usuarioServicio;

    @GetMapping
    public List<Notificacion> obtenerNotificaciones(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioServicio.obtenerPorId(usuarioId);
        return notificacionServicio.obtenerPorUsuario(usuario);
    }

    @PutMapping("/{notificacionId}/leida")
    public void marcarComoLeida(@PathVariable Long usuarioId, @PathVariable Long notificacionId) {
        notificacionServicio.marcarComoLeida(notificacionId);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarNotificaciones(
            @PathVariable Long usuarioId,
            @RequestBody List<Long> idsNotificaciones) {

        Usuario usuario = usuarioServicio.obtenerPorId(usuarioId);

        // Eliminar notificaciones que pertenecen al usuario y están en la lista
        List<Notificacion> paraEliminar = notificacionServicio.obtenerPorUsuario(usuario)
                .stream()
                .filter(noti -> idsNotificaciones.contains(noti.getId()))
                .toList();

        if (!paraEliminar.isEmpty()) {
            notificacionServicio.eliminarTodas(paraEliminar);
            return ResponseEntity.ok("✅ Notificaciones eliminadas correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ No se encontraron notificaciones para eliminar");
        }
    }

    @GetMapping("/no-leidas")
    public int contarNoLeidas(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioServicio.obtenerPorId(usuarioId);
        return (int) notificacionServicio.obtenerPorUsuario(usuario).stream()
                .filter(n -> !n.isLeido())
                .count();
    }

}
