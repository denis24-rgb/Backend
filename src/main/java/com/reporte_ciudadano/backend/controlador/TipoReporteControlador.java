package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.configuraciones.RutaProperties;
import com.reporte_ciudadano.backend.modelo.CategoriaReporte;
import com.reporte_ciudadano.backend.modelo.InstitucionTipoReporte;
import com.reporte_ciudadano.backend.modelo.TipoAvisoComunitario;
import com.reporte_ciudadano.backend.modelo.TipoReporte;
import com.reporte_ciudadano.backend.repositorio.InstitucionTipoReporteRepositorio;
import com.reporte_ciudadano.backend.repositorio.TipoReporteRepositorio;
import com.reporte_ciudadano.backend.servicio.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/panel/superadmin/tipos-reportes")
@PreAuthorize("hasRole('SUPERADMIN')")
public class TipoReporteControlador {

    private final TipoReporteServicio servicio;

    @Autowired
    private TipoReporteRepositorio tipoRepo;

    @Autowired
    private InstitucionServicio institucionServicio;

    @Autowired
    private CategoriaReporteServicio categoriaReporteServicio;

    @Autowired
    private InstitucionTipoReporteServicio institucionTipoReporteServicio;

    @Autowired
    private InstitucionTipoReporteRepositorio institucionTipoReporteRepositorio;

    @Value("${ruta.iconos}")
    private String rutaIconos;
    @Autowired
    private TipoAvisoComunitarioServicio tipoAvisoComunitarioServicio;

    @Autowired
    private RutaProperties rutaProperties;

