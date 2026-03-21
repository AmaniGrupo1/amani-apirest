package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PacientePsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.PacientePsicologoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controlador para la gestión de pacientes desde la perspectiva del psicólogo.
 *
 * <p>Permite al psicólogo listar, crear, actualizar y eliminar pacientes,
 * pero solo con la información permitida para su rol.</p>
 */
@RestController
@RequestMapping("/api/psicologo/pacientes")
@Tag(name = "Pacientes (Psicologo)", description = "Gestion de pacientes — vista psicologo")
public class PacientePsicologoController {

    private final PacientePsicologoService pacienteService;

    public PacientePsicologoController(PacientePsicologoService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @Operation(summary = "Obtener paciente", description = "Obtiene un paciente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PacientePsicologoResponseDTO> getPacienteById(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.findById(id));
    }

    @Operation(summary = "Crear paciente", description = "Crea un nuevo paciente (datos basicos)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    /** Crear un nuevo paciente (solo datos básicos) */
    public ResponseEntity<PacientePsicologoResponseDTO> crearPaciente(@RequestBody PacienteRequestDTO request) {
        PacientePsicologoResponseDTO paciente = pacienteService.create(request);
        return ResponseEntity.ok(paciente);
    }

    @Operation(summary = "Actualizar paciente", description = "Actualiza un paciente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    /** Actualizar un paciente existente */
    public ResponseEntity<PacientePsicologoResponseDTO> actualizarPaciente(
            @PathVariable Long id,
            @RequestBody PacienteRequestDTO request) {
        PacientePsicologoResponseDTO paciente = pacienteService.update(id, request);
        return ResponseEntity.ok(paciente);
    }

    @Operation(summary = "Eliminar paciente", description = "Elimina un paciente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    /** Eliminar un paciente */
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
