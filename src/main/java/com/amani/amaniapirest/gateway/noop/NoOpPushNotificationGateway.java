package com.amani.amaniapirest.gateway.noop;

import com.amani.amaniapirest.gateway.PushNotificationGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Implementación no-op de {@link PushNotificationGateway} para entornos locales y de test.
 *
 * <p>Se registra cuando {@code firebase.enabled=false} (o está ausente).
 * Las notificaciones push no se envían a ningún dispositivo.</p>
 */
@Service
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpPushNotificationGateway implements PushNotificationGateway {

    private static final Logger log = LoggerFactory.getLogger(NoOpPushNotificationGateway.class);

    @Override
    public void sendPush(String fcmToken, String title, String body) {
        log.debug("[Push NoOp] Notificación omitida (Firebase deshabilitado). Título: '{}'", title);
    }
}
