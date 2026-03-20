package com.amani.amaniapirest.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuracion de WebSocket con STOMP sobre SockJS.
 *
 * <p>Expone el endpoint {@code /ws} para las conexiones WebSocket y configura
 * un broker de mensajes en memoria con los destinos {@code /topic} (difusion)
 * y {@code /queue} (punto a punto). Los mensajes de la aplicacion se envian
 * al prefijo {@code /app}.</p>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registra el endpoint STOMP {@code /ws} con soporte SockJS como fallback.
     *
     * @param registry registro de endpoints STOMP.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }


    /**
     * Configura el broker de mensajes simple con destinos {@code /topic} y {@code /queue},
     * y establece {@code /app} como prefijo para los mensajes enviados desde el cliente.
     *
     * @param registry registro de configuracion del broker.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.setApplicationDestinationPrefixes("/app");

        registry.enableSimpleBroker("/topic", "/queue");
    }
}