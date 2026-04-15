package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.MensajeRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.MensajeResponseDTO;
import com.amani.amaniapirest.services.paciente.MensajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@Tag(name = "Mensajes (Paciente)", description = "Gestion de mensajes — vista paciente")
public class MensajeController {

    private final MensajeService mensajeService;

    public MensajeController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    /**
     * Lista todos los mensajes del paciente autenticado.
     *
     * @return lista de mensajes
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Listar mensajes", description = "Recupera todos los mensajes del paciente autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensajes retornados correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<MensajeResponseDTO>> findAll() {
        return ResponseEntity.ok(mensajeService.findAll());
    }

    /**
     * Obtiene un mensaje por su identificador único.
     *
     * @param id identificador único del mensaje
     * @return datos del mensaje
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Obtener mensaje", description = "Recupera un mensaje específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensaje retornado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún mensaje con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<MensajeResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(mensajeService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todos los mensajes enviados por un usuario específico.
     *
     * @param idUsuario identificador único del usuario
     * @return lista de mensajes enviados por el usuario
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Mensajes enviados", description = "Recupera todos los mensajes enviados por un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensajes enviados retornados correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún usuario con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/enviados/{idUsuario}")
    public ResponseEntity<List<MensajeResponseDTO>> findEnviados(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mensajeService.findEnviados(idUsuario));
    }

    /**
     * Lista todos los mensajes recibidos por un usuario específico.
     *
     * @param idUsuario identificador único del usuario
     * @return lista de mensajes recibidos por el usuario
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Mensajes recibidos", description = "Recupera todos los mensajes recibidos por un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensajes recibidos retornados correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún usuario con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/recibidos/{idUsuario}")
    public ResponseEntity<List<MensajeResponseDTO>> findRecibidos(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mensajeService.findRecibidos(idUsuario));
    }

    /**
     * Envía un nuevo mensaje desde el paciente autenticado.
     *
     * @param request datos para crear el mensaje
     * @return mensaje creado
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Enviar mensaje", description = "Envía un nuevo mensaje desde el paciente autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mensaje creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<MensajeResponseDTO> create(@Valid @RequestBody MensajeRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(mensajeService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Marca un mensaje como leído.
     *
     * @param id identificador único del mensaje a marcar como leído
     * @return mensaje marcado como leído
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Marcar como leído", description = "Marca un mensaje como leído")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensaje marcado como leído correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún mensaje con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PatchMapping("/{id}/leido")
    public ResponseEntity<MensajeResponseDTO> marcarLeido(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(mensajeService.marcarLeido(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un mensaje por su identificador único.
     *
     * @param id identificador único del mensaje a eliminar
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Eliminar mensaje", description = "Elimina un mensaje por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mensaje eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún mensaje con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            mensajeService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
