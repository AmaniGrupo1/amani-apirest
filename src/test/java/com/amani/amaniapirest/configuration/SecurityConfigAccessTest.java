package com.amani.amaniapirest.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class SecurityConfigAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void authLoginSinToken_esAccesible() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(result -> {
                    int httpStatus = result.getResponse().getStatus();
                    assertThat(httpStatus).isNotEqualTo(401).isNotEqualTo(403);
                });
    }

    @Test
    void apiSituacionesSinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/situaciones"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void apiCitasMisCitasSinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/citas/mis-citas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void apiAdminPsicologosSinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/admin/psicologos"))
                .andExpect(status().isUnauthorized());
    }
}
