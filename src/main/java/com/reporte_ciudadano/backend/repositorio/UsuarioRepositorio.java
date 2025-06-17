package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByTokenVerificacion(String tokenVerificacion);
}
