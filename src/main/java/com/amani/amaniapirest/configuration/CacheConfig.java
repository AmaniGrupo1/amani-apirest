package com.amani.amaniapirest.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuración de caché con Caffeine para el historial reciente de mensajes de chat.
 *
 * <p>Se define un caché {@code chatMessages} con TTL de 30 segundos y un máximo
 * de 500 entradas. Esto reduce las lecturas repetitivas a Firebase RTDB para
 * el mismo historial en un periodo corto.</p>
 *
 * <p><strong>No</strong> se cachean datos de autenticación ni autorización.</p>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache manager basado en Caffeine con configuración por defecto.
     *
     * @return CacheManager configurado
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .maximumSize(500));
        return cacheManager;
    }
}
