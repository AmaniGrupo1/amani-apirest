package com.amani.amaniapirest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 * Configuración de seguridad HTTP de la aplicación.
 *
 * <p>Implementa autenticación stateless mediante JWT. Define las rutas públicas
 * (login y registro de paciente) y protege el resto por rol.</p>
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("http://localhost:*", "https://amani.org", "https://*.amani.org"));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth

                                //  Públicos
                                .requestMatchers("/auth/login", "/auth/register-paciente").permitAll()
                                .requestMatchers("/api/situaciones").hasAnyRole("PACIENTE", "PSICOLOGO", "ADMIN")
                                .requestMatchers("/api/situaciones/create").hasRole("ADMIN") // Solo admin puede crear situaciones
                                .requestMatchers("/api/situaciones/update/*").hasRole("ADMIN") // Solo admin puede crear situaciones
                                .requestMatchers("/api/situaciones/delete/*").hasRole("ADMIN") // Solo admin puede crear situaciones

                                .requestMatchers("/api/psicologo/pacientes/*/psicologo").hasRole("PACIENTE") // Endpoint para que el paciente vea su psicólogo asignado
                                .requestMatchers("/api/psicologo/usuario/**").hasAnyRole("PACIENTE", "PSICOLOGO", "ADMIN") // Lookup básico de usuario, necesario para chat

                                //  Chat — accesible para pacientes, psicólogos y admins
                                .requestMatchers("/api/chats/**").hasAnyRole("PACIENTE", "PSICOLOGO", "ADMIN")

                                //  ADMIN
                                .requestMatchers("/auth/registry/pacienteAdmin").hasRole("ADMIN")
                                .requestMatchers("/auth/register-admin", "/auth/register-psicologo").hasRole("ADMIN")
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/auth/pacientes/**").hasRole("ADMIN")
                                .requestMatchers("/auth/admins").hasRole("ADMIN")
                                .requestMatchers("/api/admin/psicologos").hasRole("ADMIN") //Listamos psicologo
                                .requestMatchers("/api/admin/psicologos/create").hasRole("ADMIN") //creamos psicologo con admin
                                .requestMatchers("/api/admin/psicologos/asignar-psicologo").hasRole("ADMIN") //Asignamos el paciente al psicologo
                                .requestMatchers("/api/admin/psicologos/pacientes").hasRole("ADMIN") //creamos psicologo con admin
                                .requestMatchers("/api/pacientes/admin").hasRole("ADMIN") //Listamos pacientes
                                .requestMatchers("/api/pacientes/sin-psicologo").hasRole("ADMIN") //Listamos pacientes sin psicologos
                                 //  PSICOLOGO + ADMIN
                                .requestMatchers("/api/psicologo/**").hasAnyRole("ADMIN", "PSICOLOGO")
                                .requestMatchers("/api/psicologo/update/*").hasRole( "PSICOLOGO")
                                .requestMatchers("/api/citas/psicologo/*/horario").hasAnyRole("ADMIN", "PSICOLOGO")
                                .requestMatchers("/api/psicologo/pacientes/getAll/**").hasRole("PSICOLOGO")
                                .requestMatchers("/auth/registrar/pacienteDesde/psicologo").hasRole("PSICOLOGO")
                                .requestMatchers("/api/citas/psicologo/cita").hasAnyRole("PACIENTE","PSICOLOGO","ADMIN") // Endpoint para que el psicólogo cree una cita para un paciente asignado
                                .requestMatchers("/api/citas/psicologo/terapias").hasAnyRole("PACIENTE","PSICOLOGO","ADMIN") // Endpoint para que el psicólogo cree una cita para un paciente asignado
                                .requestMatchers("/api/citas/update/*").hasRole("ADMIN") // Endpoint para que el psicólogo cree una cita para un paciente asignado
                                .requestMatchers("/api/citas/delete/*").hasRole("ADMIN") // Endpoint para que el psicólogo cree una cita para un paciente asignado

                                .requestMatchers("/api/citas/psicologo/*/disponibilidad").hasAnyRole("PACIENTE", "PSICOLOGO", "ADMIN")
                                .requestMatchers("/api/citas/psicologo/*/editar").hasAnyRole("PACIENTE", "PSICOLOGO", "ADMIN")

                                .requestMatchers("/api/citas/psicologo/terapias")
                                .hasAnyRole("PACIENTE","PSICOLOGO", "ADMIN")
                                .requestMatchers("/api/citas/psicologo/*/duracion").hasAnyRole("PSICOLOGO", "ADMIN") // Endpoint para que el psicólogo actualice la duración de una cita
                                .requestMatchers("/api/citas/psicologo/*/agenda").hasAnyRole("PACIENTE","PSICOLOGO", "ADMIN") // Endpoint para que el psicólogo vea su agenda mensual
                                .requestMatchers("/api/citas/psicologo/*/horario-actual").hasAnyRole("PSICOLOGO", "ADMIN") // Endpoint para que el psicólogo vea su agenda mensual
// PACIENTE
                                .requestMatchers("/api/citas/mis-citas").hasRole("PACIENTE")// CANCELAR CITA
                                .requestMatchers("/api/citas/*/cancelar").hasAnyRole("PACIENTE", "PSICOLOGO", "ADMIN")// CAMBIO ESTADO
                                .requestMatchers("/api/citas/cambio/*/estado").hasAnyRole("PSICOLOGO", "ADMIN")//  Otros
                                .requestMatchers("/api/tickets-soporte/**").authenticated()
                                .requestMatchers("/docs/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                                //  Todo lo demás
                                .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, Object> body = Map.of(
                    "codigo", "UNAUTHORIZED",
                    "mensaje", "Token no válido o no proporcionado",
                    "timestamp", LocalDateTime.now().toString()
            );
            new ObjectMapper().writeValue(response.getOutputStream(), body);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, Object> body = Map.of(
                    "codigo", "FORBIDDEN",
                    "mensaje", "No tiene permisos suficientes para acceder a este recurso",
                    "timestamp", LocalDateTime.now().toString()
            );
            new ObjectMapper().writeValue(response.getOutputStream(), body);
        };
    }
}
