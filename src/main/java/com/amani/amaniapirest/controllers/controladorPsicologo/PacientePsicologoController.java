package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PacientePsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.PacientePsicologoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de pacientes desde la perspectiva del psicólogo.
 *
 * <p>Permite al psicólogo listar, crear, actualizar y eliminar pacientes,
 * pero solo con la información permitida para su rol.</p>
 */
@RestController
@RequestMapping("/api/psicologo/pacientes")
public class PacientePsicologoController {

    private final PacientePsicologoService pacienteService;

    public PacientePsicologoController(PacientePsicologoService pacienteService) {
        this.pacienteService = pacienteService;
    }

    /** Listar todos los pacientes */
    @GetMapping
    public ResponseEntity<List<PacientePsicologoResponseDTO>> getAllPacientes() {
        List<PacientePsicologoResponseDTO> pacientes = pacienteService.findAll();
        return ResponseEntity.ok(pacientes);
    }

    /** Obtener un paciente por ID */
    @GetMapping("/{id}")
    public ResponseEntity<PacientePsicologoResponseDTO> getPacienteById(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.findById(id));
    }

    /** Crear un nuevo paciente (solo datos básicos) */
    @PostMapping
    public ResponseEntity<PacientePsicologoResponseDTO> crearPaciente(@RequestBody PacienteRequestDTO request) {
        PacientePsicologoResponseDTO paciente = pacienteService.create(request);
        return ResponseEntity.ok(paciente);
    }

    /** Actualizar un paciente existente */
    @PutMapping("/{id}")
    public ResponseEntity<PacientePsicologoResponseDTO> actualizarPaciente(
            @PathVariable Long id,
            @RequestBody PacienteRequestDTO request) {
        PacientePsicologoResponseDTO paciente = pacienteService.update(id, request);
        return ResponseEntity.ok(paciente);
    }

    /** Eliminar un paciente */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
