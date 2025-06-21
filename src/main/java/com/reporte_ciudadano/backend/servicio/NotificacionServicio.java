package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.Notificacion;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.modelo.Usuario;
import com.reporte_ciudadano.backend.repositorio.NotificacionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionServicio {

    private final NotificacionRepositorio notificacionRepositorio;

    public void crear(Usuario usuario, String mensaje) {
        Notificacion noti = new Notificacion(mensaje, usuario, LocalDateTime.now());
        notificacionRepositorio.save(noti);
    }

    public void crear(Usuario usuario, Reporte reporte, String mensaje) {
        Notificacion noti = new Notificacion(mensaje, usuario, LocalDateTime.now());
        noti.setReporte(reporte);
        notificacionRepositorio.save(noti);
    }

    public List<Notificacion> obtenerPorUsuario(Usuario usuario) {
        return notificacionRepositorio.findByUsuarioOrderByFechaDesc(usuario);
    }

    public void marcarComoLeida(Long notificacionId) {
        Notificacion noti = notificacionRepositorio.findById(notificacionId).orElse(null);
        if (noti != null && !noti.isLeido()) {
            noti.setLeido(true);
            notificacionRepositorio.save(noti);
        }
    }

    public void eliminarTodas(List<Notificacion> notificaciones) {
        notificacionRepositorio.deleteAll(notificaciones);
    }

    public Notificacion obtenerPorId(Long id) {
        return notificacionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    public void guardar(Notificacion notificacion) {
        notificacionRepositorio.save(notificacion);
    }

}
