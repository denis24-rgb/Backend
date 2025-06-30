package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Usuario;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    @Modifying
    @Query("UPDATE Usuario u SET u.tokenDispositivo = null WHERE u.tokenDispositivo = :token AND u.correo <> :correo")
    void limpiarTokenDispositivoEnOtrosUsuarios(@Param("token") String token, @Param("correo") String correo);

    Optional<Usuario> findByTokenVerificacion(String tokenVerificacion);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.tokenDispositivo = null WHERE u.tokenDispositivo = :token")
    void limpiarTokenDispositivoPorToken(@Param("token") String token);

}
