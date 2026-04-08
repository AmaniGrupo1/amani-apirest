package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoPaciente.request.AsignarPacienteAlPsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PsicologoSelfResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.PsicologoConPacientesDTO;
import com.amani.amaniapirest.services.psicologo.PsicologoSelfService;
import com.amani.amaniapirest.services.serviceAdmin.PacienteAdminService;
import com.amani.amaniapirest.services.serviceAdmin.PsicologoAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/psicologos")
@RequiredArgsConstructor
public class PsicologoAdminController {

    private final PsicologoAdminService service;
    private final PsicologoSelfService selfService;
    private final PacienteAdminService psicologoPacienteService;

    // Asignar paciente a psicólogo
    @PostMapping("/asignar-psicologo")
    public ResponseEntity<Boolean> asignarPsicologo(@RequestBody AsignarPacienteAlPsicologoRequestDTO request) {
        boolean resultado = psicologoPacienteService.asignarPsicologo(
                request.getIdPaciente(),
                request.getIdPsicologo()
        );
        if (resultado) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }

    // Listar todos los psicólogos
    @GetMapping
    public ResponseEntity<List<PsicologoSelfResponseDTO>> findAllPsicologo() {
        List<PsicologoSelfResponseDTO> user = selfService.findAll();
        if (user.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(user);
    }

    // Crear psicólogo
    @PostMapping("/create")
    public ResponseEntity<PsicologoSelfResponseDTO> create(@RequestBody PsicologoRequestDTO request) {
        var psi = selfService.create(request);
        if (psi == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(psi);
    }

    // Actualizar psicólogo
    @PutMapping("/{id}")
    public ResponseEntity<PsicologoConPacientesDTO> update(@PathVariable Long id, @RequestBody PsicologoRequestDTO request) {
        try {
            return ResponseEntity.ok(service.update(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar psicólogo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Listar psicólogos con pacientes asignados
    @GetMapping("/pacientes")
    public ResponseEntity<List<PsicologoConPacientesDTO>> findAllPacientesAndPsicologos() {
        List<PsicologoConPacientesDTO> list = service.getPsicologosConPacientes();
        if (list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(list);
    }
}