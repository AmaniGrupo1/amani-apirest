package com.amani.amaniapirest.controllers.controladorAdministador;


import com.amani.amaniapirest.dto.dtoAdmin.response.DireccionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.DireccionRequestDTO;
import com.amani.amaniapirest.models.Direccion;
import com.amani.amaniapirest.services.serviceAdmin.DireccionAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones/admin")
public class DireccionAdminController {

    private final DireccionAdminService direccionService;

    public DireccionAdminController(DireccionAdminService direccionService) {
        this.direccionService = direccionService;
    }

    @GetMapping
    public ResponseEntity<List<DireccionAdminResponseDTO>> findAll() {
        return ResponseEntity.ok(direccionService.findAll());
    }

    @GetMapping("/{idDireccion}")
    public ResponseEntity<DireccionAdminResponseDTO> findById(@PathVariable Long idDireccion) {
        return ResponseEntity.ok(direccionService.findById(idDireccion));
    }

    @PostMapping
    public ResponseEntity<DireccionAdminResponseDTO> create(@RequestBody DireccionRequestDTO direccion) {
        return ResponseEntity.ok(direccionService.create(direccion));
    }

    @PutMapping("/{idDireccion}")
    public ResponseEntity<DireccionAdminResponseDTO> update(@PathVariable Long idDireccion,
                                                            @RequestBody Direccion direccion) {
        direccion.setIdDireccion(idDireccion);
        return ResponseEntity.ok(direccionService.update(direccion));
    }

    @DeleteMapping("/{idDireccion}")
    public ResponseEntity<Void> delete(@PathVariable Long idDireccion) {
        direccionService.delete(idDireccion);
        return ResponseEntity.noContent().build();
    }
}
