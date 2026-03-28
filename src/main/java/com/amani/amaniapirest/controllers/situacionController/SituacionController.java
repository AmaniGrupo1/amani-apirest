package com.amani.amaniapirest.controllers.situacionController;

import com.amani.amaniapirest.dto.situacion.SituacionDTO;
import com.amani.amaniapirest.models.Situacion;
import com.amani.amaniapirest.repository.SituacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/situaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SituacionController {

    private final SituacionRepository situacionRepository;

    @GetMapping
    public ResponseEntity<List<SituacionDTO>> listarSituaciones() {
        List<Situacion> situaciones = situacionRepository.findByActivoTrue();

        List<SituacionDTO> response = situaciones.stream()
                .map(s -> new SituacionDTO(
                        s.getIdSituacion(),
                        s.getNombre(),
                        s.getCategoria(),
                        s.getDescripcion()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SituacionDTO> obtenerSituacion(@PathVariable Long id) {
        Situacion situacion = situacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Situación no encontrada"));

        SituacionDTO response = new SituacionDTO(
                situacion.getIdSituacion(),
                situacion.getNombre(),
                situacion.getCategoria(),
                situacion.getDescripcion()
        );

        return ResponseEntity.ok(response);
    }
}
