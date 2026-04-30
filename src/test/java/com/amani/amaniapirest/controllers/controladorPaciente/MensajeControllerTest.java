package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.response.MensajeResponseDTO;
import com.amani.amaniapirest.services.paciente.MensajeService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MensajeControllerTest {

    @Mock
    private MensajeService mensajeService;

    @InjectMocks
    private MensajeController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void findAllReturnsEmptyListBoundary() throws Exception {
        when(mensajeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/mensajes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findByIdReturnsNotFoundWhenMissing() throws Exception {
        when(mensajeService.findById(99L)).thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(get("/api/mensajes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturnsCreatedWhenValid() throws Exception {
        when(mensajeService.create(any()))
                .thenReturn(new MensajeResponseDTO("Ana", "Hola", LocalDateTime.now(), false));

        mockMvc.perform(post("/api/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idSender":1,
                                  "idReceiver":2,
                                  "mensaje":"Hola",
                                  "idCita":5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Hola"));
    }

    @Test
    void createReturnsBadRequestOnBusinessError() throws Exception {
        when(mensajeService.create(any())).thenThrow(new RuntimeException("No permitido"));

        mockMvc.perform(post("/api/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idSender":1,
                                  "idReceiver":2,
                                  "mensaje":"Hola"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReturnsBadRequestWhenBlankMessage() throws Exception {
        mockMvc.perform(post("/api/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idSender":1,
                                  "idReceiver":2,
                                  "mensaje":""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void marcarLeidoReturnsNotFoundWhenMissing() throws Exception {
        when(mensajeService.marcarLeido(77L)).thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(patch("/api/mensajes/77/leido"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturnsNotFoundWhenMissing() throws Exception {
        doThrow(new RuntimeException("No existe")).when(mensajeService).delete(77L);

        mockMvc.perform(delete("/api/mensajes/77"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturnsNoContentWhenSuccess() throws Exception {
        doNothing().when(mensajeService).delete(7L);

        mockMvc.perform(delete("/api/mensajes/7"))
                .andExpect(status().isNoContent());
    }
}
