package com.amani.amaniapirest.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JitsiService {

    @Value("${jitsi.app.id}")
    private String appId;

    @Value("${jitsi.app.secret}")
    private String appSecret;

    @Value("${jitsi.app.domain}")
    private String appDomain;

    /**
     * Genera un token JWT para la sala de Jitsi especificada.
     *
     * @param room        Nombre de la sala
     * @param userName    Nombre completo del usuario
     * @param userEmail   Correo electrónico del usuario
     * @param avatarUrl   URL de la foto de perfil del usuario
     * @param isModerator Si el usuario debe tener permisos de moderador
     * @return String con el token JWT
     */
    public String generateToken(String room, String userName, String userEmail, String avatarUrl, boolean isModerator) {
        Map<String, Object> userClaim = new HashMap<>();
        userClaim.put("avatar", avatarUrl != null ? avatarUrl : "");
        userClaim.put("name", userName);
        userClaim.put("email", userEmail);
        userClaim.put("moderator", isModerator);

        Map<String, Object> featuresClaim = new HashMap<>();
        featuresClaim.put("recording", false);
        featuresClaim.put("livestreaming", false);
        featuresClaim.put("screen-sharing", true);

        Map<String, Object> contextClaim = new HashMap<>();
        contextClaim.put("user", userClaim);
        contextClaim.put("features", featuresClaim);

        long now = System.currentTimeMillis();
        Key key = Keys.hmacShaKeyFor(appSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setAudience(appId)
                .setIssuer(appId)
                .setSubject(appDomain)
                .claim("room", room)
                .claim("context", contextClaim)
                .setIssuedAt(new Date(now))                        // ✅ iat
                .setNotBefore(new Date(now - 30_000L))             // ✅ nbf = ahora - 30s
                .setExpiration(new Date(now + 7_200_000L))         // ✅ 2 horas
                .signWith(key)
                .compact();
    }

}