package com.reporte_ciudadano.backend.repositorio;


import com.reporte_ciudadano.backend.modelo.RolInstitucional;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioInstitucionalRepositorio extends JpaRepository<UsuarioInstitucional, Long> {

    Optional<UsuarioInstitucional> findByCorreo(String correo);

    Optional<UsuarioInstitucional> findByUsuario(String usuario);
    List<UsuarioInstitucional> findByInstitucionIdAndRol(Long institucionId, RolInstitucional rol);

    Optional<UsuarioInstitucional> findByTelefono(String telefono);

    long countByActivo(boolean activo);
    List<UsuarioInstitucional> findByInstitucionId(Long institucionId);

    @Query("SELECT u FROM UsuarioInstitucional u WHERE " +
            "(LOWER(u.nombre) LIKE LOWER(:termino) OR LOWER(u.usuario) LIKE LOWER(:termino) OR LOWER(u.correo) LIKE LOWER(:termino)) " +
            "AND u.institucion.id = :institucionId")
    List<UsuarioInstitucional> buscarPorNombreUsuarioCorreoYInstitucion(@Param("termino") String termino,
                                                                        @Param("institucionId") Long institucionId);

    List<UsuarioInstitucional> findByRol(RolInstitucional rol);

    @Query("SELECT u FROM UsuarioInstitucional u " +
            "WHERE LOWER(u.nombre) LIKE LOWER(:termino) " +
            "   OR LOWER(u.usuario) LIKE LOWER(:termino) " +
            "   OR LOWER(u.correo) LIKE LOWER(:termino)")
    List<UsuarioInstitucional> buscarPorNombreUsuarioCorreo(String termino);
    @Query("SELECT COUNT(u) FROM UsuarioInstitucional u WHERE u.activo = true AND u.institucion.id = :institucionId")
    int contarUsuariosActivosPorInstitucion(@Param("institucionId") Long institucionId);
    @Query("SELECT u FROM UsuarioInstitucional u WHERE LOWER(u.rol) = LOWER(:rol)")
    List<UsuarioInstitucional> findByRolNombre(@Param("rol") String rol);


}
