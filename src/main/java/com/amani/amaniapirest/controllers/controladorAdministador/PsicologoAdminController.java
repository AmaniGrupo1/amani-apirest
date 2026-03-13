package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.PsicologoAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.PsicologoAdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/psicologos")
public class PsicologoAdminController {

    private final PsicologoAdminService service;

    public PsicologoAdminController(PsicologoAdminService service) {
        this.service = service;
    }

    @GetMapping
    public List<PsicologoAdminResponseDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PsicologoAdminResponseDTO getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public PsicologoAdminResponseDTO create(@RequestBody PsicologoRequestDTO request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public PsicologoAdminResponseDTO update(@PathVariable Long id,
                                            @RequestBody PsicologoRequestDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
