package com.amani.amaniapirest.services;

import com.amani.amaniapirest.gateway.PushNotificationGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FirebaseNotificationServiceTest {

    @Mock
    private PushNotificationGateway pushGateway;

    @InjectMocks
    private FirebaseNotificationService service;

    @Test
    @DisplayName("enviarPush con token válido delega en gateway")
    void enviarPush_conTokenValido_delegaEnGateway() {
        service.enviarPush("token-fcm", "Título", "Cuerpo");

        verify(pushGateway).sendPush("token-fcm", "Título", "Cuerpo");
    }

    @Test
    @DisplayName("enviarPush con token vacío no llama a gateway")
    void enviarPush_conTokenVacio_noLlamaAGateway() {
        service.enviarPush("", "Título", "Cuerpo");

        verify(pushGateway, never()).sendPush(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("enviarPush con token nulo no llama a gateway")
    void enviarPush_conTokenNulo_noLlamaAGateway() {
        service.enviarPush(null, "Título", "Cuerpo");

        verify(pushGateway, never()).sendPush(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
