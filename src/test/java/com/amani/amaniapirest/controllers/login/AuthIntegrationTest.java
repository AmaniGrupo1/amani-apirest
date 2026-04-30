package com.amani.amaniapirest.controllers.login;

import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.Filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    void tcAuth01_LoginSuccess() throws Exception {
        String loginPayload = "{\"email\":\"admin@amani.com\",\"password\":\"admin1234\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.rol").value("admin"));
    }

    @Test
    void tcAuth02_LoginWrongPassword() throws Exception {
        String loginPayload = "{\"email\":\"admin@amani.com\",\"password\":\"wrongpassword\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tcAuth03_RegisterPaciente() throws Exception {
        String email = "pacientetest@amani.com";
        String payload = """
                {
                  "usuario": {
                    "nombre": "Paciente",
                    "apellido": "Test",
                    "dni": "12345678A",
                    "email": "%s",
                    "password": "pass1234"
                  },
                  "fechaNacimiento": "1990-01-01",
                  "aceptaTerminos": true,
                  "aceptaVideoconferencia": false,
                  "aceptaComunicacion": false
                }
                """.formatted(email);

        mockMvc.perform(post("/auth/register-paciente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        Usuario usuarioGuardado = usuarioRepository.findByEmail(email).orElse(null);
        assertThat(usuarioGuardado).isNotNull();
        assertThat(usuarioGuardado.getRol().name()).isEqualTo("paciente");
    }
}
