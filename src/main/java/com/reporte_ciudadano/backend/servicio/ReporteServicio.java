package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.HistorialEstado;
import com.reporte_ciudadano.backend.modelo.Institucion;
import com.reporte_ciudadano.backend.modelo.Notificacion;
import com.reporte_ciudadano.backend.modelo.Reporte;
import com.reporte_ciudadano.backend.modelo.TipoReporte;
import com.reporte_ciudadano.backend.modelo.Usuario;
import com.reporte_ciudadano.backend.repositorio.HistorialEstadoRepositorio;
import com.reporte_ciudadano.backend.repositorio.InstitucionRepositorio;
import com.reporte_ciudadano.backend.repositorio.InstitucionTipoReporteRepositorio;
import com.reporte_ciudadano.backend.repositorio.ReporteRepositorio;
import com.reporte_ciudadano.backend.repositorio.TipoReporteRepositorio;
import com.reporte_ciudadano.backend.repositorio.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Transactional
@Service
public class ReporteServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private InstitucionRepositorio institucionRepositorio;

    @Autowired
    private TipoReporteRepositorio tipoReporteRepositorio;

    @Autowired
    private InstitucionTipoReporteRepositorio institucionTipoReporteRepositorio;

    @Autowired
    private ReporteRepositorio reporteRepositorio;

    @Autowired
    private HistorialEstadoRepositorio historialEstadoRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    private static final List<String> ESTADOS_VALIDOS = List.of(
            "recibido", "en proceso", "resuelto", "cerrado");

    public ReporteServicio(ReporteRepositorio reporteRepositorio,
            HistorialEstadoRepositorio historialEstadoRepositorio) {
        this.reporteRepositorio = reporteRepositorio;
        this.historialEstadoRepositorio = historialEstadoRepositorio;
    }

    public List<Reporte> listarTodos() {
        return reporteRepositorio.findAll();
    }

    public Reporte guardar(Reporte reporteEntrada) {
        // Validaciones
        Long usuarioId = reporteEntrada.getUsuario().getId();
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Long institucionId = reporteEntrada.getInstitucion().getId();
        Institucion institucion = institucionRepositorio.findById(institucionId)
                .orElseThrow(() -> new IllegalArgumentException("Institución no encontrada"));

        Long tipoReporteId = reporteEntrada.getTipoReporte().getId();
        TipoReporte tipoReporte = tipoReporteRepositorio.findById(tipoReporteId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de reporte no encontrado"));

        boolean existeRelacion = institucionTipoReporteRepositorio
                .existsByInstitucionIdAndTipoReporteId(institucionId, tipoReporteId);
        if (!existeRelacion) {
            throw new IllegalArgumentException("Esta institución no atiende este tipo de reporte");
        }

        // Construcción del nuevo reporte
        Reporte nuevoReporte = new Reporte();
        nuevoReporte.setUsuario(usuario);
        nuevoReporte.setInstitucion(institucion);
        nuevoReporte.setTipoReporte(tipoReporte);
        nuevoReporte.setDescripcion(reporteEntrada.getDescripcion());
        nuevoReporte.setUbicacion(reporteEntrada.getUbicacion());
        nuevoReporte.setEstado("recibido");

        Reporte guardado = reporteRepositorio.saveAndFlush(nuevoReporte);
        System.out.println("🆔 Reporte guardado con ID: " + guardado.getId());

        // ⚠️ PEQUEÑO RETRASO para asegurar visibilidad de la transacción
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Guardar historial de estado
        HistorialEstado historial = new HistorialEstado();
        historial.setReporte(guardado);
        historial.setEstadoAnterior("recibido");
        historial.setEstadoNuevo("recibido");
        historialEstadoRepositorio.save(historial);
        // Creamos la notificacion
        notificacionServicio.crear(usuario, guardado, "📢 Tu reporte fue enviado correctamente.");
        return guardado;
    }

    // public Reporte cambiarEstado(Long idReporte, String nuevoEstado) {
    // // Verificar si el estado es válido
    // if (!ESTADOS_VALIDOS.contains(nuevoEstado.toLowerCase())) {
    // throw new IllegalArgumentException("Estado inválido: " + nuevoEstado);
    // }
    // Reporte reporte = reporteRepositorio.findById(idReporte).orElse(null);
    // if (reporte == null)
    // return null;
    //
    // String estadoAnterior = reporte.getEstado();
    // reporte.setEstado(nuevoEstado);
    // reporteRepositorio.save(reporte);
    //
    // // Guardar historial del cambio
    // HistorialEstado historial = new HistorialEstado();
    // historial.setReporte(reporte);
    // historial.setEstadoAnterior(estadoAnterior);
    // historial.setEstadoNuevo(nuevoEstado);
    // historialEstadoRepositorio.save(historial);
    //
    // // ✅ Obtener tipo de reporte con verificación nula
    // String tipo = (reporte.getTipoReporte() != null &&
    // reporte.getTipoReporte().getNombreTipo() != null)
    // ? reporte.getTipoReporte().getNombreTipo()
    // : "desconocido";
    //
    // // ✅ Armar mensaje con íconos y tipo
    // String mensaje = switch (nuevoEstado.toLowerCase()) {
    // case "en proceso" -> "✅ Tu reporte de tipo '" + tipo + "' está siendo
    // atendido por la institución: "
    // + reporte.getInstitucion().getNombreInstitucion();
    // case "resuelto" -> "🎉 Tu reporte de tipo '" + tipo + "' ha sido resuelto.
    // ¡Gracias por tu colaboración!";
    // case "cerrado" ->
    // "🔒 Tu reporte de tipo '" + tipo + "' fue cerrado. Si persiste el problema,
    // repórtalo nuevamente.";
    // default -> "ℹ️ El estado de tu reporte de tipo '" + tipo + "' ha cambiado a:
    // " + nuevoEstado;
    // };
    // // guardar notificación con relación al reporte
    // notificacionServicio.crear(reporte.getUsuario(), reporte, mensaje);
    // return reporte;
    // }

    public Reporte buscarPorId(Long id) {
        return reporteRepositorio.findById(id).orElse(null);
    }

    public List<Reporte> buscarPorUsuarioId(Long usuarioId) {
        return reporteRepositorio.findByUsuarioId(usuarioId);
    }

    public void eliminar(Long id) {
        reporteRepositorio.deleteById(id);
    }

    public List<TipoReporte> listarPorCategoria(Long categoriaId) {
        return institucionTipoReporteRepositorio.findTiposReportePorCategoria(categoriaId);
    }

    public List<HistorialEstado> obtenerHistorialPorReporte(Long reporteId) {
        return historialEstadoRepositorio.findByReporteIdOrderByFechaCambioAsc(reporteId);
    }

}
