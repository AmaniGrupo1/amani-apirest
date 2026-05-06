package com.amani.amaniapirest.gateway;

import java.util.Map;

/**
 * Abstracción del gateway de tokens personalizados de Firebase.
 *
 * <p>Desacopla la capa de dominio de la implementación concreta
 * (Firebase Auth, emulador, o stub para desarrollo local).</p>
 */
public interface FirebaseTokenGateway {

    /**
     * Genera un token personalizado de Firebase para el usuario indicado.
     *
     * @param uid             identificador único del usuario en el sistema
     * @param additionalClaims claims adicionales (ej. rol) a incluir en el token
     * @return token personalizado de Firebase
     */
    String createCustomToken(String uid, Map<String, Object> additionalClaims);
}
