package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.SesionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.SesionAdminService;
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
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SesionAdminControllerTest {

    @Mock
    private SesionAdminService service;

    @InjectMocks
    private SesionAdminController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.amani.amaniapirest.configuration.GlobalExceptionHandler())
                .build();
    }

    private SesionAdminResponseDTO sesionDto(String notas) {
        SesionAdminResponseDTO dto = new SesionAdminResponseDTO();
        dto.setNombrePaciente("Ana");
        dto.setApellidoPaciente("Lopez");
        dto.setNombrePsicologo("Luis");
        dto.setApellidoPsicologo("Martin");
        dto.setSessionDate(LocalDateTime.of(2026, 1, 15, 10, 0));
        dto.setDurationMinutes(50);
        dto.setNotas(notas);
        dto.setRecomendaciones("Continuar");
        dto.setCreatedAt(LocalDateTime.of(2026, 1, 15, 11, 0));
        dto.setUpdatedAt(LocalDateTime.of(2026, 1, 15, 11, 5));
        return dto;
    }

    @Test
    @DisplayName("getAll retorna 200 con datos")
    void getAll_retorna200() throws Exception {
        when(service.findAll()).thenReturn(List.of(sesionDto("Notas")));

        mockMvc.perform(get("/api/admin/sesiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notas").value("Notas"));
    }

    @Test
    @DisplayName("getAll retorna 204 cuando vacío")
    void getAll_retorna204() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/sesiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("getById retorna 200 cuando existe")
    void getById_retorna200() throws Exception {
        when(service.findById(1L)).thenReturn(sesionDto("Notas"));

        mockMvc.perform(get("/api/admin/sesiones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notas").value("Notas"));
    }

    @Test
    @DisplayName("getById retorna 404 cuando no existe")
    void getById_retorna404() throws Exception {
        when(service.findById(99L)).thenThrow(new NoSuchElementException("No existe"));

        mockMvc.perform(get("/api/admin/sesiones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("create retorna 201")
    void create_retorna201() throws Exception {
        when(service.create(any())).thenReturn(sesionDto("Notas"));

        mockMvc.perform(post("/api/admin/sesiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idCita\":1,\"sessionDate\":\"2026-01-15T10:00:00\",\"notas\":\"Notas\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notas").value("Notas"));
    }

    @Test
    @DisplayName("update retorna 200")
    void update_retorna200() throws Exception {
        when(service.update(eq(1L), any())).thenReturn(sesionDto("Notas actualizadas"));

        mockMvc.perform(put("/api/admin/sesiones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idCita\":1,\"sessionDate\":\"2026-01-15T10:00:00\",\"notas\":\"Notas actualizadas\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notas").value("Notas actualizadas"));
    }

    @Test
    @DisplayName("delete retorna 204")
    void delete_retorna204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/admin/sesiones/1"))
                .andExpect(status().isOk());
    }
}
