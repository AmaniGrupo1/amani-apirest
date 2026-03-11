package com.amani.amaniapirest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de seguridad de la aplicacion.
 *
 * <p>Define los beans de seguridad: el codificador de contrasenas BCrypt y
 * la cadena de filtros HTTP que permite todas las rutas y deshabilita CSRF
 * para facilitar el uso de la API REST.</p>
 */
@Configuration
public class SecurityConfig {

    /**
     * Proporciona un {@link PasswordEncoder} basado en BCrypt.
     *
     * @return instancia de {@link BCryptPasswordEncoder} para hashear contrasenas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * Permite todas las peticiones sin autenticacion y deshabilita CSRF.
     *
     * @param http objeto {@link HttpSecurity} para construir la configuracion de seguridad
     * @return {@link SecurityFilterChain} con la politica de seguridad definida
     * @throws Exception si ocurre un error al construir la cadena de filtros
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
