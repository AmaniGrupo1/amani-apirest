package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PacientePsicologoResponseDTO;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.services.psicologo.PacientePsicologoService;
import com.amani.amaniapirest.services.serviceAdmin.PsicologoAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de pacientes desde la perspectiva del psicólogo.
 *
 * <p>Permite al psicólogo listar, crear, actualizar y eliminar pacientes,
 * pero solo con la información permitida para su rol.</p>
 */
@RestController
@RequestMapping("/api/psicologo/pacientes")
@Tag(name = "Pacientes (Psicologo)", description = "Gestion de pacientes — vista psicologo")
@RequiredArgsConstructor
public class PacientePsicologoController {

    private final PacientePsicologoService pacienteService;
   private final PsicologoPacienteRepository psicologoPacienteRepository;
   private final PsicologoAdminService psicologoAdminService;


    @Operation(summary = "Obtener paciente", description = "Obtiene un paciente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PacientePsicologoResponseDTO> getPacienteById(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.findById(id));
    }

    @Operation(summary = "Listar pacientes", description = "Lista todos los pacientes activos del psicólogo logueado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay pacientes registrados"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/getAll")
    public ResponseEntity<List<PacientePsicologoResponseDTO>> getPacientes() {
        List<PacientePsicologoResponseDTO> pacientes = psicologoAdminService.getPacientesDelPsicologoLogueado();
        if (pacientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pacientes);
    }

    @Operation(summary = "Crear paciente", description = "Crea un nuevo paciente con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PacientePsicologoResponseDTO> crearPaciente(@RequestBody PacienteRequestDTO request) {
        PacientePsicologoResponseDTO paciente = pacienteService.create(request);
        return ResponseEntity.ok(paciente);
    }

    @Operation(summary = "Actualizar paciente", description = "Actualiza los datos de un paciente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
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
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
