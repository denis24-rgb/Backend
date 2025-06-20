package com.reporte_ciudadano.backend.servicio;



import com.reporte_ciudadano.backend.modelo.RolInstitucional;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.repositorio.UsuarioInstitucionalRepositorio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioInstitucionalServicio {

    @Autowired
    private UsuarioInstitucionalRepositorio usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuarioInstitucional> listarTodos() {
        return usuarioRepo.findAll();
    }


    public Optional<UsuarioInstitucional> obtenerPorId(Long id) {
        return usuarioRepo.findById(id);
    }

    public Optional<UsuarioInstitucional> obtenerPorCorreo(String correo) {
        return usuarioRepo.findByCorreo(correo);
    }

    public Optional<UsuarioInstitucional> obtenerPorUsuario(String usuario) {
        return usuarioRepo.findByUsuario(usuario);
    }

    public UsuarioInstitucional guardar(UsuarioInstitucional usuario) {
        // Validar correo
        Optional<UsuarioInstitucional> existenteCorreo = usuarioRepo.findByCorreo(usuario.getCorreo());
        if (existenteCorreo.isPresent() && !existenteCorreo.get().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("⚠️ El correo ya está registrado.");
        }

        // Validar usuario
        Optional<UsuarioInstitucional> existenteUsuario = usuarioRepo.findByUsuario(usuario.getUsuario());
        if (existenteUsuario.isPresent() && !existenteUsuario.get().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("⚠️ El nombre de usuario ya está en uso.");
        }

        // Validar teléfono
        Optional<UsuarioInstitucional> existenteTelefono = usuarioRepo.findByTelefono(usuario.getTelefono());
        if (existenteTelefono.isPresent() && !existenteTelefono.get().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("⚠️ El número de teléfono ya está registrado.");
        }

        // Encriptar contraseña si no está encriptada aún
        if (usuario.getContrasena() != null && !usuario.getContrasena().trim().isEmpty()) {
            if (!usuario.getContrasena().startsWith("$2a$")) {
                usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
            }
        } else {
            // Mantener contraseña actual si no se ingresó una nueva
            if (usuario.getId() != null) {
                usuario.setContrasena(usuarioRepo.findById(usuario.getId())
                        .map(UsuarioInstitucional::getContrasena)
                        .orElse(null));
            }
        }
        return usuarioRepo.save(usuario);
    }

    public void eliminar(Long id) {
        usuarioRepo.deleteById(id);
    }

    public long contarUsuariosActivos() {
        return usuarioRepo.countByActivo(true);
    }

    public List<UsuarioInstitucional> listarPorRol(RolInstitucional rol) {
        return usuarioRepo.findByRol(rol);
    }
    public long contarUsuarios() {
        return usuarioRepo.count();
    }
    public List<UsuarioInstitucional> buscarUsuarios(String termino) {
        String term = "%" + termino.toLowerCase() + "%";
        return usuarioRepo.buscarPorNombreUsuarioCorreo(term);
    }
    public List<UsuarioInstitucional> listarTecnicosPorInstitucion(Long institucionId) {
        return usuarioRepo.findByInstitucionIdAndRol(institucionId, RolInstitucional.TECNICO);
    }
    public List<UsuarioInstitucional> listarPorInstitucion(Long institucionId) {
        return usuarioRepo.findByInstitucionId(institucionId);
    }

    public List<UsuarioInstitucional> buscarUsuariosPorInstitucion(String termino, Long institucionId) {
        return usuarioRepo.buscarPorNombreUsuarioCorreoYInstitucion(termino, institucionId);
    }

    public int contarUsuariosActivosPorInstitucion(Long institucionId) {
        return usuarioRepo.contarUsuariosActivosPorInstitucion(institucionId);
    }
    public List<UsuarioInstitucional> listarTodosTecnicos() {
        return usuarioRepo.findByRol(RolInstitucional.TECNICO);
    }

}
