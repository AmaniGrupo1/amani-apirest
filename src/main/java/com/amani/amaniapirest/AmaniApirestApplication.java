package com.amani.amaniapirest;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Spring Boot Amani API REST.
 *
 * <p>Esta clase configura y arranca la aplicación backend que expone
 * los servicios REST para la gestion de citas, sesiones y pacientes.</p>
 *
 * @author Ivan
 */
@SpringBootApplication
@Log4j
public class AmaniApirestApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmaniApirestApplication.class, args);
        log.info("Amani API arrancada correctamente.");
    }
}
