package com.reporte_ciudadano.backend.controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reporte_ciudadano.backend.modelo.InstitucionTipoReporte;
import com.reporte_ciudadano.backend.modelo.TipoReporte;
import com.reporte_ciudadano.backend.repositorio.InstitucionTipoReporteRepositorio;

@RestController
@RequestMapping("/api/tipos-reporte")
@CrossOrigin(origins = "*")
public class TipoReporteApiControlador {

    @Autowired
    private InstitucionTipoReporteRepositorio institucionTipoReporteRepositorio;

    @GetMapping("/por-categoria/{categoriaId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerTiposPorCategoria(@PathVariable Long categoriaId) {
        List<InstitucionTipoReporte> lista = institucionTipoReporteRepositorio.findByCategoriaReporteId(categoriaId);
        List<Map<String, Object>> tipos = lista.stream().map(relacion -> {
            TipoReporte tipo = relacion.getTipoReporte();
            Map<String, Object> map = new HashMap<>();
            map.put("id", tipo.getId());
            map.put("nombre", tipo.getNombre()); // 👈 Esto debe coincidir con Flutter
            map.put("icono", tipo.getIcono());
            return map;
        }).distinct().collect(Collectors.toList());

        return ResponseEntity.ok(tipos);
    }
}
