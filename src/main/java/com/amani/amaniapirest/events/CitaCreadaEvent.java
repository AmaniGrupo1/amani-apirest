package com.amani.amaniapirest.events;

import com.amani.amaniapirest.models.Cita;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado inmediatamente después de que una nueva cita se persiste
 * en base de datos (fase AFTER_COMMIT).
 *
 * <p>Los listeners pueden usar este evento para notificar por email y/o
 * push al paciente y al psicólogo sobre la nueva cita.</p>
 */
@Getter
public class CitaCreadaEvent extends ApplicationEvent {

    private final Cita cita;

    public CitaCreadaEvent(Object source, Cita cita) {
        super(source);
        this.cita = cita;
    }

}

