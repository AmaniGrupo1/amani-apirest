package com.amani.amaniapirest.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuración del cliente Firebase Admin SDK.
 *
 * <p>Lee credenciales desde una ruta externa ({@code firebase.credentials-path})
 * o, en su defecto, desde el classpath ({@code classpath:serviceAccountKey.json}).
 * La integración se activa con {@code firebase.enabled=true} (por defecto true).</p>
 *
 * <p>Si {@code firebase.enabled=false} o las credenciales no están disponibles,
 * los beans no se crean y la aplicación arranca normalmente sin Firebase.</p>
 */
@Configuration
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.database.url:}")
    private String firebaseDatabaseUrl;

    @Value("${firebase.credentials-path:}")
    private String firebaseCredentialsPath;

    /**
     * Crea el bean {@link FirebaseApp} cargando credenciales desde una ruta externa
     * o desde el classpath como fallback.
     *
     * @return instancia inicializada de FirebaseApp
     * @throws IOException si las credenciales no se pueden leer
     */
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream credentialsStream = resolveCredentialsStream();

        FirebaseOptions.Builder builder = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream));

        if (firebaseDatabaseUrl != null && !firebaseDatabaseUrl.isBlank()) {
            builder.setDatabaseUrl(firebaseDatabaseUrl);
        } else {
            log.warn("[Firebase] No se configuró firebase.database.url; las operaciones de RTDB no estarán disponibles.");
        }

        FirebaseOptions options = builder.build();
        FirebaseApp app = FirebaseApp.initializeApp(options);
        log.info("[Firebase] FirebaseApp inicializada correctamente.");
        return app;
    }

    /**
     * Expone el bean {@link FirebaseDatabase} para operaciones de Realtime Database.
     *
     * @param firebaseApp bean de FirebaseApp inyectado
     * @return instancia de FirebaseDatabase asociada al FirebaseApp
     */
    @Bean
    public FirebaseDatabase firebaseDatabase(FirebaseApp firebaseApp) {
        FirebaseDatabase db = FirebaseDatabase.getInstance(firebaseApp);
        log.info("[Firebase] FirebaseDatabase configurada para la URL: {}", firebaseDatabaseUrl);
        return db;
    }

    /**
     * Expone el bean {@link FirebaseAuth} para la gestión de usuarios y generación de tokens.
     *
     * @param firebaseApp bean de FirebaseApp inyectado
     * @return instancia de FirebaseAuth asociada al FirebaseApp
     */
    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        FirebaseAuth auth = FirebaseAuth.getInstance(firebaseApp);
        log.info("[Firebase] FirebaseAuth configurada correctamente.");
        return auth;
    }

    /**
     * Resuelve el stream de credenciales: primero intenta la ruta externa configurada
     * en {@code firebase.credentials-path}; si no está definida o no existe, recurre
     * al classpath ({@code serviceAccountKey.json}).
     *
     * @return InputStream con las credenciales de servicio
     * @throws IOException si no se pueden leer las credenciales desde ninguna fuente
     */
    private InputStream resolveCredentialsStream() throws IOException {
        // 1. Intentar ruta externa (variable de entorno o propiedad)
        if (firebaseCredentialsPath != null && !firebaseCredentialsPath.isBlank()) {
            Path path = Paths.get(firebaseCredentialsPath);
            if (Files.exists(path)) {
                log.info("[Firebase] Cargando credenciales desde ruta externa: {}", firebaseCredentialsPath);
                return Files.newInputStream(path);
            }
            log.warn("[Firebase] Ruta de credenciales configurada pero archivo no encontrado: {}", firebaseCredentialsPath);
        }

        // 2. Fallback a classpath
        log.info("[Firebase] Cargando credenciales desde classpath: serviceAccountKey.json");
        InputStream stream = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
        if (stream == null) {
            throw new IOException("No se encontraron credenciales de Firebase: "
                    + "ni en ruta externa ni en classpath (serviceAccountKey.json). "
                    + "Configure firebase.credentials-path o coloque el archivo en el classpath.");
        }
        return stream;
    }
}
