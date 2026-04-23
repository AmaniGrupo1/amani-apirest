package com.amani.amaniapirest.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                                //  Públicos
                                .requestMatchers("/auth/login", "/auth/register-paciente").permitAll()
                                .requestMatchers("/api/situaciones").permitAll()
                                .requestMatchers("/api/psicologo/pacientes/*/psicologo").hasRole("PACIENTE") // Endpoint para que el paciente vea su psicólogo asignado

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
                                .requestMatchers("/api/citas/psicologo/*/horario").hasAnyRole("ADMIN", "PSICOLOGO")
                                .requestMatchers("/api/psicologo/pacientes/getAll/**").hasRole("PSICOLOGO")
                                .requestMatchers("/auth/registrar/pacienteDesde/psicologo").hasRole("PSICOLOGO")
                                .requestMatchers("/api/citas/psicologo/cita").hasAnyRole("PACIENTE","PSICOLOGO","ADMIN") // Endpoint para que el psicólogo cree una cita para un paciente asignado

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
                                .requestMatchers("/docs/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                                //  Todo lo demás
                                .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
