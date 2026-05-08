-- =============================================================================
-- Migracion: Añade columnas Stripe a la tabla pagos
-- =============================================================================
-- Ejecutar en PostgreSQL si prefieres migracion manual en lugar de Hibernate update:
--   psql -h localhost -U postgres -d postgres -c "\i /path/to/este/archivo"
-- =============================================================================

SET search_path TO psicologia_app;

ALTER TABLE pagos
    ADD COLUMN IF NOT EXISTS stripe_payment_intent_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS stripe_charge_id         VARCHAR(255),
    ADD COLUMN IF NOT EXISTS idempotency_key          VARCHAR(255),
    ADD COLUMN IF NOT EXISTS currency                 VARCHAR(3) NOT NULL DEFAULT 'EUR',
    ADD COLUMN IF NOT EXISTS fecha_actualizacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Actualizar registros existentes para que tengan un valor por defecto valido
UPDATE pagos SET currency = 'EUR' WHERE currency IS NULL;
