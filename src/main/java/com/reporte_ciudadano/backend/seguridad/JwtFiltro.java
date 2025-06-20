package com.reporte_ciudadano.backend.seguridad;

import com.reporte_ciudadano.backend.modelo.Usuario;
import com.reporte_ciudadano.backend.repositorio.UsuarioRepositorio;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtFiltro extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepositorio usuarioRepo;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                String correo = jwtUtil.extraerCorreo(token);

                if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreo(correo);

                    if (usuarioOpt.isPresent()) {
                        Usuario usuario = usuarioOpt.get();

                        UsuarioAppDetalles userDetails = new UsuarioAppDetalles(usuario);

                        // ✅ Solo permitir si el token coincide con el guardado
                        if (usuario.isConfirmado()
                                && token.equals(usuario.getTokenSesion()) // el token es el activo
                                && jwtUtil.validarToken(token, userDetails)) {

                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } else {
                            System.out.println("❌ Token inválido o no coincide con el token de sesión activo");
                        }
                    }
                }

            } catch (Exception e) {
                // En producción puedes registrar el error
                System.out.println("❌ Error al procesar JWT: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
