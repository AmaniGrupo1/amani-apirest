package com.amani.amaniapirest.services.payment;

import com.amani.amaniapirest.dto.payment.request.CreatePaymentIntentRequest;
import com.amani.amaniapirest.dto.payment.response.PaymentIntentResponse;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.enums.EstadoPago;
import com.amani.amaniapirest.exception.PaymentProcessingException;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Pago;
import com.amani.amaniapirest.repository.CitaRepository;
import com.amani.amaniapirest.repository.PaymentRepository;
import com.amani.amaniapirest.stripe.StripePaymentGateway;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CitaRepository citaRepository;
    @Mock
    private StripePaymentGateway stripePaymentGateway;

    @InjectMocks
    private PaymentService paymentService;

    private Cita cita;
    private Paciente paciente;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setIdPaciente(1L);

        cita = new Cita();
        cita.setIdCita(100L);
        cita.setPaciente(paciente);
        cita.setEstado(EstadoCita.pendiente);
        cita.setDurationMinutes(50);
    }

    @Test
    void createPaymentIntent_success() {
        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(100L);
        PaymentIntent stripeIntent = new PaymentIntent();
        stripeIntent.setId("pi_test_123");
        stripeIntent.setClientSecret("pi_test_secret");

        when(citaRepository.findById(100L)).thenReturn(Optional.of(cita));
        when(paymentRepository.findByCitaIdCita(100L)).thenReturn(Optional.empty());
        when(stripePaymentGateway.createPaymentIntent(anyLong(), eq("EUR"), anyString())).thenReturn(stripeIntent);
        when(paymentRepository.save(any(Pago.class))).thenAnswer(i -> i.getArgument(0));

        PaymentIntentResponse response = paymentService.createPaymentIntent(request, 1L);

        assertNotNull(response);
        assertEquals("pi_test_123", response.getPaymentIntentId());
        assertEquals("pi_test_secret", response.getClientSecret());
        assertEquals("EUR", response.getCurrency());
        verify(paymentRepository).save(any(Pago.class));
    }

    @Test
    void createPaymentIntent_citaNotFound_throws() {
        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(999L);
        when(citaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PaymentProcessingException.class, () -> paymentService.createPaymentIntent(request, 1L));
    }

    @Test
    void createPaymentIntent_alreadyPaid_throws() {
        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(100L);
        Pago existing = new Pago();
        existing.setEstadoPago(EstadoPago.PAGADO);

        when(citaRepository.findById(100L)).thenReturn(Optional.of(cita));
        when(paymentRepository.findByCitaIdCita(100L)).thenReturn(Optional.of(existing));

        assertThrows(PaymentProcessingException.class, () -> paymentService.createPaymentIntent(request, 1L));
    }
}
