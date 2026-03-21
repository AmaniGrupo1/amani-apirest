package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.SesionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.SesionAdminService;
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
@RequestMapping("/api/admin/sesiones")
@Tag(name = "Sesiones (Admin)", description = "CRUD de sesiones — vista administrador")
public class SesionAdminController {

    private final SesionAdminService sesionAdminService;

    public SesionAdminController(SesionAdminService sesionAdminService) {
        this.sesionAdminService = sesionAdminService;
    }

    @Operation(summary = "Listar sesiones", description = "Lista todas las sesiones del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public List<SesionAdminResponseDTO> getAll() {
        return sesionAdminService.findAll();
    }

    @Operation(summary = "Obtener sesion", description = "Obtiene una sesion por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{idSesion}")
    public SesionAdminResponseDTO getById(@PathVariable Long idSesion) {
        return sesionAdminService.findById(idSesion);
    }

    /**
     * Crear sesión
     */
    @Operation(summary = "Crear sesion", description = "Crea una nueva sesion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public SesionAdminResponseDTO create(@RequestBody SesionRequestDTO request) {
        return sesionAdminService.create(request);
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
    public SesionAdminResponseDTO update(
            @PathVariable Long idSesion,
            @RequestBody SesionRequestDTO request
    ) {
        return sesionAdminService.update(idSesion, request);
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
        sesionAdminService.delete(idSesion);
    }
}