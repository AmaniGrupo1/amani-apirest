package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.response.PsicologoResponseDTO;
import com.amani.amaniapirest.services.paciente.PsicologoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/paciente/psicologos")
public class PsicologoPacienteController {

    private final PsicologoService service;

    public PsicologoPacienteController(PsicologoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PsicologoResponseDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PsicologoResponseDTO getById(@PathVariable Long id) {
        return service.findById(id);
    }
}