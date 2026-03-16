package com.amani.amaniapirest.listeners;

import com.amani.amaniapirest.events.CitaCanceladaEvent;
import com.amani.amaniapirest.events.CitaCreadaEvent;
import com.amani.amaniapirest.events.CitaRecordatorioEvent;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.services.EmailService;
import com.amani.amaniapirest.services.FirebaseNotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener unificado de eventos del dominio {@link Cita}.
 *
 * <ul>
 *   <li>{@link CitaCreadaEvent}     → email + push a paciente y psicólogo.</li>
 *   <li>{@link CitaCanceladaEvent}  → email + push a ambas partes.</li>
 *   <li>{@link CitaRecordatorioEvent} → email de recordatorio 24 h antes (sin push).</li>
 * </ul>
 *
 * <p>Todos los métodos son {@code @Async} para no bloquear el hilo del contexto
 * transaccional y se ejecutan después del commit ({@code AFTER_COMMIT}).</p>
 */
@Component
public class CitaEventListener {

    private final EmailService emailService;
    private final FirebaseNotificationService firebaseService;

    public CitaEventListener(EmailService emailService, FirebaseNotificationService firebaseService) {
        this.emailService = emailService;
        this.firebaseService = firebaseService;
    }

    // ----------------------------------------------------------------
    // Cita creada
    // ----------------------------------------------------------------

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

    // ----------------------------------------------------------------
    // Cita cancelada
    // ----------------------------------------------------------------

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

    // ----------------------------------------------------------------
    // Recordatorio 24 h antes (lanzado por @Scheduled → fallbackExecution)
    // ----------------------------------------------------------------

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

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private Usuario resolverUsuarioPaciente(Cita cita) {
        Paciente p = cita.getPaciente();
        return (p != null) ? p.getUsuario() : null;
    }

    private Usuario resolverUsuarioPsicologo(Cita cita) {
        Psicologo ps = cita.getPsicologo();
        return (ps != null) ? ps.getUsuario() : null;
    }

    /**
     * Envía push solo si el usuario tiene token FCM registrado.
     * Guardia explícita en el call-site: el servicio no recibe tokens nulos.
     */
    private void enviarPushSiToken(Usuario usuario, String titulo, String cuerpo) {
        String token = usuario.getFcmToken();
        if (token != null && !token.isBlank()) {
            firebaseService.enviarPush(token, titulo, cuerpo);
        }
    }
}

