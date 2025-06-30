package com.reporte_ciudadano.backend.servicio;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.reporte_ciudadano.backend.repositorio.UsuarioRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacionPushServicio {
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    public void enviarNotificacion(String token, String titulo, String cuerpo) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(cuerpo)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Notificación FCM enviada: " + response);

        } catch (FirebaseMessagingException e) {
            if (e.getErrorCode().equals("registration-token-not-registered")) {
                System.out.println("⚠️ Token no válido, eliminando token del usuario");

                // Aquí deberías buscar al usuario por ese token y limpiar su campo en la BD
                usuarioRepositorio.limpiarTokenDispositivoPorToken(token);
            } else {
                System.err.println("⚠️ Error al enviar notificación FCM: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al enviar notificación FCM: " + e.getMessage());
        }
    }

}
