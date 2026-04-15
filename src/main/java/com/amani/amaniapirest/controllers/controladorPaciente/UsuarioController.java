package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.response.UsuarioResponseDTO;
import com.amani.amaniapirest.services.paciente.UsuarioPacienteService;
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
 * Controlador REST que permite a un usuario autenticado consultar su propio perfil.
 *
 * <p>Base path: {@code /api/usuarios}.</p>
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Consulta de perfil de usuario")
public class UsuarioController {

    private final UsuarioPacienteService usuarioService;

    public UsuarioController(UsuarioPacienteService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene los datos de un usuario por su identificador único.
     *
     * @param id identificador único del usuario
     * @return datos del usuario
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Obtener usuario", description = "Recupera los datos de un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario retornado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún usuario con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
