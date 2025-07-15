package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.configuraciones.RutaStorageConfig;
import com.reporte_ciudadano.backend.dto.ActualizacionTecnico;
import com.reporte_ciudadano.backend.modelo.*;
import com.reporte_ciudadano.backend.repositorio.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

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
    private EvidenciaRepositorio evidenciaRepositorio;

    @Autowired
    private HistorialEstadoRepositorio historialEstadoRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Autowired
    private RutaStorageConfig rutaStorageConfig;


    private static final List<String> ESTADOS_VALIDOS = List.of(
            "recibido", "en proceso", "resuelto", "cerrado");
    @Autowired
    private AsignacionTecnicoRepositorio asignacionTecnicoRepositorio;
    @Autowired
    private HistorialReporteRepositorio historialReporteRepositorio;

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
        String correo = reporteEntrada.getUsuario().getCorreo();
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con correo: " + correo));

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

        // Asignar fecha y hora de Bolivia correctamente
        nuevoReporte.setFechaReporte(LocalDate.now(ZoneId.of("America/La_Paz")));
        nuevoReporte.setHora(LocalTime.now(ZoneId.of("America/La_Paz")));

        // Guardar reporte
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

        // Crear notificación
        notificacionServicio.crear(usuario, guardado, "📢 Tu reporte fue enviado correctamente.");

        return guardado;
    }

    public Reporte cambiarEstado(Long idReporte, String nuevoEstado) {
        // Verificar si el estado es válido
        if (!ESTADOS_VALIDOS.contains(nuevoEstado.toLowerCase())) {
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstado);
        }
        Reporte reporte = reporteRepositorio.findById(idReporte).orElse(null);
        if (reporte == null)
            return null;

        String estadoAnterior = reporte.getEstado();
        reporte.setEstado(nuevoEstado);
        reporteRepositorio.save(reporte);

        // Guardar historial del cambio
        HistorialEstado historial = new HistorialEstado();
        historial.setReporte(reporte);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(nuevoEstado);
        historialEstadoRepositorio.save(historial);

        // ✅ Obtener tipo de reporte con verificación nula
        String tipo = (reporte.getTipoReporte() != null &&
                reporte.getTipoReporte().getNombre() != null)
                        ? reporte.getTipoReporte().getNombre()
                        : "desconocido";

        // ✅ Armar mensaje con íconos y tipo
        String mensaje = switch (nuevoEstado.toLowerCase()) {
            case "en proceso" -> "✅ Tu reporte de tipo '" + tipo + " está siendo atendido por la institución: "
                    + reporte.getInstitucion().getNombre();
            case "resuelto" -> "🎉 Tu reporte de tipo '" + tipo + "' ha sido resuelto. ¡Gracias por tu colaboración!";
            case "cerrado" ->
                "🔒 Tu reporte de tipo '" + tipo + "' fue cerrado. Si persiste el problema, repórtalo nuevamente.";
            default -> "ℹ️ El estado de tu reporte de tipo '" + tipo + "' ha cambiado a: " + nuevoEstado;
        };
        // guardar notificación con relación al reporte
        notificacionServicio.crear(reporte.getUsuario(), reporte, mensaje);
        return reporte;
    }
    public List<Reporte> listarPorInstitucion(Long institucionId) {
        return reporteRepositorio.findByInstitucionId(institucionId);
    }
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

    public Optional<Reporte> obtenerPorId(Long id) {
        return reporteRepositorio.findById(id);
    }

    public List<Reporte> listarPorEstados(String... estados) {
        return reporteRepositorio.findByEstadoIn(List.of(estados));
    }

    public List<Reporte> listarPorEstadosYInstitucion(Long institucionId, String... estados) {
        return reporteRepositorio.findByInstitucionIdAndEstadoIn(institucionId, List.of(estados));
    }

    public long contarReportes() {
        return reporteRepositorio.count();
    }

    public List<Reporte> obtenerConUbicacion() {
        return reporteRepositorio.findByUbicacionIsNotNull();
    }

    public long contarPorEstado(String estado) {
        return reporteRepositorio.countByEstadoIgnoreCase(estado);
    }

    public List<Reporte> obtenerConUbicacionPorInstitucion(Long institucionId) {
        return reporteRepositorio.findByInstitucionIdAndUbicacionIsNotNull(institucionId);
    }

    public int contarAsignacionesPendientesPorInstitucion(Long institucionId) {
        return reporteRepositorio.countByInstitucionIdAndEstado(institucionId, "recibido");
    }

    public int contarReportesActivosPorInstitucion(Long institucionId) {
        return reporteRepositorio.countByInstitucionIdAndEstadoNot(institucionId, "cerrado");
    }

    @Transactional
    public boolean actualizarPorTecnico(ActualizacionTecnico dto) {
        Optional<Reporte> optReporte = reporteRepositorio.findById(dto.getReporteId());

        if (optReporte.isEmpty())
            return false;

        Reporte reporte = optReporte.get();

        // Validar cambio permitido (en minúsculas porque tu campo estado es en texto)
        String estadoActual = reporte.getEstado().toLowerCase();
        String nuevoEstadoSolicitado = dto.getNuevoEstado().toLowerCase();

        boolean cambioPermitido = ("recibido".equals(estadoActual) && "en proceso".equals(nuevoEstadoSolicitado)) ||
                ("en proceso".equals(estadoActual) && "resuelto".equals(nuevoEstadoSolicitado));

        if (!cambioPermitido)
            return false;

        // Asignar nuevo estado directamente (ya no hay entidad EstadoReporte)
        reporte.setEstado(nuevoEstadoSolicitado);


        // Guardar comentario solo si se resuelve el reporte
        if ("resuelto".equals(nuevoEstadoSolicitado)) {
            reporte.setDescripcion(reporte.getDescripcion() + "\n[Comentario técnico]: " + dto.getComentario());
        }

        // Guardamos cambios
        reporteRepositorio.save(reporte);
        return true;
    }

    public List<String> listarEstadosUnicos() {
        return ESTADOS_VALIDOS;
    }

    public Reporte obtenerPorIdObligatorio(Long id) {
        return reporteRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));
    }

    @Transactional
    public void eliminarPorId(Long id) {
        // 1. Recuperar el reporte completo
        Reporte reporte = reporteRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        // 2. Eliminar archivos físicos de evidencias del ciudadano
        for (Evidencia evidencia : reporte.getEvidencias()) {
            String nombreArchivo = evidencia.getUrl();
            if (nombreArchivo != null) {
                String ruta = rutaStorageConfig.getRutaEvidencias() + "/" + nombreArchivo;
                eliminarArchivoFisico(ruta);
            }
        }

        // 3. Eliminar archivo físico del trabajo del técnico (si existe)
        asignacionTecnicoRepositorio.findByReporteId(id).ifPresent(asignacion -> {
            String imagenTrabajo = asignacion.getImagenTrabajo();
            if (imagenTrabajo != null && !imagenTrabajo.isBlank()) {
                String ruta = rutaStorageConfig.getRutaTrabajos() + "/" + imagenTrabajo;
                eliminarArchivoFisico(ruta);
            }
            asignacionTecnicoRepositorio.delete(asignacion); // 🔁 BORRAR desde aquí
        });

        // 4. Eliminar historial de cambios (si no está en cascade)
        historialReporteRepositorio.eliminarPorReporteId(id);

        // 5. Eliminar notificaciones
        notificacionServicio.eliminarPorReporteId(id);

        // 6. Eliminar el reporte (cascada se encarga de evidencias y estados)
        reporteRepositorio.delete(reporte);
    }


    private void eliminarArchivoFisico(String rutaCompleta) {
        if (rutaCompleta != null) {
            java.io.File archivo = new java.io.File(rutaCompleta);
            if (archivo.exists() && archivo.isFile()) {
                boolean eliminado = archivo.delete();
                if (eliminado) {
                    System.out.println("🗑️ Archivo eliminado correctamente: " + rutaCompleta);
                } else {
                    System.out.println("⚠️ No se pudo eliminar el archivo: " + rutaCompleta);
                }
            } else {
                System.out.println("⚠️ Archivo no encontrado: " + rutaCompleta);
            }
        }
    }

    @Transactional
    public boolean cerrarReportePorOperador(Long reporteId) {
        Optional<Reporte> optReporte = reporteRepositorio.findById(reporteId);
        if (optReporte.isEmpty())
            return false;

        Reporte reporte = optReporte.get();
        if (!"resuelto".equalsIgnoreCase(reporte.getEstado()))
            return false;

        String estadoAnterior = reporte.getEstado();
        reporte.setEstado("cerrado");
        reporteRepositorio.save(reporte);

        // Guardar historial
        HistorialEstado historial = new HistorialEstado();
        historial.setReporte(reporte);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo("cerrado");
        historial.setFechaCambio(LocalDateTime.now());
        historialEstadoRepositorio.save(historial);

        // Notificación al usuario
        notificacionServicio.crear(reporte.getUsuario(), reporte,
                "🔒 Tu reporte fue cerrado. Si persiste el problema, repórtalo nuevamente.");

        return true;
    }
    public void cambiarTipo(Long reporteId, Long tipoReporteId, UsuarioInstitucional usuario) {
        var reporte = reporteRepositorio.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        // Obtener relaciones con ese tipo
        List<InstitucionTipoReporte> relaciones = institucionTipoReporteRepositorio.findByTipoReporteId(tipoReporteId);
        if (relaciones.isEmpty()) {
            throw new RuntimeException("El tipo de reporte no está asignado a ninguna institución");
        }

        // Usamos la primera relación encontrada
        InstitucionTipoReporte nuevaRelacion = relaciones.get(0);

        String tipoAnterior = reporte.getTipoReporte().getNombre();
        String institucionAnterior = (reporte.getInstitucion() != null) ? reporte.getInstitucion().getNombre() : "Sin asignar";

        // Actualizar tipo e institución
        reporte.setTipoReporte(nuevaRelacion.getTipoReporte());
        reporte.setInstitucion(nuevaRelacion.getInstitucion());

        reporteRepositorio.save(reporte);

        // Guardar historial
        var historial = new HistorialReporte();
        historial.setDetalle("Tipo de reporte cambiado de " + tipoAnterior + " a " + nuevaRelacion.getTipoReporte().getNombre()
                + ". La institución pasó de " + institucionAnterior + " a " + nuevaRelacion.getInstitucion().getNombre() + ".");
        historial.setReporte(reporte);
        historial.setUsuario(usuario);
        historial.setFechaHora(LocalDateTime.now());

        historialReporteRepositorio.save(historial);
    }
    public int contarTotalReportesPorInstitucion(Long institucionId) {
        return reporteRepositorio.contarPorInstitucion(institucionId);
    }



    public long contarPorEstadoEInstitucion(String estado, Long institucionId) {
        return reporteRepositorio.contarPorEstadoEInstitucion(estado, institucionId);
    }

    public List<String> obtenerNombresTiposPorInstitucion(Long institucionId) {
        return reporteRepositorio.obtenerNombresTiposPorInstitucion(institucionId);
    }

    public List<Long> contarReportesPorTipoEInstitucion(Long institucionId) {
        List<String> tipos = reporteRepositorio.obtenerNombresTiposPorInstitucion(institucionId);
        return tipos.stream()
                .map(nombre -> reporteRepositorio.contarPorTipoEInstitucion(nombre, institucionId))
                .toList();
    }

    public List<String> obtenerMesesConFormatoPorInstitucion(Long institucionId) {
        return reporteRepositorio.obtenerMesesConFormatoPorInstitucion(institucionId);
    }

    public List<Long> contarReportesPorMesEInstitucion(Long institucionId) {
        return reporteRepositorio.contarPorMesEInstitucion(institucionId);
    }

    public List<String> obtenerUltimosDiasPorInstitucion(Long institucionId) {
        return reporteRepositorio.obtenerUltimosDias(institucionId);
    }

    public List<Long> contarReportesPorDiaRecienteEInstitucion(Long institucionId) {
        return reporteRepositorio.contarUltimosDias(institucionId);
    }

    public List<String> obtenerDiasEnRangoPorInstitucion(LocalDate inicio, LocalDate fin, Long institucionId) {
        return reporteRepositorio.obtenerDiasEnRango(inicio, fin, institucionId);
    }

    public List<Long> contarReportesPorDiaEnRangoEInstitucion(LocalDate inicio, LocalDate fin, Long institucionId) {
        return reporteRepositorio.contarPorDiaEnRango(inicio, fin, institucionId);
    }
}
