package com.amani.amaniapirest.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de mensajería WebSocket con STOMP.
 *
 * <p>Registra el endpoint de conexión {@code /ws} con soporte SockJS,
 * define el prefijo de destino de la aplicación {@code /app} y habilita
 * el broker simple para los canales {@code /topic} y {@code /queue}.</p>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registra los endpoints STOMP a los que los clientes pueden conectarse.
     *
     * @param registry registro donde se declaran los endpoints WebSocket
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /**
     * Configura el broker de mensajes para enrutar mensajes entre cliente y servidor.
     *
     * @param registry registro de configuración del broker de mensajes
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.setApplicationDestinationPrefixes("/app");

        registry.enableSimpleBroker("/topic", "/queue");
    }
}