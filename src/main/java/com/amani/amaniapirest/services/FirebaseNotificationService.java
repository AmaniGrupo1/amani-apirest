package com.amani.amaniapirest.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Servicio para el envío de notificaciones push mediante Firebase Cloud Messaging (FCM).
 *
 * <p>La implementación actual registra la notificación en el log para facilitar
 * el desarrollo sin credenciales de Firebase. Para activar el envío real:</p>
 * <ol>
 *   <li>Añadir {@code com.google.firebase:firebase-admin} al pom.xml.</li>
 *   <li>Situar {@code firebase-service-account.json} en {@code src/main/resources}.</li>
 *   <li>Inicializar {@code FirebaseApp} en un {@code @Bean} de configuración.</li>
 *   <li>Sustituir el cuerpo de {@link #enviarPush} por la llamada a
 *       {@code FirebaseMessaging.getInstance().send(Message)}.</li>
 * </ol>
 */
@Service
public class FirebaseNotificationService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseNotificationService.class);

    /**
     * Envía una notificación push al dispositivo identificado por {@code fcmToken}.
     *
     * <p>Si el token es nulo o vacío la operación se omite silenciosamente,
     * lo que permite trabajar con usuarios sin token registrado.</p>
     *
     * @param fcmToken token FCM del dispositivo destino
     * @param titulo   título de la notificación
     * @param cuerpo   texto del cuerpo de la notificación
     */
    public void enviarPush(String fcmToken, String titulo, String cuerpo) {
        if (!StringUtils.hasText(fcmToken)) {
            log.debug("[FCM] Token vacío — notificación omitida. Título: '{}'", titulo);
            return;
        }

        // TODO: sustituir por llamada real a Firebase Admin SDK
        // Message message = Message.builder()
        //         .setToken(fcmToken)
        //         .setNotification(Notification.builder().setTitle(titulo).setBody(cuerpo).build())
        //         .build();
        // String messageId = FirebaseMessaging.getInstance().send(message);
        // log.info("[FCM] Notificación enviada. messageId={}", messageId);

        log.info("[FCM-STUB] token={} | título='{}' | cuerpo='{}'", fcmToken, titulo, cuerpo);
    }
}

