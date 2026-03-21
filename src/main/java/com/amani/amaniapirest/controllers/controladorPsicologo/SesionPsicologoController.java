package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.SesionPsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.SesionPsicologoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/psicologo/sesiones")
@Tag(name = "Sesiones (Psicologo)", description = "Gestion de sesiones — vista psicologo")
public class SesionPsicologoController {

    private final SesionPsicologoService sesionPsicologoService;

    public SesionPsicologoController(SesionPsicologoService sesionPsicologoService) {
        this.sesionPsicologoService = sesionPsicologoService;
    }

    @Operation(summary = "Sesiones por psicologo", description = "Lista todas las sesiones de un psicologo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/psicologo/{idPsicologo}")
    public List<SesionPsicologoResponseDTO> getAllByPsicologo(@PathVariable Long idPsicologo) {
        return sesionPsicologoService.findAllByPsicologo(idPsicologo);
    }

    @Operation(summary = "Obtener sesion", description = "Obtiene una sesion por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{idSesion}")
    public SesionPsicologoResponseDTO getById(@PathVariable Long idSesion) {
        return sesionPsicologoService.findById(idSesion);
    }

    /**
     * Crear sesión
     */
    @Operation(summary = "Crear sesion", description = "Crea una nueva sesion para un psicologo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/psicologo/{idPsicologo}")
    public SesionPsicologoResponseDTO create(
            @PathVariable Long idPsicologo,
            @RequestBody SesionRequestDTO request
    ) {
        return sesionPsicologoService.create(request, idPsicologo);
    }

    /**
     * Actualizar sesión
     */
    @Operation(summary = "Actualizar sesion", description = "Actualiza una sesion existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{idSesion}")
    public SesionPsicologoResponseDTO update(
            @PathVariable Long idSesion,
            @RequestBody SesionRequestDTO request
    ) {
        return sesionPsicologoService.update(idSesion, request);
    }

    /**
     * Eliminar sesión
     */
    @Operation(summary = "Eliminar sesion", description = "Elimina una sesion por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{idSesion}")
    public void delete(@PathVariable Long idSesion) {
        sesionPsicologoService.delete(idSesion);
    }
}