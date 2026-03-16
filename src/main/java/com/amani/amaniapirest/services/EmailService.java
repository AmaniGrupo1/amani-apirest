package com.amani.amaniapirest.services;

import com.amani.amaniapirest.models.Cita;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final String FROM = "noreply@amanipsicologia.com";

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ----------------------------------------------------------------
    // Mensajería
    // ----------------------------------------------------------------

    /**
     * Envía una notificación al destinatario cuando recibe un nuevo mensaje.
     *
     * @param destinatario     email del receptor
     * @param nombreRemitente  nombre del usuario que envió el mensaje
     * @param contenidoMensaje texto del mensaje
     */
    public void enviarNotificacionMensaje(String destinatario, String nombreRemitente, String contenidoMensaje) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(FROM);
        mail.setTo(destinatario);
        mail.setSubject("Nuevo mensaje en Amani");
        mail.setText("Has recibido un nuevo mensaje de " + nombreRemitente + ":\n\n" + contenidoMensaje);
        mailSender.send(mail);
    }

    // ----------------------------------------------------------------
    // Registro
    // ----------------------------------------------------------------

    /**
     * Envía un correo de bienvenida cuando un usuario completa su registro.
     *
     * @param destinatario email del nuevo usuario
     * @param nombre       nombre de pila del nuevo usuario
     */
    public void enviarBienvenida(String destinatario, String nombre) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(FROM);
        mail.setTo(destinatario);
        mail.setSubject("¡Bienvenido/a a Amani!");
        mail.setText(
                "Hola " + nombre + ",\n\n" +
                "Tu registro en la plataforma Amani se ha completado correctamente.\n" +
                "Ya puedes iniciar sesión y comenzar a usar la aplicación.\n\n" +
                "El equipo de Amani"
        );
        mailSender.send(mail);
    }

    // ----------------------------------------------------------------
    // Citas
    // ----------------------------------------------------------------

    /**
     * Notifica a un participante que se ha creado una nueva cita.
     *
     * @param destinatario email del paciente o psicólogo
     * @param nombre       nombre del destinatario
     * @param cita         entidad cita recién creada
     */
    public void enviarCitaCreada(String destinatario, String nombre, Cita cita) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(FROM);
        mail.setTo(destinatario);
        mail.setSubject("Nueva cita programada — Amani");
        mail.setText(
                "Hola " + nombre + ",\n\n" +
                "Se ha programado una nueva cita para el " + cita.getStartDatetime() +
                " con una duración de " + cita.getDurationMinutes() + " minutos.\n" +
                (cita.getMotivo() != null ? "Motivo: " + cita.getMotivo() + "\n" : "") +
                "\nPuedes consultar todos los detalles en tu área personal de Amani.\n\n" +
                "El equipo de Amani"
        );
        mailSender.send(mail);
    }

    /**
     * Notifica a un participante que una cita ha sido cancelada.
     *
     * @param destinatario email del paciente o psicólogo
     * @param nombre       nombre del destinatario
     * @param cita         entidad cita cancelada
     * @param canceladaPor email o nombre de quien realizó la cancelación (puede ser null)
     */
    public void enviarCitaCancelada(String destinatario, String nombre, Cita cita, String canceladaPor) {
        String actorTexto = (canceladaPor != null && !canceladaPor.isBlank())
                ? " por " + canceladaPor
                : "";
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(FROM);
        mail.setTo(destinatario);
        mail.setSubject("Cita cancelada — Amani");
        mail.setText(
                "Hola " + nombre + ",\n\n" +
                "Tu cita del " + cita.getStartDatetime() +
                " ha sido cancelada" + actorTexto + ".\n\n" +
                "Si tienes alguna duda, contacta con nosotros a través de la plataforma.\n\n" +
                "El equipo de Amani"
        );
        mailSender.send(mail);
    }

    /**
     * Envía un recordatorio de cita a un participante 24 horas antes de su celebración.
     *
     * @param destinatario email del paciente o psicólogo
     * @param nombre       nombre del destinatario
     * @param cita         entidad cita próxima
     */
    public void enviarRecordatorioCita(String destinatario, String nombre, Cita cita) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(FROM);
        mail.setTo(destinatario);
        mail.setSubject("Recordatorio de cita mañana — Amani");
        mail.setText(
                "Hola " + nombre + ",\n\n" +
                "Te recordamos que mañana tienes una cita a las " + cita.getStartDatetime().toLocalTime() +
                " con una duración de " + cita.getDurationMinutes() + " minutos.\n\n" +
                "¡Nos vemos pronto!\n\n" +
                "El equipo de Amani"
        );
        mailSender.send(mail);
    }
}

