package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.ajustes.IdiomaRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.AjusteResponseDTO;
import com.amani.amaniapirest.services.paciente.AjusteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AjusteControllerTest {

    @Mock
    private AjusteService ajusteService;

    @InjectMocks
    private AjusteController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private AjusteResponseDTO ajusteDto(Long id, String idioma) {
        AjusteResponseDTO dto = new AjusteResponseDTO();
        dto.setIdAjuste(id);
        dto.setIdUsuario(id);
        dto.setIdioma(idioma);
        dto.setNotificaciones(true);
        dto.setTema(false);
        dto.setTimezone("Europe/Madrid");
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }

    @Test
    @DisplayName("findAll 200 con datos")
    void findAll200WithData() throws Exception {
        when(ajusteService.findAll()).thenReturn(List.of(ajusteDto(1L, "es")));

        mockMvc.perform(get("/api/ajustes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAjuste").value(1));
    }

    @Test
    @DisplayName("findById 200")
    void findById200() throws Exception {
        when(ajusteService.findById(1L)).thenReturn(ajusteDto(1L, "es"));

        mockMvc.perform(get("/api/ajustes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAjuste").value(1));
    }

    @Test
    @DisplayName("findById 404")
    void findById404() throws Exception {
        when(ajusteService.findById(99L)).thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(get("/api/ajustes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("create 201")
    void create201() throws Exception {
        when(ajusteService.create(any())).thenReturn(ajusteDto(1L, "es"));

        mockMvc.perform(post("/api/ajustes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idUsuario\":1,\"idioma\":\"es\",\"notificaciones\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idioma").value("es"));
    }

    @Test
    @DisplayName("create 400")
    void create400() throws Exception {
        mockMvc.perform(post("/api/ajustes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("update 200")
    void update200() throws Exception {
        when(ajusteService.update(eq(1L), any())).thenReturn(ajusteDto(1L, "en"));

        mockMvc.perform(put("/api/ajustes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idUsuario\":1,\"idioma\":\"en\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idioma").value("en"));
    }

    @Test
    @DisplayName("update 404")
    void update404() throws Exception {
        when(ajusteService.update(eq(99L), any())).thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(put("/api/ajustes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idUsuario\":1,\"idioma\":\"en\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("delete 204")
    void delete204() throws Exception {
        doNothing().when(ajusteService).delete(1L);

        mockMvc.perform(delete("/api/ajustes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("delete 404")
    void delete404() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("No existe")).when(ajusteService).delete(99L);

        mockMvc.perform(delete("/api/ajustes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("actualizarIdioma 200")
    void actualizarIdioma200() throws Exception {
        doNothing().when(ajusteService).actualizarIdioma(eq(10L), any(IdiomaRequestDTO.class));

        mockMvc.perform(put("/api/ajustes/10/idioma")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idioma\": \"de\"}"))
                .andExpect(status().isOk());
    }
}
