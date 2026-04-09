package com.amani.amaniapirest.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Servicio para el envío de notificaciones push mediante Firebase Cloud Messaging (FCM).
 *
 * <p>Usa Firebase Admin SDK para enviar notificaciones. Si el token es nulo/ vacío
 * la notificación se omite.</p>
 */
@Service
public class FirebaseNotificationService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseNotificationService.class);

    public void enviarPush(String fcmToken, String titulo, String cuerpo) {
        if (!StringUtils.hasText(fcmToken)) {
            log.debug("[FCM] Token vacío — notificación omitida. Título: '{}'", titulo);
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder().setTitle(titulo).setBody(cuerpo).build())
                    .build();

            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("[FCM] Notificación enviada. messageId={}", messageId);
        } catch (Exception ex) {
            log.error("[FCM] Error enviando notificación: {}", ex.getMessage(), ex);
        }
    }
}

