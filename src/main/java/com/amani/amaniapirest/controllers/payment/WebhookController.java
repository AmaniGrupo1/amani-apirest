package com.amani.amaniapirest.controllers.payment;

import com.amani.amaniapirest.services.payment.WebhookService;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Controlador que recibe los webhooks de Stripe.
 *
 * <p>Este endpoint es <b>público</b> (sin autenticación JWT) porque Stripe
 * firma cada evento con el webhook secret. La verificación de firma es la
 * única garantía de seguridad necesaria.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Controlador para la recepción de webhooks de plataformas de pago")
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/stripe")
    @Operation(summary = "Recibir webhook de Stripe", description = "Endpoint para recibir y procesar eventos webhook de Stripe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook procesado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Petición incorrecta o error de validación de firma"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> handleStripeWebhook(
            HttpServletRequest request,
            @RequestHeader("Stripe-Signature") String sigHeader) throws IOException {
        String payload = readBody(request);
        log.info("Webhook Stripe recibido: length={}, sigHeader presente={}", payload.length(), sigHeader != null);
        webhookService.processWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

    private String readBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
