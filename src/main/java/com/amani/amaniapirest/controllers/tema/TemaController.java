package com.amani.amaniapirest.controllers.tema;



import com.amani.amaniapirest.dto.colorNegroBlanco.UpdateTemaDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.AjusteResponseDTO;

import com.amani.amaniapirest.services.UsuarioService;
import com.amani.amaniapirest.services.paciente.AjusteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de ajustes de tema del usuario.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ajustes")
@Tag(name = "Ajustes de Tema", description = "API para gestionar los ajustes de interfaz (tema) del usuario")
public class TemaController {

    private final AjusteService ajusteService;
    private final UsuarioService usuarioService;  // ← Inyectar UsuarioService

    @Operation(summary = "Actualizar tema", description = "Actualiza el tema de la interfaz para el usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tema actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/tema")
    public ResponseEntity<AjusteResponseDTO> actualizarTema(
            Authentication authentication,
            @RequestBody @Valid UpdateTemaDTO dto
    ) {
        // Obtener el email del authentication
        String email = authentication.getName();

        // Buscar el usuario por email para obtener su ID
        Long idUsuario = usuarioService.findIdByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        return ResponseEntity.ok(
                ajusteService.actualizarTema(idUsuario, dto)
        );
    }
}