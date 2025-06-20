package com.reporte_ciudadano.backend.configuraciones;

//import com.reporte_ciudadano.backend.seguridad.UsuarioDetallesServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

import com.reporte_ciudadano.backend.seguridad.JwtFiltro;
import com.reporte_ciudadano.backend.seguridad.UsuarioDetallesServicio;

import java.io.IOException;
import java.util.Collection;

@Configuration
public class SecurityConfig {

    @Autowired
    private UsuarioDetallesServicio usuarioDetallesServicio;

    @Autowired
    private JwtFiltro jwtFiltro;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .cacheControl(cache -> cache.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**",
                                "/imagenes/**", "/diseño/**",
                                "/verificacion/**", // si usas confirmación de token por HTML
                                "/nuevo-dispositivo-confirmado", // si usas esta vista HTML también
                                "/confirmacion",
                                "/api/usuariosApp/**")
                        .permitAll()
                        .requestMatchers("/panel/superadmin/**").hasRole("SUPERADMIN")
                        .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/asignaciones/**").hasAnyRole("ADMINISTRADOR", "OPERADOR")
                        .requestMatchers("/reportes/**", "/instituciones/**").authenticated()
                        // ✅ Estas rutas requieren token de sesión válido (autenticado)
                        .requestMatchers(
                                "/api/usuarios/**", // Incluye /{usuarioId}/notificaciones y más
                                "/api/reportes/**",
                                "/archivos/**",
                                "/api/contactos/**",
                                "/api/evidencias/**",
                                "/historial/**",
                                "/api/tipos-reporte/**")
                        .authenticated()
                        .anyRequest().authenticated()

                )

                .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form

                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/redirigir", true) // ✅ Aquí hacemos que se redirija al controlador que
                                                               // guarda sesión y maneja el rol
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioDetallesServicio);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
