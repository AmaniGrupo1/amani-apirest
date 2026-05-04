package com.amani.amaniapirest.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que se ejecuta una vez por petición HTTP.
 *
 * <p>Extrae el token del encabezado {@code Authorization: Bearer <token>},
 * lo valida y, si es correcto, establece la autenticación en el
 * {@link SecurityContextHolder} para el resto de la cadena de filtros.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        //Excluir rutas publicas
        String path = request.getServletPath();

        // Excluir solo login y register-paciente
        if (path.equals("/auth/login") || path.equals("/auth/register-paciente")) {
            log.info("Ruta pública, saltando filtro JWT: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[JWT] Token ausente o formato incorrecto. Path: {}, Auth header: {}",
                    path, authHeader == null ? "null" : authHeader.substring(0, Math.min(30, authHeader.length())));
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String email;
        log.info("[JWT] Petición a: {}", path);

        try {
            email = jwtUtil.extractEmail(token);
            log.info("[JWT] Email extraído del token: {}", email);
        } catch (Exception e) {
            log.error("[JWT] Error al extraer email del token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                log.info("[JWT] UserDetails cargado: email={}, authorities={}",
                        userDetails.getUsername(), userDetails.getAuthorities());

                if (jwtUtil.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("[JWT] Autenticación OK — email={}, roles={}",
                            email, userDetails.getAuthorities());
                } else {
                    log.warn("[JWT] Token inválido o expirado para email={}", email);
                }
            } catch (Exception e) {
                log.error("[JWT] Error al cargar usuario '{}': {}", email, e.getMessage());
            }
        } else if (email == null) {
            log.warn("[JWT] Email extraído es null");
        }

        filterChain.doFilter(request, response);
    }
}



