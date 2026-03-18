package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPsicologo.response.UsuarioPsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.UsuarioPsicologoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone los datos de perfil del usuario autenticado
 * como psicólogo.
 *
 * <p>Base URL: {@code /api/psicologo/usuario}</p>
 */
@RestController
@RequestMapping("/api/psicologo/usuario")
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
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioPsicologoResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioPsicologoService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

