package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.ProgresoEmocionalAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.ProgresoEmocionalRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.ProgresoEmocionalAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/progreso-emocional")
@Tag(name = "Progreso Emocional (Admin)", description = "CRUD de progreso emocional — vista administrador")
public class ProgresoEmocionalAdminController {

    private final ProgresoEmocionalAdminService progresoService;

    public ProgresoEmocionalAdminController(ProgresoEmocionalAdminService progresoService) {
        this.progresoService = progresoService;
    }

    @Operation(summary = "Obtener progreso", description = "Obtiene un registro de progreso emocional por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{idProgreso}")
    public ResponseEntity<ProgresoEmocionalAdminResponseDTO> getById(@PathVariable Long idProgreso) {
        return ResponseEntity.ok(progresoService.findById(idProgreso));
    }

    /** Crear un nuevo progreso emocional */
    @Operation(summary = "Crear progreso", description = "Crea un nuevo registro de progreso emocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProgresoEmocionalAdminResponseDTO> create(@RequestBody ProgresoEmocionalRequestDTO request) {
        return new ResponseEntity<>(progresoService.create(request), HttpStatus.CREATED);
    }

    /** Actualizar un progreso existente */
    @Operation(summary = "Actualizar progreso", description = "Actualiza un registro de progreso emocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{idProgreso}")
    public ResponseEntity<ProgresoEmocionalAdminResponseDTO> update(@PathVariable Long idProgreso,
                                                                    @RequestBody ProgresoEmocionalRequestDTO request) {
        return ResponseEntity.ok(progresoService.update(idProgreso, request));
    }

    /** Eliminar un progreso emocional */
    @Operation(summary = "Eliminar progreso", description = "Elimina un registro de progreso emocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{idProgreso}")
    public ResponseEntity<Void> delete(@PathVariable Long idProgreso) {
        progresoService.delete(idProgreso);
        return ResponseEntity.noContent().build();
    }
}