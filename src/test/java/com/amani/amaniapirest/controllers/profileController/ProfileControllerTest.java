package com.amani.amaniapirest.controllers.profileController;

import com.amani.amaniapirest.dto.profile.psicologo.PsicologoDTO;
import com.amani.amaniapirest.dto.profile.UsuarioDTO;
import com.amani.amaniapirest.services.profile.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import jakarta.servlet.ServletException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();
    }

    @Test
    void uploadFotoWithValidImageReturnsUpdatedProfile() throws Exception {
        PsicologoDTO dto = new PsicologoDTO(
                10L,
                "Clinica",
                8,
                "Perfil actualizado",
                "LIC-100",
                new UsuarioDTO(70L, "Ana", "Lopez", "ana@amani.com", "/uploads/ana.jpg")
        );

        when(profileService.updateProfilePhoto(eq(10L), any())).thenReturn(dto);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "foto.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        mockMvc.perform(multipart("/api/psicologo/10/foto").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPsicologo").value(10))
                .andExpect(jsonPath("$.usuario.email").value("ana@amani.com"));
    }

    @Test
    void uploadFotoWithInvalidFileReturnsServerError() throws Exception {
        when(profileService.updateProfilePhoto(eq(10L), any()))
                .thenThrow(new RuntimeException("Archivo invalido"));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "script.txt",
                "text/plain",
                "not-an-image".getBytes()
        );

        assertThrows(ServletException.class, () ->
                mockMvc.perform(multipart("/api/psicologo/10/foto").file(file))
        );
    }

    @Test
    void getPerfilReturnsOkWhenServiceReturnsData() throws Exception {
        PsicologoDTO dto = new PsicologoDTO(
                12L,
                "TCC",
                5,
                "Desc",
                "LIC-200",
                new UsuarioDTO(72L, "Mario", "Perez", "mario@amani.com", "/avatar-default.svg")
        );

        when(profileService.getProfile(12L)).thenReturn(dto);

        mockMvc.perform(get("/api/psicologo/12/perfil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPsicologo").value(12));
    }

    @Test
    void getPsicologoAsignadoReturnsNotFoundWhenNotAssigned() throws Exception {
        when(profileService.obtenerPsicologoAsignado(25L)).thenReturn(null);

        mockMvc.perform(get("/api/psicologo/pacientes/25/psicologo"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Debes esperar a que un administrador te asigne un psicólogo"));
    }
}
