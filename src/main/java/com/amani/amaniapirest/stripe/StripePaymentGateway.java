package com.amani.amaniapirest.stripe;

import com.amani.amaniapirest.configuration.StripeConfig;
import com.amani.amaniapirest.exception.PaymentProcessingException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Gateway de pagos que abstrae la interacción directa con la API de Stripe.
 *
 * <p>Centraliza la creación de PaymentIntents y reembolsos para facilitar
 * testing y permitir cambiar de pasarela en el futuro sin tocar la capa de
 * servicio de negocio.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StripePaymentGateway {

    private final StripeConfig stripeConfig;

    private void ensureConfigured() {
        if (!stripeConfig.isConfigured()) {
            throw new PaymentProcessingException(
                "Stripe no está configurado. Configure la variable de entorno STRIPE_SECRET_KEY."
            );
        }
    }

    /**
     * Crea un PaymentIntent en Stripe con monto, divisa e idempotency key.
     *
     * @param amount         importe en la unidad mínima de la divisa (céntimos)
     * @param currency       código ISO de divisa, p. ej. "EUR"
     * @param idempotencyKey clave de idempotencia para evitar duplicados
     * @return PaymentIntent creado con clientSecret listo para el cliente
     */
    public PaymentIntent createPaymentIntent(long amount, String currency, String idempotencyKey) {
        ensureConfigured();
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency(currency)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            log.info("Creando PaymentIntent por {} {} con idempotencyKey={}", amount, currency, idempotencyKey);
            return PaymentIntent.create(params, createIdempotencyOptions(idempotencyKey));
        } catch (Exception e) {
            log.error("Error al crear PaymentIntent en Stripe", e);
            throw new PaymentProcessingException("No se pudo crear el PaymentIntent: " + e.getMessage(), e);
        }
    }

    /**
     * Solicita un reembolso completo de un cargo previo.
     *
     * @param chargeId       identificador del cargo a reembolsar
     * @param idempotencyKey clave de idempotencia
     * @return Refund creado en Stripe
     */
    public Refund refundPayment(String chargeId, String idempotencyKey) {
        ensureConfigured();
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setCharge(chargeId)
                    .build();

            log.info("Solicitando reembolso para chargeId={}", chargeId);
            return Refund.create(params, createIdempotencyOptions(idempotencyKey));
        } catch (Exception e) {
            log.error("Error al reembolsar cargo {}", chargeId, e);
            throw new PaymentProcessingException("No se pudo procesar el reembolso: " + e.getMessage(), e);
        }
    }

    private com.stripe.net.RequestOptions createIdempotencyOptions(String idempotencyKey) {
        return com.stripe.net.RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();
    }
}
