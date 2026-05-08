package com.amani.amaniapirest.services;

import com.amani.amaniapirest.models.Cita;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("enviarNotificacionMensaje delega en mailSender")
    void enviarNotificacionMensaje_delegaEnMailSender() {
        emailService.enviarNotificacionMensaje("dest@amani.com", "Ana", "Hola");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getTo()).containsExactly("dest@amani.com");
        assertThat(sent.getSubject()).isEqualTo("Nuevo mensaje en Amani");
    }

    @Test
    @DisplayName("enviarBienvenida delega en mailSender")
    void enviarBienvenida_delegaEnMailSender() {
        emailService.enviarBienvenida("nuevo@amani.com", "Pedro");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getTo()).containsExactly("nuevo@amani.com");
    }

    @Test
    @DisplayName("enviarCitaCreada delega en mailSender")
    void enviarCitaCreada_delegaEnMailSender() {
        Cita cita = new Cita();
        cita.setStartDatetime(LocalDateTime.of(2026, 5, 10, 10, 0));
        cita.setDurationMinutes(60);
        cita.setMotivo("Seguimiento");

        emailService.enviarCitaCreada("paciente@amani.com", "Ana", cita);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("enviarCitaCancelada delega en mailSender")
    void enviarCitaCancelada_delegaEnMailSender() {
        Cita cita = new Cita();
        cita.setStartDatetime(LocalDateTime.of(2026, 5, 10, 10, 0));
        cita.setDurationMinutes(60);

        emailService.enviarCitaCancelada("paciente@amani.com", "Ana", cita, "Dr. Psi");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("enviarRecordatorioCita delega en mailSender")
    void enviarRecordatorioCita_delegaEnMailSender() {
        Cita cita = new Cita();
        cita.setStartDatetime(LocalDateTime.of(2026, 5, 10, 10, 0));
        cita.setDurationMinutes(60);

        emailService.enviarRecordatorioCita("paciente@amani.com", "Ana", cita);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("mailSender lanza excepción cuando falla SMTP")
    void mailSender_lanzaExcepcion_cuandoFallaSMTP() {
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() -> emailService.enviarBienvenida("fail@amani.com", "Test"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("SMTP error");
    }
}
