package com.amani.amaniapirest.controllers.preguntasController;


import com.amani.amaniapirest.dto.dtoPregunta.admin.OpcionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.paciente.PreguntaPacienteResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.OpcionAdminResDTO;
import com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.admin.PreguntaAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/preguntas")
@RequiredArgsConstructor
public class PreguntaAdminController {

    private final PreguntaAdminService preguntaAdminService;

    @GetMapping
    public ResponseEntity<List<OpcionAdminResponseDTO>> findAll() {
        List<OpcionAdminResponseDTO> opciones = preguntaAdminService.findAll();
        if (opciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(opciones);
    }

    @PostMapping
    public ResponseEntity<PreguntaPacienteResponseDTO> create(@RequestBody OpcionAdminResDTO request) {
        var pre = preguntaAdminService.create(request);
        if (pre == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pre);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        preguntaAdminService.delete(id);
    }
}
