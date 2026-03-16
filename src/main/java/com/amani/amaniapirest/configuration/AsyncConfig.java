package com.amani.amaniapirest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración del pool de hilos asíncronos y del scheduler de tareas.
 *
 * <ul>
 *   <li>{@code @EnableAsync}      — habilita el procesamiento asíncrono de {@code @Async}.</li>
 *   <li>{@code @EnableScheduling} — habilita la ejecución de tareas con {@code @Scheduled}.</li>
 * </ul>
 *
 * <p>El executor {@code taskExecutor} se usa automáticamente por todos los métodos
 * {@code @Async} de la aplicación. Ajustar {@code corePoolSize} y {@code maxPoolSize}
 * según la carga esperada en producción.</p>
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    /**
     * Define el pool de hilos para la ejecución asíncrona.
     *
     * <ul>
     *   <li>Core:  4 hilos siempre activos.</li>
     *   <li>Max:  10 hilos en picos de carga.</li>
     *   <li>Cola: 500 tareas en espera antes de rechazar.</li>
     * </ul>
     *
     * @return executor configurado para tareas asíncronas
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("amani-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}

