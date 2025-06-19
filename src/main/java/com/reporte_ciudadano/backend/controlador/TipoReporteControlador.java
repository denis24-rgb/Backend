package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.InstitucionTipoReporte;
import com.reporte_ciudadano.backend.modelo.TipoReporte;
import com.reporte_ciudadano.backend.repositorio.InstitucionTipoReporteRepositorio;
import com.reporte_ciudadano.backend.repositorio.TipoReporteRepositorio;
import com.reporte_ciudadano.backend.servicio.CategoriaReporteServicio;
import com.reporte_ciudadano.backend.servicio.InstitucionServicio;
import com.reporte_ciudadano.backend.servicio.InstitucionTipoReporteServicio;
import com.reporte_ciudadano.backend.servicio.TipoReporteServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tipos-reporte")
@CrossOrigin(origins = "*")
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
    private InstitucionTipoReporte institucionTipoReporte;
    @Autowired
    private InstitucionTipoReporteRepositorio institucionTipoReporteRepositorio;

    @GetMapping("/vista")
    public String mostrarVista(Model model) {

        model.addAttribute("tipos", tipoRepo.findAll());
        model.addAttribute("instituciones", institucionServicio.listarTodas());
        model.addAttribute("categorias", categoriaReporteServicio.listarTodas());
        return "panel/tipos_reportes";
    }

    @PostMapping("/crear")
    public String crearTipoReporte(@RequestParam("nombre") String nombre,
            @RequestParam("icono") MultipartFile archivo,
            @RequestParam("institucionId") Long institucionId,
            @RequestParam("categoriaId") Long categoriaId,
            RedirectAttributes redirect) throws IOException {
        if (!archivo.isEmpty() && archivo.getOriginalFilename().endsWith(".png")) {
            String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();

            // 🔧 Esta es la línea correcta que reemplaza las dos anteriores
            Path rutaFinal = Paths.get("src/main/resources/static/imagenes", nombreArchivo);

            // 💾 Guardar el archivo en la carpeta visible del proyecto
            Files.copy(archivo.getInputStream(), rutaFinal, StandardCopyOption.REPLACE_EXISTING);

            // Guardar en base de datos
            TipoReporte nuevoTipo = new TipoReporte();
            nuevoTipo.setNombre(nombre);
            nuevoTipo.setIcono(nombreArchivo);
            tipoRepo.save(nuevoTipo);

            // Guardar relación en InstitucionTipoReporte
            InstitucionTipoReporte relacion = new InstitucionTipoReporte();
            relacion.setTipoReporte(nuevoTipo);
            relacion.setInstitucion(institucionServicio.obtenerPorId(institucionId).orElseThrow());
            relacion.setCategoriaReporte(categoriaReporteServicio.obtenerPorId(categoriaId).orElseThrow());

            institucionTipoReporteServicio.guardar(relacion);

            redirect.addFlashAttribute("mensajeExito", "Tipo de reporte creado y asignado correctamente.");
        } else {
            redirect.addFlashAttribute("mensajeError", "Debes subir un ícono válido (.png).");
        }

        return "redirect:/panel/superadmin/tipos-reportes";
    }

    @PostMapping("/eliminar")
    public String eliminarTipoReporte(@RequestParam("id") Long id, RedirectAttributes redirect) {
        try {
            // Obtener el tipo antes de eliminarlo para conocer el ícono
            TipoReporte tipo = tipoRepo.findById(id).orElse(null);

            if (tipo != null) {
                // Ruta del ícono a eliminar
                Path rutaIcono = Paths.get("src/main/resources/static/imagenes", tipo.getIcono());

                // Eliminar archivo si existe
                Files.deleteIfExists(rutaIcono);
            }
            // Eliminar el registro de la base de datos
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
            RedirectAttributes redirectAttributes) {
        try {
            TipoReporte tipo = tipoRepo.findById(id).orElseThrow();
            tipo.setNombre(nombre);

            if (iconoFile != null && !iconoFile.isEmpty()) {
                // Ruta del ícono anterior
                String iconoAnterior = tipo.getIcono();
                Path rutaAnterior = Paths.get("src/main/resources/static/imagenes", iconoAnterior);

                // Eliminar si existe
                Files.deleteIfExists(rutaAnterior);

                // Guardar nuevo ícono
                String nuevoNombreArchivo = UUID.randomUUID() + "_" + iconoFile.getOriginalFilename();
                Path rutaDestino = Paths.get("src/main/resources/static/imagenes", nuevoNombreArchivo);
                Files.copy(iconoFile.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);
                tipo.setIcono(nuevoNombreArchivo);
            }

            tipoRepo.save(tipo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Tipo de reporte actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar el tipo de reporte.");
        }
        return "redirect:/panel/superadmin/tipos-reportes";
    }

    public TipoReporteControlador(TipoReporteServicio servicio) {
        this.servicio = servicio;
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

    @GetMapping("/por-categoria/{categoriaId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerTiposPorCategoria(@PathVariable Long categoriaId) {
        List<InstitucionTipoReporte> lista = institucionTipoReporteRepositorio.findByCategoriaReporteId(categoriaId);
        List<Map<String, Object>> tipos = lista.stream().map(relacion -> {
            TipoReporte tipo = relacion.getTipoReporte();
            Map<String, Object> map = new HashMap<>();
            map.put("id", tipo.getId());
            map.put("nombre", tipo.getNombre());
            map.put("icono", tipo.getIcono());
            return map;
        }).distinct().collect(Collectors.toList());

        return ResponseEntity.ok(tipos);
    }

}