package com.amani.amaniapirest.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspecto que intercepta llamadas a servicios clave para actualizar
 * los contadores de métricas de negocio de Amani de forma transparente.
 *
 * <p>Usa AOP para no acoplar los servicios de dominio con Micrometer.</p>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricsAspect {

    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;
    private final Counter citasCreatedCounter;
    private final Counter citasCancelledCounter;
    private final Counter stripePaymentSuccessCounter;
    private final Counter stripePaymentFailureCounter;
    private final MeterRegistry meterRegistry;

    // -------------------------------------------------------------------------
    // Autenticación
    // -------------------------------------------------------------------------

    /** Incrementa el contador de logins exitosos cuando AuthService.login() retorna. */
    @AfterReturning(
            pointcut = "execution(* com.amani.amaniapirest.services.AuthService.login(..))",
            returning = "result"
    )
    public void onLoginSuccess(Object result) {
        loginSuccessCounter.increment();
        log.debug("[Metrics] Login exitoso registrado");
    }

    /** Incrementa el contador de logins fallidos cuando AuthService.login() lanza excepción. */
    @AfterThrowing(
            pointcut = "execution(* com.amani.amaniapirest.services.AuthService.login(..))",
            throwing = "ex"
    )
    public void onLoginFailure(Exception ex) {
        loginFailureCounter.increment();
        log.debug("[Metrics] Intento de login fallido registrado: {}", ex.getClass().getSimpleName());
    }

    // -------------------------------------------------------------------------
    // Citas
    // -------------------------------------------------------------------------

    /** Registra métricas cuando se crea una cita. */
    @AfterReturning(
            pointcut = "execution(* com.amani.amaniapirest.services.CitaService.crearCita(..))"
    )
    public void onCitaCreated() {
        citasCreatedCounter.increment();
        log.debug("[Metrics] Cita creada registrada");
    }

    /** Registra métricas cuando se cancela una cita. */
    @AfterReturning(
            pointcut = "execution(* com.amani.amaniapirest.services.CitaService.cancelarCita(..))"
    )
    public void onCitaCancelled() {
        citasCancelledCounter.increment();
        log.debug("[Metrics] Cita cancelada registrada");
    }

    // -------------------------------------------------------------------------
    // Pagos Stripe
    // -------------------------------------------------------------------------

    /** Registra pago Stripe exitoso. */
    @AfterReturning(
            pointcut = "execution(* com.amani.amaniapirest.stripe..*.*(..)) && " +
                       "execution(* *.create*(..))"
    )
    public void onStripePaymentSuccess() {
        stripePaymentSuccessCounter.increment();
        log.debug("[Metrics] Pago Stripe exitoso registrado");
    }

    /** Registra pago Stripe fallido. */
    @AfterThrowing(
            pointcut = "execution(* com.amani.amaniapirest.stripe..*.*(..))",
            throwing = "ex"
    )
    public void onStripePaymentFailure(Exception ex) {
        stripePaymentFailureCounter.increment();
        log.debug("[Metrics] Pago Stripe fallido registrado: {}", ex.getClass().getSimpleName());
    }
}
