package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.MensajeRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.MensajeResponseDTO;
import com.amani.amaniapirest.services.paciente.MensajeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    private final MensajeService mensajeService;

    public MensajeController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    /** GET /api/mensajes — Lista todos los mensajes. */
    @GetMapping
    public ResponseEntity<List<MensajeResponseDTO>> findAll() {
        return ResponseEntity.ok(mensajeService.findAll());
    }

    /** GET /api/mensajes/{id} — Obtiene un mensaje por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<MensajeResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(mensajeService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/mensajes/enviados/{idUsuario} — Lista los mensajes enviados por un usuario. */
    @GetMapping("/enviados/{idUsuario}")
    public ResponseEntity<List<MensajeResponseDTO>> findEnviados(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mensajeService.findEnviados(idUsuario));
    }

    /** GET /api/mensajes/recibidos/{idUsuario} — Lista los mensajes recibidos por un usuario. */
    @GetMapping("/recibidos/{idUsuario}")
    public ResponseEntity<List<MensajeResponseDTO>> findRecibidos(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mensajeService.findRecibidos(idUsuario));
    }

    /** POST /api/mensajes — Envía un nuevo mensaje. */
    @PostMapping
    public ResponseEntity<MensajeResponseDTO> create(@Valid @RequestBody MensajeRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(mensajeService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PATCH /api/mensajes/{id}/leido — Marca un mensaje como leído. */
    @PatchMapping("/{id}/leido")
    public ResponseEntity<MensajeResponseDTO> marcarLeido(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(mensajeService.marcarLeido(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/mensajes/{id} — Elimina un mensaje. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            mensajeService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

