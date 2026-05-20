package com.amani.amaniapirest.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Configuración de métricas de negocio personalizadas con Micrometer.
 *
 * <p>Expone contadores y gauges específicos de Amani que aparecen
 * en {@code /actuator/prometheus} y son visibles en Grafana.</p>
 *
 * <p>Métricas registradas:</p>
 * <ul>
 *   <li>{@code amani.auth.login.success} — logins exitosos</li>
 *   <li>{@code amani.auth.login.failure} — intentos de login fallidos</li>
 *   <li>{@code amani.citas.created} — citas creadas</li>
 *   <li>{@code amani.citas.cancelled} — citas canceladas</li>
 *   <li>{@code amani.payments.stripe.success} — pagos Stripe exitosos</li>
 *   <li>{@code amani.payments.stripe.failure} — pagos Stripe fallidos</li>
 *   <li>{@code amani.websocket.sessions.active} — sesiones WebSocket activas</li>
 * </ul>
 */
@Slf4j
@Configuration
public class MonitoringConfig {

    // -------------------------------------------------------------------------
    // Gauge atómico — sesiones WebSocket activas
    // -------------------------------------------------------------------------

    /**
     * Contador atómico de sesiones WebSocket activas.
     * Debe ser inyectado en {@code WebSocketConfig} para incrementar/decrementar.
     */
    @Bean
    public AtomicInteger activeWebSocketSessions() {
        return new AtomicInteger(0);
    }

    // -------------------------------------------------------------------------
    // Métricas de negocio — MeterBinder
    // -------------------------------------------------------------------------

    /**
     * Registra todas las métricas de negocio de Amani.
     * Spring Boot las enlaza automáticamente al {@link MeterRegistry} global.
     */
    @Bean
    public MeterBinder amaniBusinessMetrics(AtomicInteger activeWebSocketSessions) {
        return registry -> {
            // Gauge: sesiones WebSocket activas
            Gauge.builder("amani.websocket.sessions.active", activeWebSocketSessions, AtomicInteger::get)
                    .description("Número de sesiones WebSocket activas actualmente")
                    .register(registry);

            log.info("[Monitoring] Métricas de negocio Amani registradas correctamente en MeterRegistry");
        };
    }

    // -------------------------------------------------------------------------
    // Beans de contadores — inyectables en servicios
    // -------------------------------------------------------------------------

    /** Contador de logins exitosos. */
    @Bean
    public Counter loginSuccessCounter(MeterRegistry registry) {
        return Counter.builder("amani.auth.login.success")
                .description("Número total de autenticaciones exitosas")
                .tag("type", "jwt")
                .register(registry);
    }

    /** Contador de logins fallidos. */
    @Bean
    public Counter loginFailureCounter(MeterRegistry registry) {
        return Counter.builder("amani.auth.login.failure")
                .description("Número total de intentos de login fallidos")
                .tag("type", "jwt")
                .register(registry);
    }

    /** Contador de citas creadas. */
    @Bean
    public Counter citasCreatedCounter(MeterRegistry registry) {
        return Counter.builder("amani.citas.created")
                .description("Número total de citas creadas")
                .register(registry);
    }

    /** Contador de citas canceladas. */
    @Bean
    public Counter citasCancelledCounter(MeterRegistry registry) {
        return Counter.builder("amani.citas.cancelled")
                .description("Número total de citas canceladas")
                .register(registry);
    }

    /** Contador de pagos Stripe exitosos. */
    @Bean
    public Counter stripePaymentSuccessCounter(MeterRegistry registry) {
        return Counter.builder("amani.payments.stripe.success")
                .description("Número total de pagos Stripe completados exitosamente")
                .tag("provider", "stripe")
                .register(registry);
    }

    /** Contador de pagos Stripe fallidos. */
    @Bean
    public Counter stripePaymentFailureCounter(MeterRegistry registry) {
        return Counter.builder("amani.payments.stripe.failure")
                .description("Número total de pagos Stripe fallidos o rechazados")
                .tag("provider", "stripe")
                .register(registry);
    }
}
