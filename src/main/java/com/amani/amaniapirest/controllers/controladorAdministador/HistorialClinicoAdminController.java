package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.HistorialClinicoAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.HistorialClinicoRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.HistorialClinicoAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/historial")
public class HistorialClinicoAdminController {

    private final HistorialClinicoAdminService adminService;

    public HistorialClinicoAdminController(HistorialClinicoAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<List<HistorialClinicoAdminResponseDTO>> findAll() {
        return ResponseEntity.ok(adminService.findAllAdmin());
    }

    @GetMapping("/{idHistory}")
    public ResponseEntity<HistorialClinicoAdminResponseDTO> findById(@PathVariable Long idHistory) {
        try {
            return ResponseEntity.ok(adminService.findByIdAdmin(idHistory));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<HistorialClinicoAdminResponseDTO> create(@RequestBody HistorialClinicoRequestDTO request) {
        return ResponseEntity.ok(adminService.createAdmin(request));
    }

    @PutMapping("/{idHistory}")
    public ResponseEntity<HistorialClinicoAdminResponseDTO> update(@PathVariable Long idHistory,
                                                                   @RequestBody HistorialClinicoRequestDTO request) {
        try {
            return ResponseEntity.ok(adminService.updateAdmin(idHistory, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{idHistory}")
    public ResponseEntity<Void> delete(@PathVariable Long idHistory) {
        try {
            adminService.deleteAdmin(idHistory);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
