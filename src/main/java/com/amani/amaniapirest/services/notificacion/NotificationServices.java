package com.amani.amaniapirest.services.notificacion;

import com.amani.amaniapirest.dto.notificacion.NotificacionResponseDTO;
import com.amani.amaniapirest.gateway.PushNotificationGateway;
import com.amani.amaniapirest.models.Notificacion;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.notificacion.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de notificaciones.
 *
 * <p>Depende de {@link PushNotificationGateway} en lugar de la implementación
 * concreta de Firebase, permitiendo funcionar tanto en local (NoOp) como en GCP.</p>
 */
@Service
@RequiredArgsConstructor
public class NotificationServices {

    private static final Logger log = LoggerFactory.getLogger(NotificationServices.class);

    private final PushNotificationGateway pushGateway;
    private final NotificacionRepository notificacionRepository;

    public void enviarNotificacion(Usuario usuario, String titulo, String mensaje) {
        if (usuario == null) return;
        if (!usuario.isNotificacionesActivas()) return;

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setCreadaEn(LocalDateTime.now());

        notificacionRepository.save(notificacion);

        if (usuario.getFcmToken() != null && !usuario.getFcmToken().isBlank()) {
            try {
                pushGateway.sendPush(usuario.getFcmToken(), titulo, mensaje);
            } catch (Exception e) {
                log.error("[Notificación] Error enviando push a usuario {}: {}",
                        usuario.getIdUsuario(), e.getMessage());
            }
        }
    }

    public List<NotificacionResponseDTO> getNotificaciones(Long idUsuario) {
        return notificacionRepository.findByUsuario_IdUsuarioOrderByCreadaEnDesc(idUsuario)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public NotificacionResponseDTO marcarLeida(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        n.setLeida(true);
        return toDTO(notificacionRepository.save(n));
    }

    public void marcarTodasLeidas(Long idUsuario) {
        List<Notificacion> lista =
                notificacionRepository.findByUsuario_IdUsuarioOrderByCreadaEnDesc(idUsuario);

        lista.forEach(n -> n.setLeida(true));

        notificacionRepository.saveAll(lista);
    }

    public long contarNoLeidas(Long idUsuario) {
        return notificacionRepository.countByUsuario_IdUsuarioAndLeidaFalse(idUsuario);
    }

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
