package com.amani.amaniapirest.controllers.preguntasController;


import com.amani.amaniapirest.dto.dtoPregunta.admin.OpcionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.paciente.PreguntaPacienteResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.OpcionAdminResDTO;
import com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.admin.PreguntaAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controlador REST de administracion para gestionar las preguntas del test inicial.
 *
 * <p>Base path: {@code /api/admin/preguntas}. Permite listar, crear y eliminar preguntas
 * con sus opciones de respuesta.</p>
 */
@RestController
@RequestMapping("/api/admin/preguntas")
@RequiredArgsConstructor
@Tag(name = "Preguntas (Admin)", description = "Gestion de preguntas del test inicial — vista administrador")
public class PreguntaAdminController {

    private final PreguntaAdminService preguntaAdminService;

    /**
     * Lista todas las preguntas con sus opciones.
     *
     * @return lista de preguntas o 204 si no hay ninguna.
     */
    @Operation(summary = "Listar preguntas", description = "Lista todas las preguntas con sus opciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<OpcionAdminResponseDTO>> findAll() {
        List<OpcionAdminResponseDTO> opciones = preguntaAdminService.findAll();
        if (opciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(opciones);
    }

    /**
     * Crea una nueva pregunta con sus opciones de respuesta.
     *
     * @param request datos de la pregunta y sus opciones.
     * @return la pregunta creada o 404 si fallo.
     */
    @Operation(summary = "Crear pregunta", description = "Crea una nueva pregunta con sus opciones de respuesta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PreguntaPacienteResponseDTO> create(@RequestBody OpcionAdminResDTO request) {
        var pre = preguntaAdminService.create(request);
        if (pre == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pre);
    }

    /**
     * Elimina una pregunta y sus opciones asociadas.
     *
     * @param id identificador de la pregunta a eliminar.
     */
    @Operation(summary = "Eliminar pregunta", description = "Elimina una pregunta y sus opciones asociadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        preguntaAdminService.delete(id);
    }
}
