package com.amani.amaniapirest.controllers.controladorPsicologo;


import com.amani.amaniapirest.dto.dtoPaciente.request.MensajeRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.MensajeResponseDTO;
import com.amani.amaniapirest.services.paciente.MensajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psicologo/mensajes")
public class MensajePsicologoController {

    private final MensajeService mensajeService;

    public MensajePsicologoController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @GetMapping
    public ResponseEntity<List<MensajeResponseDTO>> getAllMensajes() {
        return ResponseEntity.ok(mensajeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MensajeResponseDTO> getMensajeById(@PathVariable Long id) {
        return ResponseEntity.ok(mensajeService.findById(id));
    }

    @GetMapping("/enviados/{idUsuario}")
    public ResponseEntity<List<MensajeResponseDTO>> getMensajesEnviados(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mensajeService.findEnviados(idUsuario));
    }

    @GetMapping("/recibidos/{idUsuario}")
    public ResponseEntity<List<MensajeResponseDTO>> getMensajesRecibidos(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mensajeService.findRecibidos(idUsuario));
    }

    @PostMapping
    public ResponseEntity<MensajeResponseDTO> crearMensaje(@RequestBody MensajeRequestDTO request) {
        return ResponseEntity.ok(mensajeService.create(request));
    }

    @PutMapping("/marcarLeido/{id}")
    public ResponseEntity<MensajeResponseDTO> marcarLeido(@PathVariable Long id) {
        return ResponseEntity.ok(mensajeService.marcarLeido(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMensaje(@PathVariable Long id) {
        mensajeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
