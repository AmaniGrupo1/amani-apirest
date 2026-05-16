package com.amani.amaniapirest.controllers.login;

import com.amani.amaniapirest.configuration.GlobalExceptionHandler;
import com.amani.amaniapirest.dto.dtoAdmin.response.AdministradorDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.RegistryRequestDTO;
import com.amani.amaniapirest.services.serviciosLogin.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("login retorna token y datos del usuario")
    void loginReturnsTokenAndUserData() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponseDTO(1L, "Ana", "PACIENTE", "jwt-token", null, 10L, "es", true));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@amani.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.rol").value("PACIENTE"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    @DisplayName("register paciente retorna LoginResponse")
    void registerPacienteReturnsLoginResponse() throws Exception {
        when(authService.registerPaciente(any())).thenReturn(new LoginResponseDTO(2L, "Pedro", "PACIENTE", "token-2", null, 20L, "es", true));

        mockMvc.perform(post("/auth/register-paciente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aceptaTerminos\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(2))
                .andExpect(jsonPath("$.token").value("token-2"));
    }

    @Test
    @DisplayName("listar admins retorna 200 con lista")
    void listarAdminsReturnsOkWithList() throws Exception {
        List<AdministradorDTO> admins = List.of(new AdministradorDTO(1L, "Admin", "Principal", "admin@amani.com", "admin", true));
        when(authService.listarAdministradores()).thenReturn(admins);

        mockMvc.perform(get("/auth/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].email").value("admin@amani.com"));
    }

    @Test
    @DisplayName("register admin retorna LoginResponse")
    void registerAdminReturnsLoginResponse() throws Exception {
        when(authService.registerAdmin(any(RegistryRequestDTO.class)))
                .thenReturn(new LoginResponseDTO(3L, "Root", "ADMIN", "token-admin", null, null, "es", true));

        String body = objectMapper.writeValueAsString(new RegistryRequestDTO("Root", "User", "root@amani.com", "strongpass1"));

        mockMvc.perform(post("/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMIN"))
                .andExpect(jsonPath("$.token").value("token-admin"));
    }

    @Test
    @DisplayName("dar baja paciente retorna mensaje de confirmación")
    void darBajaPacienteReturnsConfirmationMessage() throws Exception {
        doNothing().when(authService).darBajaPsicologo(25L);

        mockMvc.perform(put("/auth/pacientes/25/baja"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Paciente dado de baja correctamente"));
    }

    @Test
    @DisplayName("login debe retornar 401 cuando credenciales inválidas")
    void login_debeRetornar401_cuandoCredencialesInvalidas() throws Exception {
        when(authService.login(any())).thenThrow(new BadCredentialsException("Credenciales inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"bad@amani.com\",\"password\":\"wrong123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("login debe retornar 404 cuando usuario no existe")
    void login_debeRetornar404_cuandoUsuarioNoExiste() throws Exception {
        when(authService.login(any())).thenThrow(new NoSuchElementException("Usuario no encontrado"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"missing@amani.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("registerAdmin debe retornar 400 cuando email ya existe")
    void registerAdmin_debeRetornar400_cuandoEmailYaExiste() throws Exception {
        when(authService.registerAdmin(any())).thenThrow(new IllegalStateException("Email ya existe"));

        String body = objectMapper.writeValueAsString(new RegistryRequestDTO("Root", "User", "dup@amani.com", "strongpass1"));

        mockMvc.perform(post("/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("darBajaPaciente debe retornar 404 cuando paciente no existe")
    void darBajaPaciente_debeRetornar404_cuandoPacienteNoExiste() throws Exception {
        doThrow(new NoSuchElementException("No existe")).when(authService).darBajaPsicologo(99L);

        mockMvc.perform(put("/auth/pacientes/99/baja"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("listarAdmins debe retornar 200 aunque lista vacía (bug conocido: no devuelve 204)")
    void listarAdmins_debeRetornar200_cuandoListaVacia() throws Exception {
        when(authService.listarAdministradores()).thenReturn(List.of());

        mockMvc.perform(get("/auth/admins"))
                .andExpect(status().isOk());
    }
}
