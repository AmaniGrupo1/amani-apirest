package com.amani.amaniapirest.controllers.historialCita;


import com.amani.amaniapirest.dto.historialCita.HistorialCitaResponseDTO;
import com.amani.amaniapirest.services.historialCita.HistorialCitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial-citas")
@RequiredArgsConstructor
public class HistorialCitaController {

    private final HistorialCitaService historialCitaService;

    @GetMapping
    public List<HistorialCitaResponseDTO> obtenerHistorial() {
        return historialCitaService.obtenerHistorialCitas();
    }
}
