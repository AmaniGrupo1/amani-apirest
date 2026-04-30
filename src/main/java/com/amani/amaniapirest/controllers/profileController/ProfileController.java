package com.amani.amaniapirest.controllers.profileController;

import com.amani.amaniapirest.dto.profile.PsicologoDTO;
import com.amani.amaniapirest.dto.profile.UpdatePsicologoRequestDTO;
import com.amani.amaniapirest.dto.profile.UpdatePsicologoResponseDTO;
import com.amani.amaniapirest.services.profile.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST para la gestión del perfil del psicólogo.
 *
 * <p>Expone endpoints para la carga de foto de perfil y la consulta de datos
 * del psicólogo autenticado o de un paciente específico.</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/psicologo")
@Tag(name = "Perfil (Psicólogo)", description = "Gestión de perfil y foto de psicólogo")
public class ProfileController {
    private final ProfileService psicologoSelfService;

    /**
     * Sube una foto de perfil para el psicólogo.
     *
     * @param id      identificador del psicólogo
     * @param file    archivo de imagen a subir
     * @return {@link PsicologoDTO} con la foto actualizada
     */
    @Operation(summary = "Subir foto de perfil", description = "Carga y actualiza la foto de perfil del psicólogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Foto subida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Archivo inválido", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Psicólogo no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/{id}/foto")
    public PsicologoDTO uploadFoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return psicologoSelfService.updateProfilePhoto(id, file);
    }


    /**
     * Obtiene el perfil completo del psicólogo.
     *
     * @param id identificador del psicólogo
     * @return {@link PsicologoDTO} con los datos del perfil
     */
    @Operation(summary = "Obtener perfil de psicólogo", description = "Recupera la información completa del psicólogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil recuperado exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Psicólogo no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}/perfil")
    public ResponseEntity<PsicologoDTO> getPerfil(@PathVariable Long id) {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            PsicologoDTO dto = psicologoSelfService.getProfile(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Obtiene el psicólogo asignado a un paciente específico.
     *
     * @param idPaciente identificador del paciente
     * @return {@link PsicologoDTO} con los datos del psicólogo
     */
    @Operation(summary = "Obtener psicólogo asignado", description = "Recupera el psicólogo asignado a un paciente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Psicólogo recuperado exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Paciente o psicólogo no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/pacientes/{idPaciente}/psicologo")
    public ResponseEntity<?> getPsicologoAsignado(@PathVariable Long idPaciente) {

        PsicologoDTO dto =
                psicologoSelfService.obtenerPsicologoAsignado(idPaciente);

        if (dto == null) {
            return ResponseEntity
                    .status(404)
                    .body("Debes esperar a que un administrador te asigne un psicólogo");
        }

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UpdatePsicologoResponseDTO> updatePsicologo(
            @PathVariable Long id,
            @RequestBody UpdatePsicologoRequestDTO dto
    ) {
        UpdatePsicologoResponseDTO updated = psicologoSelfService.updatePsicologoProfile(id, dto);
        return ResponseEntity.ok(updated);
    }
}
