package com.amani.amaniapirest.controllers.tema;

import com.amani.amaniapirest.dto.colorNegroBlanco.UpdateTemaDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.AjusteResponseDTO;
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

    @PutMapping("/tema")
    public ResponseEntity<AjusteResponseDTO> actualizarTema(
            Authentication authentication,
            @RequestBody @Valid UpdateTemaDTO dto
    ) {

        Long idUsuario = Long.parseLong(authentication.getName());

        return ResponseEntity.ok(
                ajusteService.actualizarTema(idUsuario, dto)
        );
    }
}