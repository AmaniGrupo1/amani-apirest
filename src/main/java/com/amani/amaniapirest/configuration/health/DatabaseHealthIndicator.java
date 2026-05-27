package com.amani.amaniapirest.configuration.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Health indicator personalizado que valida la conexión a PostgreSQL
 * y la existencia del schema {@code psicologia_app}.
 *
 * <p>Se muestra en {@code /actuator/health} bajo la clave {@code database}
 * y es usado por Prometheus para las alertas de base de datos.</p>
 */
@Slf4j
@Component("database")
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            // Verificar conectividad básica
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result == null || result != 1) {
                return Health.down()
                        .withDetail("error", "La consulta de comprobación no devolvió el resultado esperado")
                        .build();
            }

            // Verificar existencia del schema de la aplicación
            Integer schemaExists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = 'psicologia_app'",
                    Integer.class
            );

            // Obtener estadísticas de conexión del pool HikariCP
            Long activeConnections = jdbcTemplate.queryForObject(
                    "SELECT count(*) FROM pg_stat_activity WHERE datname = current_database()",
                    Long.class
            );

            return Health.up()
                    .withDetail("schema", schemaExists != null && schemaExists > 0 ? "psicologia_app OK" : "MISSING")
                    .withDetail("activeConnections", activeConnections)
                    .withDetail("database", "PostgreSQL")
                    .build();

        } catch (Exception ex) {
            log.error("[Health] Fallo en la comprobación de salud de la base de datos", ex);
            return Health.down()
                    .withDetail("error", ex.getMessage())
                    .build();
        }
    }
}
