package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.DiarioEmocionRequestDTO;

import com.amani.amaniapirest.dto.dtoPaciente.response.DiarioEmocionResponseDTO;
import com.amani.amaniapirest.services.paciente.DiarioEmocionService;
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
@RequestMapping("/api/diario-emocion")
@Tag(name = "Diario Emocional (Paciente)", description = "Gestion del diario emocional — vista paciente")
public class DiarioEmocionController {

    private final DiarioEmocionService diarioEmocionService;

    public DiarioEmocionController(DiarioEmocionService diarioEmocionService) {
        this.diarioEmocionService = diarioEmocionService;
    }

    /** GET /api/diario-emocion — Lista todas las entradas del diario. */
    @Operation(summary = "Listar entradas", description = "Lista todas las entradas del diario emocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<DiarioEmocionResponseDTO>> findAll() {
        return ResponseEntity.ok(diarioEmocionService.findAll());
    }

    /** GET /api/diario-emocion/{id} — Obtiene una entrada por ID. */
    @Operation(summary = "Obtener entrada", description = "Obtiene una entrada del diario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DiarioEmocionResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(diarioEmocionService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/diario-emocion/paciente/{idPaciente} — Lista las entradas de un paciente. */
    @Operation(summary = "Entradas por paciente", description = "Lista las entradas del diario de un paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<DiarioEmocionResponseDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(diarioEmocionService.findByPaciente(idPaciente));
    }

    /** POST /api/diario-emocion — Crea una nueva entrada en el diario. */
    @Operation(summary = "Crear entrada", description = "Crea una nueva entrada en el diario emocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DiarioEmocionResponseDTO> create(@Valid @RequestBody DiarioEmocionRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(diarioEmocionService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/diario-emocion/{id} — Actualiza una entrada del diario. */
    @Operation(summary = "Actualizar entrada", description = "Actualiza una entrada del diario emocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DiarioEmocionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DiarioEmocionRequestDTO request) {
        try {
            return ResponseEntity.ok(diarioEmocionService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/diario-emocion/{id} — Elimina una entrada del diario. */
    @Operation(summary = "Eliminar entrada", description = "Elimina una entrada del diario emocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            diarioEmocionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