    public TipoReporteControlador(TipoReporteServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public String mostrarVista(Model model) {
        List<TipoReporte> tipos = tipoRepo.findAll();
        List<TipoAvisoComunitario> avisos = tipoAvisoComunitarioServicio.listarTodos();

        // Convertir avisos a "TipoReporte simulado"
        for (TipoAvisoComunitario aviso : avisos) {
            TipoReporte tipoSimulado = new TipoReporte();
            tipoSimulado.setId(-aviso.getId()); // ID negativo para diferenciarlos
            tipoSimulado.setNombre(aviso.getNombre());
            tipoSimulado.setIcono(aviso.getIcono());
            tipoSimulado.setAsignaciones(List.of()); // sin asignaciones
            tipos.add(tipoSimulado);
        }

        model.addAttribute("tipos", tipos); // ahora incluye reportes y avisos
        model.addAttribute("instituciones", institucionServicio.listarTodas());
        model.addAttribute("categorias", categoriaReporteServicio.listarTodas());
        return "panel/tipos_reportes";
    }

    @PostMapping("/crear")
    public String crearTipoReporte(@RequestParam("nombre") String nombre,
                                   @RequestParam("icono") MultipartFile archivo,
                                   @RequestParam(value = "institucionId", required = false) Long institucionId,
                                   @RequestParam("categoriaId") Long categoriaId,
                                   RedirectAttributes redirect) throws IOException {

        if (archivo.isEmpty() || !archivo.getOriginalFilename().endsWith(".png")) {
            redirect.addFlashAttribute("mensajeError", "Debes subir un ícono válido (.png).");
            return "redirect:/panel/superadmin/tipos-reportes";
        }

        String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
        Path rutaFinal = Paths.get(rutaProperties.getIconos(), nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaFinal, StandardCopyOption.REPLACE_EXISTING);

        CategoriaReporte categoria = categoriaReporteServicio.obtenerPorId(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no válida."));

        // Si la categoría contiene "aviso", lo guardamos en TipoAvisoComunitario
        if (categoria.getNombre().toLowerCase().contains("aviso")) {
            TipoAvisoComunitario nuevoAviso = new TipoAvisoComunitario();
            nuevoAviso.setNombre(nombre);
            nuevoAviso.setIcono(nombreArchivo);
            tipoAvisoComunitarioServicio.guardar(nuevoAviso);

            redirect.addFlashAttribute("mensajeExito", "Aviso comunitario creado correctamente.");
            return "redirect:/panel/superadmin/tipos-reportes";
        }

        // Caso contrario, lo tratamos como TipoReporte
        TipoReporte nuevoTipo = new TipoReporte();
        nuevoTipo.setNombre(nombre);
        nuevoTipo.setIcono(nombreArchivo);
        tipoRepo.save(nuevoTipo);

        if (institucionId == null) {
            redirect.addFlashAttribute("mensajeError", "Debes seleccionar una institución para esta categoría.");
            return "redirect:/panel/superadmin/tipos-reportes";
        }

        InstitucionTipoReporte relacion = new InstitucionTipoReporte();
        relacion.setTipoReporte(nuevoTipo);
        relacion.setInstitucion(institucionServicio.obtenerPorId(institucionId).orElseThrow());
        relacion.setCategoriaReporte(categoria);
        institucionTipoReporteServicio.guardar(relacion);

        redirect.addFlashAttribute("mensajeExito", "Tipo de reporte creado correctamente.");
        return "redirect:/panel/superadmin/tipos-reportes";
    }

    @PostMapping("/eliminar")
    public String eliminarTipoReporte(@RequestParam("id") Long id, RedirectAttributes redirect) {
        try {
            // ✅ Si es un Aviso Comunitario (ID negativo)
            if (id < 0) {
                Long idAviso = Math.abs(id);
                TipoAvisoComunitario aviso = tipoAvisoComunitarioServicio.buscarPorId(idAviso);
                if (aviso != null) {
                    Path rutaIcono = Paths.get(rutaIconos, aviso.getIcono());
                    Files.deleteIfExists(rutaIcono);
                    tipoAvisoComunitarioServicio.eliminarPorId(idAviso);
                    redirect.addFlashAttribute("mensajeExito", "Aviso comunitario eliminado correctamente.");
                } else {
                    redirect.addFlashAttribute("mensajeError", "Aviso comunitario no encontrado.");
                }
                return "redirect:/panel/superadmin/tipos-reportes";
            }

            // ✅ Caso normal: TipoReporte
            boolean usadoEnReportes = tipoRepo.estaUsadoEnReportes(id);
            if (usadoEnReportes) {
                redirect.addFlashAttribute("mensajeError", "No se puede eliminar este tipo porque ya fue usado en uno o más reportes.");
                return "redirect:/panel/superadmin/tipos-reportes";
            }

            TipoReporte tipo = tipoRepo.findById(id).orElse(null);
            if (tipo != null) {
                Path rutaIcono = Paths.get(rutaIconos, tipo.getIcono());
                Files.deleteIfExists(rutaIcono);

                List<InstitucionTipoReporte> relaciones = institucionTipoReporteRepositorio.findByTipoReporteId(id);
                if (!relaciones.isEmpty()) {
                    institucionTipoReporteRepositorio.deleteAll(relaciones);
                }

                tipoRepo.deleteById(id);
            }

            redirect.addFlashAttribute("mensajeExito", "Tipo de reporte eliminado correctamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensajeError", "Ocurrió un error inesperado al intentar eliminar el tipo.");
            e.printStackTrace();
        }

        return "redirect:/panel/superadmin/tipos-reportes";
    }

    @PostMapping("/editar")
    public String editarTipoReporte(@RequestParam Long id,
                                    @RequestParam String nombre,
                                    @RequestParam(value = "icono", required = false) MultipartFile iconoFile,
                                    @RequestParam(required = false) Long institucionId,
                                    @RequestParam(required = false) Long categoriaId,
                                    RedirectAttributes redirectAttributes) {
        try {
            boolean esAviso = id < 0;
            String nombreFinal = nombre;

            if (esAviso) {
                Long idAviso = Math.abs(id);
                TipoAvisoComunitario aviso = tipoAvisoComunitarioServicio.buscarPorId(idAviso);
                if (aviso == null) {
                    redirectAttributes.addFlashAttribute("mensajeError", "Aviso comunitario no encontrado.");
                    return "redirect:/panel/superadmin/tipos-reportes";
                }

                aviso.setNombre(nombreFinal);

                if (iconoFile != null && !iconoFile.isEmpty()) {
                    String anterior = aviso.getIcono();
                    Path rutaAnterior = Paths.get(rutaIconos, anterior);
                    Files.deleteIfExists(rutaAnterior);

                    String nuevoNombre = iconoFile.getOriginalFilename();
                    Path rutaDestino = Paths.get(rutaIconos, nuevoNombre);

                    if (Files.exists(rutaDestino)) {
                        nuevoNombre = System.currentTimeMillis() + "_" + nuevoNombre;
                        rutaDestino = Paths.get(rutaIconos, nuevoNombre);
                    }

                    Files.copy(iconoFile.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);
                    aviso.setIcono(nuevoNombre);
                }

                tipoAvisoComunitarioServicio.guardar(aviso);
                redirectAttributes.addFlashAttribute("mensajeExito", "Aviso comunitario editado correctamente.");
                return "redirect:/panel/superadmin/tipos-reportes";
            }

            // Si no es aviso, seguimos con lógica normal de TipoReporte
            TipoReporte tipo = tipoRepo.findById(id).orElseThrow();
            tipo.setNombre(nombreFinal);

            if (iconoFile != null && !iconoFile.isEmpty()) {
                String iconoAnterior = tipo.getIcono();
                Path rutaAnterior = Paths.get(rutaIconos, iconoAnterior);
                Files.deleteIfExists(rutaAnterior);

                String nombreOriginal = iconoFile.getOriginalFilename();
                Path rutaDestino = Paths.get(rutaIconos, nombreOriginal);

                if (Files.exists(rutaDestino)) {
                    String nombreUnico = System.currentTimeMillis() + "_" + nombreOriginal;
                    rutaDestino = Paths.get(rutaIconos, nombreUnico);
                    tipo.setIcono(nombreUnico);
                } else {
                    tipo.setIcono(nombreOriginal);
                }

                Files.copy(iconoFile.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);
            }

            tipoRepo.save(tipo);

            InstitucionTipoReporte relacion = institucionTipoReporteServicio.buscarPorTipoReporteId(id);
            if (relacion != null) {
                relacion.setInstitucion(institucionServicio.obtenerPorId(institucionId).orElseThrow());
                relacion.setCategoriaReporte(categoriaReporteServicio.obtenerPorId(categoriaId).orElseThrow());
                institucionTipoReporteServicio.guardar(relacion);
            }

            redirectAttributes.addFlashAttribute("mensajeExito", "Tipo de reporte actualizado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar el tipo.");
        }

        return "redirect:/panel/superadmin/tipos-reportes";
    }

    @GetMapping("/listar")
    public List<TipoReporte> obtenerTodos() {
        return servicio.listarTodos();
    }

    @GetMapping("/{id}")
    public TipoReporte obtenerPorId(@PathVariable Long id) {
        return servicio.buscarPorId(id);
    }

    @GetMapping("/por-categoria")
    public List<TipoReporte> obtenerPorCategoria(@RequestParam Long categoria) {
        return servicio.listarPorCategoria(categoria);
    }

}
