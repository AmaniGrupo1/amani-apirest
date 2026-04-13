package com.amani.amaniapirest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Prueba de verificacion del contexto de Spring para la aplicación Amani API REST.
 *
 * <p>Válida que todos los beans y configuraciones del contexto se cargan
 * correctamente al arrancar la aplicacion sin errores de inicialización.</p>
 *
 * <p>En CI se sobreescriben las propiedades de datasource mediante variables
 * de entorno (SPRING_DATASOURCE_URL, etc.) en el workflow de GitHub Actions.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class AmaniApirestApplicationTests {

    /**
     * Verifica que el contexto de Spring se carga sin errores.
     */
    @Test
    void contextLoads() {
    }
}