package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.SesionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.SesionAdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sesiones")
public class SesionAdminController {

    private final SesionAdminService sesionAdminService;

    public SesionAdminController(SesionAdminService sesionAdminService) {
        this.sesionAdminService = sesionAdminService;
    }

    @GetMapping
    public List<SesionAdminResponseDTO> getAll() {
        return sesionAdminService.findAll();
    }

    @GetMapping("/{idSesion}")
    public SesionAdminResponseDTO getById(@PathVariable Long idSesion) {
        return sesionAdminService.findById(idSesion);
    }

    /**
     * Crear sesión
     */
    @PostMapping
    public SesionAdminResponseDTO create(@RequestBody SesionRequestDTO request) {
        return sesionAdminService.create(request);
    }

    /**
     * Actualizar sesión
     */
    @PutMapping("/{idSesion}")
    public SesionAdminResponseDTO update(
            @PathVariable Long idSesion,
            @RequestBody SesionRequestDTO request
    ) {
        return sesionAdminService.update(idSesion, request);
    }

    /**
     * Eliminar sesión
     */
    @DeleteMapping("/{idSesion}")
    public void delete(@PathVariable Long idSesion) {
        sesionAdminService.delete(idSesion);
    }
}