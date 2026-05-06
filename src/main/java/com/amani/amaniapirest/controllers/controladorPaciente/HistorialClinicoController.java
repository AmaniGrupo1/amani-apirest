package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.HistorialClinicoRequestDTO;
import com.amani.amaniapirest.dto.historialClinico.HistorialClinicoResponseDTO;
import com.amani.amaniapirest.services.paciente.HistorialClinicoService;
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
@RequestMapping("/api/historial-clinico")
@Tag(name = "Historial Clinico (Paciente)", description = "Gestion de historial clinico — vista paciente")
public class HistorialClinicoController {

    private final HistorialClinicoService historialClinicoService;

    public HistorialClinicoController(HistorialClinicoService historialClinicoService) {
        this.historialClinicoService = historialClinicoService;
    }

    /**
     * Lista todos los registros clínicos del paciente autenticado.
     *
     * @return lista de registros clínicos
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Listar historiales", description = "Recupera todos los registros clínicos del paciente autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros clínicos retornados correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<HistorialClinicoResponseDTO>> findAll() {
        return ResponseEntity.ok(historialClinicoService.findAll());
    }

    /**
     * Obtiene un registro clínico por su identificador único.
     *
     * @param id identificador único del registro clínico
     * @return datos del registro clínico
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Obtener historial", description = "Recupera un registro clínico específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro clínico retornado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún registro con el ID especificado", content = @Content),
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

    /**
     * Lista todos los registros clínicos de un paciente específico.
     *
     * @param idPaciente identificador único del paciente
     * @return lista de registros clínicos del paciente
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Historial por paciente", description = "Recupera todos los registros clínicos de un paciente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros clínicos del paciente retornados correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró al paciente con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })

    //-----------------------------------------
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<HistorialClinicoResponseDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(historialClinicoService.findByPaciente(idPaciente));
    }

    /**
     * Crea un nuevo registro clínico para el paciente autenticado.
     *
     * @param request datos para crear el registro clínico
     * @return registro clínico creado
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Crear historial", description = "Crea un nuevo registro clínico para el paciente autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro clínico creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
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

    /**
     * Actualiza un registro clínico existente.
     *
     * @param id      identificador único del registro clínico a actualizar
     * @param request nuevos datos del registro clínico
     * @return registro clínico actualizado
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Actualizar historial", description = "Actualiza un registro clínico existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro clínico actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún registro con el ID especificado", content = @Content),
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

    /**
     * Elimina un registro clínico por su identificador único.
     *
     * @param id identificador único del registro clínico a eliminar
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Eliminar historial", description = "Elimina un registro clínico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro clínico eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún registro con el ID especificado", content = @Content),
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
