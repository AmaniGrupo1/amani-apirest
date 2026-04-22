package com.amani.amaniapirest.controllers.notificacion;

import com.amani.amaniapirest.dto.notificacion.NotificacionConfigDTO;
import com.amani.amaniapirest.dto.notificacion.NotificacionResponseDTO;
import com.amani.amaniapirest.models.Notificacion;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.notificacion.NotificacionRepository;
import com.amani.amaniapirest.services.UsuarioService;
import com.amani.amaniapirest.services.notificacion.NotificationServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificationServices notificationServices;
    private final UsuarioService usuarioService;

    // ─────────────────────────────────────────
    @GetMapping("/{idUsuario}")
    public List<NotificacionResponseDTO> getNotificaciones(
            @PathVariable Long idUsuario) {

        return notificationServices.getNotificaciones(idUsuario);
    }

    // ─────────────────────────────────────────
    @PutMapping("/leer/{id}")
    public NotificacionResponseDTO marcarLeida(@PathVariable Long id) {
        return notificationServices.marcarLeida(id);
    }

    // ─────────────────────────────────────────
    @PutMapping("/leer-todas/{idUsuario}")
    public void marcarTodasLeidas(@PathVariable Long idUsuario) {
        notificationServices.marcarTodasLeidas(idUsuario);
    }

    // ─────────────────────────────────────────
    @GetMapping("/no-leidas/{idUsuario}")
    public long contarNoLeidas(@PathVariable Long idUsuario) {
        return notificationServices.contarNoLeidas(idUsuario);
    }

    // ─────────────────────────────────────────
    @PutMapping("/configuracion/{idUsuario}/activar")
    public NotificacionConfigDTO actualizarNotificaciones(
            @PathVariable Long idUsuario,
            @RequestParam boolean activar
    ) {
        return usuarioService.actualizarNotificaciones(idUsuario, activar);
    }

    // ─────────────────────────────────────────
    @GetMapping("/configuracion/{idUsuario}")
    public boolean obtenerEstado(@PathVariable Long idUsuario) {
        return usuarioService.obtenerEstadoNotificaciones(idUsuario);
    }
}