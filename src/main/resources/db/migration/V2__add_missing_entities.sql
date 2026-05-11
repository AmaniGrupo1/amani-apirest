-- =============================================================================
-- V2 — Añade tablas y enums faltantes para compatibilidad con amani.sql legacy
-- =============================================================================
-- Objetivo: Cubrir el gap entre el schema inicial (amani.sql) y las entidades
-- JPA actuales: DocumentoLegal, TiposTerapia, Notificacion, TicketSoporte.
-- También corrige la tabla citas (faltan id_tipo_terapia y modalidad).
-- =============================================================================

-- ==============================
-- ENUMS (idempotentes)
-- ==============================

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'modalidad_cita') THEN
        CREATE TYPE modalidad_cita AS ENUM ('PRESENCIAL', 'LLAMADA');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tipo_ticket_soporte') THEN
        CREATE TYPE tipo_ticket_soporte AS ENUM ('problema', 'pregunta', 'sugerencia');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'categoria_ticket_soporte') THEN
        CREATE TYPE categoria_ticket_soporte AS ENUM ('tecnico', 'cuenta', 'pago', 'app', 'otro');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'estado_ticket_soporte') THEN
        CREATE TYPE estado_ticket_soporte AS ENUM ('abierto', 'en_progreso', 'cerrado');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tipo_documento_legal') THEN
        CREATE TYPE tipo_documento_legal AS ENUM ('terminos', 'privacidad');
    END IF;
END $$;

-- ==============================
-- TABLAS (idempotentes)
-- ==============================

CREATE TABLE IF NOT EXISTS psicologia_app.tipos_terapia
(
    id_tipo          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre           VARCHAR(100),
    duracion_minutos INT            NOT NULL,
    precio           DECIMAL(10, 2) NOT NULL DEFAULT 0,
    activo           BOOLEAN                 DEFAULT TRUE
);

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
-- ALTER TABLE citas (compatibilidad legacy)
-- ==============================
-- Si la BD fue creada con amani.sql antiguo, faltan columnas en citas.
-- Se añaden de forma segura con valores por defecto.

ALTER TABLE psicologia_app.citas
    ADD COLUMN IF NOT EXISTS id_tipo_terapia BIGINT;

ALTER TABLE psicologia_app.citas
    ADD COLUMN IF NOT EXISTS modalidad psicologia_app.modalidad_cita DEFAULT 'PRESENCIAL';

-- Insertar un tipo de terapia por defecto si la tabla está vacía,
-- para poder asignar FK a citas existentes.
INSERT INTO psicologia_app.tipos_terapia (nombre, duracion_minutos, precio, activo)
SELECT 'Terapia General', 50, 0.00, TRUE
WHERE NOT EXISTS (SELECT 1 FROM psicologia_app.tipos_terapia);

-- Actualizar citas existentes que no tengan tipo_terapia asignado
UPDATE psicologia_app.citas
SET id_tipo_terapia = (SELECT MIN(id_tipo) FROM psicologia_app.tipos_terapia)
WHERE id_tipo_terapia IS NULL;

-- Aplicar restricciones solo si ya no hay NULLs
-- (Flyway fallará en entornos inconsistentes, lo cual es deseable)
ALTER TABLE psicologia_app.citas
    ALTER COLUMN modalidad SET NOT NULL;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM psicologia_app.citas WHERE id_tipo_terapia IS NULL) THEN
        RAISE EXCEPTION 'Existen citas sin id_tipo_terapia; corrige los datos antes de aplicar esta migración.';
    END IF;
END $$;

ALTER TABLE psicologia_app.citas
    ALTER COLUMN id_tipo_terapia SET NOT NULL;

ALTER TABLE psicologia_app.citas
    ADD CONSTRAINT IF NOT EXISTS fk_citas_tipo_terapia
        FOREIGN KEY (id_tipo_terapia)
            REFERENCES psicologia_app.tipos_terapia (id_tipo)
            ON DELETE RESTRICT;

-- ==============================
-- ÍNDICES faltantes
-- ==============================

CREATE INDEX IF NOT EXISTS idx_documentos_tipo
    ON psicologia_app.documentos_legales (tipo);

CREATE INDEX IF NOT EXISTS idx_documentos_activo
    ON psicologia_app.documentos_legales (activo);

CREATE INDEX IF NOT EXISTS idx_citas_tipo_terapia
    ON psicologia_app.citas (id_tipo_terapia);
