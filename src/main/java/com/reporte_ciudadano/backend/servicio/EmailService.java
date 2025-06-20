package com.reporte_ciudadano.backend.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  @Autowired
  private JavaMailSender mailSender;

  public void enviarCorreoVerificacion(String para, String token) {
    String urlVerificacion = "http://192.168.2.108:8084/api/usuariosApp/confirmar?token=" + token;

    SimpleMailMessage mensaje = new SimpleMailMessage();
    mensaje.setTo(para);
    mensaje.setSubject("Verificación de Correo - Reporte Ciudadano");
    mensaje.setText(
        "Por favor, haz clic en el siguiente enlace para confirmar tu correo en Report App:\n\n" + urlVerificacion);

    mailSender.send(mensaje);
    System.out.println("Correo de verificación enviado a: " + para); // <- LOG para verificar
  }

  public void enviarCorreoLoginNuevoDispositivo(String correo, String token) {
    String urlLogin = "http://192.168.2.108:8084/api/usuariosApp/confirmar-login?token=" + token;

    SimpleMailMessage mensaje = new SimpleMailMessage();
    mensaje.setTo(correo);
    mensaje.setSubject("Verificación de nuevo dispositivo - Reporte Ciudadano");
    mensaje.setText("Haz clic en el siguiente enlace para iniciar sesión desde el nuevo dispositivo:\n\n" + urlLogin);

    mailSender.send(mensaje);
    System.out.println("Correo de verificación enviado a nuevo dispositivo: " + correo);
  }

}
