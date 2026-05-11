package com.amani.amaniapirest.controllers.payment;

import com.amani.amaniapirest.configuration.GlobalExceptionHandler;
import com.amani.amaniapirest.configuration.UserDetailsImpl;
import com.amani.amaniapirest.dto.payment.request.CreatePaymentIntentRequest;
import com.amani.amaniapirest.dto.payment.response.PaymentIntentResponse;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.services.payment.PaymentService;
import com.amani.amaniapirest.services.payment.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private PaymentController paymentController;

    @InjectMocks
    private WebhookController webhookController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController, webhookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createIntentWithAuthReturns200() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(10L);
        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);

        PaymentIntentResponse responseDto = new PaymentIntentResponse("secret", "pi_123", new BigDecimal("50.00"), "EUR");
        when(paymentService.createPaymentIntent(anyLong(), anyLong())).thenReturn(responseDto);

        var response = paymentController.createPaymentIntent(new CreatePaymentIntentRequest(1L), auth);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPaymentIntentId()).isEqualTo("pi_123");
    }

    @Test
    void webhookReceives200() throws Exception {
        mockMvc.perform(post("/api/webhooks/stripe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Stripe-Signature", "sig_test")
                        .content("{\"id\":\"evt_test\",\"type\":\"payment_intent.succeeded\"}"))
                .andExpect(status().isOk());

        verify(webhookService).processWebhook(any(), any());
    }
}
