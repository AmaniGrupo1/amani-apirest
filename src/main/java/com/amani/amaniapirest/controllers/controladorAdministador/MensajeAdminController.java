package com.amani.amaniapirest.controllers.controladorAdministador;


import com.amani.amaniapirest.dto.dtoPaciente.request.MensajeRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.MensajeResponseDTO;
import com.amani.amaniapirest.services.paciente.MensajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de administración para la gestión de mensajes del sistema.
 *
 * <p>Base path: {@code /api/admin/mensajes}. Permite listar, consultar, enviar,
 * marcar como leídos y eliminar mensajes entre usuarios.</p>
 */
@RestController
@RequestMapping("/api/admin/mensajes")
@Tag(name = "Mensajes (Admin)", description = "Gestion de mensajes — vista administrador")
public class MensajeAdminController {

    private final MensajeService mensajeService;

    public MensajeAdminController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    /**
     * Lista todos los mensajes del sistema.
     *
     * @return lista completa de mensajes.
     */
    /**
     * Lista todos los mensajes del sistema.
     *
     * @return lista completa de mensajes
     */
    @Operation(summary = "Listar mensajes", description = "Obtiene la lista de todos los mensajes del sistema")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay mensajes registrados", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<MensajeResponseDTO>> getAllMensajes() {
        return ResponseEntity.ok(mensajeService.findAll());
    }

    /**
     * Obtiene un mensaje por su identificador.
     *
     * @param id identificador del mensaje
     * @return el mensaje encontrado
     */
    @Operation(summary = "Obtener mensaje", description = "Obtiene los detalles de un mensaje por su identificador")
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<MensajeResponseDTO> getMensajeById(@PathVariable Long id) {
        return ResponseEntity.ok(mensajeService.findById(id));
    }

    /**
     * Lista los mensajes enviados por un usuario.
     *
     * @param idUsuario identificador del usuario emisor
     * @return lista de mensajes enviados
     */
    @Operation(summary = "Mensajes enviados", description = "Obtiene la lista de mensajes enviados por un usuario especifico")
    @GetMapping("/enviados/{idUsuario}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay mensajes enviados", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<MensajeResponseDTO>> getMensajesEnviados(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mensajeService.findEnviados(idUsuario));
    }

    /**
     * Lista los mensajes recibidos por un usuario.
     *
     * @param idUsuario identificador del usuario receptor
     * @return lista de mensajes recibidos
     */
    @Operation(summary = "Mensajes recibidos", description = "Obtiene la lista de mensajes recibidos por un usuario especifico")
    @GetMapping("/recibidos/{idUsuario}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay mensajes recibidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<MensajeResponseDTO>> getMensajesRecibidos(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mensajeService.findRecibidos(idUsuario));
    }

    /**
     * Envia un nuevo mensaje.
     *
     * @param request datos del mensaje a enviar
     * @return el mensaje creado
     */
    @Operation(summary = "Enviar mensaje", description = "Crea y envia un nuevo mensaje entre usuarios")
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<MensajeResponseDTO> crearMensaje(@RequestBody MensajeRequestDTO request) {
        return ResponseEntity.ok(mensajeService.create(request));
    }

    /**
     * Marca un mensaje como leido.
     *
     * @param id identificador del mensaje
     * @return el mensaje actualizado
     */
    @Operation(summary = "Marcar como leido", description = "Actualiza el estado de un mensaje para marcarlo como leido")
    @PutMapping("/marcarLeido/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<MensajeResponseDTO> marcarLeido(@PathVariable Long id) {
        return ResponseEntity.ok(mensajeService.marcarLeido(id));
    }

    /**
     * Elimina un mensaje por su identificador.
     *
     * @param id identificador del mensaje a eliminar
     * @return 204 No Content si se elimino correctamente, 404 si no existe
     */
    @Operation(summary = "Eliminar mensaje", description = "Elimina un mensaje del sistema por su identificador")
    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Void> eliminarMensaje(@PathVariable Long id) {
        mensajeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
