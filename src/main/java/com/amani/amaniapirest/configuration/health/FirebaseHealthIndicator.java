package com.amani.amaniapirest.configuration.health;

import com.google.firebase.FirebaseApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Indicador de salud de Firebase.
 *
 * <p>En modo local ({@code firebase.enabled=false}): devuelve UP con detalle
 * indicando que Firebase está desactivado.</p>
 *
 * <p>En modo GCP ({@code firebase.enabled=true}): devuelve UP si
 * {@link FirebaseApp} está inicializado, DOWN en caso contrario.</p>
 */
@Component
public class FirebaseHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(FirebaseHealthIndicator.class);

    private final boolean firebaseEnabled;
    private final FirebaseApp firebaseApp;

    public FirebaseHealthIndicator(
            @Value("${firebase.enabled:false}") boolean firebaseEnabled,
            @Nullable FirebaseApp firebaseApp) {
        this.firebaseEnabled = firebaseEnabled;
        this.firebaseApp = firebaseApp;
    }

    @Override
    public Health health() {
        if (!firebaseEnabled) {
            return Health.up()
                    .withDetail("status", "DISABLED")
                    .withDetail("mode", "local")
                    .withDetail("message", "Firebase está desactivado. Operaciones de chat y push son no-op.")
                    .build();
        }

        if (firebaseApp != null) {
            return Health.up()
                    .withDetail("status", "CONNECTED")
                    .withDetail("mode", "gcp")
                    .withDetail("name", firebaseApp.getName())
                    .withDetail("project", firebaseApp.getOptions().getProjectId())
                    .build();
        }

        return Health.down()
                .withDetail("status", "DISCONNECTED")
                .withDetail("mode", "gcp")
                .withDetail("message", "Firebase habilitado pero FirebaseApp no está inicializada.")
                .build();
    }
}
