package com.reporte_ciudadano.backend.configuraciones;

import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.modelo.RolInstitucional;
import com.reporte_ciudadano.backend.repositorio.UsuarioInstitucionalRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class CreadorSuperadmin {

    @Autowired
    private UsuarioInstitucionalRepositorio usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void crearSuperadminSiNoExiste() {
        String correo = "superadmin@app.com";

        if (usuarioRepo.findByCorreo(correo).isEmpty()) {
            UsuarioInstitucional admin = new UsuarioInstitucional();
            admin.setActivo(true);
            admin.setCorreo(correo);
            admin.setNombre("Super Administrador");
            admin.setRol(RolInstitucional.SUPERADMIN);
            admin.setTelefono("70000000");
            admin.setUsuario("superadmin");
            admin.setContrasena(passwordEncoder.encode("123456"));
            admin.setInstitucion(null); // asegurarse de que en la entidad se permita null

            usuarioRepo.save(admin);
            System.out.println("✅ Superadmin creado automáticamente");
        } else {
            System.out.println("ℹ️ Ya existe un superadmin, no se creó otro.");
        }
    }
}
