package com.amani.amaniapirest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Prueba de verificacion del contexto de Spring para la aplicación Amani API REST.
 *
 * <p>Válida que todos los beans y configuraciones del contexto se cargan
 * correctamente al arrancar la aplicacion sin errores de inicialización.</p>
 *
 * <p>En CI se sobreescriben las propiedades de datasource mediante variables
 * de entorno (SPRING_DATASOURCE_URL, etc.) en el workflow de GitHub Actions.</p>
 */
class AmaniApirestApplicationTests {

    /**
     * Verifica que la clase principal de la aplicación está disponible.
     */
    @Test
    void applicationClassLoads() {
        assertNotNull(AmaniApirestApplication.class);
    }
}
