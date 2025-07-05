package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.CategoriaReporte;
import com.reporte_ciudadano.backend.modelo.InstitucionTipoReporte;
import com.reporte_ciudadano.backend.modelo.TipoReporte;
import com.reporte_ciudadano.backend.repositorio.InstitucionTipoReporteRepositorio;
import com.reporte_ciudadano.backend.repositorio.TipoReporteRepositorio;
import com.reporte_ciudadano.backend.servicio.CategoriaReporteServicio;
import com.reporte_ciudadano.backend.servicio.InstitucionServicio;
import com.reporte_ciudadano.backend.servicio.InstitucionTipoReporteServicio;
import com.reporte_ciudadano.backend.servicio.TipoReporteServicio;

import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String RUTA_ICONOS = "/opt/reporte_ciudadano/iconos-tipo-reporte";

    public TipoReporteControlador(TipoReporteServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public String mostrarVista(Model model) {
        model.addAttribute("tipos", tipoRepo.findAll());
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
        Path rutaFinal = Paths.get(RUTA_ICONOS, nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaFinal, StandardCopyOption.REPLACE_EXISTING);

        // Guardar en base de datos
        TipoReporte nuevoTipo = new TipoReporte();
        nuevoTipo.setNombre(nombre);
        nuevoTipo.setIcono(nombreArchivo);
        tipoRepo.save(nuevoTipo);

        CategoriaReporte categoria = categoriaReporteServicio.obtenerPorId(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no válida."));

        if (!categoria.getNombre().toLowerCase().contains("aviso")) {
            // Requiere institución
            if (institucionId == null) {
                redirect.addFlashAttribute("mensajeError", "Debes seleccionar una institución para esta categoría.");
                return "redirect:/panel/superadmin/tipos-reportes";
            }

            relacion.setCategoriaReporte(categoria);

            relacion.setCategoriaReporte(categoriaReporteServicio.obtenerPorId(categoriaId).orElseThrow());


        redirect.addFlashAttribute("mensajeExito", "Tipo de reporte creado correctamente.");
        return "redirect:/panel/superadmin/tipos-reportes";
    }

    @PostMapping("/eliminar")
    public String eliminarTipoReporte(@RequestParam("id") Long id, RedirectAttributes redirect) {
        try {
            TipoReporte tipo = tipoRepo.findById(id).orElse(null);
            if (tipo != null) {
                Path rutaIcono = Paths.get(RUTA_ICONOS, tipo.getIcono());
                Files.deleteIfExists(rutaIcono);
            }
            tipoRepo.deleteById(id);
            redirect.addFlashAttribute("mensajeExito", "Tipo de reporte eliminado correctamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensajeError",
                    "No se puede eliminar este tipo porque ya fue usado en uno o más reportes.");
        }
        return "redirect:/panel/superadmin/tipos-reportes";
    }

    @PostMapping("/editar")
    public String editarTipoReporte(@RequestParam Long id,
                                    @RequestParam String nombre,
                                    @RequestParam(value = "icono", required = false) MultipartFile iconoFile,
                                    @RequestParam Long institucionId,
                                    @RequestParam Long categoriaId,
                                    RedirectAttributes redirectAttributes) {
        try {
            TipoReporte tipo = tipoRepo.findById(id).orElseThrow();
            tipo.setNombre(nombre);

            if (iconoFile != null && !iconoFile.isEmpty()) {
                String iconoAnterior = tipo.getIcono();
                Path rutaAnterior = Paths.get(RUTA_ICONOS, iconoAnterior);
                Files.deleteIfExists(rutaAnterior);

                String nombreOriginal = iconoFile.getOriginalFilename();
                Path rutaDestino = Paths.get(RUTA_ICONOS, nombreOriginal);

                if (Files.exists(rutaDestino)) {
                    String nombreUnico = System.currentTimeMillis() + "_" + nombreOriginal;
                    rutaDestino = Paths.get(RUTA_ICONOS, nombreUnico);
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
            redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar el tipo de reporte.");
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