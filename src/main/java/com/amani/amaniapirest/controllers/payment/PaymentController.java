package com.amani.amaniapirest.controllers.payment;

import com.amani.amaniapirest.dto.payment.request.CreatePaymentIntentRequest;
import com.amani.amaniapirest.dto.payment.request.RefundRequest;
import com.amani.amaniapirest.dto.payment.response.PaymentIntentResponse;
import com.amani.amaniapirest.dto.payment.response.RefundResponse;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.services.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones de pago de pacientes.
 *
 * <p>Todas las rutas bajo {@code /api/payments} requieren autenticación JWT.
 * El backend crea el PaymentIntent en Stripe y devuelve únicamente el
 * {@code clientSecret} al cliente, nunca la clave secreta de Stripe.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PacientesRepository pacientesRepository;

    /**
     * Crea un PaymentIntent en Stripe para una cita reservada.
     *
     * @param request   DTO con el ID de la cita a pagar
     * @param auth      autenticación JWT del paciente
     * @return clientSecret y metadatos del PaymentIntent
     */
    @PostMapping("/create-intent")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @Valid @RequestBody CreatePaymentIntentRequest request,
            Authentication auth) {
        String email = auth.getName();
        Long pacienteId = pacientesRepository.findByUsuario_Email(email)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"))
                .getIdPaciente();
        log.info("Solicitud de PaymentIntent: citaId={}, pacienteId={}", request.getCitaId(), pacienteId);
        PaymentIntentResponse response = paymentService.createPaymentIntent(request, pacienteId);
        return ResponseEntity.ok(response);
    }

    /**
     * Inicia un reembolso completo del pago asociado a una cita.
     *
     * <p>Restringido a ADMIN y PSICOLOGO.</p>
     */
    @PostMapping("/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'PSICOLOGO')")
    public ResponseEntity<RefundResponse> refundPayment(
            @Valid @RequestBody RefundRequest request) {
        log.info("Solicitud de reembolso: citaId={}", request.getCitaId());
        RefundResponse response = paymentService.refundPayment(request);
        return ResponseEntity.ok(response);
    }
}
