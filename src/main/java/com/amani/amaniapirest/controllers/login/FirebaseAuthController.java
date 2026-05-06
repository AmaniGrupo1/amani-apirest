package com.amani.amaniapirest.controllers.login;

import com.amani.amaniapirest.gateway.FirebaseTokenGateway;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para la obtención de tokens personalizados de Firebase.
 *
 * <p>Depende de la abstracción {@link FirebaseTokenGateway} en lugar del SDK
 * de Firebase directamente, lo que permite desacoplar el controlador de la
 * infraestructura.</p>
 *
 * <p>Cuando {@code firebase.enabled=false}, se inyecta
 * {@link com.amani.amaniapirest.gateway.noop.NoOpFirebaseTokenGateway} que lanza
 * {@link UnsupportedOperationException}, y este controlador devuelve 503.</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Firebase Auth", description = "Obtención de tokens para Firebase")
public class FirebaseAuthController {

    private static final Logger log = LoggerFactory.getLogger(FirebaseAuthController.class);
    private final FirebaseTokenGateway firebaseTokenGateway;
    private final UsuarioRepository usuarioRepository;

    @Operation(summary = "Obtener Firebase Custom Token",
               description = "Genera un token de Firebase para el usuario autenticado")
    @GetMapping("/firebase-token")
    public ResponseEntity<Map<String, String>> getFirebaseToken(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            log.warn("[Firebase Auth] Usuario no encontrado para email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String uid = String.valueOf(usuario.getIdUsuario());

            Map<String, Object> additionalClaims = new HashMap<>();
            additionalClaims.put("role", usuario.getRol().name());

            String customToken = firebaseTokenGateway.createCustomToken(uid, additionalClaims);

            Map<String, String> response = new HashMap<>();
            response.put("firebaseToken", customToken);

            log.info("[Firebase Auth] Token generado correctamente para usuario ID: {}", uid);
            return ResponseEntity.ok(response);
        } catch (UnsupportedOperationException e) {
            log.warn("[Firebase Auth] Servicio Firebase no disponible en este entorno.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Firebase Auth no está disponible en este entorno."));
        } catch (Exception e) {
            log.error("[Firebase Auth] Error al crear custom token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
