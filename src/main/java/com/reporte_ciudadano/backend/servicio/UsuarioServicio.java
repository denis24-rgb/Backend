package com.reporte_ciudadano.backend.servicio;

import com.reporte_ciudadano.backend.modelo.Usuario;
import com.reporte_ciudadano.backend.repositorio.UsuarioRepositorio;
import com.reporte_ciudadano.backend.seguridad.JwtUtil;
import com.reporte_ciudadano.backend.seguridad.UsuarioAppDetalles;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepositorio.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepositorio.findById(id);
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
    }

    public void eliminarUsuario(Long id) {
        usuarioRepositorio.deleteById(id);
    }

    public Usuario crearUsuarioConToken(String correo) {
        // Validar si el correo ya existe
        Optional<Usuario> usuarioExistente = usuarioRepositorio.findByCorreo(correo);

        if (usuarioExistente.isPresent()) {
            throw new IllegalStateException("El correo ya está registrado. Introduce otro correo");
        }

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setConfirmado(false);
        // Generar un token único y asignarlo al usuario
        String token = UUID.randomUUID().toString();
        usuario.setTokenVerificacion(token);

        // Guardar el usuario en la base de datos
        usuarioRepositorio.save(usuario);

        // Enviar el correo con el enlace de verificación
        emailService.enviarCorreoVerificacion(correo, usuario.getTokenVerificacion());

        System.out.println("Token generado: " + token); // <- LOG para verificar

        return usuario;
    }

    public boolean confirmarUsuario(String token) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByTokenVerificacion(token);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setConfirmado(true);
            usuarioRepositorio.save(usuario);
            return true;
        }
        System.out.println("Token no encontrado: " + token);
        return false;
    }

    public Usuario completarDatos(String correo, String nombre, String telefono, String direccion) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCorreo(correo);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (!usuario.isConfirmado()) {
                throw new IllegalStateException("El correo no está confirmado.");
            }
            usuario.setNombre(nombre);
            usuario.setTelefono(telefono);
            usuario.setDireccion(direccion);
            return usuarioRepositorio.save(usuario);
        }
        throw new IllegalStateException("Usuario no encontrado.");
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepositorio.findByCorreo(correo);
    }

    public Optional<Usuario> buscarPorToken(String token) {
        return usuarioRepositorio.findByTokenVerificacion(token);
    }

    /**************************************************************** */
    public Optional<Usuario> confirmarLoginDesdeToken(String token) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByTokenVerificacion(token);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            UsuarioAppDetalles detalles = new UsuarioAppDetalles(usuario);
            String nuevoTokenSesion = jwtUtil.generarToken(detalles);
            usuario.setTokenSesion(nuevoTokenSesion);
            // ACTUALIZA TOKEN DE SESIÓN
            usuarioRepositorio.save(usuario);
            return Optional.of(usuario);
        }

        return Optional.empty();
    }

}
