package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoPaciente.request.AsignarPacienteAlPsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PsicologoSelfResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.PsicologoConPacientesDTO;
import com.amani.amaniapirest.services.psicologo.PsicologoSelfService;
import com.amani.amaniapirest.services.serviceAdmin.PacienteAdminService;
import com.amani.amaniapirest.services.serviceAdmin.PsicologoAdminService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PsicologoAdminControllerTest {

    @Mock
    private PsicologoAdminService service;

    @Mock
    private PsicologoSelfService selfService;

    @Mock
    private PacienteAdminService psicologoPacienteService;

    @InjectMocks
    private PsicologoAdminController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void asignarPsicologoReturns200WhenTrue() throws Exception {
        when(psicologoPacienteService.asignarPsicologo(1L, 2L)).thenReturn(true);

        mockMvc.perform(post("/api/admin/psicologos/asignar-psicologo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idPaciente":1,"idPsicologo":2}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void asignarPsicologoReturns400WhenFalse() throws Exception {
        when(psicologoPacienteService.asignarPsicologo(1L, 2L)).thenReturn(false);

        mockMvc.perform(post("/api/admin/psicologos/asignar-psicologo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idPaciente":1,"idPsicologo":2}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void findAllPsicologoReturns200() throws Exception {
        PsicologoSelfResponseDTO dto = new PsicologoSelfResponseDTO(1L, "Ana", "Lopez", "TCC", 5, "Descripción", "LIC-001");
        when(selfService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/psicologos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPsicologo").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Ana"));
    }

    @Test
    void findAllPsicologoReturns204WhenEmpty() throws Exception {
        when(selfService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/psicologos"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createReturns200() throws Exception {
        PsicologoSelfResponseDTO dto = new PsicologoSelfResponseDTO(1L, "Carlos", "Ruiz", "Psicodinámica", 3, "Desc", "LIC-002");
        when(selfService.create(any(PsicologoRequestDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/admin/psicologos/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombrePsicologo":"Carlos","apellidoPsicologo":"Ruiz","email":"carlos@example.com","password":"secret","especialidad":"Psicodinámica","experiencia":3,"descripcion":"Desc","licencia":"LIC-002"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPsicologo").value(1));
    }

    @Test
    void createReturns404WhenNull() throws Exception {
        when(selfService.create(any(PsicologoRequestDTO.class))).thenReturn(null);

        mockMvc.perform(post("/api/admin/psicologos/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombrePsicologo":"Carlos","apellidoPsicologo":"Ruiz","email":"carlos@example.com","password":"secret","especialidad":"TCC","experiencia":3,"descripcion":"Desc","licencia":"LIC-002"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns200() throws Exception {
        PsicologoConPacientesDTO dto = new PsicologoConPacientesDTO(1L, "Ana", "Lopez", "ana@example.com", "TCC", "LIC-001", LocalDateTime.now(), List.of());
        when(service.update(any(), any(PsicologoRequestDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/admin/psicologos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombrePsicologo":"Ana","apellidoPsicologo":"Lopez","email":"ana@example.com","password":"secret","especialidad":"TCC","experiencia":5,"descripcion":"Desc","licencia":"LIC-001"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPsicologo").value(1));
    }

    @Test
    void updateReturns404WhenNotFound() throws Exception {
        when(service.update(any(), any(PsicologoRequestDTO.class))).thenThrow(new RuntimeException("No encontrado"));

        mockMvc.perform(put("/api/admin/psicologos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombrePsicologo":"Ana","apellidoPsicologo":"Lopez","email":"ana@example.com","password":"secret","especialidad":"TCC","experiencia":5,"descripcion":"Desc","licencia":"LIC-001"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/admin/psicologos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturns404WhenNotFound() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("No encontrado")).when(service).delete(99L);

        mockMvc.perform(delete("/api/admin/psicologos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllPacientesAndPsicologosReturns200() throws Exception {
        PsicologoConPacientesDTO dto = new PsicologoConPacientesDTO(1L, "Ana", "Lopez", "ana@example.com", "TCC", "LIC-001", LocalDateTime.now(), List.of());
        when(service.getPsicologosConPacientes()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/psicologos/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPsicologo").value(1));
    }

    @Test
    void findAllPacientesAndPsicologosReturns204WhenEmpty() throws Exception {
        when(service.getPsicologosConPacientes()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/psicologos/pacientes"))
                .andExpect(status().isNoContent());
    }
}
