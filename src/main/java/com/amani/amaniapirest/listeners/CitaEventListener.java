package com.amani.amaniapirest.listeners;

import com.amani.amaniapirest.events.CitaCanceladaEvent;
import com.amani.amaniapirest.events.CitaCreadaEvent;
import com.amani.amaniapirest.events.CitaRecordatorioEvent;
import com.amani.amaniapirest.gateway.PushNotificationGateway;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.services.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener unificado de eventos del dominio {@link Cita}.
 *
 * <p>Depende de {@link PushNotificationGateway} (abstracción) en lugar
 * de la implementación concreta de Firebase, permitiendo que funcione
 * correctamente tanto en modo local (NoOp) como en GCP (Firebase).</p>
 */
@Component
public class CitaEventListener {

    private final EmailService emailService;
    private final PushNotificationGateway pushGateway;

    public CitaEventListener(EmailService emailService, PushNotificationGateway pushGateway) {
        this.emailService = emailService;
        this.pushGateway = pushGateway;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCitaCreada(CitaCreadaEvent event) {
        Cita cita = event.getCita();

        Usuario pacienteUsuario = resolverUsuarioPaciente(cita);
        Usuario psicologoUsuario = resolverUsuarioPsicologo(cita);

        if (pacienteUsuario != null) {
            emailService.enviarCitaCreada(pacienteUsuario.getEmail(), pacienteUsuario.getNombre(), cita);
            enviarPushSiToken(pacienteUsuario, "Nueva cita programada",
                    "Tu cita ha sido agendada para el " + cita.getStartDatetime());
        }

        if (psicologoUsuario != null) {
            emailService.enviarCitaCreada(psicologoUsuario.getEmail(), psicologoUsuario.getNombre(), cita);
            enviarPushSiToken(psicologoUsuario, "Nueva cita asignada",
                    "Tienes una nueva cita el " + cita.getStartDatetime());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCitaCancelada(CitaCanceladaEvent event) {
        Cita cita = event.getCita();
        String canceladaPor = event.getCanceladaPor();

        Usuario pacienteUsuario = resolverUsuarioPaciente(cita);
        Usuario psicologoUsuario = resolverUsuarioPsicologo(cita);

        if (pacienteUsuario != null) {
            emailService.enviarCitaCancelada(pacienteUsuario.getEmail(), pacienteUsuario.getNombre(), cita, canceladaPor);
            enviarPushSiToken(pacienteUsuario, "Cita cancelada",
                    "Tu cita del " + cita.getStartDatetime() + " ha sido cancelada.");
        }

        if (psicologoUsuario != null) {
            emailService.enviarCitaCancelada(psicologoUsuario.getEmail(), psicologoUsuario.getNombre(), cita, canceladaPor);
            enviarPushSiToken(psicologoUsuario, "Cita cancelada",
                    "La cita del " + cita.getStartDatetime() + " ha sido cancelada.");
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onCitaRecordatorio(CitaRecordatorioEvent event) {
        Cita cita = event.getCita();

        Usuario pacienteUsuario = resolverUsuarioPaciente(cita);
        Usuario psicologoUsuario = resolverUsuarioPsicologo(cita);

        if (pacienteUsuario != null) {
            emailService.enviarRecordatorioCita(pacienteUsuario.getEmail(), pacienteUsuario.getNombre(), cita);
        }
        if (psicologoUsuario != null) {
            emailService.enviarRecordatorioCita(psicologoUsuario.getEmail(), psicologoUsuario.getNombre(), cita);
        }
    }

    private Usuario resolverUsuarioPaciente(Cita cita) {
        Paciente p = cita.getPaciente();
        return (p != null) ? p.getUsuario() : null;
    }

    private Usuario resolverUsuarioPsicologo(Cita cita) {
        Psicologo ps = cita.getPsicologo();
        return (ps != null) ? ps.getUsuario() : null;
    }

    private void enviarPushSiToken(Usuario usuario, String titulo, String cuerpo) {
        String token = usuario.getFcmToken();
        if (token != null && !token.isBlank()) {
            pushGateway.sendPush(token, titulo, cuerpo);
        }
    }
}
