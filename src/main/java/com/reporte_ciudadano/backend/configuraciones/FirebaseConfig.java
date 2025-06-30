package com.reporte_ciudadano.backend.configuraciones;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            String configPath = System.getenv("FIREBASE_CONFIG_PATH");
            if (configPath == null || configPath.isEmpty()) {
                throw new IllegalStateException("⚠️ La variable de entorno FIREBASE_CONFIG_PATH no está configurada.");
            }

            FileInputStream serviceAccount = new FileInputStream(configPath);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase inicializado en backend");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
