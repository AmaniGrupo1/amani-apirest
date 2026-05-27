package com.amani.amaniapirest.controllers.role;

import com.amani.amaniapirest.dto.roles.CambiarRolRequestDTO;

import com.amani.amaniapirest.dto.roles.CambiarRolResponseDTO;
import com.amani.amaniapirest.dto.roles.UsuarioDTO;
import com.amani.amaniapirest.services.roles.AdminRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de roles de administración.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Roles de Administración", description = "API para gestionar los roles y usuarios desde la administración")
public class AdminRoleController {

    private final AdminRoleService adminRoleService;



    @Operation(summary = "Cambiar rol de usuario", description = "Modifica el rol asignado a un usuario específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol de usuario cambiado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/cambiar-rol")
    public ResponseEntity<CambiarRolResponseDTO> cambiarRol(
            @Valid @RequestBody CambiarRolRequestDTO request
    ) {

        return ResponseEntity.ok(
                adminRoleService.cambiarRol(request)
        );
    }

    @Operation(summary = "Listar usuarios", description = "Obtiene una lista de usuarios, con la posibilidad de filtrar por rol o DNI")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No se encontraron usuarios"),
            @ApiResponse(responseCode = "400", description = "Parámetros de filtrado inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/listarUsuarios")
    public ResponseEntity<?> getUsuarios(
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String dni
    ) {
        try {
            List<UsuarioDTO> usuarios = adminRoleService.getUsuarios(rol, dni);
            if (usuarios == null || usuarios.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(usuarios);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al obtener usuarios", "detail", e.getMessage()));
        }
    }
}
