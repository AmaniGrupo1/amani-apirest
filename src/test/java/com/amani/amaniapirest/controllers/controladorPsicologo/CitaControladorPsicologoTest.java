package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoAgenda.request.HorarioRequestDTO;
import com.amani.amaniapirest.dto.dtoAgenda.response.AgendaItemDTO;
import com.amani.amaniapirest.dto.dtoAgenda.response.DisponibilidadDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.CitaPsicologoResponseDTO;
import com.amani.amaniapirest.dto.terapiasDTO.TerapiaResponseDTO;
import com.amani.amaniapirest.services.CitaAgendaService;
import com.amani.amaniapirest.services.psicologo.CitaServicePsicologo;
import com.amani.amaniapirest.services.terapiaService.TerapiaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CitaControladorPsicologoTest {

    @Mock
    private CitaServicePsicologo citaService;

    @Mock
    private CitaAgendaService citaAgendaService;

    @Mock
    private TerapiaService terapiaService;

    @InjectMocks
    private CitaControladorPsicologo controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void findAllByPsicologoReturnsOk() throws Exception {
        when(citaService.findAllByPsicologo(7L)).thenReturn(
                List.of(new CitaPsicologoResponseDTO(99L, 10L, "Ana", "Lopez", null, 60, "confirmada", "Seguimiento"))
        );

        mockMvc.perform(get("/api/citas/psicologo/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCita").value(99));
    }

    @Test
    void crearCitaReturnsOkWhenServiceSucceeds() throws Exception {
        when(citaAgendaService.crearCita(any())).thenReturn(AgendaItemDTO.builder().id(100L).estado("pendiente").build());

        mockMvc.perform(post("/api/citas/psicologo/cita")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateCitaBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.estado").value("pendiente"));
    }

    @Test
    void crearCitaReturnsBadRequestWhenSlotConflict() throws Exception {
        when(citaAgendaService.crearCita(any())).thenThrow(new IllegalStateException("Slot ocupado"));

        mockMvc.perform(post("/api/citas/psicologo/cita")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateCitaBody()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void disponibilidadUsesDefaultDurationWhenNotProvided() throws Exception {
        when(citaAgendaService.getDuracionDefault(5L)).thenReturn(60);
        when(citaAgendaService.getDisponibilidadConDuracion(5L, "2026-05-02", 60))
                .thenReturn(DisponibilidadDTO.builder().fecha(LocalDate.of(2026, 5, 2)).diaCompleto(false).slotsLibres(List.of()).build());

        mockMvc.perform(get("/api/citas/psicologo/5/disponibilidad").param("fecha", "2026-05-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fecha").value("2026-05-02"))
                .andExpect(jsonPath("$.diaCompleto").value(false));
    }

    @Test
    void actualizarDuracionReturnsNoContent() throws Exception {
        doNothing().when(citaAgendaService).actualizarDuracionPsicologo(3L, 45);

        mockMvc.perform(put("/api/citas/psicologo/3/duracion").param("duracion", "45"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTerapiasReturnsOk() throws Exception {
        when(terapiaService.getAllTerapias()).thenReturn(List.of(new TerapiaResponseDTO(1L, "TCC", 60, BigDecimal.valueOf(45.50))));

        mockMvc.perform(get("/api/citas/psicologo/terapias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTipo").value(1))
                .andExpect(jsonPath("$[0].nombre").value("TCC"));
    }

    @Test
    void getHorarioActualReturnsOk() throws Exception {
        when(citaAgendaService.getHorarioActual(9L)).thenReturn(new HorarioRequestDTO());

        mockMvc.perform(get("/api/citas/psicologo/9/horario-actual"))
                .andExpect(status().isOk());
    }

    @Test
    void cancelarCitaReturnsOk() throws Exception {
        when(citaAgendaService.cancelarCita(50L)).thenReturn(AgendaItemDTO.builder().id(50L).estado("cancelada").build());

        mockMvc.perform(patch("/api/citas/50/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.estado").value("cancelada"));
    }

    @Test
    void actualizarHorarioReturnsNoContent() throws Exception {
        doNothing().when(citaAgendaService).actualizarHorario(org.mockito.ArgumentMatchers.eq(9L), any());

        mockMvc.perform(put("/api/citas/psicologo/9/horario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void agregarDiaNoDisponibleReturnsNoContent() throws Exception {
        doNothing().when(citaAgendaService).addBloqueo(org.mockito.ArgumentMatchers.eq(9L), any());

        mockMvc.perform(post("/api/citas/psicologo/9/dias-no-disponibles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fecha\":\"2026-05-10\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarDiaNoDisponibleReturnsNoContent() throws Exception {
        doNothing().when(citaAgendaService).removeBloqueo(9L, "2026-05-10");

        mockMvc.perform(delete("/api/citas/psicologo/9/dias-no-disponibles/2026-05-10"))
                .andExpect(status().isNoContent());
    }

    private String validCreateCitaBody() {
        return """
                {
                  "idPaciente": 10,
                  "idPsicologo": 5,
                  "startDatetime": "2026-05-02T10:00:00",
                  "durationMinutes": 60,
                  "estado": "pendiente",
                  "metodoPago": "ONLINE",
                  "estadoPago": "PENDIENTE",
                  "monto": 45.50,
                  "motivo": "Seguimiento",
                  "idTipoTerapia": 1,
                  "modalidad": "LLAMADA"
                }
                """;
    }
}
