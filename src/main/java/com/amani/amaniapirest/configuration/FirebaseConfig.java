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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuración del cliente Firebase Admin SDK.
 *
 * <p>Solo se activa cuando {@code firebase.enabled=true} en la configuración.
 * Cuando la propiedad está ausente o es {@code false}, ninguno de los beans
 * Firebase se registra y la aplicación arranca normalmente sin Firebase.</p>
 *
 * <h3>Credenciales (orden de resolución)</h3>
 * <ol>
 *   <li>Contenido del secreto inyectado por Spring Cloud GCP
 *       (property {@code firebase-service-account}).</li>
 *   <li>Ruta externa configurada en {@code firebase.credentials-path}.</li>
 *   <li>Classpath: {@code serviceAccountKey.json}.</li>
 *   <li>Application Default Credentials (ADC) automático en GCP.</li>
 * </ol>
 *
 * <h3>Emuladores</h3>
 * <p>Cuando {@code firebase.emulator.enabled=true}, se configuran las variables
 * de entorno para conectar a los emuladores locales de Firebase Auth y RTDB.</p>
 */
@Configuration
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.database.url:}")
    private String firebaseDatabaseUrl;

    @Value("${firebase.credentials-path:}")
    private String firebaseCredentialsPath;

    @Value("${firebase-service-account:}")
    private String firebaseSecretPayload;

    @Value("${firebase.emulator.enabled:false}")
    private boolean emulatorEnabled;

    @Value("${firebase.auth.emulator-host:}")
    private String authEmulatorHost;

    @Value("${firebase.auth.emulator-port:9099}")
    private int authEmulatorPort;

    @Value("${spring.cloud.gcp.project-id:}")
    private String gcpProjectId;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        log.info("[Firebase] Iniciando configuración de Firebase (emulator={})", emulatorEnabled);

        if (emulatorEnabled) {
            configureEmulators();
        }

        FirebaseApp existingApp = getExistingApp();
        if (existingApp != null) {
            log.info("[Firebase] FirebaseApp ya inicializada, reutilizando instancia existente.");
            return existingApp;
        }

        InputStream credentialsStream = resolveCredentialsStream();
        GoogleCredentials credentials;

        if (credentialsStream != null) {
            credentials = GoogleCredentials.fromStream(credentialsStream);
            log.info("[Firebase] Credenciales cargadas desde fuente explícita.");
        } else {
            log.info("[Firebase] Usando Application Default Credentials (ADC).");
            credentials = GoogleCredentials.getApplicationDefault();
        }

        FirebaseOptions.Builder builder = FirebaseOptions.builder()
                .setCredentials(credentials);

        if (firebaseDatabaseUrl != null && !firebaseDatabaseUrl.isBlank()) {
            builder.setDatabaseUrl(firebaseDatabaseUrl);
        } else {
            log.warn("[Firebase] No se configuró firebase.database.url; las operaciones de RTDB no estarán disponibles.");
        }

        if (gcpProjectId != null && !gcpProjectId.isBlank()) {
            builder.setProjectId(gcpProjectId);
        }

        FirebaseOptions options = builder.build();
        FirebaseApp app = FirebaseApp.initializeApp(options);
        log.info("[Firebase] FirebaseApp inicializada correctamente (name={}).", app.getName());
        return app;
    }

    @Bean
    public FirebaseDatabase firebaseDatabase(FirebaseApp firebaseApp) {
        FirebaseDatabase db = FirebaseDatabase.getInstance(firebaseApp);
        log.info("[Firebase] FirebaseDatabase configurada para la URL: {}", firebaseDatabaseUrl);
        return db;
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        FirebaseAuth auth = FirebaseAuth.getInstance(firebaseApp);
        log.info("[Firebase] FirebaseAuth configurada correctamente.");
        return auth;
    }

    /**
     * Comprueba si ya existe una instancia de FirebaseApp inicializada.
     * Previene el error "FirebaseApp name [DEFAULT] already exists".
     */
    private FirebaseApp getExistingApp() {
        try {
            return FirebaseApp.getInstance();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * Configura las variables de entorno para los emuladores de Firebase.
     */
    private void configureEmulators() {
        String authHost = authEmulatorHost.isEmpty() ? "localhost" : authEmulatorHost;
        String authEmulatorUrl = String.format("%s:%d", authHost, authEmulatorPort);
        System.setProperty("FIREBASE_AUTH_EMULATOR_HOST", authEmulatorUrl);
        log.info("[Firebase] Emulador Auth configurado: {}", authEmulatorUrl);

        if (firebaseDatabaseUrl != null && !firebaseDatabaseUrl.isBlank()) {
            log.info("[Firebase] Emulador RTDB configurado vía firebase.database.url: {}", firebaseDatabaseUrl);
        }

        log.info("[Firebase] ══════════════════════════════════════════════");
        log.info("[Firebase] ⚠️  MODO EMULADOR ACTIVADO — No usar en producción");
        log.info("[Firebase] ══════════════════════════════════════════════");
    }

    /**
     * Resuelve el stream de credenciales en orden de prioridad:
     * <ol>
     *   <li>Contenido del secreto inyectado por Spring Cloud GCP.</li>
     *   <li>Ruta externa configurada en {@code firebase.credentials-path}.</li>
     *   <li>Classpath: {@code serviceAccountKey.json}.</li>
     * </ol>
     * <p>Si ninguna fuente está disponible, retorna {@code null} para que
     * el llamador use Application Default Credentials (ADC).</p>
     *
     * @return InputStream con las credenciales, o null si se debe usar ADC
     * @throws IOException si ocurre un error de I/O leyendo credenciales
     */
    private InputStream resolveCredentialsStream() throws IOException {
        // 1. Secreto inyectado por Spring Cloud GCP (spring.config.import=optional:sm://)
        if (firebaseSecretPayload != null && !firebaseSecretPayload.isBlank()) {
            log.info("[Firebase] Cargando credenciales desde Secret Manager (vía spring.config.import). Longitud: {}",
                    firebaseSecretPayload.length());
            return new ByteArrayInputStream(firebaseSecretPayload.trim().getBytes(StandardCharsets.UTF_8));
        }

        // 2. Ruta externa (variable de entorno o propiedad)
        if (firebaseCredentialsPath != null && !firebaseCredentialsPath.isBlank()) {
            Path path = Paths.get(firebaseCredentialsPath);
            if (Files.exists(path)) {
                log.info("[Firebase] Cargando credenciales desde ruta externa: {}", firebaseCredentialsPath);
                return Files.newInputStream(path);
            }
            log.warn("[Firebase] Ruta de credenciales configurada pero archivo no encontrado: {}", firebaseCredentialsPath);
        }

        // 3. Fallback a classpath
        InputStream classpathStream = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
        if (classpathStream != null) {
            log.info("[Firebase] Cargando credenciales desde classpath: serviceAccountKey.json");
            return classpathStream;
        }

        // 4. ADC (se retorna null para que el llamador use GoogleCredentials.getApplicationDefault())
        log.info("[Firebase] No se encontraron credenciales explícitas. Se usará Application Default Credentials (ADC).");
        return null;
    }
}
