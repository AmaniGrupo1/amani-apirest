package com.amani.amaniapirest.services.payment;

import com.amani.amaniapirest.configuration.StripeConfig;
import com.amani.amaniapirest.stripe.WebhookEventProcessor;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio que recibe el payload del webhook, verifica la firma de Stripe
 * y delega el procesamiento al {@link WebhookEventProcessor}.
 *
 * <p>Devuelve 200 lo más rápido posible; cualquier excepción se
 * propaga al controlador para que Stripe reintente.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final StripeConfig stripeConfig;
    private final WebhookEventProcessor webhookEventProcessor;

    /**
     * Ejecuta la operación correspondiente a processWebhook.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public void processWebhook(String payload, String sigHeader) {
        if (sigHeader == null || sigHeader.isBlank()) {
            throw new IllegalArgumentException("Falta header Stripe-Signature");
        }

        String webhookSecret = stripeConfig.getWebhookSecret();
        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.warn("Webhook recibido pero stripe.webhook-secret no está configurado. Evento ignorado.");
            throw new IllegalStateException("stripe.webhook-secret no está configurado");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Firma de webhook inválida", e);
            throw new IllegalArgumentException("Firma de webhook inválida", e);
        }

        log.info("Webhook verificado: id={}, type={}", event.getId(), event.getType());
        webhookEventProcessor.process(event);
    }
}
