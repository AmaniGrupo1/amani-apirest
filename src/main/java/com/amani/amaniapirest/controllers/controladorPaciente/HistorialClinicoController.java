package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.HistorialClinicoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.HistorialClinicoResponseDTO;
import com.amani.amaniapirest.services.paciente.HistorialClinicoService;
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
@RequestMapping("/api/historial-clinico")
@Tag(name = "Historial Clinico (Paciente)", description = "Gestion de historial clinico — vista paciente")
public class HistorialClinicoController {

    private final HistorialClinicoService historialClinicoService;

    public HistorialClinicoController(HistorialClinicoService historialClinicoService) {
        this.historialClinicoService = historialClinicoService;
    }

    /** GET /api/historial-clinico — Lista todos los registros clínicos. */
    @Operation(summary = "Listar historiales", description = "Lista todos los registros clinicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<HistorialClinicoResponseDTO>> findAll() {
        return ResponseEntity.ok(historialClinicoService.findAll());
    }

    /** GET /api/historial-clinico/{id} — Obtiene un registro clínico por ID. */
    @Operation(summary = "Obtener historial", description = "Obtiene un registro clinico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<HistorialClinicoResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(historialClinicoService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/historial-clinico/paciente/{idPaciente} — Lista el historial de un paciente. */
    @Operation(summary = "Historial por paciente", description = "Lista el historial clinico de un paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<HistorialClinicoResponseDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(historialClinicoService.findByPaciente(idPaciente));
    }

    /** POST /api/historial-clinico — Crea un nuevo registro clínico. */
    @Operation(summary = "Crear historial", description = "Crea un nuevo registro clinico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<HistorialClinicoResponseDTO> create(@Valid @RequestBody HistorialClinicoRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(historialClinicoService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/historial-clinico/{id} — Actualiza un registro clínico. */
    @Operation(summary = "Actualizar historial", description = "Actualiza un registro clinico existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<HistorialClinicoResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody HistorialClinicoRequestDTO request) {
        try {
            return ResponseEntity.ok(historialClinicoService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/historial-clinico/{id} — Elimina un registro clínico. */
    @Operation(summary = "Eliminar historial", description = "Elimina un registro clinico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            historialClinicoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
