package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoPaciente.response.TicketSoporteResponseDTO;
import com.amani.amaniapirest.enums.EstadoTicketSoporte;
import com.amani.amaniapirest.services.serviceAdmin.SoporteTicketAdminService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SoporteTicketAdminControllerTest {

    @Mock
    private SoporteTicketAdminService soporteTicketAdminService;

    @InjectMocks
    private SoporteTicketAdminController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void findAllReturnsNoContentWhenEmpty() throws Exception {
        when(soporteTicketAdminService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/tickets-soporte/admin"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findAllByEstadoReturnsNoContentWhenEmpty() throws Exception {
        when(soporteTicketAdminService.findByEstado(EstadoTicketSoporte.cerrado)).thenReturn(List.of());

        mockMvc.perform(get("/api/tickets-soporte/admin").param("estado", "cerrado"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findAllByEstadoReturnsOk() throws Exception {
        when(soporteTicketAdminService.findByEstado(EstadoTicketSoporte.abierto))
                .thenReturn(List.of(TicketSoporteResponseDTO.builder().idTicket(1L).build()));

        mockMvc.perform(get("/api/tickets-soporte/admin").param("estado", "abierto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTicket").value(1));
    }

    @Test
    void updateEstadoReturnsBadRequestWhenEnumInvalid() throws Exception {
        mockMvc.perform(put("/api/tickets-soporte/admin/5/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"INVALIDO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEstadoReturnsOkWhenValid() throws Exception {
        when(soporteTicketAdminService.updateEstado(org.mockito.ArgumentMatchers.eq(5L), any()))
                .thenReturn(TicketSoporteResponseDTO.builder().idTicket(5L).estado(EstadoTicketSoporte.en_progreso).build());

        mockMvc.perform(put("/api/tickets-soporte/admin/5/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"en_progreso\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("en_progreso"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        doNothing().when(soporteTicketAdminService).delete(4L);

        mockMvc.perform(delete("/api/tickets-soporte/admin/4"))
                .andExpect(status().isNoContent());
    }
}
