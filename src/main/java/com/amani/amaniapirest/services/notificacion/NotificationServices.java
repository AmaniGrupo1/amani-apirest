package com.amani.amaniapirest.services.notificacion;

import com.amani.amaniapirest.dto.notificacion.NotificacionResponseDTO;
import com.amani.amaniapirest.models.Notificacion;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.notificacion.NotificacionRepository;
import com.amani.amaniapirest.services.FirebaseNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServices {

    private final FirebaseNotificationService firebase;
    private final NotificacionRepository notificacionRepository;

    // ─────────────────────────────────────────
    public void enviarNotificacion(Usuario usuario, String titulo, String mensaje) {

        if (usuario == null) return;
        if (!usuario.isNotificacionesActivas()) return;

        // Guardar en BD SIEMPRE
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setCreadaEn(LocalDateTime.now());

        notificacionRepository.save(notificacion);

        // DEBUG CLAVE
        System.out.println("FCM TOKEN: " + usuario.getFcmToken());

        // PUSH SOLO SI ES VÁLIDO
        if (usuario.getFcmToken() != null && !usuario.getFcmToken().isBlank()) {
            try {
                firebase.enviarPush(usuario.getFcmToken(), titulo, mensaje);
            } catch (Exception e) {
                System.err.println("ERROR enviando push FCM: " + e.getMessage());
            }
        }
    }

    // ─────────────────────────────────────────
    public List<NotificacionResponseDTO> getNotificaciones(Long idUsuario) {
        return notificacionRepository.findByUsuario_IdUsuarioOrderByCreadaEnDesc(idUsuario)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ─────────────────────────────────────────
    public NotificacionResponseDTO marcarLeida(Long id) {

        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        n.setLeida(true);
        return toDTO(notificacionRepository.save(n));
    }

    // ─────────────────────────────────────────
    public void marcarTodasLeidas(Long idUsuario) {

        List<Notificacion> lista =
                notificacionRepository.findByUsuario_IdUsuarioOrderByCreadaEnDesc(idUsuario);

        lista.forEach(n -> n.setLeida(true));

        notificacionRepository.saveAll(lista);
    }

    // ─────────────────────────────────────────
    public long contarNoLeidas(Long idUsuario) {
        return notificacionRepository.countByUsuario_IdUsuarioAndLeidaFalse(idUsuario);
    }

    // ─────────────────────────────────────────
    private NotificacionResponseDTO toDTO(Notificacion n) {

        return NotificacionResponseDTO.builder()
                .id(n.getIdNotificacion())
                .titulo(n.getTitulo())
                .mensaje(n.getMensaje())
                .leida(n.getLeida())
                .fecha(n.getCreadaEn())
                .build();
    }
}