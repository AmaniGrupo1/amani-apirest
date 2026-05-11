package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PsicologoSelfResponseDTO;
import com.amani.amaniapirest.dto.profile.UsuarioDTO;
import com.amani.amaniapirest.dto.profile.psicologo.PsicologoDTO;
import com.amani.amaniapirest.services.psicologo.PsicologoSelfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PsicologoSelfControllerTest {

    @Mock
    private PsicologoSelfService psicologoSelfService;

    @InjectMocks
    private PsicologoSelfController psicologoSelfController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(psicologoSelfController).build();
    }

    @Test
    @DisplayName("getById 200")
    void getById200() throws Exception {
        PsicologoDTO dto = new PsicologoDTO(1L, "Clínica", 5, "Descripción", "LIC-123",
                new UsuarioDTO(10L, "Ana", "Lopez", "ana@amani.com", null));
        when(psicologoSelfService.findProfileById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/psicologo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPsicologo").value(1));
    }

    @Test
    @DisplayName("getById 404 cuando no existe")
    void getById404() throws Exception {
        when(psicologoSelfService.findProfileById(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Psicólogo no encontrado"));

        mockMvc.perform(get("/api/psicologo/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("update 200")
    void update200() throws Exception {
        PsicologoSelfResponseDTO dto = new PsicologoSelfResponseDTO(1L, "Ana", "Lopez", "Clínica", 5, "Descripción", "LIC-123");
        when(psicologoSelfService.update(eq(1L), any(PsicologoRequestDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/psicologo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPsicologoRequestBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    @DisplayName("update 404 cuando no existe")
    void update404() throws Exception {
        when(psicologoSelfService.update(eq(99L), any(PsicologoRequestDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Psicólogo no encontrado"));

        mockMvc.perform(put("/api/psicologo/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPsicologoRequestBody()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("delete 200")
    void delete200() throws Exception {
        doNothing().when(psicologoSelfService).delete(1L);

        mockMvc.perform(delete("/api/psicologo/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete 404 cuando no existe")
    void delete404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Psicólogo no encontrado"))
                .when(psicologoSelfService).delete(99L);

        mockMvc.perform(delete("/api/psicologo/99"))
                .andExpect(status().isNotFound());
    }

    private String validPsicologoRequestBody() {
        return """
                {
                  "nombrePsicologo": "Ana",
                  "apellidoPsicologo": "Lopez",
                  "email": "ana@amani.com",
                  "password": "secret",
                  "especialidad": "Clínica",
                  "experiencia": 5,
                  "descripcion": "Descripción",
                  "licencia": "LIC-123"
                }
                """;
    }
}
