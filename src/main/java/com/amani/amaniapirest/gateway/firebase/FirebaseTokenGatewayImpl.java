package com.amani.amaniapirest.gateway.firebase;

import com.amani.amaniapirest.gateway.FirebaseTokenGateway;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementación de {@link FirebaseTokenGateway} usando Firebase Admin SDK.
 *
 * <p>Solo se registra cuando {@link FirebaseApp} está disponible.</p>
 */
@Service
@Primary
@ConditionalOnBean(FirebaseApp.class)
public class FirebaseTokenGatewayImpl implements FirebaseTokenGateway {

    private static final Logger log = LoggerFactory.getLogger(FirebaseTokenGatewayImpl.class);

    private final FirebaseAuth firebaseAuth;

    public FirebaseTokenGatewayImpl(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public String createCustomToken(String uid, Map<String, Object> additionalClaims) {
        try {
            String customToken = firebaseAuth.createCustomToken(uid, additionalClaims);
            log.debug("[Firebase Auth] Token generado correctamente para uid: {}", uid);
            return customToken;
        } catch (FirebaseAuthException e) {
            log.error("[Firebase Auth] Error al crear custom token para uid {}: {}", uid, e.getMessage(), e);
            throw new RuntimeException("Error al generar token de Firebase: " + e.getMessage(), e);
        }
    }
}
