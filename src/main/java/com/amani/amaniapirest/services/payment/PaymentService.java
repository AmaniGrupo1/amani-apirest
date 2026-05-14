package com.amani.amaniapirest.services.payment;

import com.amani.amaniapirest.dto.payment.request.CreatePaymentIntentRequest;
import com.amani.amaniapirest.dto.payment.request.RefundRequest;
import com.amani.amaniapirest.dto.payment.response.PaymentIntentResponse;
import com.amani.amaniapirest.dto.payment.response.RefundResponse;
import com.amani.amaniapirest.enums.EstadoPago;
import com.amani.amaniapirest.enums.MetodoPago;
import com.amani.amaniapirest.exception.PaymentProcessingException;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Pago;
import com.amani.amaniapirest.repository.CitaRepository;
import com.amani.amaniapirest.repository.PaymentRepository;
import com.amani.amaniapirest.stripe.StripePaymentGateway;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Servicio de pagos que orquesta la creación de PaymentIntents,
 * persistencia de transacciones y reembolsos.
 *
 * <p>Toda la lógica de Stripe vive en {@link StripePaymentGateway};
 * esta clase solo coordina negocio + persistencia.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CitaRepository citaRepository;
    private final StripePaymentGateway stripePaymentGateway;

    private static final BigDecimal DEFAULT_SESSION_PRICE = new BigDecimal("50.00");

    @Transactional
    public PaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request, Long pacienteId) {
        Cita cita = citaRepository.findById(request.getCitaId())
                .orElseThrow(() -> new PaymentProcessingException("Cita no encontrada: " + request.getCitaId()));

        if (cita.getPaciente() == null || !cita.getPaciente().getIdPaciente().equals(pacienteId)) {
            throw new PaymentProcessingException("La cita no pertenece al paciente autenticado");
        }

        if (cita.getEstado().name().equalsIgnoreCase("cancelada")) {
            throw new PaymentProcessingException("No se puede pagar una cita cancelada");
        }

        paymentRepository.findByCitaIdCita(cita.getIdCita()).ifPresent(existing -> {
            if (existing.getEstadoPago() == EstadoPago.PAGADO) {
                throw new PaymentProcessingException("La cita ya está pagada");
            }
        });

        BigDecimal amount = (cita.getPago() != null && cita.getPago().getMonto() != null)
                ? cita.getPago().getMonto()
                : DEFAULT_SESSION_PRICE;

        long amountInCents = amount.multiply(new BigDecimal("100")).longValueExact();
        String idempotencyKey = "cita-" + cita.getIdCita() + "-" + UUID.randomUUID();
        String currency = "EUR";

        PaymentIntent stripeIntent = stripePaymentGateway.createPaymentIntent(amountInCents, currency, idempotencyKey);

        Pago pago = Pago.builder()
                .cita(cita)
                .monto(amount)
                .metodoPago(MetodoPago.TARJETA)
                .estadoPago(EstadoPago.PENDIENTE)
                .stripePaymentIntentId(stripeIntent.getId())
                .idempotencyKey(idempotencyKey)
                .currency(currency)
                .build();
        paymentRepository.save(pago);

        log.info("PaymentIntent creado: id={}, citaId={}, amount={}", stripeIntent.getId(), cita.getIdCita(), amount);

        return PaymentIntentResponse.builder()
                .clientSecret(stripeIntent.getClientSecret())
                .paymentIntentId(stripeIntent.getId())
                .amount(amount)
                .currency(currency)
                .build();
    }

    @Transactional
    public RefundResponse refundPayment(RefundRequest request) {
        Pago pago = paymentRepository.findByCitaIdCita(request.getCitaId())
                .orElseThrow(() -> new PaymentProcessingException("No existe pago para la cita " + request.getCitaId()));

        if (pago.getEstadoPago() != EstadoPago.PAGADO) {
            throw new PaymentProcessingException("Solo se pueden reembolsar pagos confirmados");
        }

        if (pago.getStripeChargeId() == null || pago.getStripeChargeId().isBlank()) {
            throw new PaymentProcessingException("No se encontró el chargeId del pago");
        }

        String idempotencyKey = "refund-" + pago.getIdPago() + "-" + UUID.randomUUID();
        Refund stripeRefund = stripePaymentGateway.refundPayment(pago.getStripeChargeId(), idempotencyKey);

        pago.setEstadoPago(EstadoPago.REEMBOLSADO);
        paymentRepository.save(pago);

        log.info("Reembolso procesado: refundId={}, citaId={}", stripeRefund.getId(), request.getCitaId());

        return RefundResponse.builder()
                .status(stripeRefund.getStatus())
                .refundId(stripeRefund.getId())
                .build();
    }
}
