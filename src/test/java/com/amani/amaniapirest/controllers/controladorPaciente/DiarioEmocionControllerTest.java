package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.DiarioEmocionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.DiarioEmocionResponseDTO;
import com.amani.amaniapirest.services.paciente.DiarioEmocionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DiarioEmocionControllerTest {

    @Mock
    private DiarioEmocionService service;

    @InjectMocks
    private DiarioEmocionController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private DiarioEmocionResponseDTO diarioDto(Long id, String emocion, String nota) {
        DiarioEmocionResponseDTO dto = new DiarioEmocionResponseDTO();
        dto.setIdDiario(id);
        dto.setTitulo("Entrada");
        dto.setFecha(LocalDateTime.of(2026, 2, 10, 9, 0));
        dto.setEmocion(emocion);
        dto.setIntensidad(7);
        dto.setNota(nota);
        return dto;
    }

    @Test
    @DisplayName("findAll retorna 200 con datos")
    void findAll_retorna200() throws Exception {
        when(service.findAll()).thenReturn(List.of(diarioDto(1L, "Feliz", "Buen dia")));

        mockMvc.perform(get("/api/diario-emocion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idDiario").value(1));
    }

    @Test
    @DisplayName("findById retorna 200 cuando existe")
    void findById_retorna200() throws Exception {
        when(service.findById(1L)).thenReturn(diarioDto(1L, "Feliz", "Buen dia"));

        mockMvc.perform(get("/api/diario-emocion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDiario").value(1));
    }

    @Test
    @DisplayName("findById retorna 404 cuando no existe")
    void findById_retorna404() throws Exception {
        when(service.findById(99L)).thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(get("/api/diario-emocion/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("create retorna 201 cuando datos válidos")
    void create_retorna201() throws Exception {
        when(service.create(any())).thenReturn(diarioDto(1L, "alegria", "Buen dia"));

        mockMvc.perform(post("/api/diario-emocion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPaciente\":1,\"titulo\":\"Entrada\",\"emocion\":\"alegria\",\"intensidad\":7,\"nota\":\"Buen dia\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.emocion").value("alegria"));
    }

    @Test
    @DisplayName("create retorna 400 cuando datos inválidos")
    void create_retorna400() throws Exception {
        mockMvc.perform(post("/api/diario-emocion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("update retorna 200 cuando existe")
    void update_retorna200() throws Exception {
        when(service.update(eq(1L), any())).thenReturn(diarioDto(1L, "tristeza", "Mal dia"));

        mockMvc.perform(put("/api/diario-emocion/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPaciente\":1,\"titulo\":\"Entrada\",\"emocion\":\"tristeza\",\"intensidad\":4,\"nota\":\"Mal dia\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emocion").value("tristeza"));
    }

    @Test
    @DisplayName("update retorna 404 cuando no existe")
    void update_retorna404() throws Exception {
        when(service.update(eq(99L), any())).thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(put("/api/diario-emocion/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPaciente\":1,\"titulo\":\"Entrada\",\"emocion\":\"tristeza\",\"intensidad\":4,\"nota\":\"Mal dia\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("delete retorna 204 cuando existe")
    void delete_retorna204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/diario-emocion/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("delete retorna 404 cuando no existe")
    void delete_retorna404() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("No existe")).when(service).delete(99L);

        mockMvc.perform(delete("/api/diario-emocion/99"))
                .andExpect(status().isNotFound());
    }
}
