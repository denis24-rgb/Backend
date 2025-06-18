package com.reporte_ciudadano.backend.controlador;



import com.reporte_ciudadano.backend.modelo.RolInstitucional;
import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
import com.reporte_ciudadano.backend.servicio.InstitucionServicio;
import com.reporte_ciudadano.backend.servicio.UsuarioInstitucionalServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioInstitucionalControlador {

    @Autowired
    private UsuarioInstitucionalServicio usuarioServicio;

    @Autowired
    private InstitucionServicio institucionServicio;

    @GetMapping
    public String listarUsuarios(@RequestParam(value = "buscar", required = false) String buscar,
                                 Model model, Principal principal) {
        // Obtener el usuario autenticado
        String correo = principal.getName();
        UsuarioInstitucional actual = usuarioServicio.obtenerPorCorreo(correo).orElseThrow();

        List<UsuarioInstitucional> usuarios;

        if (buscar != null && !buscar.trim().isEmpty()) {
            // Filtrar por búsqueda dentro de su institución si no es superadmin
            usuarios = actual.getRol() == RolInstitucional.SUPERADMIN
                    ? usuarioServicio.buscarUsuarios(buscar.trim())
                    : usuarioServicio.buscarUsuariosPorInstitucion(buscar.trim(), actual.getInstitucion().getId());
        } else {
            // Listar todos o solo los de su institución
            usuarios = actual.getRol() == RolInstitucional.SUPERADMIN
                    ? usuarioServicio.listarTodos()
                    : usuarioServicio.listarPorInstitucion(actual.getInstitucion().getId());
        }

        model.addAttribute("usuarios", usuarios);

        // Enviar usuario vacío o con error
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new UsuarioInstitucional());
        }

        // SUPERADMIN puede ver todas las instituciones, otros no
        if (actual.getRol() == RolInstitucional.SUPERADMIN) {
            model.addAttribute("instituciones", institucionServicio.listarTodas());
        } else {
            model.addAttribute("instituciones", List.of(actual.getInstitucion()));
        }

        model.addAttribute("roles", RolInstitucional.values());

        return "usuarios";
    }


    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute UsuarioInstitucional usuario,
                                 RedirectAttributes redirectAttrs,
                                 Principal principal) {
        try {
            // Obtener el usuario autenticado
            String correo = principal.getName();
            UsuarioInstitucional actual = usuarioServicio.obtenerPorCorreo(correo).orElseThrow();

            // Si no es superadmin, forzar institución
            if (actual.getRol() != RolInstitucional.SUPERADMIN) {
                usuario.setInstitucion(actual.getInstitucion());
            }

            usuarioServicio.guardar(usuario);
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
            redirectAttrs.addFlashAttribute("usuario", usuario);
            return "redirect:/usuarios";
        }

        return "redirect:/usuarios";
    }





    @PostMapping("/eliminar")
    public String eliminarUsuario(@RequestParam("id") Long id) {
        usuarioServicio.eliminar(id);
        return "redirect:/usuarios";
    }
}
