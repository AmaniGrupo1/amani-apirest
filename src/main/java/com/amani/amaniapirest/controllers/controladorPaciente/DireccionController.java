package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.DireccionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.DireccionResponseDTO;
import com.amani.amaniapirest.services.paciente.DireccionService;
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
@RequestMapping("/api/direcciones")
@Tag(name = "Direcciones (Paciente)", description = "Gestion de direcciones — vista paciente")
public class DireccionController {

    private final DireccionService direccionService;

    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    /**
     * Lista todas las direcciones del paciente autenticado.
     *
     * @return lista de direcciones
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Listar direcciones", description = "Recupera todas las direcciones del paciente autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Direcciones retornadas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<DireccionResponseDTO>> findAll() {
        return ResponseEntity.ok(direccionService.findAll());
    }

    /**
     * Obtiene una dirección por su identificador único.
     *
     * @param id identificador único de la dirección
     * @return datos de la dirección
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Obtener dirección", description = "Recupera una dirección específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dirección retornada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ninguna dirección con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DireccionResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(direccionService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todas las direcciones de un paciente específico.
     *
     * @param idPaciente identificador único del paciente
     * @return lista de direcciones del paciente
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Direcciones por paciente", description = "Recupera todas las direcciones de un paciente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Direcciones del paciente retornadas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró al paciente con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<DireccionResponseDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(direccionService.findByPaciente(idPaciente));
    }

    /**
     * Crea una nueva dirección para el paciente autenticado.
     *
     * @param request datos para crear la dirección
     * @return dirección creada
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Crear dirección", description = "Crea una nueva dirección para el paciente autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dirección creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DireccionResponseDTO> create(@Valid @RequestBody DireccionRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(direccionService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza una dirección existente.
     *
     * @param id      identificador único de la dirección a actualizar
     * @param request nuevos datos de la dirección
     * @return dirección actualizada
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Actualizar dirección", description = "Actualiza una dirección existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dirección actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ninguna dirección con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DireccionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DireccionRequestDTO request) {
        try {
            return ResponseEntity.ok(direccionService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina una dirección por su identificador único.
     *
     * @param id identificador único de la dirección a eliminar
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Eliminar dirección", description = "Elimina una dirección por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dirección eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ninguna dirección con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            direccionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
