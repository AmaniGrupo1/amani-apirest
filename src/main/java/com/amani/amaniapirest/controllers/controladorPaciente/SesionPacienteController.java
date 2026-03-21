package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.SesionResponseDTO;
import com.amani.amaniapirest.services.paciente.SesionService;
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
@RequestMapping("/api/paciente/sesiones")
@Tag(name = "Sesiones (Paciente)", description = "Gestion de sesiones — vista paciente")
public class SesionPacienteController {

    private final SesionService sesionService;

    public SesionPacienteController(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    /**
     * Obtener todas las sesiones de un paciente
     */
    @Operation(summary = "Sesiones por paciente", description = "Obtiene todas las sesiones de un paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<SesionResponseDTO>> findAllByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(sesionService.findAllByPaciente(idPaciente));
    }

    /**
     * Obtener una sesión por ID
     */
    @Operation(summary = "Obtener sesion", description = "Obtiene una sesion por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SesionResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(sesionService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crear sesión (solo si tu lógica lo permite)
     */
    @Operation(summary = "Crear sesion", description = "Crea una nueva sesion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<SesionResponseDTO> create(@Valid @RequestBody SesionRequestDTO request) {
        try {
            SesionResponseDTO sesion = sesionService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(sesion);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualizar sesión
     */
    @Operation(summary = "Actualizar sesion", description = "Actualiza una sesion existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<SesionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody SesionRequestDTO request) {

        try {
            return ResponseEntity.ok(sesionService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar sesión
     */
    @Operation(summary = "Eliminar sesion", description = "Elimina una sesion por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            sesionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}