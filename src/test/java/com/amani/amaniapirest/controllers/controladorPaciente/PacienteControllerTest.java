package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.PacienteResponseDTO;
import com.amani.amaniapirest.dto.profile.UsuarioDTO;
import com.amani.amaniapirest.dto.profile.paciente.PacienteDTO;
import com.amani.amaniapirest.services.paciente.PacienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
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
class PacienteControllerTest {

    @Mock
    private PacienteService pacienteService;

    @InjectMocks
    private PacienteController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void findAllReturns200WithData() throws Exception {
        PacienteResponseDTO dto = new PacienteResponseDTO(LocalDate.of(1990, 1, 1), "masculino", "600000000", LocalDateTime.now(), LocalDateTime.now());
        when(pacienteService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].genero").value("masculino"));
    }

    @Test
    void findAllReturns200WhenEmpty() throws Exception {
        when(pacienteService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findByUsuarioIdReturns200() throws Exception {
        PacienteDTO dto = new PacienteDTO(1L, "600000000", "masculino", LocalDate.of(1990, 1, 1), new UsuarioDTO(10L, "Ana", "Lopez", "ana@amani.com", null));
        when(pacienteService.findByUsuarioId(10L)).thenReturn(dto);

        mockMvc.perform(get("/api/pacientes/usuario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaciente").value(1));
    }

    @Test
    void findByUsuarioIdReturns404WhenMissing() throws Exception {
        when(pacienteService.findByUsuarioId(99L)).thenThrow(new RuntimeException("No encontrado"));

        mockMvc.perform(get("/api/pacientes/usuario/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdReturns200() throws Exception {
        PacienteDTO dto = new PacienteDTO(1L, "600000000", "masculino", LocalDate.of(1990, 1, 1), new UsuarioDTO(10L, "Ana", "Lopez", "ana@amani.com", null));
        when(pacienteService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaciente").value(1));
    }

    @Test
    void findByIdReturns404WhenMissing() throws Exception {
        when(pacienteService.findById(99L)).thenThrow(new RuntimeException("No encontrado"));

        mockMvc.perform(get("/api/pacientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturns201WhenValid() throws Exception {
        PacienteResponseDTO dto = new PacienteResponseDTO(LocalDate.of(1990, 1, 1), "femenino", "611111111", LocalDateTime.now(), LocalDateTime.now());
        when(pacienteService.create(any(PacienteRequestDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPacienteRequestBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.genero").value("femenino"));
    }

    @Test
    void createReturns400OnBusinessError() throws Exception {
        when(pacienteService.create(any(PacienteRequestDTO.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPacienteRequestBody()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReturns200() throws Exception {
        PacienteResponseDTO dto = new PacienteResponseDTO(LocalDate.of(1991, 2, 2), "femenino", "622222222", LocalDateTime.now(), LocalDateTime.now());
        when(pacienteService.update(any(), any(PacienteRequestDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPacienteRequestBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telefono").value("622222222"));
    }

    @Test
    void updateReturns404WhenMissing() throws Exception {
        when(pacienteService.update(any(), any(PacienteRequestDTO.class))).thenThrow(new RuntimeException("No encontrado"));

        mockMvc.perform(put("/api/pacientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPacienteRequestBody()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns204() throws Exception {
        doNothing().when(pacienteService).delete(1L);

        mockMvc.perform(delete("/api/pacientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturns404WhenMissing() throws Exception {
        doThrow(new RuntimeException("No encontrado")).when(pacienteService).delete(99L);

        mockMvc.perform(delete("/api/pacientes/99"))
                .andExpect(status().isNotFound());
    }

    private String validPacienteRequestBody() {
        return """
                {
                  "idUsuario": 1,
                  "fechaNacimiento": "1990-01-01",
                  "genero": "femenino",
                  "telefono": "611111111",
                  "aceptaTerminos": true
                }
                """;
    }
}
