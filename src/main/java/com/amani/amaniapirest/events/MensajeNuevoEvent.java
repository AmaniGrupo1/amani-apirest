package com.amani.amaniapirest.events;

import com.amani.amaniapirest.models.Mensaje;
import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado tras persistir un nuevo mensaje entre usuarios.
 *
 * <p>El listener comprueba si el destinatario tiene sesión WebSocket activa:
 * <ul>
 *   <li>Si está conectado → entrega el mensaje en tiempo real por STOMP.</li>
 *   <li>Si está offline   → envía una notificación push via Firebase.</li>
 * </ul>
 * </p>
 */
public class MensajeNuevoEvent extends ApplicationEvent {

    private final Mensaje mensaje;

    public MensajeNuevoEvent(Object source, Mensaje mensaje) {
        super(source);
        this.mensaje = mensaje;
    }

    public Mensaje getMensaje() {
        return mensaje;
    }
}

