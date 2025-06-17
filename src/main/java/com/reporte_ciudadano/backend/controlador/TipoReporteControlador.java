package com.reporte_ciudadano.backend.controlador;

import com.reporte_ciudadano.backend.modelo.TipoReporte;
import com.reporte_ciudadano.backend.repositorio.TipoReporteRepositorio;
import com.reporte_ciudadano.backend.servicio.TipoReporteServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-reporte")
@CrossOrigin(origins = "*")
public class TipoReporteControlador {

    private final TipoReporteServicio servicio;
    @Autowired
    private TipoReporteRepositorio tipoReporteRepositorio;

    public TipoReporteControlador(TipoReporteServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<TipoReporte> obtenerTodos() {
        return servicio.listarTodos();
    }

    @PostMapping
    public TipoReporte crear(@RequestBody TipoReporte tipo) {
        return servicio.guardar(tipo);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }

    @GetMapping("/{id}")
    public TipoReporte obtenerPorId(@PathVariable Long id) {
        return servicio.buscarPorId(id);
    }

    @GetMapping("/por-categoria") // /api/tipos-reporte/por-categoria?categoria=1
    public List<TipoReporte> obtenerPorCategoria(@RequestParam Long categoria) {
        return tipoReporteRepositorio.findByCategoriaId(categoria);
    }
}