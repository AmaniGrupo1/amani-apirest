package com.amani.amaniapirest.services;

import com.amani.amaniapirest.gateway.PushNotificationGateway;
import com.google.firebase.FirebaseApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Servicio de envío de notificaciones push.
 *
 * <p>Delega en {@link PushNotificationGateway} para desacoplar
 * el dominio de la implementación concreta de Firebase.</p>
 *
 * <p>Solo se instancia si {@link FirebaseApp} está disponible.</p>
 */
@Service
@ConditionalOnBean(FirebaseApp.class)
public class FirebaseNotificationService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseNotificationService.class);

    private final PushNotificationGateway pushGateway;

    public FirebaseNotificationService(PushNotificationGateway pushGateway) {
        this.pushGateway = pushGateway;
    }

    public void enviarPush(String fcmToken, String titulo, String cuerpo) {
        if (!StringUtils.hasText(fcmToken)) {
            log.debug("[FCM] Token vacío — notificación omitida. Título: '{}'", titulo);
            return;
        }
        pushGateway.sendPush(fcmToken, titulo, cuerpo);
    }
}
