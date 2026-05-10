-- =============================================================================
-- REPARACION MANUAL — Añade tablas y enums faltantes SIN borrar datos existentes
-- =============================================================================
-- Ejecutar con: psql -h localhost -p 5432 -U postgres -d postgres -f repair_schema.sql
-- =============================================================================

-- Asegurar que trabajamos en el schema correcto
SET search_path TO psicologia_app;

-- ==============================
-- ENUMS (idempotentes)
-- ==============================

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'modalidad_cita') THEN
        CREATE TYPE psicologia_app.modalidad_cita AS ENUM ('PRESENCIAL', 'LLAMADA');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tipo_ticket_soporte') THEN
        CREATE TYPE psicologia_app.tipo_ticket_soporte AS ENUM ('problema', 'pregunta', 'sugerencia');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'categoria_ticket_soporte') THEN
        CREATE TYPE psicologia_app.categoria_ticket_soporte AS ENUM ('tecnico', 'cuenta', 'pago', 'app', 'otro');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'estado_ticket_soporte') THEN
        CREATE TYPE psicologia_app.estado_ticket_soporte AS ENUM ('abierto', 'en_progreso', 'cerrado');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tipo_documento_legal') THEN
        CREATE TYPE psicologia_app.tipo_documento_legal AS ENUM ('terminos', 'privacidad');
    END IF;
END $$;

-- ==============================
-- TABLAS FALTANTES (idempotentes)
-- ==============================

CREATE TABLE IF NOT EXISTS psicologia_app.documentos_legales
(
    id_documento        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo                psicologia_app.tipo_documento_legal NOT NULL,
    titulo              VARCHAR(255)         NOT NULL,
    contenido           TEXT                 NOT NULL,
    icono               VARCHAR(100),
    orden_visualizacion INT                  NOT NULL DEFAULT 0,
    version             VARCHAR(20),
    activo              BOOLEAN              NOT NULL DEFAULT TRUE,
    creado_en           TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en      TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS psicologia_app.tipos_terapia
(
    id_tipo          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre           VARCHAR(100),
    duracion_minutos INT            NOT NULL,
    precio           DECIMAL(10, 2) NOT NULL DEFAULT 0,
    activo           BOOLEAN                 DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS psicologia_app.notificaciones
(
    id_notificacion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario      BIGINT NOT NULL,
    titulo          VARCHAR(200),
    mensaje         TEXT,
    leida           BOOLEAN   DEFAULT FALSE,
    creada_en       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notificaciones_usuario
        FOREIGN KEY (id_usuario)
            REFERENCES psicologia_app.usuarios (id_usuario)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS psicologia_app.tickets_soporte
(
    id_ticket      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo         VARCHAR(200)             NOT NULL,
    descripcion    TEXT                     NOT NULL,
    tipo           psicologia_app.tipo_ticket_soporte      NOT NULL,
    categoria      psicologia_app.categoria_ticket_soporte NOT NULL,
    estado         psicologia_app.estado_ticket_soporte    NOT NULL DEFAULT 'abierto',
    creado_en      TIMESTAMP                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP                         DEFAULT CURRENT_TIMESTAMP,
    cerrado_en     TIMESTAMP,
    id_usuario     BIGINT                   NOT NULL,

    CONSTRAINT fk_tickets_usuario
        FOREIGN KEY (id_usuario)
            REFERENCES psicologia_app.usuarios (id_usuario)
            ON DELETE CASCADE
);

-- ==============================
-- ALTER TABLE citas (compatibilidad)
-- ==============================

ALTER TABLE psicologia_app.citas
    ADD COLUMN IF NOT EXISTS id_tipo_terapia BIGINT;

ALTER TABLE psicologia_app.citas
    ADD COLUMN IF NOT EXISTS modalidad psicologia_app.modalidad_cita DEFAULT 'PRESENCIAL';

-- Insertar un tipo de terapia por defecto si la tabla está vacía
INSERT INTO psicologia_app.tipos_terapia (nombre, duracion_minutos, precio, activo)
SELECT 'Terapia General', 50, 0.00, TRUE
WHERE NOT EXISTS (SELECT 1 FROM psicologia_app.tipos_terapia);

-- Actualizar citas existentes que no tengan tipo_terapia asignado
UPDATE psicologia_app.citas
SET id_tipo_terapia = (SELECT MIN(id_tipo) FROM psicologia_app.tipos_terapia)
WHERE id_tipo_terapia IS NULL;

-- Solo aplicar NOT NULL si ya no hay NULLs
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM psicologia_app.citas WHERE id_tipo_terapia IS NULL) THEN
        ALTER TABLE psicologia_app.citas ALTER COLUMN id_tipo_terapia SET NOT NULL;
    END IF;
END $$;

ALTER TABLE psicologia_app.citas ALTER COLUMN modalidad SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_citas_tipo_terapia'
          AND table_schema = 'psicologia_app'
    ) THEN
        ALTER TABLE psicologia_app.citas
            ADD CONSTRAINT fk_citas_tipo_terapia
                FOREIGN KEY (id_tipo_terapia)
                    REFERENCES psicologia_app.tipos_terapia (id_tipo)
                    ON DELETE RESTRICT;
    END IF;
END $$;

-- ==============================
-- ÍNDICES faltantes
-- ==============================

CREATE INDEX IF NOT EXISTS idx_documentos_tipo
    ON psicologia_app.documentos_legales (tipo);

CREATE INDEX IF NOT EXISTS idx_documentos_activo
    ON psicologia_app.documentos_legales (activo);

CREATE INDEX IF NOT EXISTS idx_citas_tipo_terapia
    ON psicologia_app.citas (id_tipo_terapia);

-- ==============================
-- REGISTRO FLYWAY (evita que V1/V2 se ejecuten de nuevo)
-- ==============================
-- Si usas Flyway, inserta manualmente los metadatos para que no re-ejecute V1/V2.
-- Descomenta las líneas siguientes SOLO si ya tienes datos y no quieres que Flyway
-- intente ejecutar V1 (que haría DROP SCHEMA).

-- INSERT INTO psicologia_app.flyway_schema_history
--     (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success)
-- VALUES
--     (1, '1', 'init schema', 'SQL', 'V1__init_schema.sql', NULL, 'postgres', NOW(), 0, TRUE)
-- ON CONFLICT (installed_rank) DO NOTHING;
--
-- INSERT INTO psicologia_app.flyway_schema_history
--     (installed_rank, version, description, type, script, checksum, installed_on, execution_time, success)
-- VALUES
--     (2, '2', 'add missing entities', 'SQL', 'V2__add_missing_entities.sql', NULL, 'postgres', NOW(), 0, TRUE)
-- ON CONFLICT (installed_rank) DO NOTHING;
