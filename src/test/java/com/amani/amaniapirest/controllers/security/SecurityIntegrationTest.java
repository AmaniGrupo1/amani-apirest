package com.amani.amaniapirest.controllers.security;

import com.amani.amaniapirest.configuration.JwtUtil;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.Filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String tokenPaciente;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();

        if (!usuarioRepository.existsByEmail("paciente_sec@amani.com")) {
            Usuario paciente = Usuario.builder()
                    .nombre("Paciente")
                    .apellido("Sec")
                    .email("paciente_sec@amani.com")
                    .password(passwordEncoder.encode("pass123"))
                    .rol(RolUsuario.paciente)
                    .build();
            usuarioRepository.save(paciente);
        }
        
        Usuario pacienteGuardado = usuarioRepository.findByEmail("paciente_sec@amani.com").get();
        org.springframework.security.core.userdetails.UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(pacienteGuardado.getEmail())
                .password(pacienteGuardado.getPassword())
                .authorities("ROLE_PACIENTE")
                .build();
        tokenPaciente = jwtUtil.generateToken(userDetails, "paciente");
    }

    @Test
    void tcSec01_AccessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/citas/mis-citas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tcSec02_AccessAdminWithPacienteToken() throws Exception {
        mockMvc.perform(get("/api/admin/preguntas")
                        .header("Authorization", "Bearer " + tokenPaciente))
                .andExpect(status().isForbidden());
    }
}
