package com.amani.amaniapirest.controllers.notificacion;

import com.amani.amaniapirest.dto.notificacion.NotificacionConfigDTO;
import com.amani.amaniapirest.dto.notificacion.NotificacionResponseDTO;
import com.amani.amaniapirest.services.UsuarioService;
import com.amani.amaniapirest.services.notificacion.NotificationServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    @Mock
    private NotificationServices notificationServices;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private NotificacionController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getNotificacionesReturnsEmptyList() throws Exception {
        when(notificationServices.getNotificaciones(8L)).thenReturn(List.of());

        mockMvc.perform(get("/api/notificaciones/8"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getNotificacionesReturnsData() throws Exception {
        NotificacionResponseDTO dto = NotificacionResponseDTO.builder()
                .id(1L).titulo("Recordatorio").mensaje("Cita mañana").leida(false).fecha(LocalDateTime.now()).build();
        when(notificationServices.getNotificaciones(8L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/notificaciones/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Recordatorio"));
    }

    @Test
    void contarNoLeidasReturnsZeroBoundary() throws Exception {
        when(notificationServices.contarNoLeidas(8L)).thenReturn(0L);

        mockMvc.perform(get("/api/notificaciones/no-leidas/8"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void marcarTodasLeidasReturnsOk() throws Exception {
        doNothing().when(notificationServices).marcarTodasLeidas(8L);

        mockMvc.perform(put("/api/notificaciones/leer-todas/8"))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarConfiguracionReturnsState() throws Exception {
        when(usuarioService.actualizarNotificaciones(8L, true)).thenReturn(new NotificacionConfigDTO(8L, true));

        mockMvc.perform(put("/api/notificaciones/configuracion/8/activar").param("activar", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(8))
                .andExpect(jsonPath("$.notificacionesActivas").value(true));
    }
}
