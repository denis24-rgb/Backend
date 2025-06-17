package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Notificacion;
import com.reporte_ciudadano.backend.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepositorio extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioOrderByFechaDesc(Usuario usuario);
}
