package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.MensajeRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.MensajeResponseDTO;
import com.amani.amaniapirest.services.paciente.MensajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/mensajes")
@Tag(name = "Mensajes (Paciente)", description = "Gestion de mensajes — vista paciente")
public class MensajeController {

    private final MensajeService mensajeService;

    public MensajeController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    /** GET /api/mensajes — Lista todos los mensajes. */
    @Operation(summary = "Listar mensajes", description = "Lista todos los mensajes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<MensajeResponseDTO>> findAll() {
        return ResponseEntity.ok(mensajeService.findAll());
    }

    /** GET /api/mensajes/{id} — Obtiene un mensaje por ID. */
    @Operation(summary = "Obtener mensaje", description = "Obtiene un mensaje por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
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

    /** GET /api/mensajes/enviados/{idUsuario} — Lista los mensajes enviados por un usuario. */
    @Operation(summary = "Mensajes enviados", description = "Lista los mensajes enviados por un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/enviados/{idUsuario}")
    public ResponseEntity<List<MensajeResponseDTO>> findEnviados(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mensajeService.findEnviados(idUsuario));
    }

    /** GET /api/mensajes/recibidos/{idUsuario} — Lista los mensajes recibidos por un usuario. */
    @Operation(summary = "Mensajes recibidos", description = "Lista los mensajes recibidos por un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/recibidos/{idUsuario}")
    public ResponseEntity<List<MensajeResponseDTO>> findRecibidos(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mensajeService.findRecibidos(idUsuario));
    }

    /** POST /api/mensajes — Envía un nuevo mensaje. */
    @Operation(summary = "Enviar mensaje", description = "Envia un nuevo mensaje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
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

    /** PATCH /api/mensajes/{id}/leido — Marca un mensaje como leído. */
    @Operation(summary = "Marcar como leido", description = "Marca un mensaje como leido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
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

    /** DELETE /api/mensajes/{id} — Elimina un mensaje. */
    @Operation(summary = "Eliminar mensaje", description = "Elimina un mensaje por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
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
