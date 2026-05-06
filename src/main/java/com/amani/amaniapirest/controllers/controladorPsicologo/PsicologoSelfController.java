package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PsicologoSelfResponseDTO;
import com.amani.amaniapirest.dto.profile.psicologo.PsicologoDTO;
import com.amani.amaniapirest.services.psicologo.PsicologoSelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de datos del psicólogo autenticado.
 *
 * <p>Expone endpoints para consulta y actualización de su perfil profesional.</p>
 */
@RestController
@RequestMapping("/api/psicologo")
@Tag(name = "Psicólogo (Self)", description = "Gestión de perfil del psicólogo autenticado")
public class PsicologoSelfController {

    private final PsicologoSelfService service;

    public PsicologoSelfController(PsicologoSelfService service) {
        this.service = service;
    }

    /**
     * Obtiene el perfil completo del psicólogo por ID.
     *
     * @param id identificador del psicólogo
     * @return {@link PsicologoDTO} con los datos del perfil
     */
    @Operation(summary = "Obtener perfil por ID", description = "Recupera la información completa de un psicólogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil recuperado exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Psicólogo no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public PsicologoDTO getById(@PathVariable Long id) {
        return service.findProfileById(id);
    }


    /**
     * Actualiza los datos del perfil del psicólogo.
     *
     * @param id      identificador del psicólogo
     * @param request {@link PsicologoRequestDTO} con los nuevos datos
     * @return {@link PsicologoSelfResponseDTO} con los datos actualizados
     */
    @Operation(summary = "Actualizar perfil", description = "Actualiza la información del perfil del psicólogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Psicólogo no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public PsicologoSelfResponseDTO update(@PathVariable Long id,
                                           @RequestBody PsicologoRequestDTO request) {
        return service.update(id, request);
    }

    /**
     * Elimina el perfil del psicólogo.
     *
     * @param id identificador del psicólogo a eliminar
     */
    @Operation(summary = "Eliminar perfil", description = "Elimina la cuenta de psicólogo del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Psicólogo eliminado exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Psicólogo no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
