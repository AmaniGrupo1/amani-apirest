package com.amani.amaniapirest.gateway.noop;

import com.amani.amaniapirest.gateway.FirebaseTokenGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementación no-op de {@link FirebaseTokenGateway} para entornos locales y de test.
 *
 * <p>Se registra cuando {@code firebase.enabled=false} (o está ausente).
 * Lanza {@link UnsupportedOperationException} si se intenta generar un token,
 * proporcionando un mensaje claro al llamador.</p>
 */
@Service
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpFirebaseTokenGateway implements FirebaseTokenGateway {

    private static final Logger log = LoggerFactory.getLogger(NoOpFirebaseTokenGateway.class);

    @Override
    public String createCustomToken(String uid, Map<String, Object> additionalClaims) {
        log.warn("[Token NoOp] Intento de generar token de Firebase con Firebase deshabilitado. uid: {}", uid);
        throw new UnsupportedOperationException(
                "Firebase Auth no está disponible en este entorno. "
                + "Habilite firebase.enabled=true para usar la autenticación de Firebase.");
    }
}
