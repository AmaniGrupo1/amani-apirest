package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPsicologo.response.UsuarioPsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.UsuarioPsicologoService;
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

/**
 * Controlador REST que expone los datos de perfil del usuario autenticado
 * como psicólogo.
 *
 * <p>Base URL: {@code /api/psicologo/usuario}</p>
 */
@RestController
@RequestMapping("/api/psicologo/usuario")
@Tag(name = "Usuario Psicologo", description = "Consulta de perfil de usuario del psicologo")
public class UsuarioPsicologoController {

    private final UsuarioPsicologoService usuarioPsicologoService;

    public UsuarioPsicologoController(UsuarioPsicologoService usuarioPsicologoService) {
        this.usuarioPsicologoService = usuarioPsicologoService;
    }

    /**
     * GET /api/psicologo/usuario/{id}
     * <p>Devuelve los datos de perfil del psicólogo cuyo {@code idUsuario} coincide
     * con el parámetro de ruta.</p>
     *
     * @param id identificador del usuario psicólogo
     * @return {@code 200 OK} con el DTO de perfil, o {@code 404 Not Found} si no existe
     */
    @Operation(summary = "Obtener perfil", description = "Obtiene los datos de perfil del psicologo por su ID de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioPsicologoResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioPsicologoService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
