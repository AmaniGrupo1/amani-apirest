package com.amani.amaniapirest.controllers.controladorPsicologo;


import com.amani.amaniapirest.dto.dtoPsicologo.response.HistorialClinicoPsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.HistorialClinicoPsicologoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que permite a un psicologo gestionar historiales clinicos de sus pacientes.
 *
 * <p>Base path: {@code /api/psicologo/historial}. Soporta consulta por psicologo,
 * busqueda por ID y actualizacion parcial de diagnostico/observaciones.</p>
 */
@RestController
@RequestMapping("/api/psicologo/historial")
@Tag(name = "Historial Clinico (Psicologo)", description = "Gestion de historiales clinicos — vista psicologo")
public class HistorialClinicoPsicologoController {

    private final HistorialClinicoPsicologoService psicologoService;

    public HistorialClinicoPsicologoController(HistorialClinicoPsicologoService psicologoService) {
        this.psicologoService = psicologoService;
    }

    /**
     * Lista todos los historiales clinicos asociados a un psicologo.
     *
     * @param idPsicologo identificador del psicologo.
     * @return lista de historiales del psicologo.
     */
    @Operation(summary = "Historiales por psicologo", description = "Lista todos los historiales asociados a un psicologo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/psicologo/{idPsicologo}")
    public ResponseEntity<List<HistorialClinicoPsicologoResponseDTO>> findAllByPsicologo(@PathVariable Long idPsicologo) {
        return ResponseEntity.ok(psicologoService.findAllByPsicologo(idPsicologo));
    }

    /**
     * Obtiene un historial clinico por su identificador.
     *
     * @param idHistory identificador del historial.
     * @return el historial encontrado o 404 si no existe.
     */
    @Operation(summary = "Obtener historial", description = "Obtiene un historial clinico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{idHistory}")
    public ResponseEntity<HistorialClinicoPsicologoResponseDTO> findById(@PathVariable Long idHistory) {
        try {
            return ResponseEntity.ok(psicologoService.findByIdPsicologo(idHistory));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualiza parcialmente el diagnostico y/o las observaciones de un historial.
     *
     * @param idHistory     identificador del historial.
     * @param diagnostico   nuevo diagnostico (opcional).
     * @param observaciones nuevas observaciones (opcional).
     * @return el historial actualizado o 404 si no existe.
     */
    @Operation(summary = "Actualizar diagnostico/observaciones", description = "Actualiza parcialmente diagnostico y/o observaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PatchMapping("/{idHistory}")
    public ResponseEntity<HistorialClinicoPsicologoResponseDTO> updateObservaciones(
            @PathVariable Long idHistory,
            @RequestParam(required = false) String diagnostico,
            @RequestParam(required = false) String observaciones) {
        try {
            return ResponseEntity.ok(psicologoService.updateObservaciones(idHistory, diagnostico, observaciones));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
