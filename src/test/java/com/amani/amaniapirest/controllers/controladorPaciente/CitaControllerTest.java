package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoAgenda.response.AgendaItemDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.CitaPacienteViewResponseDTO;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.enums.EstadoPago;
import com.amani.amaniapirest.enums.MetodoPago;
import com.amani.amaniapirest.enums.ModalidadCita;
import com.amani.amaniapirest.services.CitaAgendaService;
import com.amani.amaniapirest.services.paciente.CitaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CitaControllerTest {

    @Mock
    private CitaService citaService;

    @Mock
    private CitaAgendaService citaAgendaService;

    @InjectMocks
    private CitaController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getMisCitasReturns200() {
        Authentication auth = mock(Authentication.class);
        lenient().when(citaService.obtenerIdPacienteDesdeAuth(any(Authentication.class))).thenReturn(1L);

        CitaPacienteViewResponseDTO dto = CitaPacienteViewResponseDTO.builder()
                .idCita(1L).fecha(LocalDate.now()).horaInicio(LocalTime.of(10, 0)).horaFin(LocalTime.of(11, 0))
                .estado(EstadoCita.pendiente).modalidad(ModalidadCita.LLAMADA).motivo("Seguimiento").build();
        lenient().when(citaService.findByPaciente(anyLong())).thenReturn(List.of(dto));

        List<CitaPacienteViewResponseDTO> result = controller.getMisCitas(auth);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdCita()).isEqualTo(1L);
    }

    @Test
    void findByIdReturns200() throws Exception {
        CitaPacienteViewResponseDTO dto = CitaPacienteViewResponseDTO.builder()
                .idCita(1L).fecha(LocalDate.now()).horaInicio(LocalTime.of(10, 0)).horaFin(LocalTime.of(11, 0))
                .estado(EstadoCita.pendiente).build();
        when(citaService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/citas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCita").value(1));
    }

    @Test
    void findByIdReturns404WhenMissing() throws Exception {
        when(citaService.findById(99L)).thenThrow(new RuntimeException("No encontrada"));

        mockMvc.perform(get("/api/citas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns200() throws Exception {
        CitaPacienteViewResponseDTO dto = CitaPacienteViewResponseDTO.builder()
                .idCita(1L).fecha(LocalDate.now()).horaInicio(LocalTime.of(12, 0)).horaFin(LocalTime.of(13, 0))
                .estado(EstadoCita.confirmada).build();
        when(citaService.update(any(), any(CitaRequestDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/citas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCitaRequestBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCita").value(1));
    }

    @Test
    void updateReturns404WhenMissing() throws Exception {
        when(citaService.update(any(), any(CitaRequestDTO.class))).thenThrow(new RuntimeException("No encontrada"));

        mockMvc.perform(put("/api/citas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCitaRequestBody()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns204() throws Exception {
        doNothing().when(citaService).delete(1L);

        mockMvc.perform(delete("/api/citas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturns404WhenMissing() throws Exception {
        doThrow(new RuntimeException("No encontrada")).when(citaService).delete(99L);

        mockMvc.perform(delete("/api/citas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAgendaPacienteMesReturns200() throws Exception {
        AgendaItemDTO item = AgendaItemDTO.builder()
                .id(1L).fecha(LocalDate.of(2026, 5, 10)).horaInicio(LocalTime.of(10, 0)).horaFin(LocalTime.of(11, 0))
                .estado("pendiente").motivo("Seguimiento").build();
        when(citaAgendaService.getAgendaPaciente(5L, "2026-05")).thenReturn(List.of(item));

        mockMvc.perform(get("/api/citas/paciente/5/agenda").param("month", "2026-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("pendiente"));
    }

    private String validCitaRequestBody() {
        return """
                {
                  "idPaciente": 1,
                  "idPsicologo": 2,
                  "startDatetime": "2026-05-10T12:00:00",
                  "durationMinutes": 60,
                  "estado": "confirmada",
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
