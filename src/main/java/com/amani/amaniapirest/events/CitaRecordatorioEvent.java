package com.amani.amaniapirest.events;

import com.amani.amaniapirest.models.Cita;
import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado por el scheduler cuando una cita ocurre en menos de 24 horas.
 *
 * <p>Al ser originado por un {@code @Scheduled} y no por una transacción JPA,
 * el listener usa {@code fallbackExecution = true} para ejecutarse igualmente
 * fuera de un contexto transaccional.</p>
 *
 * <p>Solo se envía notificación por email (sin push) para evitar saturar al usuario.</p>
 */
public class CitaRecordatorioEvent extends ApplicationEvent {

    private final Cita cita;

    public CitaRecordatorioEvent(Object source, Cita cita) {
        super(source);
        this.cita = cita;
    }

    public Cita getCita() {
        return cita;
    }
}

