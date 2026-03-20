package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PsicologoSelfResponseDTO;
import com.amani.amaniapirest.services.psicologo.PsicologoSelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controlador REST que permite a un psicologo gestionar su propio perfil profesional.
 *
 * <p>Base path: {@code /api/psicologo}. Soporta operaciones CRUD sobre el perfil
 * del psicologo autenticado.</p>
 */
@RestController
@RequestMapping("/api/psicologo")
@Tag(name = "Perfil Psicologo", description = "Gestion del perfil profesional del psicologo")
public class PsicologoSelfController {

    private final PsicologoSelfService service;

    public PsicologoSelfController(PsicologoSelfService service) {
        this.service = service;
    }

    /**
     * Obtiene el perfil de un psicologo por su identificador.
     *
     * @param id identificador del psicologo.
     * @return datos del perfil profesional.
     */
    @Operation(summary = "Obtener perfil", description = "Obtiene el perfil de un psicologo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public PsicologoSelfResponseDTO getById(@PathVariable Long id) {
        return service.findById(id);
    }

    /**
     * Crea un nuevo perfil de psicologo.
     *
     * @param request datos del perfil a crear.
     * @return el perfil creado.
     */
    @Operation(summary = "Crear perfil", description = "Crea un nuevo perfil de psicologo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public PsicologoSelfResponseDTO create(@RequestBody PsicologoRequestDTO request) {
        return service.create(request);
    }

    /**
     * Actualiza el perfil de un psicologo.
     *
     * @param id      identificador del psicologo.
     * @param request datos actualizados.
     * @return el perfil actualizado.
     */
    @Operation(summary = "Actualizar perfil", description = "Actualiza el perfil de un psicologo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public PsicologoSelfResponseDTO update(@PathVariable Long id,
                                           @RequestBody PsicologoRequestDTO request) {
        return service.update(id, request);
    }

    /**
     * Elimina el perfil de un psicologo.
     *
     * @param id identificador del psicologo a eliminar.
     */
    @Operation(summary = "Eliminar perfil", description = "Elimina el perfil de un psicologo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
