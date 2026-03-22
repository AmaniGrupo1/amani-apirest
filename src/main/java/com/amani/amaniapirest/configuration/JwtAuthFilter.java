package com.amani.amaniapirest.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
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
@Log4j
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
            log.warn("No se envió token JWT o formato incorrecto. Authorization: " + authHeader);
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String email;
        log.info("PATH: " + request.getServletPath());

        final String authHeaderr = request.getHeader("Authorization");
        log.info("Authorization Header: " + authHeaderr);
        try {
            email = jwtUtil.extractEmail(token);
        } catch (Exception e) {
            // Token malformado o expirado → continuar sin autenticar
            filterChain.doFilter(request, response);
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            log.info("EMAIL del token: " + email);
            if (jwtUtil.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("TOKEN VÁLIDO, autenticando usuario...");
            }
        }

        filterChain.doFilter(request, response);
    }
}



