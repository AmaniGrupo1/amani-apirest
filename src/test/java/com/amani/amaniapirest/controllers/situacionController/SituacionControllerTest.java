package com.amani.amaniapirest.controllers.situacionController;

import com.amani.amaniapirest.configuration.GlobalExceptionHandler;
import com.amani.amaniapirest.dto.situacion.SituacionDTO;
import com.amani.amaniapirest.dto.situacion.SituacionRequest;
import com.amani.amaniapirest.models.Situacion;
import com.amani.amaniapirest.repository.SituacionRepository;
import com.amani.amaniapirest.services.situacionService.SituacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SituacionControllerTest {

    @Mock
    private SituacionRepository situacionRepository;

    @Mock
    private SituacionService situacionService;

    @InjectMocks
    private SituacionController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listarSituacionesReturns200WithData() throws Exception {
        Situacion s1 = Situacion.builder().idSituacion(1L).nombre("Estrés laboral").categoria("Trabajo").descripcion("Presión en el trabajo").build();
        Situacion s2 = Situacion.builder().idSituacion(2L).nombre("Ansiedad social").categoria("Social").descripcion("Miedo a interactuar").build();
        when(situacionRepository.findByActivoTrue()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/situaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idSituacion").value(1))
                .andExpect(jsonPath("$[1].idSituacion").value(2));
    }

    @Test
    void listarSituacionesReturns200WithEmptyList() throws Exception {
        when(situacionRepository.findByActivoTrue()).thenReturn(List.of());

        mockMvc.perform(get("/api/situaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void obtenerSituacionReturns200() throws Exception {
        Situacion s = Situacion.builder().idSituacion(1L).nombre("Depresión").categoria("Salud mental").descripcion("Tristeza persistente").build();
        when(situacionRepository.findById(1L)).thenReturn(Optional.of(s));

        mockMvc.perform(get("/api/situaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSituacion").value(1))
                .andExpect(jsonPath("$.nombre").value("Depresión"));
    }

    @Test
    void obtenerSituacionReturns500WhenNotFound() throws Exception {
        when(situacionRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/situaciones/99"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createReturns200() throws Exception {
        SituacionDTO dto = new SituacionDTO(1L, "Nueva situación", "Categoría", "Descripción");
        when(situacionService.create(any(SituacionRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/situaciones/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Nueva situación","categoria":"Categoría","descripcion":"Descripción","activo":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSituacion").value(1))
                .andExpect(jsonPath("$.nombre").value("Nueva situación"));
    }

    @Test
    void updateReturns200() throws Exception {
        SituacionDTO dto = new SituacionDTO(1L, "Actualizada", "Cat", "Desc");
        when(situacionService.update(any(), any(SituacionRequest.class))).thenReturn(dto);

        mockMvc.perform(put("/api/situaciones/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Actualizada","categoria":"Cat","descripcion":"Desc","activo":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Actualizada"));
    }

    @Test
    void updateReturns500WhenNotFound() throws Exception {
        when(situacionService.update(any(), any(SituacionRequest.class))).thenThrow(new RuntimeException("Situación no encontrada"));

        mockMvc.perform(put("/api/situaciones/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"X","categoria":"Y","descripcion":"Z","activo":true}
                                """))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteReturns204() throws Exception {
        doNothing().when(situacionService).delete(1L);

        mockMvc.perform(delete("/api/situaciones/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturns500WhenNotFound() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("Situación no encontrada")).when(situacionService).delete(99L);

        mockMvc.perform(delete("/api/situaciones/delete/99"))
                .andExpect(status().isInternalServerError());
    }
}
