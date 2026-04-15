package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.DiarioEmocionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.DiarioEmocionResponseDTO;
import com.amani.amaniapirest.services.paciente.DiarioEmocionService;
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
@RequestMapping("/api/diario-emocion")
@Tag(name = "Diario Emocional (Paciente)", description = "Gestion del diario emocional — vista paciente")
public class DiarioEmocionController {

    private final DiarioEmocionService diarioEmocionService;

    public DiarioEmocionController(DiarioEmocionService diarioEmocionService) {
        this.diarioEmocionService = diarioEmocionService;
    }

    /**
     * Lista todas las entradas del diario emocional del paciente autenticado.
     *
     * @return lista de entradas del diario emocional
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Listar entradas", description = "Recupera todas las entradas del diario emocional del paciente autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entradas del diario emocional retornadas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<DiarioEmocionResponseDTO>> findAll() {
        return ResponseEntity.ok(diarioEmocionService.findAll());
    }

    /**
     * Obtiene una entrada del diario emocional por su identificador único.
     *
     * @param id identificador único de la entrada del diario emocional
     * @return entrada del diario emocional
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Obtener entrada", description = "Recupera una entrada específica del diario emocional por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrada del diario emocional retornada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ninguna entrada con el ID especificado", content = @Content),
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

    /**
     * Lista todas las entradas del diario emocional de un paciente específico.
     *
     * @param idPaciente identificador único del paciente
     * @return lista de entradas del diario emocional del paciente
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Entradas por paciente", description = "Recupera todas las entradas del diario emocional de un paciente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entradas del diario emocional del paciente retornadas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró al paciente con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<DiarioEmocionResponseDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(diarioEmocionService.findByPaciente(idPaciente));
    }

    /**
     * Crea una nueva entrada en el diario emocional del paciente autenticado.
     *
     * @param request datos para crear la entrada del diario emocional
     * @return entrada del diario emocional creada
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Crear entrada", description = "Crea una nueva entrada en el diario emocional del paciente autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrada del diario emocional creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
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

    /**
     * Actualiza una entrada existente del diario emocional.
     *
     * @param id      identificador único de la entrada del diario emocional a actualizar
     * @param request nuevos datos para la entrada del diario emocional
     * @return entrada del diario emocional actualizada
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Actualizar entrada", description = "Actualiza una entrada existente del diario emocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrada del diario emocional actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ninguna entrada con el ID especificado", content = @Content),
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

    /**
     * Elimina una entrada del diario emocional por su identificador único.
     *
     * @param id identificador único de la entrada del diario emocional a eliminar
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Eliminar entrada", description = "Elimina una entrada del diario emocional por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Entrada del diario emocional eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ninguna entrada con el ID especificado", content = @Content),
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
