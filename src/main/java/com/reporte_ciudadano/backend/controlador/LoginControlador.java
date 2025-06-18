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

    private final UsuarioInstitucionalServicio usuarioServicio;

    // Mostrar el formulario de login
    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login"; // login.html en templates
    }

    // Redirigir al panel según el rol del usuario
    @GetMapping("/redirigir") //NUEVO DE LISETH
    public String redireccionarPorRol(Authentication auth, HttpSession session) {
        String correo = ((UserDetails) auth.getPrincipal()).getUsername();


        Optional<UsuarioInstitucional> usuarioOpt = usuarioServicio.obtenerPorCorreo(correo);
        if (usuarioOpt.isEmpty()) return "redirect:/login?error";

        UsuarioInstitucional usuario = usuarioOpt.get();
        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioRol", usuario.getRol().name());

        return switch (usuario.getRol()) {
            case TECNICO -> "redirect:/tecnico";
            case OPERADOR -> "redirect:/operador";
            case ADMINISTRADOR -> "redirect:/inicio";
            case SUPERADMIN -> "redirect:/panel/superadmin";
            default -> "redirect:/login?error=rol";
        };
    }

}
// FIN NUEVO CÓDIGO LISETH
