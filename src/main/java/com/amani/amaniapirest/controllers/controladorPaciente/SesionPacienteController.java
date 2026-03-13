package com.amani.amaniapirest.controllers.controladorPaciente;
import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.SesionResponseDTO;
import com.amani.amaniapirest.services.paciente.SesionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paciente/sesiones")
public class SesionPacienteController {

    private final SesionService sesionService;

    public SesionPacienteController(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    /**
     * Obtener todas las sesiones de un paciente
     */
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<SesionResponseDTO>> findAllByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(sesionService.findAllByPaciente(idPaciente));
    }

    /**
     * Obtener una sesión por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SesionResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(sesionService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crear sesión (solo si tu lógica lo permite)
     */
    @PostMapping
    public ResponseEntity<SesionResponseDTO> create(@Valid @RequestBody SesionRequestDTO request) {
        try {
            SesionResponseDTO sesion = sesionService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(sesion);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualizar sesión
     */
    @PutMapping("/{id}")
    public ResponseEntity<SesionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody SesionRequestDTO request) {

        try {
            return ResponseEntity.ok(sesionService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar sesión
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            sesionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}