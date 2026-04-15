package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.SesionPsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.SesionPsicologoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psicologo/sesiones")
@Tag(name = "Sesiones (Psicologo)", description = "Gestion de sesiones — vista psicologo")
public class SesionPsicologoController {

    private final SesionPsicologoService sesionPsicologoService;

    public SesionPsicologoController(SesionPsicologoService sesionPsicologoService) {
        this.sesionPsicologoService = sesionPsicologoService;
    }

    @Operation(summary = "Sesiones por psicologo", description = "Lista todas las sesiones asignadas a un psicólogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/psicologo/{idPsicologo}")
    public ResponseEntity<List<SesionPsicologoResponseDTO>> getAllByPsicologo(@PathVariable Long idPsicologo) {
        return ResponseEntity.ok(sesionPsicologoService.findAllByPsicologo(idPsicologo));
    }

    @Operation(summary = "Obtener sesion", description = "Obtiene una sesión por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{idSesion}")
    public ResponseEntity<SesionPsicologoResponseDTO> getById(@PathVariable Long idSesion) {
        return ResponseEntity.ok(sesionPsicologoService.findById(idSesion));
    }

    /**
     * Crear sesión
     */
    @Operation(summary = "Crear sesion", description = "Crea una nueva sesión para un psicólogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/psicologo/{idPsicologo}")
    public ResponseEntity<SesionPsicologoResponseDTO> create(
            @PathVariable Long idPsicologo,
            @RequestBody SesionRequestDTO request
    ) {
        return new ResponseEntity<>(sesionPsicologoService.create(request, idPsicologo), HttpStatus.CREATED);
    }

    /**
     * Actualizar sesión
     */
    @Operation(summary = "Actualizar sesion", description = "Actualiza una sesión existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{idSesion}")
    public ResponseEntity<SesionPsicologoResponseDTO> update(
            @PathVariable Long idSesion,
            @RequestBody SesionRequestDTO request
    ) {
        return ResponseEntity.ok(sesionPsicologoService.update(idSesion, request));
    }

    /**
     * Eliminar sesión
     */
    @Operation(summary = "Eliminar sesion", description = "Elimina una sesión por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{idSesion}")
    public ResponseEntity<Void> delete(@PathVariable Long idSesion) {
        sesionPsicologoService.delete(idSesion);
        return ResponseEntity.noContent().build();
    }
}