package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.ContactoEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactoEmergenciaRepositorio extends JpaRepository<ContactoEmergencia, Long> {
    List<ContactoEmergencia> findByTipoContacto(String tipoContacto);
}
