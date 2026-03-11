package com.amani.amaniapirest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de arranque de la aplicación Amani API REST.
 *
 * <p>Inicializa el contexto de Spring Boot y levanta todos los componentes
 * configurados: seguridad, persistencia JPA, WebSocket y capa de servicios.</p>
 */
@SpringBootApplication
public class AmaniApirestApplication {

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args argumentos de línea de comandos pasados al arrancar la JVM
     */
    public static void main(String[] args) {
        SpringApplication.run(AmaniApirestApplication.class, args);
    }
}
