package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Notificacion;
import com.reporte_ciudadano.backend.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificacionRepositorio extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioOrderByFechaDesc(Usuario usuario);
    @Modifying
    @Query("DELETE FROM Notificacion n WHERE n.reporte.id = :reporteId")
    void deleteByReporteId(@Param("reporteId") Long reporteId);

}
