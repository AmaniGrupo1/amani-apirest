package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.PsicologoAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.PsicologoAdminService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/psicologos")
@RequiredArgsConstructor
public class PsicologoAdminController {

    private final PsicologoAdminService service;

    @GetMapping
    public ResponseEntity<List<PsicologoAdminResponseDTO>> getAll() {
        List<PsicologoAdminResponseDTO> list = service.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PsicologoAdminResponseDTO> getById(@PathVariable Long id) {
        var s = service.findById(id);
        if (s == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(s);
    }

    @PostMapping
    public ResponseEntity<PsicologoAdminResponseDTO> create(@RequestBody PsicologoRequestDTO request) {
        var psicologo = service.create(request);
        if (psicologo == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(psicologo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PsicologoAdminResponseDTO> update(@PathVariable Long id,
                                                            @RequestBody PsicologoRequestDTO request) {
        var s = service.update(id, request);
        if (s == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(s);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/pacientes")
    public ResponseEntity<List<PsicologoAdminResponseDTO>> findAllPacientesAndPsicologos() {
        List<PsicologoAdminResponseDTO> c = service.getPacientesConPsicologo();
        if (c.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(c);
    }
}
