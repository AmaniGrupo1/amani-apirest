package com.amani.amaniapirest.controllers.profileController;

import com.amani.amaniapirest.configuration.ErrorResponse;
import com.amani.amaniapirest.dto.profile.PsicologoDTO;
import com.amani.amaniapirest.services.profile.ProfileService;
import com.amani.amaniapirest.services.psicologo.PsicologoSelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/psicologo")
public class ProfileController {
    private final ProfileService psicologoSelfService;

    @PostMapping("/{id}/foto")
    public PsicologoDTO uploadFoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return psicologoSelfService.updateProfilePhoto(id, file);
    }


    //OBTENGO LA FOTO DE PERFIL DEL PSICOLOGO
    @GetMapping("/{id}/perfil")
    public ResponseEntity<PsicologoDTO> getPerfil(@PathVariable Long id) {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("USER LOGUEADO: " + auth.getName() + ", ROLES: " + auth.getAuthorities());
            PsicologoDTO dto = psicologoSelfService.getProfile(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace(); // Esto te muestra la excepción real en consola
            return ResponseEntity.badRequest().body(null); // O podrías retornar un mensaje de error específico
             //return
        }
    }
}
