//package com.reporte_ciudadano.backend.seguridad;
//
//
//
//import com.reporte_ciudadano.backend.modelo.UsuarioInstitucional;
//import com.reporte_ciudadano.backend.repositorio.UsuarioInstitucionalRepositorio;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UsuarioDetallesServicio implements UserDetailsService {
//
//    @Autowired
//    private UsuarioInstitucionalRepositorio usuarioRepo;
//
//    @Override
//    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
//        UsuarioInstitucional usuario = usuarioRepo.findByCorreo(correo)
//                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + correo));
//
//        return new UsuarioDetalles(usuario);
//    }
//}
