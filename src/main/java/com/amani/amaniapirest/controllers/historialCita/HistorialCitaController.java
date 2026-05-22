package com.amani.amaniapirest.controllers.historialCita;


import com.amani.amaniapirest.dto.historialCita.HistorialCitaResponseDTO;
import com.amani.amaniapirest.services.historialCita.HistorialCitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial-citas")
@RequiredArgsConstructor
@Tag(name = "Historial Cita", description = "Controlador para la gestión del historial de citas")
public class HistorialCitaController {

    private final HistorialCitaService historialCitaService;

    @GetMapping
    @Operation(summary = "Obtener historial de citas", description = "Obtiene una lista con el historial de todas las citas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de citas obtenido exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<HistorialCitaResponseDTO> obtenerHistorial() {
        return historialCitaService.obtenerHistorialCitas();
    }
}
