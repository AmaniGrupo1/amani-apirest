package com.amani.amaniapirest.controllers.notificacion;

import com.amani.amaniapirest.dto.notificacion.NotificacionConfigDTO;
import com.amani.amaniapirest.dto.notificacion.NotificacionResponseDTO;
import com.amani.amaniapirest.models.Notificacion;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.notificacion.NotificacionRepository;
import com.amani.amaniapirest.services.UsuarioService;
import com.amani.amaniapirest.services.notificacion.NotificationServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de notificaciones.
 */
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "API para gestionar las notificaciones de los usuarios")
public class NotificacionController {

    private final NotificationServices notificationServices;
    private final UsuarioService usuarioService;

    // ─────────────────────────────────────────
    @Operation(summary = "Obtener notificaciones", description = "Obtiene la lista de notificaciones para un usuario específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificaciones obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{idUsuario}")
    public List<NotificacionResponseDTO> getNotificaciones(
            @PathVariable Long idUsuario) {

        return notificationServices.getNotificaciones(idUsuario);
    }

    // ─────────────────────────────────────────
    @Operation(summary = "Marcar notificación como leída", description = "Actualiza el estado de una notificación a leída")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación marcada como leída exitosamente"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @PutMapping("/leer/{id}")
    public NotificacionResponseDTO marcarLeida(@PathVariable Long id) {
        return notificationServices.marcarLeida(id);
    }

    // ─────────────────────────────────────────
    @Operation(summary = "Marcar todas las notificaciones como leídas", description = "Actualiza el estado de todas las notificaciones de un usuario a leídas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificaciones marcadas como leídas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/leer-todas/{idUsuario}")
    public void marcarTodasLeidas(@PathVariable Long idUsuario) {
        notificationServices.marcarTodasLeidas(idUsuario);
    }

    // ─────────────────────────────────────────
    @Operation(summary = "Contar notificaciones no leídas", description = "Obtiene la cantidad de notificaciones no leídas de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad de notificaciones no leídas obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/no-leidas/{idUsuario}")
    public long contarNoLeidas(@PathVariable Long idUsuario) {
        return notificationServices.contarNoLeidas(idUsuario);
    }

    // ─────────────────────────────────────────
    @Operation(summary = "Activar o desactivar notificaciones", description = "Actualiza la configuración de notificaciones de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuración de notificaciones actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/configuracion/{idUsuario}/activar")
    public NotificacionConfigDTO actualizarNotificaciones(
            @PathVariable Long idUsuario,
            @RequestParam boolean activar
    ) {
        return usuarioService.actualizarNotificaciones(idUsuario, activar);
    }

    // ─────────────────────────────────────────
    @Operation(summary = "Obtener estado de notificaciones", description = "Obtiene el estado actual de la configuración de notificaciones de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado de notificaciones obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/configuracion/{idUsuario}")
    public boolean obtenerEstado(@PathVariable Long idUsuario) {
        return usuarioService.obtenerEstadoNotificaciones(idUsuario);
    }
}