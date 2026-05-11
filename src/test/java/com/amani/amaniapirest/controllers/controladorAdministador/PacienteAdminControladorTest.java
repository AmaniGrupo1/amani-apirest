package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.PacienteAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.PacienteBasicoResponseDTO;
import com.amani.amaniapirest.services.paciente.PacienteService;
import com.amani.amaniapirest.services.serviceAdmin.PacienteAdminService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PacienteAdminControladorTest {

    @Mock
    private PacienteAdminService pacienteService;

    @Mock
    private PacienteService pacienteServicePaciente;

    @InjectMocks
    private PacienteAdminControlador controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void findAllAdminReturns200() throws Exception {
        PacienteAdminResponseDTO dto = new PacienteAdminResponseDTO(
                1L, "Carlos", "Lopez", "carlos@amani.com", LocalDate.of(1990, 1, 1),
                "masculino", "600000000", null, null, true, null, null,
                List.of(), List.of(), List.of(), 35
        );
        when(pacienteService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pacientes/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPaciente").value(1));
    }

    @Test
    void findAllAdminReturns200WhenEmpty() throws Exception {
        when(pacienteService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/pacientes/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findByIdAdminReturns200() throws Exception {
        PacienteAdminResponseDTO dto = new PacienteAdminResponseDTO(
                1L, "Carlos", "Lopez", "carlos@amani.com", LocalDate.of(1990, 1, 1),
                "masculino", "600000000", null, null, true, null, null,
                List.of(), List.of(), List.of(), 35
        );
        when(pacienteService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/pacientes/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaciente").value(1));
    }

    @Test
    void findByIdAdminReturns404WhenNotFound() throws Exception {
        when(pacienteService.findById(99L)).thenThrow(new RuntimeException("No encontrado"));

        mockMvc.perform(get("/api/pacientes/admin/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAdminReturns201() throws Exception {
        PacienteAdminResponseDTO dto = new PacienteAdminResponseDTO(
                1L, "Ana", "García", "ana@amani.com", LocalDate.of(1995, 5, 5),
                "femenino", "611111111", null, null, true, null, null,
                List.of(), List.of(), List.of(), 30
        );
        when(pacienteService.create(any(PacienteRequestDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/pacientes/adminAndPsicologo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fechaNacimiento":"1995-05-05","genero":"femenino","telefono":"611111111","aceptaTerminos":true,"usuario":{"nombre":"Ana","apellido":"García","email":"ana@amani.com","password":"secret","rol":"paciente","activo":true}}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPaciente").value(1));
    }

    @Test
    void updateAdminReturns200() throws Exception {
        PacienteAdminResponseDTO dto = new PacienteAdminResponseDTO(
                1L, "Ana", "García", "ana@amani.com", LocalDate.of(1995, 5, 5),
                "femenino", "622222222", null, null, true, null, null,
                List.of(), List.of(), List.of(), 30
        );
        when(pacienteService.update(any(), any(PacienteRequestDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/pacientes/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fechaNacimiento":"1995-05-05","genero":"femenino","telefono":"622222222","aceptaTerminos":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telefono").value("622222222"));
    }

    @Test
    void updateAdminReturns404WhenMissing() throws Exception {
        when(pacienteService.update(any(), any(PacienteRequestDTO.class))).thenThrow(new RuntimeException("No encontrado"));

        mockMvc.perform(put("/api/pacientes/admin/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fechaNacimiento":"1995-05-05","genero":"femenino","telefono":"622222222","aceptaTerminos":true}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPacientesSinPsicologoReturns200() throws Exception {
        PacienteBasicoResponseDTO dto = new PacienteBasicoResponseDTO(
                1L, 10L, "Carlos", "Lopez", "carlos@amani.com", "12345678A",
                LocalDate.of(1990, 1, 1), "masculino", "600000000",
                List.of(), List.of(), List.of()
        );
        when(pacienteServicePaciente.getPacientesSinPsicologo()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pacientes/sin-psicologo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPaciente").value(1));
    }
}
