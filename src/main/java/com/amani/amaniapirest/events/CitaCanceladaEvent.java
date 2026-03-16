package com.amani.amaniapirest.events;

import com.amani.amaniapirest.models.Cita;
import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado cuando una cita pasa al estado {@code cancelada}.
 *
 * <p>Incluye la cita afectada y el email del actor que realizó la cancelación
 * (paciente, psicólogo o administrador) para personalizar la notificación.</p>
 */
public class CitaCanceladaEvent extends ApplicationEvent {

    private final Cita cita;
    /** Email del usuario que realizó la cancelación (puede ser null si es sistema). */
    private final String canceladaPor;

    public CitaCanceladaEvent(Object source, Cita cita, String canceladaPor) {
        super(source);
        this.cita = cita;
        this.canceladaPor = canceladaPor;
    }

    public Cita getCita() {
        return cita;
    }

    public String getCanceladaPor() {
        return canceladaPor;
    }
}

