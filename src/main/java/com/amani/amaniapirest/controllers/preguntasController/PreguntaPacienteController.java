package com.amani.amaniapirest.controllers.preguntasController;

import com.amani.amaniapirest.dto.dtoPregunta.ResultadoTestResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.paciente.PreguntaPacienteResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.RespuestasRequestDTO;
import com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.paciente.PreguntaPacienteService;
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
 * Controlador REST que permite a un paciente ver las preguntas del test inicial
 * y enviar sus respuestas.
 *
 * <p>Base path: {@code /api/paciente/preguntas}.</p>
 */
@RestController
@RequestMapping("/api/paciente/preguntas")
@RequiredArgsConstructor
@Tag(name = "Preguntas (Paciente)", description = "Test inicial de preguntas — vista paciente")
public class PreguntaPacienteController {

    private final PreguntaPacienteService preguntaPacienteService;

    /**
     * Obtiene todas las preguntas disponibles para el paciente.
     *
     * @return lista de preguntas con opciones o 204 si no hay preguntas.
     */
    @Operation(summary = "Obtener preguntas", description = "Obtiene todas las preguntas disponibles del test inicial")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<PreguntaPacienteResponseDTO>> getPreguntas() {
        List<PreguntaPacienteResponseDTO> pacienteResponseDTOS = preguntaPacienteService.getPreguntas();
        if (pacienteResponseDTOS.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pacienteResponseDTOS);
    }

    /**
     * Registra las respuestas de un paciente al test inicial.
     *
     * @param idPaciente identificador del paciente que responde.
     * @param respuestas lista de respuestas seleccionadas.
     */
    @Operation(summary = "Responder test", description = "Registra las respuestas del paciente al test inicial")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/responder/{idPaciente}")
    public ResponseEntity<ResultadoTestResponseDTO> responder(
            @PathVariable Long idPaciente,
            @RequestBody List<RespuestasRequestDTO> respuestas
    ) {

        ResultadoTestResponseDTO resultado =
                preguntaPacienteService.responder(idPaciente, respuestas);

        return ResponseEntity.ok(resultado);
    }

}
