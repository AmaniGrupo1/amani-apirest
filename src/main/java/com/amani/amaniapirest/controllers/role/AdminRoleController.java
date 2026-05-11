package com.amani.amaniapirest.controllers.role;

import com.amani.amaniapirest.dto.roles.CambiarRolRequestDTO;
import com.amani.amaniapirest.dto.roles.CambiarRolResponseDTO;
import com.amani.amaniapirest.dto.roles.UsuarioDTO;
import com.amani.amaniapirest.services.roles.AdminRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleService adminRoleService;



    @PutMapping("/cambiar-rol")
    public ResponseEntity<CambiarRolResponseDTO> cambiarRol(
            @Valid @RequestBody CambiarRolRequestDTO request
    ) {

        return ResponseEntity.ok(
                adminRoleService.cambiarRol(request)
        );
    }

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
