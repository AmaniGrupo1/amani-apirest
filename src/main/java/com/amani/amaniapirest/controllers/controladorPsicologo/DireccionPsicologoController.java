package com.amani.amaniapirest.controllers.controladorPsicologo;


import com.amani.amaniapirest.dto.dtoPsicologo.response.DireccionPsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.DireccionPsicologoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST que permite a un psicologo consultar las direcciones de sus pacientes.
 *
 * <p>Base path: {@code /api/direcciones/psicologo}.</p>
 */
@RestController
@RequestMapping("/api/direcciones/psicologo")
@Tag(name = "Direcciones (Psicologo)", description = "Consulta de direcciones — vista psicologo")
public class DireccionPsicologoController {

    private final DireccionPsicologoService direccionService;

    public DireccionPsicologoController(DireccionPsicologoService direccionService) {
        this.direccionService = direccionService;
    }

    /**
     * Lista las direcciones de un paciente.
     *
     * @param idPaciente identificador del paciente.
     * @return lista de direcciones del paciente.
     */
    @Operation(summary = "Direcciones por paciente", description = "Lista las direcciones asociadas a un paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{idPaciente}")
    public ResponseEntity<List<DireccionPsicologoResponseDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(direccionService.findByPaciente(idPaciente));
    }

    /**
     * Obtiene el detalle de las direcciones de un paciente.
     *
     * @param idPaciente identificador del paciente.
     * @return lista detallada de direcciones.
     */
    @Operation(summary = "Detalle de direcciones", description = "Obtiene el detalle completo de las direcciones de un paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/detalle/{idPaciente}")
    public ResponseEntity<List<DireccionPsicologoResponseDTO>> findDetalle(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(direccionService.findByPaciente(idPaciente));
    }

}
