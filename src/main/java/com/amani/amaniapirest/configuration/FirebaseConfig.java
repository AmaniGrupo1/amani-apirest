package com.amani.amaniapirest.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 */
@Configuration
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.database.url:}")
    private String firebaseDatabaseUrl;

    @Value("${firebase.credentials-path:}")
    private String firebaseCredentialsPath;

    @Autowired(required = false)
    private SecretManagerTemplate secretManagerTemplate;

    @Value("${firebase.secret-name:firebase-service-account}")
    private String firebaseSecretName;

    @Value("${spring.cloud.gcp.project-id:}")
    private String gcpProjectId;

    /**
     * Crea el bean {@link FirebaseApp} cargando credenciales desde Secret Manager,
     * una ruta externa o desde el classpath como fallback.
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
     * Resuelve el stream de credenciales: primero intenta desde Secret Manager;
     * luego intenta la ruta externa configurada en {@code firebase.credentials-path};
     * si no está definida o no existe, recurre al classpath ({@code serviceAccountKey.json}).
     *
     * @return InputStream con las credenciales de servicio
     * @throws IOException si no se pueden leer las credenciales desde ninguna fuente
     */
    private InputStream resolveCredentialsStream() throws IOException {
        // 1. Intentar desde Secret Manager programáticamente (varias formas de nombre)
        if (secretManagerTemplate != null) {
            java.util.List<String> candidates = new java.util.ArrayList<>();
            // nombre simple configurado
            candidates.add(firebaseSecretName);
            // formas usadas por spring-cloud-gcp (prefijos comunes)
            candidates.add("sm://" + firebaseSecretName);
            candidates.add("sm@" + firebaseSecretName);
            // nombre completo con proyecto
            if (gcpProjectId != null && !gcpProjectId.isBlank()) {
                candidates.add("projects/" + gcpProjectId + "/secrets/" + firebaseSecretName + "/versions/latest");
            }

            for (String candidate : candidates) {
                try {
                    log.info("[Firebase] Intentando recuperar secreto desde Secret Manager (candidate='{}')", candidate);
                    String secretPayload = secretManagerTemplate.getSecretString(candidate);
                    if (secretPayload != null && !secretPayload.isBlank()) {
                        String trimmedSecret = secretPayload.trim();
                        log.info("[Firebase] Cargando credenciales desde Secret Manager ({}). Longitud: {}",
                                candidate, trimmedSecret.length());
                        return new java.io.ByteArrayInputStream(trimmedSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    }
                } catch (Exception e) {
                    log.warn("[Firebase] No fue posible leer el secreto '{}' desde Secret Manager: {}", candidate, e.toString());
                    log.debug("[Firebase] Traza al leer secreto '{}':", candidate, e);
                }
            }
            log.warn("[Firebase] No se pudo recuperar el secreto '{}' desde Secret Manager con ninguna de las formas probadas.", firebaseSecretName);
        }

        // 2. Intentar ruta externa (variable de entorno o propiedad)
        if (firebaseCredentialsPath != null && !firebaseCredentialsPath.isBlank()) {
            Path path = Paths.get(firebaseCredentialsPath);
            if (Files.exists(path)) {
                log.info("[Firebase] Cargando credenciales desde ruta externa: {}", firebaseCredentialsPath);
                return Files.newInputStream(path);
            }
            log.warn("[Firebase] Ruta de credenciales configurada pero archivo no encontrado: {}", firebaseCredentialsPath);
        }

        // 3. Fallback a classpath
        log.info("[Firebase] Cargando credenciales desde classpath: serviceAccountKey.json");
        InputStream stream = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
        if (stream == null) {
            throw new IOException("No se encontraron credenciales de Firebase: "
                    + "ni en Secret Manager, ni en ruta externa ni en classpath (serviceAccountKey.json). "
                    + "Configure Secret Manager, firebase.credentials-path o coloque el archivo en el classpath.");
        }
        return stream;
    }
}
