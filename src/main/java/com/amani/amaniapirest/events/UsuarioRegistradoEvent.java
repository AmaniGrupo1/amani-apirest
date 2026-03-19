package com.amani.amaniapirest.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado tras completar el registro de un nuevo usuario en el sistema.
 *
 * <p>{@link com.amani.amaniapirest.listeners.UsuarioRegistradoListener} escucha
 * este evento en fase AFTER_COMMIT para enviar el correo de bienvenida.</p>
 */
@Getter
public class UsuarioRegistradoEvent extends ApplicationEvent {

    private final String email;
    private final String nombre;

    public UsuarioRegistradoEvent(Object source, String email, String nombre) {
        super(source);
        this.email = email;
        this.nombre = nombre;
    }

}

