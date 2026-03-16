package com.amani.amaniapirest.controllers.preguntasController;

import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.PreguntaRequestDTO;
import com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.PreguntaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/preguntas")
@RequiredArgsConstructor
public class PreguntaAdminController {

    private final PreguntaServicio preguntaService;

    // CREAR
    @PreAuthorize("hasRole('admin')") // Solo los usuarios con el rol ADMIN pueden crear preguntas
    @PostMapping
    public void crearPregunta(@RequestBody PreguntaRequestDTO dto){

        preguntaService.crearPregunta(dto);

    }

    // ELIMINAR
    @PreAuthorize("hasRole('admin')") // Solo los usuarios con el rol ADMIN pueden eliminar preguntas
    @DeleteMapping("/{id}")
    public void eliminarPregunta(@PathVariable Long id){
        preguntaService.eliminarPregunta(id);
    }

}
