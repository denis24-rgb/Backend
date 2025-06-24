package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.AsignacionTecnico;
import com.reporte_ciudadano.backend.modelo.HistorialEstado;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.repositorio.AsignacionTecnicoRepositorio;
import com.reporte_ciudadano.backend.repositorio.HistorialEstadoRepositorio;
import com.reporte_ciudadano.backend.repositorio.ReporteRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AsignacionTecnicoServicio {

    @Autowired
    private ReporteRepositorio reporteRepo;

    @Autowired
    private AsignacionTecnicoRepositorio asignacionRepo;

    @Autowired
    private HistorialEstadoRepositorio historialRepo;

    @Autowired
    private NotificacionServicio notificacionServicio;

    public List<AsignacionTecnico> listarTodas() {
        return asignacionRepo.findAll();
    }

    public Optional<AsignacionTecnico> obtenerPorId(Long id) {
        return asignacionRepo.findById(id);
    }

    public List<AsignacionTecnico> listarPorTecnico(Long tecnicoId) {
        return asignacionRepo.findByTecnicoId(tecnicoId);
    }

    public AsignacionTecnico guardar(AsignacionTecnico asignacion) {
        return asignacionRepo.save(asignacion);
    }

    public void eliminar(Long id) {
        asignacionRepo.deleteById(id);
    }

    @Transactional
    public boolean tomarReporte(Long reporteId, Long tecnicoId) {
        Optional<AsignacionTecnico> asignacionOpt = asignacionRepo.findByReporteIdAndTecnicoId(reporteId, tecnicoId);
        if (asignacionOpt.isEmpty()) return false;

        Reporte reporte = asignacionOpt.get().getReporte();
        if (!"recibido".equalsIgnoreCase(reporte.getEstado())) return false;

        String estadoAnterior = reporte.getEstado();
        reporte.setEstado("en proceso");
        reporteRepo.save(reporte);

        // Guardar historial
        HistorialEstado historial = new HistorialEstado();
        historial.setReporte(reporte);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo("en proceso");
        historial.setFechaCambio(LocalDateTime.now());
        historialRepo.save(historial);

        // Notificación al usuario
        notificacionServicio.crear(reporte.getUsuario(), reporte,
                "✅ Tu reporte está siendo atendido por la institución: " + reporte.getInstitucion().getNombre());

        return true;
    }

    public List<Reporte> listarPorTecnicoYEstado(Long tecnicoId, String estadoNombre) {
        return asignacionRepo.findByTecnicoIdAndReporte_Estado(tecnicoId, estadoNombre)
                .stream()
                .map(AsignacionTecnico::getReporte)
                .toList();
    }

    public long contarPorTecnicoYEstado(Long tecnicoId, String estadoNombre) {
        return asignacionRepo.countByTecnicoIdAndReporte_EstadoIgnoreCase(tecnicoId, estadoNombre);
    }
    public long contarPendientes(Long tecnicoId) {
        return asignacionRepo.countByTecnicoIdAndReporte_EstadoIgnoreCaseAndFechaFinalizacionIsNull(tecnicoId, "recibido");
    }

    public long contarEnProceso(Long tecnicoId) {
        return asignacionRepo.countByTecnicoIdAndReporte_EstadoIgnoreCaseAndFechaFinalizacionIsNull(tecnicoId, "en proceso");
    }

    public long contarResueltos(Long tecnicoId) {
        return asignacionRepo.countByTecnicoIdAndReporte_EstadoIgnoreCaseAndFechaFinalizacionIsNotNull(tecnicoId, "resuelto");
    }

    public long contarCerrados(Long tecnicoId) {
        return asignacionRepo.countByTecnicoIdAndReporte_EstadoIgnoreCaseAndFechaFinalizacionIsNotNull(tecnicoId, "cerrado");
    }

    public Optional<AsignacionTecnico> obtenerPorReporte(Long reporteId) {
        return asignacionRepo.findByReporteId(reporteId);
    }

    public List<AsignacionTecnico> listarPorAsignacionYEstado(Long tecnicoId, String estado) {
        return asignacionRepo.findByTecnicoIdAndReporte_Estado(tecnicoId, estado);
    }
    @Transactional
    public boolean finalizarReportePorTecnico(Long reporteId, String comentario, String imagenTrabajo) {
        Optional<AsignacionTecnico> asignacionOpt = asignacionRepo.findByReporteId(reporteId);
        if (asignacionOpt.isEmpty()) return false;

        AsignacionTecnico asignacion = asignacionOpt.get();
        Reporte reporte = asignacion.getReporte();

        if (!"en proceso".equalsIgnoreCase(reporte.getEstado())) return false;

        String estadoAnterior = reporte.getEstado();
        reporte.setEstado("resuelto");
        reporteRepo.save(reporte);

        // Guardar en la tabla asignacion_tecnico
        asignacion.setComentarioTecnico(comentario);
        asignacion.setImagenTrabajo(imagenTrabajo);
        asignacion.setFechaFinalizacion(LocalDateTime.now());
        asignacionRepo.save(asignacion);

        // Guardar historial
        HistorialEstado historial = new HistorialEstado();
        historial.setReporte(reporte);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo("resuelto");
        historial.setFechaCambio(LocalDateTime.now());
        historialRepo.save(historial);

        // Notificación al usuario
        notificacionServicio.crear(reporte.getUsuario(), reporte,
                "🎉 Tu reporte ha sido resuelto. ¡Gracias por tu colaboración!");

        return true;
    }

}
