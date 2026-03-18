package com.amani.amaniapirest.events;

import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado tras completar el registro de un nuevo usuario en el sistema.
 *
 * <p>{@link com.amani.amaniapirest.listeners.UsuarioRegistradoListener} escucha
 * este evento en fase AFTER_COMMIT para enviar el correo de bienvenida.</p>
 */
public class UsuarioRegistradoEvent extends ApplicationEvent {

    private final String email;
    private final String nombre;

    public UsuarioRegistradoEvent(Object source, String email, String nombre) {
        super(source);
        this.email = email;
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getNombre() {
        return nombre;
    }
}

