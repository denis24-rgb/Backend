//package com.reporte_ciudadano.backend.servicio;
//
//
//import com.reporte_ciudadano.backend.modelo.EstadoReporte;
//import com.reporte_ciudadano.backend.repositorio.EstadoReporteRepositorio;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class EstadoReporteServicio {
//
//    @Autowired
//    private EstadoReporteRepositorio estadoRepo;
//
//    public List<EstadoReporte> listarTodos() {
//        return estadoRepo.findAll();
//    }
//
//    public Optional<EstadoReporte> obtenerPorId(Long id) {
//        return estadoRepo.findById(id);
//    }
//
//    public EstadoReporte guardar(EstadoReporte estado) {
//        return estadoRepo.save(estado);
//    }
//
//    public void eliminar(Long id) {
//        estadoRepo.deleteById(id);
//    }
//}
