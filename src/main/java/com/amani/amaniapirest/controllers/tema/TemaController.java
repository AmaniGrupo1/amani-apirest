package com.amani.amaniapirest.controllers.tema;



import com.amani.amaniapirest.dto.colorNegroBlanco.UpdateTemaDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.AjusteResponseDTO;

import com.amani.amaniapirest.services.UsuarioService;
import com.amani.amaniapirest.services.paciente.AjusteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ajustes")
public class TemaController {

    private final AjusteService ajusteService;
    private final UsuarioService usuarioService;  // ← Inyectar UsuarioService

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