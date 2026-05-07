package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.historialClinico.HistorialClinicoResponseDTO;
import com.amani.amaniapirest.services.paciente.HistorialClinicoService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HistorialClinicoControllerTest {

    @Mock
    private HistorialClinicoService historialClinicoService;

    @InjectMocks
    private HistorialClinicoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void findAllReturnsEmptyListWhenNoData() throws Exception {
        when(historialClinicoService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/historial-clinico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findByIdReturnsNotFoundWhenMissing() throws Exception {
        when(historialClinicoService.findById(99L)).thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(get("/api/historial-clinico/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturnsCreatedWhenValid() throws Exception {
        HistorialClinicoResponseDTO dto = new HistorialClinicoResponseDTO("Eval", "Dx", "Tx", "Obs", LocalDateTime.now());
        when(historialClinicoService.create(any())).thenReturn(dto);

        mockMvc.perform(post("/api/historial-clinico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idPaciente":1,"titulo":"Eval","diagnostico":"Dx","tratamiento":"Tx","observaciones":"Obs"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Eval"));
    }

    @Test
    void createReturnsBadRequestOnBusinessError() throws Exception {
        when(historialClinicoService.create(any())).thenThrow(new RuntimeException("Regla"));

        mockMvc.perform(post("/api/historial-clinico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idPaciente":1,"titulo":"Eval"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReturnsBadRequestWhenMissingRequiredField() throws Exception {
        mockMvc.perform(post("/api/historial-clinico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idPaciente":1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReturnsNotFoundWhenIdDoesNotExist() throws Exception {
        when(historialClinicoService.update(org.mockito.ArgumentMatchers.eq(30L), any()))
                .thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(put("/api/historial-clinico/30")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idPaciente":1,"titulo":"Nuevo"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturnsNoContentWhenSuccess() throws Exception {
        doNothing().when(historialClinicoService).delete(7L);

        mockMvc.perform(delete("/api/historial-clinico/7"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturnsNotFoundWhenMissing() throws Exception {
        doThrow(new RuntimeException("No existe")).when(historialClinicoService).delete(7L);

        mockMvc.perform(delete("/api/historial-clinico/7"))
                .andExpect(status().isNotFound());
    }
}
