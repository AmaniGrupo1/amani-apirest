package com.amani.amaniapirest.configuration;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Stripe cargada desde application.properties.
 *
 * <p>Las claves sensibles se inyectan mediante variables de entorno
 * y NUNCA deben estar hardcodeadas. En producción se recomienda usar
 * GCP Secret Manager o un gestor de secretos equivalente.</p>
 *
 * <p>En desarrollo local sin claves configuradas, el bean se crea pero
 * Stripe permanece desactivado hasta que se proporcione una secret key.</p>
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "stripe")
@Getter
@Setter
public class StripeConfig {

    private String secretKey;
    private String publishableKey;
    private String webhookSecret;
    private String currency = "EUR";

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isBlank()) {
            log.warn("stripe.secret-key no está configurada. Las operaciones de pago estarán desactivadas hasta que se configure una clave válida.");
            return;
        }
        Stripe.apiKey = secretKey;
        log.info("Stripe configurado correctamente. Publishable key presente: {}", publishableKey != null && !publishableKey.isBlank());
    }

    public boolean isConfigured() {
        return secretKey != null && !secretKey.isBlank();
    }
}
