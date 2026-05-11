package com.amani.amaniapirest.stripe;

import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.enums.EstadoPago;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Pago;
import com.amani.amaniapirest.repository.CitaRepository;
import com.amani.amaniapirest.repository.PaymentRepository;
import com.amani.amaniapirest.services.notificacion.NotificationServices;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Procesador idempotente de eventos webhook de Stripe.
 *
 * <p>Cada evento se trata como fuente única de verdad del estado del pago.
 * El procesamiento es síncrono pero atómico: si falla, Stripe reintentará
 * automáticamente hasta que devolvamos 200 OK.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookEventProcessor {

    private final PaymentRepository paymentRepository;
    private final CitaRepository citaRepository;
    private final NotificationServices notificationServices;

    @Transactional
    public void process(Event event) {
        String type = event.getType();
        log.info("Procesando evento Stripe: id={}, type={}", event.getId(), type);

        switch (type) {
            case "payment_intent.succeeded" -> handlePaymentIntentSucceeded(event);
            case "payment_intent.payment_failed" -> handlePaymentIntentFailed(event);
            case "charge.refunded" -> handleChargeRefunded(event);
            default -> log.warn("Evento Stripe no manejado: {}", type);
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent == null) {
            log.error("No se pudo deserializar PaymentIntent del evento {}", event.getId());
            return;
        }

        String paymentIntentId = paymentIntent.getId();
        Pago pago = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseGet(() -> {
                    log.warn("Pago no encontrado para paymentIntentId={}. Puede ser un evento duplicado o tardío.", paymentIntentId);
                    return null;
                });

        if (pago == null || pago.getEstadoPago() == EstadoPago.PAGADO) {
            log.info("Pago ya procesado o no existe. Ignorando evento.", paymentIntentId);
            return;
        }

        pago.setEstadoPago(EstadoPago.PAGADO);
        pago.setFechaPago(LocalDateTime.now());
        String latestChargeId = paymentIntent.getLatestCharge();
        if (latestChargeId != null) {
            pago.setStripeChargeId(latestChargeId);
        }
        paymentRepository.save(pago);

        Cita cita = pago.getCita();
        if (cita != null && cita.getEstado() == EstadoCita.pendiente) {
            cita.setEstado(EstadoCita.confirmada);
            citaRepository.save(cita);
            notificationServices.enviarNotificacion(cita.getPsicologo().getUsuario(), "Pago recibido", "El paciente ha pagado la cita del " + cita.getStartDatetime());
            notificationServices.enviarNotificacion(cita.getPaciente().getUsuario(), "Cita confirmada", "Tu cita del " + cita.getStartDatetime() + " ha sido confirmada tras el pago.");
            log.info("Cita {} confirmada tras pago exitoso {}", cita.getIdCita(), paymentIntentId);
        }

        log.info("Evento payment_intent.succeeded procesado: paymentIntentId={}, citaId={}",
                paymentIntentId, cita != null ? cita.getIdCita() : "N/A");
    }

    private void handlePaymentIntentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent == null) {
            log.error("No se pudo deserializar PaymentIntent del evento {}", event.getId());
            return;
        }

        String paymentIntentId = paymentIntent.getId();
        paymentRepository.findByStripePaymentIntentId(paymentIntentId).ifPresent(pago -> {
            if (pago.getEstadoPago() != EstadoPago.PAGADO) {
                pago.setEstadoPago(EstadoPago.FALLIDO);
                paymentRepository.save(pago);
                log.info("Pago marcado como FALLIDO para paymentIntentId={}", paymentIntentId);
            }
        });
    }

    private void handleChargeRefunded(Event event) {
        Charge charge = (Charge) event.getDataObjectDeserializer().getObject().orElse(null);
        if (charge == null) {
            log.error("No se pudo deserializar Charge del evento {}", event.getId());
            return;
        }

        String chargeId = charge.getId();
        paymentRepository.findByStripeChargeId(chargeId).ifPresentOrElse(pago -> {
            pago.setEstadoPago(EstadoPago.REEMBOLSADO);
            paymentRepository.save(pago);
            log.info("Pago marcado como REEMBOLSADO para chargeId={}", chargeId);
        }, () -> log.warn("No se encontró pago para chargeId={}", chargeId));
    }
}
