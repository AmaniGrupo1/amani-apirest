package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.ProgresoEmocionalRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.ProgresoEmocionalResponseDTO;
import com.amani.amaniapirest.services.paciente.ProgresoEmocionalService;
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
@RequestMapping("/api/progreso-emocional")
@Tag(name = "Progreso Emocional (Paciente)", description = "Gestion de progreso emocional — vista paciente")
public class ProgresoEmocionalController {

    private final ProgresoEmocionalService progresoEmocionalService;

    public ProgresoEmocionalController(ProgresoEmocionalService progresoEmocionalService) {
        this.progresoEmocionalService = progresoEmocionalService;
    }

    /** GET /api/progreso-emocional — Lista todos los registros de progreso. */
    @Operation(summary = "Listar progresos", description = "Lista todos los registros de progreso emocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ProgresoEmocionalResponseDTO>> findAll() {
        return ResponseEntity.ok(progresoEmocionalService.findAll());
    }

    /** GET /api/progreso-emocional/{id} — Obtiene un registro de progreso por ID. */
    @Operation(summary = "Obtener progreso", description = "Obtiene un registro de progreso por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProgresoEmocionalResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(progresoEmocionalService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/progreso-emocional/paciente/{idPaciente} — Lista el progreso de un paciente. */
    @Operation(summary = "Progreso por paciente", description = "Lista el progreso emocional de un paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<ProgresoEmocionalResponseDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(progresoEmocionalService.findByPaciente(idPaciente));
    }

    /** POST /api/progreso-emocional — Crea un nuevo registro de progreso. */
    @Operation(summary = "Crear progreso", description = "Crea un nuevo registro de progreso emocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProgresoEmocionalResponseDTO> create(@Valid @RequestBody ProgresoEmocionalRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(progresoEmocionalService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/progreso-emocional/{id} — Actualiza un registro de progreso. */
    @Operation(summary = "Actualizar progreso", description = "Actualiza un registro de progreso emocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProgresoEmocionalResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProgresoEmocionalRequestDTO request) {
        try {
            return ResponseEntity.ok(progresoEmocionalService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/progreso-emocional/{id} — Elimina un registro de progreso. */
    @Operation(summary = "Eliminar progreso", description = "Elimina un registro de progreso emocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            progresoEmocionalService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
