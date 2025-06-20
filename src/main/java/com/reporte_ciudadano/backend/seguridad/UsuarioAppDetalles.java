package com.reporte_ciudadano.backend.seguridad;

import com.reporte_ciudadano.backend.modelo.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UsuarioAppDetalles implements UserDetails {

    private final Usuario usuario;

    public UsuarioAppDetalles(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // No maneja roles aún
    }

    @Override
    public String getPassword() {
        return null; // No hay contraseña, solo JWT por ahora
    }

    @Override
    public String getUsername() {
        return usuario.getCorreo(); // El identificador
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.isConfirmado(); // Solo se habilita si confirmó su correo
    }
}
