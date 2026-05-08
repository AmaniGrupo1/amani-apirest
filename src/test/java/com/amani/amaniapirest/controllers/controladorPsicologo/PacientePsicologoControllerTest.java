package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PacientePsicologoResponseDTO;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.services.psicologo.PacientePsicologoService;
import com.amani.amaniapirest.services.serviceAdmin.PsicologoAdminService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
class PacientePsicologoControllerTest {

    @Mock
    private PacientePsicologoService pacientePsicologoService;

    @Mock
    private PsicologoPacienteRepository psicologoPacienteRepository;

    @Mock
    private PsicologoAdminService psicologoAdminService;

    @InjectMocks
    private PacientePsicologoController pacientePsicologoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pacientePsicologoController).build();
    }

    @Test
    @DisplayName("getPacienteById 200")
    void getPacienteById200() throws Exception {
        PacientePsicologoResponseDTO dto = new PacientePsicologoResponseDTO();
        dto.setIdPaciente(1L);
        dto.setNombre("Laura");
        dto.setApellido("Martínez");
        when(pacientePsicologoService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/psicologo/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaciente").value(1));
    }

    @Test
    @DisplayName("getPacienteById 404")
    void getPacienteById404() throws Exception {
        when(pacientePsicologoService.findById(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado"));

        mockMvc.perform(get("/api/psicologo/pacientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getPacientes 200 con datos")
    void getPacientes200() throws Exception {
        PacientePsicologoResponseDTO dto = new PacientePsicologoResponseDTO();
        dto.setIdPaciente(1L);
        dto.setNombre("Laura");
        when(psicologoAdminService.getPacientesDelPsicologoLogueado()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/psicologo/pacientes/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPaciente").value(1));
    }

    @Test
    @DisplayName("getPacientes 204 vacío")
    void getPacientes204() throws Exception {
        when(psicologoAdminService.getPacientesDelPsicologoLogueado()).thenReturn(List.of());

        mockMvc.perform(get("/api/psicologo/pacientes/getAll"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("crearPaciente 200")
    void crearPaciente200() throws Exception {
        PacientePsicologoResponseDTO dto = new PacientePsicologoResponseDTO();
        dto.setIdPaciente(1L);
        dto.setNombre("Laura");
        when(pacientePsicologoService.create(any(PacienteRequestDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/psicologo/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPacienteRequestBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaciente").value(1));
    }

    @Test
    @DisplayName("actualizarPaciente 200")
    void actualizarPaciente200() throws Exception {
        PacientePsicologoResponseDTO dto = new PacientePsicologoResponseDTO();
        dto.setIdPaciente(1L);
        dto.setTelefono("611111111");
        when(pacientePsicologoService.update(eq(1L), any(PacienteRequestDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/psicologo/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPacienteRequestBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telefono").value("611111111"));
    }

    @Test
    @DisplayName("eliminarPaciente 204")
    void eliminarPaciente204() throws Exception {
        doNothing().when(pacientePsicologoService).delete(1L);

        mockMvc.perform(delete("/api/psicologo/pacientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("eliminarPaciente 404")
    void eliminarPaciente404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado"))
                .when(pacientePsicologoService).delete(99L);

        mockMvc.perform(delete("/api/psicologo/pacientes/99"))
                .andExpect(status().isNotFound());
    }

    private String validPacienteRequestBody() {
        return """
                {
                  "idUsuario": 1,
                  "fechaNacimiento": "1990-05-15",
                  "genero": "femenino",
                  "telefono": "600000000"
                }
                """;
    }
}
