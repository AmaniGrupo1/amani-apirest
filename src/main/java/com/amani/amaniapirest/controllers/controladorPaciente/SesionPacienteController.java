package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.SesionResponseDTO;
import com.amani.amaniapirest.services.paciente.SesionService;
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
@RequestMapping("/api/paciente/sesiones")
@Tag(name = "Sesiones (Paciente)", description = "Gestion de sesiones — vista paciente")
public class SesionPacienteController {

    private final SesionService sesionService;

    public SesionPacienteController(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    /**
     * Lista todas las sesiones de un paciente específico.
     *
     * @param idPaciente identificador único del paciente
     * @return lista de sesiones del paciente
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Sesiones por paciente", description = "Recupera todas las sesiones de un paciente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sesiones del paciente retornadas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró al paciente con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<SesionResponseDTO>> findAllByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(sesionService.findAllByPaciente(idPaciente));
    }

    /**
     * Obtiene una sesión por su identificador único.
     *
     * @param id identificador único de la sesión
     * @return datos de la sesión
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Obtener sesión", description = "Recupera una sesión específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sesión retornada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ninguna sesión con el ID especificado", content = @Content),
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
     * Crea una nueva sesión para el paciente autenticado.
     *
     * @param request datos para crear la sesión
     * @return sesión creada
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Crear sesión", description = "Crea una nueva sesión para el paciente autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sesión creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
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
     * Actualiza una sesión existente.
     *
     * @param id      identificador único de la sesión a actualizar
     * @param request nuevos datos de la sesión
     * @return sesión actualizada
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Actualizar sesión", description = "Actualiza una sesión existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sesión actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ninguna sesión con el ID especificado", content = @Content),
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
     * Elimina una sesión por su identificador único.
     *
     * @param id identificador único de la sesión a eliminar
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Eliminar sesión", description = "Elimina una sesión por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sesión eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ninguna sesión con el ID especificado", content = @Content),
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