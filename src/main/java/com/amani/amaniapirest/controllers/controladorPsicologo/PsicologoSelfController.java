package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PsicologoSelfResponseDTO;
import com.amani.amaniapirest.dto.profile.PsicologoDTO;
import com.amani.amaniapirest.services.psicologo.PsicologoSelfService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/psicologo")
public class PsicologoSelfController {

    private final PsicologoSelfService service;

    public PsicologoSelfController(PsicologoSelfService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public PsicologoDTO getById(@PathVariable Long id) {
        return service.findProfileById(id);
    }


    @PutMapping("/{id}")
    public PsicologoSelfResponseDTO update(@PathVariable Long id,
                                           @RequestBody PsicologoRequestDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
