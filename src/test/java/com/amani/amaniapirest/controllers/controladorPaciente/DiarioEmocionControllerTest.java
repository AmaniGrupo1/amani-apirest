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

    @Test
    @DisplayName("findAll retorna 200 con datos")
    void findAll_retorna200() throws Exception {
        when(service.findAll()).thenReturn(List.of(new DiarioEmocionResponseDTO(1L, "Feliz", "Buen día", null, 1L)));

        mockMvc.perform(get("/api/diario-emocion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("findById retorna 200 cuando existe")
    void findById_retorna200() throws Exception {
        when(service.findById(1L)).thenReturn(new DiarioEmocionResponseDTO(1L, "Feliz", "Buen día", null, 1L));

        mockMvc.perform(get("/api/diario-emocion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
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
        when(service.create(any())).thenReturn(new DiarioEmocionResponseDTO(1L, "Feliz", "Buen día", null, 1L));

        mockMvc.perform(post("/api/diario-emocion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estadoAnimo\":\"Feliz\",\"notas\":\"Buen día\",\"idPaciente\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estadoAnimo").value("Feliz"));
    }

    @Test
    @DisplayName("create retorna 400 cuando datos inválidos")
    void create_retorna400() throws Exception {
        when(service.create(any())).thenThrow(new RuntimeException("Datos inválidos"));

        mockMvc.perform(post("/api/diario-emocion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("update retorna 200 cuando existe")
    void update_retorna200() throws Exception {
        when(service.update(eq(1L), any())).thenReturn(new DiarioEmocionResponseDTO(1L, "Triste", "Mal día", null, 1L));

        mockMvc.perform(put("/api/diario-emocion/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estadoAnimo\":\"Triste\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoAnimo").value("Triste"));
    }

    @Test
    @DisplayName("update retorna 404 cuando no existe")
    void update_retorna404() throws Exception {
        when(service.update(eq(99L), any())).thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(put("/api/diario-emocion/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estadoAnimo\":\"Triste\"}"))
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
