package com.amani.amaniapirest.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Configuración del cliente Firebase Admin SDK.
 *
 * <p>Lee las credenciales desde {@code serviceAccountKey.json} (classpath) y expone un
 * {@link com.google.firebase.FirebaseApp} como bean para que
 * {@link com.amani.amaniapirest.services.FirebaseNotificationService} pueda usar FCM.</p>
 *
 * <p>El bean solo se crea si el archivo de credenciales existe en el classpath.
 * En entornos donde no está disponible (p.ej. CI), la configuración se omite
 * y {@link com.google.firebase.messaging.FirebaseMessaging} no estará disponible,
 * pero la aplicación arranca correctamente.</p>
 */
@Configuration
public class FirebaseConfig {
    @Value("${firebase.database.url:}")
    private String firebaseDatabaseUrl;

    @Bean
    @ConditionalOnResource(resources = "classpath:serviceAccountKey.json")
    public FirebaseApp firebaseApp() throws IOException {
        InputStream serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream();

        FirebaseOptions.Builder builder = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount));

        // Si se provee URL de Realtime Database la añadimos (necesario para RTDB)
        if (firebaseDatabaseUrl != null && !firebaseDatabaseUrl.isBlank()) {
            builder.setDatabaseUrl(firebaseDatabaseUrl);
        }

        FirebaseOptions options = builder.build();
        return FirebaseApp.initializeApp(options);
    }
}
