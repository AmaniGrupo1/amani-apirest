package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.response.TicketSoporteResponseDTO;
import com.amani.amaniapirest.enums.CategoriaTicketSoporte;
import com.amani.amaniapirest.enums.EstadoTicketSoporte;
import com.amani.amaniapirest.enums.TipoTicketSoporte;
import com.amani.amaniapirest.services.paciente.SoporteTicketService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SoporteTicketControllerTest {

    @Mock
    private SoporteTicketService soporteTicketService;

    @InjectMocks
    private SoporteTicketController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void findMisTicketsReturnsFilteredList() throws Exception {
        TicketSoporteResponseDTO dto = TicketSoporteResponseDTO.builder()
                .idTicket(1L).titulo("Ayuda").estado(EstadoTicketSoporte.abierto).build();
        when(soporteTicketService.findMisTickets(EstadoTicketSoporte.abierto)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/tickets-soporte").param("estado", "abierto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTicket").value(1));
    }

    @Test
    void findByIdReturnsForbiddenWhenPermissionError() throws Exception {
        when(soporteTicketService.findById(3L)).thenThrow(new RuntimeException("sin permiso para ver este ticket"));

        mockMvc.perform(get("/api/tickets-soporte/3"))
                .andExpect(status().isForbidden());
    }

    @Test
    void findByIdReturnsNotFoundWhenMissing() throws Exception {
        when(soporteTicketService.findById(999L)).thenThrow(new RuntimeException("no encontrado"));

        mockMvc.perform(get("/api/tickets-soporte/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturnsCreated() throws Exception {
        TicketSoporteResponseDTO dto = TicketSoporteResponseDTO.builder()
                .idTicket(10L)
                .titulo("Error pago")
                .descripcion("No pasa tarjeta")
                .tipo(TipoTicketSoporte.problema)
                .categoria(CategoriaTicketSoporte.pago)
                .estado(EstadoTicketSoporte.abierto)
                .creadoEn(LocalDateTime.now())
                .build();

        when(soporteTicketService.create(any())).thenReturn(dto);

        mockMvc.perform(post("/api/tickets-soporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo":"Error pago",
                                  "descripcion":"No pasa tarjeta",
                                  "tipo":"problema",
                                  "categoria":"pago"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTicket").value(10));
    }

    @Test
    void createReturnsBadRequestWhenValidationFails() throws Exception {
        mockMvc.perform(post("/api/tickets-soporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "descripcion":"Sin título",
                                  "tipo":"problema",
                                  "categoria":"tecnico"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
