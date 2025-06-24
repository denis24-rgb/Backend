// NUEVO CÓDIGO LISETH - Controlador de login completo
package com.reporte_ciudadano.backend.controlador;


import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LoginControlador {

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login"; // login.html en templates
    }
}

// FIN NUEVO CÓDIGO LISETH
