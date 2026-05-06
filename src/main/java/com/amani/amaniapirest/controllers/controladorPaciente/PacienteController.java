package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.PacienteResponseDTO;
import com.amani.amaniapirest.dto.profile.paciente.PacienteDTO;
import com.amani.amaniapirest.services.paciente.PacienteService;
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
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "Gestion de pacientes — vista paciente")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    /**
     * Lista todos los pacientes del sistema.
     *
     * @return lista de pacientes
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Listar pacientes", description = "Recupera todos los pacientes del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pacientes retornados correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> findAll() {
        return ResponseEntity.ok(pacienteService.findAll());
    }

    /**
     * Obtiene un paciente por su identificador único (idUsuario Firebase).
     *
     * @param id identificador único del usuario paciente (Firebase idUsuario)
     * @return datos del paciente
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Obtener paciente por usuario", description = "Recupera un paciente específico por su idUsuario Firebase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente retornado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún paciente con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/usuario/{id}")
    public ResponseEntity<PacienteDTO> findByUsuarioId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pacienteService.findByUsuarioId(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene un paciente por su identificador único (idPaciente de tabla).
     *
     * @param id identificador único del paciente (idPaciente de tabla)
     * @return datos del paciente
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Obtener paciente", description = "Recupera un paciente específico por su ID de tabla")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente retornado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún paciente con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pacienteService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo perfil de paciente.
     *
     * @param request datos para crear el perfil del paciente
     * @return perfil de paciente creado
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Crear paciente", description = "Crea un nuevo perfil de paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Perfil de paciente creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PacienteResponseDTO> create(@Valid @RequestBody PacienteRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza un perfil de paciente existente.
     *
     * @param id      identificador único del paciente a actualizar
     * @param request nuevos datos del perfil del paciente
     * @return perfil de paciente actualizado
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Actualizar paciente", description = "Actualiza un perfil de paciente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil de paciente actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún paciente con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDTO request) {
        try {
            return ResponseEntity.ok(pacienteService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un paciente por su identificador único.
     *
     * @param id identificador único del paciente a eliminar
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Eliminar paciente", description = "Elimina un paciente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Paciente eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún paciente con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            pacienteService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
