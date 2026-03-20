package com.amani.amaniapirest.services;

import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.events.CitaRecordatorioEvent;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.repository.CitaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler que comprueba periódicamente si hay citas programadas en las próximas
 * 24 horas y publica un {@link CitaRecordatorioEvent} para cada una.
 *
 * <p>Se ejecuta cada hora mediante una expresión cron. Spring llama a los
 * métodos {@code @Scheduled} fuera de cualquier transacción, por lo que el
 * listener correspondiente usa {@code fallbackExecution = true}.</p>
 */
@Component
public class CitaRecordatorioScheduler {

    private static final Logger log = LoggerFactory.getLogger(CitaRecordatorioScheduler.class);

    private final CitaRepository citaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CitaRecordatorioScheduler(CitaRepository citaRepository,
                                      ApplicationEventPublisher eventPublisher) {
        this.citaRepository = citaRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Busca citas confirmadas que se celebrarán entre las próximas 23 y 25 horas
     * (ventana de 2 h para tolerar desfases en la ejecución del cron) y emite
     * un evento de recordatorio por cada una.
     *
     * <p>Cron: cada hora en punto → {@code 0 0 * * * *}</p>
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(readOnly = true)
    public void enviarRecordatorios() {
        LocalDateTime desde = LocalDateTime.now().plusHours(23);
        LocalDateTime hasta = LocalDateTime.now().plusHours(25);

        List<Cita> citasProximas = citaRepository
                .findByStartDatetimeBetweenAndEstado(desde, hasta, EstadoCita.confirmada);

        if (citasProximas.isEmpty()) {
            log.debug("[Scheduler] Sin citas próximas en la ventana [{} – {}]", desde, hasta);
            return;
        }

        log.info("[Scheduler] Enviando {} recordatorio(s) de cita.", citasProximas.size());
        citasProximas.forEach(cita ->
                eventPublisher.publishEvent(new CitaRecordatorioEvent(this, cita))
        );
    }
}

