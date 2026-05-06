package com.amani.amaniapirest.gateway;

/**
 * Abstracción del gateway de notificaciones push.
 *
 * <p>Desacopla la capa de dominio de la implementación concreta
 * (Firebase FCM, emulador, o no-op para desarrollo local).</p>
 */
public interface PushNotificationGateway {

    /**
     * Envía una notificación push al dispositivo del destinatario.
     *
     * @param fcmToken token FCM del dispositivo de destino
     * @param title    título de la notificación
     * @param body     contenido de la notificación
     */
    void sendPush(String fcmToken, String title, String body);
}
