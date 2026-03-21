package com.amani.amaniapirest.controllers.preguntasController;


import com.amani.amaniapirest.dto.dtoPregunta.psicologo.RespuestaPacientePsicologoResponseDTO;
import com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.psicologo.RespuestaPsicologoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que permite a un psicólogo consultar las respuestas
 * de los pacientes al test inicial.
 *
 * <p>Base path: {@code /api/psicologo/respuestas}.</p>
 */
@RestController
@RequestMapping("/api/psicologo/respuestas")
@RequiredArgsConstructor
@Tag(name = "Respuestas (Psicólogo)", description = "Consulta de respuestas al test inicial — vista psicólogo")
public class RespuestaPsicologoController {

    private final RespuestaPsicologoService respuestaPsicologoService;

    /**
     * Obtiene todas las respuestas de todos los pacientes al test inicial.
     *
     * @return lista de respuestas con datos del paciente, pregunta y opción seleccionada.
     */
    @Operation(
            summary = "Obtener respuestas de pacientes",
            description = "Devuelve todas las respuestas de los pacientes al test inicial. "
                    + "Requiere rol PSICOLOGO o ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de respuestas obtenida correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RespuestaPacientePsicologoResponseDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado — token JWT ausente o inválido",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado — el usuario no tiene rol PSICOLOGO ni ADMIN",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @GetMapping
    public List<RespuestaPacientePsicologoResponseDTO> getRespuestasPacientes() {
        return respuestaPsicologoService.getRespuestasPacientes();
    }
}
