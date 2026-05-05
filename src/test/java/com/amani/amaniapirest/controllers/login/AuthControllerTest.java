package com.amani.amaniapirest.controllers.login;

import com.amani.amaniapirest.dto.dtoAdmin.response.AdministradorDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.RegistryRequestDTO;
import com.amani.amaniapirest.services.serviciosLogin.AuthService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void loginReturnsTokenAndUserData() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponseDTO(1L, "Ana", "PACIENTE", "jwt-token", null, 10L, "es"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@amani.com\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.rol").value("PACIENTE"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void registerPacienteReturnsLoginResponse() throws Exception {
        when(authService.registerPaciente(any())).thenReturn(new LoginResponseDTO(2L, "Pedro", "PACIENTE", "token-2", null, 20L, "es"));

        mockMvc.perform(post("/auth/register-paciente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aceptaTerminos\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(2))
                .andExpect(jsonPath("$.token").value("token-2"));
    }

    @Test
    void listarAdminsReturnsOkWithList() throws Exception {
        List<AdministradorDTO> admins = List.of(new AdministradorDTO(1L, "Admin", "Principal", "admin@amani.com", "admin", true));
        when(authService.listarAdministradores()).thenReturn(admins);

        mockMvc.perform(get("/auth/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].email").value("admin@amani.com"));
    }

    @Test
    void registerAdminReturnsLoginResponse() throws Exception {
        when(authService.registerAdmin(any(RegistryRequestDTO.class)))
                .thenReturn(new LoginResponseDTO(3L, "Root", "ADMIN", "token-admin", null, null, "es"));

        String body = objectMapper.writeValueAsString(new RegistryRequestDTO("Root", "User", "root@amani.com", "strong-pass"));

        mockMvc.perform(post("/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMIN"))
                .andExpect(jsonPath("$.token").value("token-admin"));
    }

    @Test
    void darBajaPacienteReturnsConfirmationMessage() throws Exception {
        doNothing().when(authService).darBajaPaciente(25L);

        mockMvc.perform(put("/auth/pacientes/25/baja"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Paciente dado de baja correctamente"));
    }
}
