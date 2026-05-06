package com.amani.amaniapirest.gateway.firebase;

import com.amani.amaniapirest.gateway.PushNotificationGateway;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Implementación de {@link PushNotificationGateway} usando Firebase Cloud Messaging.
 *
 * <p>Solo se registra cuando {@link FirebaseApp} está disponible.</p>
 */
@Service
@Primary
@ConditionalOnBean(FirebaseApp.class)
public class FirebasePushNotificationGateway implements PushNotificationGateway {

    private static final Logger log = LoggerFactory.getLogger(FirebasePushNotificationGateway.class);

    @Override
    public void sendPush(String fcmToken, String title, String body) {
        if (!StringUtils.hasText(fcmToken)) {
            log.debug("[FCM] Token vacío — notificación omitida. Título: '{}'", title);
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("[FCM] Notificación enviada. messageId={}", messageId);
        } catch (Exception ex) {
            log.error("[FCM] Error enviando notificación: {}", ex.getMessage(), ex);
        }
    }
}
