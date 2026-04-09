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
 * Controlador REST de administracion para la gestion de mensajes del sistema.
 *
 * <p>Base path: {@code /api/admin/mensajes}. Permite listar, consultar, enviar,
 * marcar como leidos y eliminar mensajes entre usuarios.</p>
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
    @Operation(summary = "Listar mensajes", description = "Lista todos los mensajes del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<MensajeResponseDTO>> getAllMensajes() {
        return ResponseEntity.ok(mensajeService.findAll());
    }

    /**
     * Obtiene un mensaje por su identificador.
     *
     * @param id identificador del mensaje.
     * @return el mensaje encontrado.
     */
    @Operation(summary = "Obtener mensaje", description = "Obtiene un mensaje por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<MensajeResponseDTO> getMensajeById(@PathVariable Long id) {
        return ResponseEntity.ok(mensajeService.findById(id));
    }

    /**
     * Lista los mensajes enviados por un usuario.
     *
     * @param idUsuario identificador del usuario emisor.
     * @return lista de mensajes enviados.
     */
    @Operation(summary = "Mensajes enviados", description = "Lista los mensajes enviados por un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/enviados/{idUsuario}")
    public ResponseEntity<List<MensajeResponseDTO>> getMensajesEnviados(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mensajeService.findEnviados(idUsuario));
    }

    /**
     * Lista los mensajes recibidos por un usuario.
     *
     * @param idUsuario identificador del usuario receptor.
     * @return lista de mensajes recibidos.
     */
    @Operation(summary = "Mensajes recibidos", description = "Lista los mensajes recibidos por un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/recibidos/{idUsuario}")
    public ResponseEntity<List<MensajeResponseDTO>> getMensajesRecibidos(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mensajeService.findRecibidos(idUsuario));
    }

    /**
     * Envia un nuevo mensaje.
     *
     * @param request datos del mensaje a enviar.
     * @return el mensaje creado.
     */
    @Operation(summary = "Enviar mensaje", description = "Envia un nuevo mensaje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<MensajeResponseDTO> crearMensaje(@RequestBody MensajeRequestDTO request) {
        return ResponseEntity.ok(mensajeService.create(request));
    }

    /**
     * Marca un mensaje como leido.
     *
     * @param id identificador del mensaje.
     * @return el mensaje actualizado.
     */
    @Operation(summary = "Marcar como leido", description = "Marca un mensaje como leido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/marcarLeido/{id}")
    public ResponseEntity<MensajeResponseDTO> marcarLeido(@PathVariable Long id) {
        return ResponseEntity.ok(mensajeService.marcarLeido(id));
    }

    /**
     * Elimina un mensaje por su identificador.
     *
     * @param id identificador del mensaje a eliminar.
     * @return 204 No Content.
     */
    @Operation(summary = "Eliminar mensaje", description = "Elimina un mensaje por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMensaje(@PathVariable Long id) {
        mensajeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
