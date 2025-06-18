//package com.reporte_ciudadano.backend.servicio;
//
//import com.reporte_ciudadano.backend.modelo.ImagenReporte;
//import com.reporte_ciudadano.backend.repositorio.ImagenReporteRepositorio;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ImagenReporteServicio {
//
//    @Autowired
//    private ImagenReporteRepositorio imagenRepo;
//
//    public List<ImagenReporte> listarTodas() {
//        return imagenRepo.findAll();
//    }
//
//    public List<ImagenReporte> listarPorReporte(Long reporteId) {
//        return imagenRepo.findByReporteId(reporteId);
//    }
//
//    public Optional<ImagenReporte> obtenerPorId(Long id) {
//        return imagenRepo.findById(id);
//    }
//
//    public ImagenReporte guardar(ImagenReporte imagen) {
//        return imagenRepo.save(imagen);
//    }
//
//    public void eliminar(Long id) {
//        imagenRepo.deleteById(id);
//    }
//}
