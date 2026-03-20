package com.amani.amaniapirest.configuration;

import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Log4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        boolean adminExiste = usuarioRepository.existsByRol((RolUsuario.admin));

        if (!adminExiste) {
            Usuario admin = Usuario.builder()
                    .nombre("Admin")
                    .apellido("Principal")
                    .email("admin@amani.com")
                    .password(passwordEncoder.encode("admin1234"))
                    .rol(RolUsuario.admin)
                    .build();

            usuarioRepository.save(admin);
            log.info(">>> Admin inicial creado: admin@amani.com / admin1234");
        }
    }
}