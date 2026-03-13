package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.SesionPsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.SesionPsicologoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psicologo/sesiones")
public class SesionPsicologoController {

    private final SesionPsicologoService sesionPsicologoService;

    public SesionPsicologoController(SesionPsicologoService sesionPsicologoService) {
        this.sesionPsicologoService = sesionPsicologoService;
    }

    /**
     * Obtener todas las sesiones de un psicólogo
     */
    @GetMapping("/psicologo/{idPsicologo}")
    public List<SesionPsicologoResponseDTO> getAllByPsicologo(@PathVariable Long idPsicologo) {
        return sesionPsicologoService.findAllByPsicologo(idPsicologo);
    }

    /**
     * Obtener sesión por ID
     */
    @GetMapping("/{idSesion}")
    public SesionPsicologoResponseDTO getById(@PathVariable Long idSesion) {
        return sesionPsicologoService.findById(idSesion);
    }

    /**
     * Crear sesión
     */
    @PostMapping("/psicologo/{idPsicologo}")
    public SesionPsicologoResponseDTO create(
            @PathVariable Long idPsicologo,
            @RequestBody SesionRequestDTO request
    ) {
        return sesionPsicologoService.create(request, idPsicologo);
    }

    /**
     * Actualizar sesión
     */
    @PutMapping("/{idSesion}")
    public SesionPsicologoResponseDTO update(
            @PathVariable Long idSesion,
            @RequestBody SesionRequestDTO request
    ) {
        return sesionPsicologoService.update(idSesion, request);
    }

    /**
     * Eliminar sesión
     */
    @DeleteMapping("/{idSesion}")
    public void delete(@PathVariable Long idSesion) {
        sesionPsicologoService.delete(idSesion);
    }
}