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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


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
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://10.0.2.2",       // Emulador Android
                "http://localhost",     // localhost
                "http://127.0.0.1",     // 127.0.0.1
                "http://192.168.1.*"    // Redes locales (ajusta si es necesario)
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        //  Públicos
                        .requestMatchers("/auth/login", "/auth/register-paciente").permitAll()
                        .requestMatchers("/api/situaciones").permitAll()
                        .requestMatchers("/api/psicologo/pacientes/*/psicologo").hasRole( "PACIENTE") // Endpoint para que el paciente vea su psicólogo asignado

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
                        //  PSICOLOGO + ADMIN
                        .requestMatchers("/api/psicologo/**").hasAnyRole("ADMIN", "PSICOLOGO")
                        .requestMatchers("/api/psicologo/pacientes/getAll/**").hasRole( "PSICOLOGO")
                        // Permitir a psicólogos y admin acceder a cualquier endpoint de心理学家


                        //  Otros
                        .requestMatchers("/docs/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        //  Todo lo demás
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
