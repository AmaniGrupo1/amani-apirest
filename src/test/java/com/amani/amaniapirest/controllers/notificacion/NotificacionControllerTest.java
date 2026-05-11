package com.amani.amaniapirest.controllers.notificacion;

import com.amani.amaniapirest.configuration.GlobalExceptionHandler;
import com.amani.amaniapirest.dto.notificacion.NotificacionConfigDTO;
import com.amani.amaniapirest.dto.notificacion.NotificacionResponseDTO;
import com.amani.amaniapirest.services.UsuarioService;
import com.amani.amaniapirest.services.notificacion.NotificationServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("getNotificaciones retorna lista vacía")
    void getNotificacionesReturnsEmptyList() throws Exception {
        when(notificationServices.getNotificaciones(8L)).thenReturn(List.of());

        mockMvc.perform(get("/api/notificaciones/8"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("getNotificaciones retorna datos")
    void getNotificacionesReturnsData() throws Exception {
        NotificacionResponseDTO dto = NotificacionResponseDTO.builder()
                .id(1L).titulo("Recordatorio").mensaje("Cita mañana").leida(false).fecha(LocalDateTime.now()).build();
        when(notificationServices.getNotificaciones(8L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/notificaciones/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Recordatorio"));
    }

    @Test
    @DisplayName("contarNoLeidas retorna cero (boundary)")
    void contarNoLeidasReturnsZeroBoundary() throws Exception {
        when(notificationServices.contarNoLeidas(8L)).thenReturn(0L);

        mockMvc.perform(get("/api/notificaciones/no-leidas/8"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("marcarTodasLeidas retorna 200")
    void marcarTodasLeidasReturnsOk() throws Exception {
        doNothing().when(notificationServices).marcarTodasLeidas(8L);

        mockMvc.perform(put("/api/notificaciones/leer-todas/8"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("actualizarConfiguracion retorna estado actualizado")
    void actualizarConfiguracionReturnsState() throws Exception {
        when(usuarioService.actualizarNotificaciones(8L, true)).thenReturn(new NotificacionConfigDTO(8L, true));

        mockMvc.perform(put("/api/notificaciones/configuracion/8/activar").param("activar", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(8))
                .andExpect(jsonPath("$.notificacionesActivas").value(true));
    }

    @Test
    @DisplayName("marcarLeida debe retornar 404 cuando notificación no existe")
    void marcarLeida_debeRetornar404_cuandoNotificacionNoExiste() throws Exception {
        when(notificationServices.marcarLeida(99L)).thenThrow(new NoSuchElementException("No encontrada"));

        mockMvc.perform(put("/api/notificaciones/leer/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getNotificaciones debe retornar 500 cuando service falla")
    void getNotificaciones_debeRetornar500_cuandoServiceFalla() throws Exception {
        when(notificationServices.getNotificaciones(any())).thenThrow(new RuntimeException("Error interno"));

        mockMvc.perform(get("/api/notificaciones/8"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("actualizarConfiguracion debe retornar 404 cuando usuario no existe")
    void actualizarConfiguracion_debeRetornar404_cuandoUsuarioNoExiste() throws Exception {
        when(usuarioService.actualizarNotificaciones(any(), eq(true))).thenThrow(new NoSuchElementException("Usuario no encontrado"));

        mockMvc.perform(put("/api/notificaciones/configuracion/8/activar").param("activar", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("obtenerEstado debe retornar 404 cuando usuario no existe")
    void obtenerEstado_debeRetornar404_cuandoUsuarioNoExiste() throws Exception {
        when(usuarioService.obtenerEstadoNotificaciones(any())).thenThrow(new NoSuchElementException("Usuario no encontrado"));

        mockMvc.perform(get("/api/notificaciones/configuracion/8"))
                .andExpect(status().isNotFound());
    }
}
