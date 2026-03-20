package com.amani.amaniapirest.configuration;

import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Componente que se ejecuta al arrancar la aplicacion para garantizar que
 * exista al menos un usuario administrador en la base de datos.
 *
 * <p>Si no se encuentra ningun usuario con rol {@link com.amani.amaniapirest.enums.RolUsuario#admin},
 * se crea uno con credenciales por defecto ({@code admin@amani.com / admin1234}).</p>
 */
@Log4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Verifica si existe un admin en el sistema y, en caso contrario, crea uno
     * con credenciales por defecto.
     *
     * @param args argumentos de linea de comandos (no utilizados).
     */
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