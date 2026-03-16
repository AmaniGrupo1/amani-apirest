package com.amani.amaniapirest.listeners;

import com.amani.amaniapirest.events.UsuarioRegistradoEvent;
import com.amani.amaniapirest.services.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener del dominio {@link com.amani.amaniapirest.models.Usuario}.
 *
 * <p>Envía el email de bienvenida tras confirmar que el nuevo usuario
 * ha sido persistido correctamente en base de datos (fase AFTER_COMMIT).</p>
 */
@Component
public class UsuarioRegistradoListener {

    private final EmailService emailService;

    public UsuarioRegistradoListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUsuarioRegistrado(UsuarioRegistradoEvent event) {
        emailService.enviarBienvenida(event.getEmail(), event.getNombre());
    }
}

