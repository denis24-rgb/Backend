package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.configuraciones.RutaStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ArchivoServicio {

    @Autowired
    private RutaStorageConfig rutaStorage;

    public String guardarEvidencia(MultipartFile archivo) throws IOException {
        String nombreArchivo = archivo.getOriginalFilename();

        // Arma la ruta completa usando la configuración
        String rutaDestino = rutaStorage.getRutaEvidencias() + nombreArchivo;

        // Crea el archivo y transfiere el contenido
        File destino = new File(rutaDestino);
        archivo.transferTo(destino);

        System.out.println("📁 Evidencia guardada en: " + rutaDestino);

        return nombreArchivo;
    }
}
