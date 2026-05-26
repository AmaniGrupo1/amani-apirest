-- ============================================================
-- AMANI · PostgreSQL Schema + Seed
-- ============================================================

DROP SCHEMA IF EXISTS psicologia_app CASCADE;
CREATE SCHEMA psicologia_app;
SET search_path TO psicologia_app;

-- ============================================================
-- ENUMS
-- ============================================================

CREATE TYPE rol_usuario AS ENUM (
    'admin',
    'psicologo',
    'paciente'
    );

CREATE TYPE estado_cita AS ENUM (
    'pendiente',
    'confirmada',
    'cancelada',
    'completada'
    );

CREATE TYPE estado_pago AS ENUM (
    'PENDIENTE',
    'PAGADO',
    'FALLIDO',
    'REEMBOLSADO'
    );

CREATE TYPE metodo_pago AS ENUM (
    'EFECTIVO',
    'TARJETA'
    );

CREATE TYPE modalidad_cita AS ENUM (
    'PRESENCIAL',
    'LLAMADA'
    );

CREATE TYPE tipo_ticket_soporte AS ENUM (
    'problema',
    'pregunta',
    'sugerencia'
    );

CREATE TYPE categoria_ticket_soporte AS ENUM (
    'tecnico',
    'cuenta',
    'pago',
    'app',
    'otro'
    );

CREATE TYPE estado_ticket_soporte AS ENUM (
    'abierto',
    'en_progreso',
    'cerrado'
    );

CREATE TYPE tipo_documento_legal AS ENUM (
    'terminos',
    'privacidad'
    );

-- ============================================================
-- USUARIOS
-- ============================================================

CREATE TABLE usuarios (
                          id_usuario             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          nombre                 VARCHAR(100)  NOT NULL,
                          apellido               VARCHAR(100),
                          email                  VARCHAR(150)  NOT NULL UNIQUE,
                          password               VARCHAR(255)  NOT NULL,
                          dni                    VARCHAR(20)   UNIQUE,
                          rol                    rol_usuario   NOT NULL,
                          foto_perfil_url        TEXT,
                          activo                 BOOLEAN       NOT NULL DEFAULT TRUE,
                          notificaciones_activas BOOLEAN       NOT NULL DEFAULT TRUE,
                          fcm_token              VARCHAR(255),
                          fecha_registro         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          fecha_baja             TIMESTAMP
);

CREATE INDEX idx_usuarios_email ON usuarios (email);

-- ============================================================
-- PSICÓLOGOS
-- ============================================================

CREATE TABLE psicologos (
                            id_psicologo      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            id_usuario        BIGINT        NOT NULL UNIQUE,
                            especialidad      VARCHAR(150),
                            experiencia       INT           CHECK (experiencia >= 0),
                            descripcion       TEXT,
                            licencia          VARCHAR(100),
                            duracion_default  INT           NOT NULL DEFAULT 50,
                            created_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_psicologo_usuario
                                FOREIGN KEY (id_usuario)
                                    REFERENCES usuarios (id_usuario)
                                    ON DELETE CASCADE
);

-- ============================================================
-- PACIENTES
-- ============================================================

CREATE TABLE pacientes (
                           id_paciente      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           id_usuario       BIGINT      NOT NULL UNIQUE,
                           id_psicologo     BIGINT,
                           fecha_nacimiento DATE,
                           genero           VARCHAR(30),
                           telefono         VARCHAR(30),
                           created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_paciente_usuario
                               FOREIGN KEY (id_usuario)
                                   REFERENCES usuarios (id_usuario)
                                   ON DELETE CASCADE
);

-- FK diferida para evitar dependencia circular
ALTER TABLE pacientes
    ADD CONSTRAINT fk_paciente_psicologo
        FOREIGN KEY (id_psicologo)
            REFERENCES psicologos (id_psicologo)
            ON DELETE SET NULL;

-- ============================================================
-- RELACIÓN PSICÓLOGO ↔ PACIENTE
-- ============================================================

CREATE TABLE psicologo_paciente (
                                    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                    id_paciente  BIGINT    NOT NULL,
                                    id_psicologo BIGINT    NOT NULL,
                                    fecha_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    fecha_fin    TIMESTAMP,

                                    CONSTRAINT fk_relacion_paciente
                                        FOREIGN KEY (id_paciente)
                                            REFERENCES pacientes (id_paciente)
                                            ON DELETE CASCADE,

                                    CONSTRAINT fk_relacion_psicologo
                                        FOREIGN KEY (id_psicologo)
                                            REFERENCES psicologos (id_psicologo)
                                            ON DELETE CASCADE
);

-- ============================================================
-- TIPOS DE TERAPIA
-- ============================================================

CREATE TABLE tipos_terapia (
                               id_tipo           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               nombre            VARCHAR(100)   NOT NULL,
                               duracion_minutos  INT            NOT NULL,
                               precio            DECIMAL(10, 2) NOT NULL DEFAULT 0,
                               activo            BOOLEAN        DEFAULT TRUE
);

-- ============================================================
-- CITAS
-- ============================================================

CREATE TABLE citas (
                       id_cita          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       id_paciente      BIGINT         NOT NULL,
                       id_psicologo     BIGINT         NOT NULL,
                       id_tipo_terapia  BIGINT         NOT NULL,
                       start_datetime   TIMESTAMP      NOT NULL,
                       duration_minutes INT            DEFAULT 50,
                       modalidad        modalidad_cita NOT NULL DEFAULT 'PRESENCIAL',
                       estado           estado_cita    NOT NULL DEFAULT 'pendiente',
                       motivo           TEXT,
                       created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_cita_paciente
                           FOREIGN KEY (id_paciente)
                               REFERENCES pacientes (id_paciente)
                               ON DELETE RESTRICT,

                       CONSTRAINT fk_cita_psicologo
                           FOREIGN KEY (id_psicologo)
                               REFERENCES psicologos (id_psicologo)
                               ON DELETE RESTRICT,

                       CONSTRAINT fk_cita_tipo_terapia
                           FOREIGN KEY (id_tipo_terapia)
                               REFERENCES tipos_terapia (id_tipo)
                               ON DELETE RESTRICT
);

CREATE INDEX idx_citas_start          ON citas (start_datetime);
CREATE INDEX idx_citas_psicologo_fecha ON citas (id_psicologo, start_datetime);

-- ============================================================
-- PAGOS
-- ============================================================

CREATE TABLE pagos (
                       id_pago                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       id_cita                   BIGINT         NOT NULL UNIQUE,
                       metodo_pago               metodo_pago    NOT NULL,
                       estado_pago               estado_pago    NOT NULL DEFAULT 'PENDIENTE',
                       monto                     DECIMAL(10, 2) NOT NULL,
                       currency                  VARCHAR(3)     NOT NULL DEFAULT 'EUR',
                       stripe_payment_intent_id  VARCHAR(255),
                       stripe_charge_id          VARCHAR(255),
                       idempotency_key           VARCHAR(255),
                       fecha_creacion            TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
                       fecha_pago                TIMESTAMP,
                       fecha_actualizacion       TIMESTAMP,

                       CONSTRAINT fk_pago_cita
                           FOREIGN KEY (id_cita)
                               REFERENCES citas (id_cita)
                               ON DELETE CASCADE
);

-- ============================================================
-- SESIONES
-- ============================================================

CREATE TABLE sesiones (
                          id_sesion        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          id_cita          BIGINT UNIQUE,
                          id_paciente      BIGINT,
                          id_psicologo     BIGINT,
                          session_date     TIMESTAMP,
                          duration_minutes INT,
                          notas            TEXT,
                          recomendaciones  TEXT,
                          created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_sesion_cita
                              FOREIGN KEY (id_cita)
                                  REFERENCES citas (id_cita)
                                  ON DELETE CASCADE,

                          CONSTRAINT fk_sesion_paciente
                              FOREIGN KEY (id_paciente)
                                  REFERENCES pacientes (id_paciente)
                                  ON DELETE CASCADE,

                          CONSTRAINT fk_sesion_psicologo
                              FOREIGN KEY (id_psicologo)
                                  REFERENCES psicologos (id_psicologo)
                                  ON DELETE SET NULL
);

-- ============================================================
-- HORARIOS
-- ============================================================

CREATE TABLE horario_psicologo (
                                   id_horario   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   id_psicologo BIGINT    NOT NULL,
                                   dia_semana   SMALLINT  NOT NULL,
                                   hora_inicio  TIME      NOT NULL,
                                   hora_fin     TIME      NOT NULL,
                                   activo       BOOLEAN   NOT NULL DEFAULT TRUE,
                                   created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CHECK (dia_semana BETWEEN 0 AND 6),
                                   CHECK (hora_fin > hora_inicio),

                                   CONSTRAINT fk_horario_psicologo
                                       FOREIGN KEY (id_psicologo)
                                           REFERENCES psicologos (id_psicologo)
                                           ON DELETE CASCADE
);

CREATE INDEX idx_horario_psicologo ON horario_psicologo (id_psicologo, dia_semana);

-- ============================================================
-- BLOQUEOS DE AGENDA
-- ============================================================

CREATE TABLE bloqueos_agenda (
                                 id_bloqueo   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                 id_psicologo BIGINT NOT NULL,
                                 fecha        DATE   NOT NULL,
                                 hora_inicio  TIME,
                                 hora_fin     TIME,
                                 motivo       TEXT,
                                 created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CHECK (
                                     (hora_inicio IS NULL AND hora_fin IS NULL)
                                         OR (hora_inicio IS NOT NULL AND hora_fin IS NOT NULL AND hora_fin > hora_inicio)
                                     ),

                                 CONSTRAINT fk_bloqueo_psicologo
                                     FOREIGN KEY (id_psicologo)
                                         REFERENCES psicologos (id_psicologo)
                                         ON DELETE CASCADE
);

CREATE INDEX idx_bloqueos_psicologo ON bloqueos_agenda (id_psicologo, fecha);

-- ============================================================
-- HISTORIAL CLÍNICO
-- ============================================================

CREATE TABLE historial_clinico (
                                   id_history    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   id_paciente   BIGINT       NOT NULL,
                                   titulo        VARCHAR(200),
                                   diagnostico   TEXT,
                                   tratamiento   TEXT,
                                   observaciones TEXT,
                                   creado_en     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_historial_paciente
                                       FOREIGN KEY (id_paciente)
                                           REFERENCES pacientes (id_paciente)
                                           ON DELETE CASCADE
);

-- ============================================================
-- DIARIO EMOCIONAL
-- ============================================================

CREATE TABLE diario_emociones (
                                  id_diario   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                  id_paciente BIGINT      NOT NULL,
                                  titulo      TEXT,
                                  fecha       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
                                  emocion     VARCHAR(100),
                                  intensidad  INT         CHECK (intensidad BETWEEN 1 AND 10),
                                  nota        TEXT,

                                  CONSTRAINT fk_diario_paciente
                                      FOREIGN KEY (id_paciente)
                                          REFERENCES pacientes (id_paciente)
                                          ON DELETE CASCADE
);

-- ============================================================
-- PROGRESO EMOCIONAL
-- ============================================================

CREATE TABLE progreso_emocional (
                                    id_progress    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                    id_paciente    BIGINT NOT NULL,
                                    fecha          DATE   NOT NULL,
                                    nivel_estres   INT,
                                    nivel_ansiedad INT,
                                    nivel_animo    INT,
                                    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT fk_progreso_paciente
                                        FOREIGN KEY (id_paciente)
                                            REFERENCES pacientes (id_paciente)
                                            ON DELETE CASCADE
);

-- ============================================================
-- SITUACIONES CLÍNICAS
-- ============================================================

CREATE TABLE situaciones (
                             id_situacion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             nombre       VARCHAR(150) NOT NULL,
                             categoria    VARCHAR(100),
                             descripcion  TEXT,
                             activo       BOOLEAN   DEFAULT TRUE,
                             created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE paciente_situacion (
                                    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                    id_paciente   BIGINT    NOT NULL,
                                    id_situacion  BIGINT    NOT NULL,
                                    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT fk_psit_paciente
                                        FOREIGN KEY (id_paciente)
                                            REFERENCES pacientes (id_paciente)
                                            ON DELETE CASCADE,

                                    CONSTRAINT fk_psit_situacion
                                        FOREIGN KEY (id_situacion)
                                            REFERENCES situaciones (id_situacion)
                                            ON DELETE CASCADE
);

-- ============================================================
-- MENSAJERÍA
-- ============================================================

CREATE TABLE mensajes (
                          id_mensaje  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          sender_id   BIGINT,
                          receiver_id BIGINT,
                          id_cita     BIGINT,
                          mensaje     TEXT      NOT NULL,
                          enviado_en  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          leido       BOOLEAN   DEFAULT FALSE,

                          CONSTRAINT fk_mensaje_sender
                              FOREIGN KEY (sender_id)
                                  REFERENCES usuarios (id_usuario)
                                  ON DELETE SET NULL,

                          CONSTRAINT fk_mensaje_receiver
                              FOREIGN KEY (receiver_id)
                                  REFERENCES usuarios (id_usuario)
                                  ON DELETE SET NULL,

                          CONSTRAINT fk_mensaje_cita
                              FOREIGN KEY (id_cita)
                                  REFERENCES citas (id_cita)
                                  ON DELETE SET NULL
);

CREATE INDEX idx_mensajes_sender   ON mensajes (sender_id, enviado_en);
CREATE INDEX idx_mensajes_receiver ON mensajes (receiver_id, leido);

-- ============================================================
-- CUESTIONARIOS
-- ============================================================

CREATE TABLE preguntas (
                           id_pregunta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           texto       TEXT      NOT NULL,
                           tipo        VARCHAR(50) DEFAULT 'texto',
                           creado_en   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE opciones (
                          id_opcion   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          id_pregunta BIGINT       NOT NULL,
                          texto       VARCHAR(200) NOT NULL,
                          valor       INT,

                          CONSTRAINT fk_opcion_pregunta
                              FOREIGN KEY (id_pregunta)
                                  REFERENCES preguntas (id_pregunta)
                                  ON DELETE CASCADE
);

CREATE TABLE respuestas (
                            id_respuesta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            id_paciente  BIGINT    NOT NULL,
                            id_pregunta  BIGINT    NOT NULL,
                            id_opcion    BIGINT,
                            texto        TEXT,
                            creado_en    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_respuesta_paciente
                                FOREIGN KEY (id_paciente)
                                    REFERENCES pacientes (id_paciente)
                                    ON DELETE CASCADE,

                            CONSTRAINT fk_respuesta_pregunta
                                FOREIGN KEY (id_pregunta)
                                    REFERENCES preguntas (id_pregunta)
                                    ON DELETE CASCADE,

                            CONSTRAINT fk_respuesta_opcion
                                FOREIGN KEY (id_opcion)
                                    REFERENCES opciones (id_opcion)
                                    ON DELETE SET NULL
);

-- ============================================================
-- DATOS COMPLEMENTARIOS
-- ============================================================

CREATE TABLE direcciones (
                             id_direccion  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             id_paciente   BIGINT      NOT NULL,
                             calle         VARCHAR(150),
                             ciudad        VARCHAR(100),
                             provincia     VARCHAR(100),
                             codigo_postal VARCHAR(20),
                             pais          VARCHAR(100),
                             descripcion   TEXT,

                             CONSTRAINT fk_direccion_paciente
                                 FOREIGN KEY (id_paciente)
                                     REFERENCES pacientes (id_paciente)
                                     ON DELETE CASCADE
);

CREATE TABLE ajustes (
                         id_ajuste     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         id_usuario    BIGINT      NOT NULL,
                         idioma        VARCHAR(10) DEFAULT 'es',
                         notificaciones BOOLEAN    DEFAULT TRUE,
                         tema          BOOLEAN     NOT NULL,
                         timezone      VARCHAR(100) DEFAULT 'Europe/Madrid',
                         updated_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_ajuste_usuario
                             FOREIGN KEY (id_usuario)
                                 REFERENCES usuarios (id_usuario)
                                 ON DELETE CASCADE
);

CREATE TABLE archivos (
                          id_archivo  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          id_sesion   BIGINT       NOT NULL,
                          nombre      VARCHAR(200),
                          tipo_mime   VARCHAR(100),
                          datos       BYTEA        NOT NULL,
                          creado_en   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_archivo_sesion
                              FOREIGN KEY (id_sesion)
                                  REFERENCES sesiones (id_sesion)
                                  ON DELETE CASCADE
);

CREATE INDEX idx_archivos_sesion ON archivos (id_sesion);

-- ============================================================
-- TUTORES (menores de edad)
-- ============================================================

CREATE TABLE tutores (
                         id_tutor    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         id_paciente BIGINT      NOT NULL,
                         nombre      VARCHAR(100),
                         telefono    VARCHAR(30),
                         email       VARCHAR(150),
                         dni         VARCHAR(20),
                         tipo        VARCHAR(20),

                         CONSTRAINT fk_tutor_paciente
                             FOREIGN KEY (id_paciente)
                                 REFERENCES pacientes (id_paciente)
                                 ON DELETE CASCADE
);

-- ============================================================
-- CONSENTIMIENTOS RGPD
-- ============================================================

CREATE TABLE consentimientos (
                                 id_consentimiento      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                 id_paciente            BIGINT      NOT NULL,
                                 fecha_aceptacion       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
                                 acepta_terminos        BOOLEAN     NOT NULL DEFAULT FALSE,
                                 version_documento      VARCHAR(20) NOT NULL,
                                 acepta_videoconferencia BOOLEAN    DEFAULT FALSE,
                                 acepta_comunicacion    BOOLEAN     DEFAULT FALSE,

                                 CONSTRAINT fk_consentimiento_paciente
                                     FOREIGN KEY (id_paciente)
                                         REFERENCES pacientes (id_paciente)
                                         ON DELETE CASCADE
);

-- ============================================================
-- NOTIFICACIONES
-- ============================================================

CREATE TABLE notificaciones (
                                id_notificacion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                id_usuario      BIGINT       NOT NULL,
                                titulo          VARCHAR(200),
                                mensaje         TEXT,
                                leida           BOOLEAN   DEFAULT FALSE,
                                creada_en       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_notificacion_usuario
                                    FOREIGN KEY (id_usuario)
                                        REFERENCES usuarios (id_usuario)
                                        ON DELETE CASCADE
);

-- ============================================================
-- TICKETS DE SOPORTE
-- ============================================================

CREATE TABLE tickets_soporte (
                                 id_ticket      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                 titulo         VARCHAR(200)             NOT NULL,
                                 descripcion    TEXT                     NOT NULL,
                                 tipo           tipo_ticket_soporte      NOT NULL,
                                 categoria      categoria_ticket_soporte NOT NULL,
                                 estado         estado_ticket_soporte    NOT NULL DEFAULT 'abierto',
                                 creado_en      TIMESTAMP                NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 actualizado_en TIMESTAMP                DEFAULT CURRENT_TIMESTAMP,
                                 cerrado_en     TIMESTAMP,
                                 id_usuario     BIGINT                   NOT NULL,

                                 CONSTRAINT fk_ticket_usuario
                                     FOREIGN KEY (id_usuario)
                                         REFERENCES usuarios (id_usuario)
                                         ON DELETE CASCADE
);

CREATE INDEX idx_tickets_usuario ON tickets_soporte (id_usuario);
CREATE INDEX idx_tickets_estado  ON tickets_soporte (estado);
CREATE INDEX idx_tickets_creado  ON tickets_soporte (creado_en);

-- ============================================================
-- DOCUMENTOS LEGALES
-- ============================================================

CREATE TABLE documentos_legales (
                                    id_documento        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                    tipo                tipo_documento_legal NOT NULL,
                                    titulo              VARCHAR(255)         NOT NULL,
                                    contenido           TEXT                 NOT NULL,
                                    icono               VARCHAR(100),
                                    orden_visualizacion INT                  NOT NULL DEFAULT 0,
                                    version             VARCHAR(20),
                                    activo              BOOLEAN              NOT NULL DEFAULT TRUE,
                                    creado_en           TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    actualizado_en      TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_documentos_tipo   ON documentos_legales (tipo);
CREATE INDEX idx_documentos_activo ON documentos_legales (activo);

-- ============================================================
-- VISTAS
-- ============================================================

CREATE OR REPLACE VIEW vista_agenda_psicologo AS
SELECT
    p.id_psicologo,
    u.nombre || ' ' || COALESCE(u.apellido, '') AS nombre_psicologo,
    c.start_datetime::DATE                       AS fecha,
    c.start_datetime::TIME                       AS hora_inicio,
    (c.start_datetime + (c.duration_minutes || ' minutes')::INTERVAL)::TIME AS hora_fin,
    'cita'          AS tipo,
    c.estado::TEXT  AS detalle,
    c.id_cita       AS referencia_id
FROM psicologos p
         JOIN usuarios u ON u.id_usuario = p.id_usuario
         JOIN citas    c ON c.id_psicologo = p.id_psicologo
WHERE c.estado IN ('pendiente', 'confirmada')

UNION ALL

SELECT
    b.id_psicologo,
    u.nombre || ' ' || COALESCE(u.apellido, '') AS nombre_psicologo,
    b.fecha,
    COALESCE(b.hora_inicio, '00:00'::TIME)       AS hora_inicio,
    COALESCE(b.hora_fin,    '23:59'::TIME)       AS hora_fin,
    'bloqueo'                                    AS tipo,
    COALESCE(b.motivo, 'Día bloqueado')          AS detalle,
    b.id_bloqueo                                 AS referencia_id
FROM bloqueos_agenda b
         JOIN psicologos p ON p.id_psicologo = b.id_psicologo
         JOIN usuarios   u ON u.id_usuario   = p.id_usuario

ORDER BY fecha, hora_inicio;

CREATE OR REPLACE VIEW vista_pacientes_psicologo AS
SELECT
    pp.id_psicologo,
    up.nombre || ' ' || COALESCE(up.apellido, '') AS nombre_psicologo,
    pa.id_paciente,
    ua.nombre || ' ' || COALESCE(ua.apellido, '') AS nombre_paciente,
    ua.email,
    pa.telefono,
    pa.fecha_nacimiento,
    pa.genero,
    pp.fecha_inicio
FROM psicologo_paciente pp
         JOIN psicologos ps ON ps.id_psicologo = pp.id_psicologo
         JOIN usuarios   up ON up.id_usuario   = ps.id_usuario
         JOIN pacientes  pa ON pa.id_paciente  = pp.id_paciente
         JOIN usuarios   ua ON ua.id_usuario   = pa.id_usuario
WHERE pp.fecha_fin IS NULL
ORDER BY pp.id_psicologo, ua.apellido, ua.nombre;

-- ============================================================
-- SEED — USUARIOS
-- Password demo: Pandora01 (BCrypt)
-- ============================================================

INSERT INTO usuarios (nombre, apellido, email, password, dni, rol)
VALUES
    ('Admin',  'Sistema',  'admin@amani.com',  '$2a$10$8gVAL.7kqmx4FtyNwd52WOipu2ncuFWcXCnHXwwcKH9q9fL/LIaUm', '00000000A', 'admin'),
    ('Maria',  'Gonzalez', 'maria@amani.com',  '$2a$10$8gVAL.7kqmx4FtyNwd52WOipu2ncuFWcXCnHXwwcKH9q9fL/LIaUm', '11111111B', 'psicologo'),
    ('Carlos', 'Ruiz',     'carlos@amani.com', '$2a$10$8gVAL.7kqmx4FtyNwd52WOipu2ncuFWcXCnHXwwcKH9q9fL/LIaUm', '22222222C', 'psicologo'),
    ('Lucia',  'Martinez', 'lucia@gmail.com',  '$2a$10$8gVAL.7kqmx4FtyNwd52WOipu2ncuFWcXCnHXwwcKH9q9fL/LIaUm', '33333333D', 'paciente'),
    ('David',  'Sanchez',  'david@gmail.com',  '$$2a$10$8gVAL.7kqmx4FtyNwd52WOipu2ncuFWcXCnHXwwcKH9q9fL/LIaUm', '44444444E', 'paciente'),
    ('Elena',  'Torres',   'elena@gmail.com',  '$$2a$10$8gVAL.7kqmx4FtyNwd52WOipu2ncuFWcXCnHXwwcKH9q9fL/LIaUm', '55555555F', 'paciente');

-- ============================================================
-- SEED — PSICÓLOGOS
-- ============================================================

INSERT INTO psicologos (id_usuario, especialidad, experiencia, descripcion, licencia, duracion_default)
VALUES
    (2, 'Ansiedad y estrés',  8,  'Especialista en ansiedad y terapia cognitivo conductual.', 'PSY-2024-001', 50),
    (3, 'Terapia familiar',   12, 'Psicólogo clínico enfocado en relaciones familiares.',     'PSY-2024-002', 60);

-- ============================================================
-- SEED — PACIENTES
-- ============================================================

INSERT INTO pacientes (id_usuario, id_psicologo, fecha_nacimiento, genero, telefono)
VALUES
    (4, 1, '1999-05-10', 'Femenino',  '+34611111111'),
    (5, 1, '1995-11-20', 'Masculino', '+34622222222'),
    (6, 2, '2001-02-15', 'Femenino',  '+34633333333');

-- ============================================================
-- SEED — RELACIÓN PSICÓLOGO ↔ PACIENTE
-- ============================================================

INSERT INTO psicologo_paciente (id_paciente, id_psicologo)
VALUES
    (1, 1),
    (2, 1),
    (3, 2);

-- ============================================================
-- SEED — TIPOS DE TERAPIA
-- ============================================================

INSERT INTO tipos_terapia (nombre, duracion_minutos, precio, activo)
VALUES
    ('Terapia Cognitivo Conductual',   60, 55.00, TRUE),
    ('Terapia de Pareja',              90, 85.00, TRUE),
    ('Terapia Infantil',               50, 50.00, TRUE),
    ('Mindfulness y Gestión Emocional',45, 40.00, TRUE),
    ('Terapia Online Individual',      50, 45.00, TRUE);

-- ============================================================
-- SEED — CITAS
-- ============================================================

INSERT INTO citas (id_paciente, id_psicologo, id_tipo_terapia, start_datetime, duration_minutes, modalidad, estado, motivo)
VALUES
    (1, 1, 1, CURRENT_TIMESTAMP + INTERVAL '1 day', 60, 'PRESENCIAL', 'confirmada', 'Ansiedad y estrés laboral'),
    (2, 1, 4, CURRENT_TIMESTAMP + INTERVAL '2 day', 45, 'LLAMADA',    'pendiente',  'Gestión emocional'),
    (3, 2, 2, CURRENT_TIMESTAMP + INTERVAL '3 day', 90, 'PRESENCIAL', 'confirmada', 'Problemas familiares');

-- ============================================================
-- SEED — PAGOS
-- ============================================================

INSERT INTO pagos (id_cita, metodo_pago, estado_pago, monto, currency)
VALUES
    (1, 'TARJETA',  'PAGADO',   55.00, 'EUR'),
    (2, 'EFECTIVO', 'PENDIENTE',40.00, 'EUR'),
    (3, 'TARJETA',  'PAGADO',   85.00, 'EUR');

-- ============================================================
-- SEED — SESIONES
-- ============================================================

INSERT INTO sesiones (id_cita, id_paciente, id_psicologo, session_date, duration_minutes, notas, recomendaciones)
VALUES
    (1, 1, 1, CURRENT_TIMESTAMP, 60, 'Paciente con síntomas de ansiedad moderada.', 'Practicar respiración consciente.');

-- ============================================================
-- SEED — HORARIOS
-- ============================================================

INSERT INTO horario_psicologo (id_psicologo, dia_semana, hora_inicio, hora_fin)
VALUES
    (1, 1, '09:00', '14:00'),
    (1, 3, '16:00', '20:00'),
    (2, 2, '10:00', '18:00');

-- ============================================================
-- SEED — BLOQUEOS DE AGENDA
-- ============================================================

INSERT INTO bloqueos_agenda (id_psicologo, fecha, hora_inicio, hora_fin, motivo)
VALUES
    (1, CURRENT_DATE + 5, '10:00', '12:00', 'Reunión clínica');

-- ============================================================
-- SEED — HISTORIAL CLÍNICO
-- ============================================================

INSERT INTO historial_clinico (id_paciente, titulo, diagnostico, tratamiento, observaciones)
VALUES
    (1, 'Primera evaluación', 'Ansiedad moderada', 'Terapia cognitivo conductual', 'Buena predisposición al tratamiento.');

-- ============================================================
-- SEED — DIARIO EMOCIONAL
-- ============================================================

INSERT INTO diario_emociones (id_paciente, titulo, emocion, intensidad, nota)
VALUES
    (1, 'Semana complicada', 'Ansiedad', 8, 'Mucho estrés por trabajo y estudios.'),
    (2, 'Mejorando',         'Calma',    4, 'He dormido mejor esta semana.');

-- ============================================================
-- SEED — PROGRESO EMOCIONAL
-- ============================================================

INSERT INTO progreso_emocional (id_paciente, fecha, nivel_estres, nivel_ansiedad, nivel_animo)
VALUES
    (1, CURRENT_DATE, 8, 7, 4),
    (2, CURRENT_DATE, 4, 3, 7);

-- ============================================================
-- SEED — SITUACIONES CLÍNICAS
-- ============================================================

INSERT INTO situaciones (nombre, categoria, descripcion, activo)
VALUES
    ('Ansiedad Generalizada',  'Ansiedad',           'Preocupación excesiva constante, nerviosismo y dificultad para relajarse.',              TRUE),
    ('Estrés Laboral',         'Estrés',             'Sobrecarga emocional relacionada con el entorno laboral y agotamiento mental.',          TRUE),
    ('Depresión Moderada',     'Estado de Ánimo',    'Estado persistente de tristeza, apatía y pérdida de interés.',                          TRUE),
    ('Problemas de Autoestima','Desarrollo Personal','Dificultades relacionadas con la autopercepción y confianza personal.',                  TRUE),
    ('Duelo Emocional',        'Duelo',              'Proceso emocional asociado a pérdidas personales o afectivas.',                          TRUE),
    ('Insomnio',               'Sueño',              'Dificultad para iniciar o mantener el sueño de forma adecuada.',                         TRUE),
    ('Dependencia Emocional',  'Relaciones',         'Necesidad excesiva de aprobación o afecto en relaciones personales.',                    TRUE),
    ('Ataques de Pánico',      'Ansiedad',           'Episodios repentinos de miedo intenso acompañados de síntomas físicos.',                 TRUE),
    ('Problemas Familiares',   'Familia',            'Conflictos o dificultades de convivencia dentro del entorno familiar.',                  TRUE),
    ('Burnout Académico',      'Estrés',             'Agotamiento emocional y mental relacionado con estudios o exámenes.',                    TRUE);

-- ============================================================
-- SEED — PACIENTE ↔ SITUACIÓN
-- ============================================================

INSERT INTO paciente_situacion (id_paciente, id_situacion)
VALUES
    (1, 1),
    (2, 2);

-- ============================================================
-- SEED — MENSAJES
-- ============================================================

INSERT INTO mensajes (sender_id, receiver_id, id_cita, mensaje)
VALUES
    (4, 2, 1, 'Hola, necesito cambiar el horario.'),
    (2, 4, 1, 'Perfecto, revisamos disponibilidad.');

-- ============================================================
-- SEED — NOTIFICACIONES
-- ============================================================

INSERT INTO notificaciones (id_usuario, titulo, mensaje)
VALUES
    (4, 'Recordatorio de cita', 'Tu sesión es mañana a las 10:00.'),
    (5, 'Pago pendiente',       'Tienes un pago pendiente.');

-- ============================================================
-- SEED — TICKETS DE SOPORTE
-- ============================================================

INSERT INTO tickets_soporte (titulo, descripcion, tipo, categoria, estado, id_usuario)
VALUES
    ('Problema con videollamada', 'No puedo acceder a la sesión online.', 'problema', 'tecnico', 'abierto', 4);

-- ============================================================
-- SEED — AJUSTES
-- ============================================================

INSERT INTO ajustes (id_usuario, idioma, notificaciones, tema, timezone)
VALUES
    (4, 'es', TRUE, TRUE, 'Europe/Madrid');

-- ============================================================
-- SEED — CONSENTIMIENTOS
-- ============================================================

INSERT INTO consentimientos (id_paciente, acepta_terminos, version_documento, acepta_videoconferencia, acepta_comunicacion)
VALUES
    (1, TRUE, '1.0', TRUE, TRUE);

-- ============================================================
-- SEED — DOCUMENTOS LEGALES
-- ============================================================

INSERT INTO documentos_legales (tipo, titulo, contenido, icono, orden_visualizacion, version, activo)
VALUES
    (
        'terminos',
        'Términos y Condiciones de Uso - AMANI Psicología',
        '# TÉRMINOS Y CONDICIONES DE USO

    ## 1. ACEPTACIÓN DE LOS TÉRMINOS
    Al acceder y utilizar la plataforma de AMANI Psicología, usted acepta quedar vinculado por estos Términos y Condiciones.

    ## 2. DESCRIPCIÓN DEL SERVICIO
    AMANI Psicología ofrece servicios de terapia psicológica en línea y presencial, incluyendo consultas individuales, terapia de pareja y familiar, orientación psicológica y seguimiento de pacientes.

    ## 3. RESPONSABILIDAD DEL USUARIO
    El usuario se compromete a proporcionar información veraz, mantener la confidencialidad de sus credenciales, utilizar los servicios de manera ética y asistir puntualmente a las citas agendadas.

    ## 4. POLÍTICA DE CANCELACIÓN
    Las cancelaciones deben realizarse con al menos 24 horas de anticipación. Las cancelaciones tardías o inasistencias sin aviso pueden generar cargos adicionales.

    ## 5. CONFIDENCIALIDAD
    Todas las sesiones y comunicaciones están protegidas por el secreto profesional. La información del paciente solo se compartirá con su consentimiento expreso, por mandato judicial o en situaciones de riesgo vital.

    ## 6. PAGOS Y FACTURACIÓN
    Los pagos se procesan a través de nuestra plataforma segura. Se emitirán facturas electrónicas por cada servicio. Los precios están sujetos a cambios con previo aviso.

    ## 7. CONTACTO
    📧 legal@amani-psicologia.com  |  📞 +34 900 123 456

    Última actualización: 20/05/2024 — Versión: 2.0',
        'gavel',
        0,
        '2.0',
        TRUE
    ),
    (
        'privacidad',
        'Política de Privacidad y Protección de Datos - AMANI Psicología',
        '# POLÍTICA DE PRIVACIDAD

    ## 1. INFORMACIÓN QUE RECOPILAMOS
    Recopilamos datos personales (nombre, DNI, fecha de nacimiento, email, teléfono), datos clínicos (historial de sesiones, notas, evaluaciones) y datos de uso de la plataforma.

    ## 2. FINALIDAD DEL TRATAMIENTO
    Sus datos se utilizan para brindar servicios psicológicos personalizados, gestionar citas, comunicación sobre su tratamiento, facturación y mejora del servicio.

    ## 3. BASE LEGAL
    El tratamiento se basa en el consentimiento explícito del paciente, la ejecución de un contrato de servicio y las obligaciones legales vigentes.

    ## 4. CONFIDENCIALIDAD Y SECRETO PROFESIONAL
    Toda la información clínica está protegida por el secreto profesional, el RGPD/LOPDGDD y el Código Deontológico de la Psicología.

    ## 5. DERECHOS DEL USUARIO
    Usted tiene derecho a acceder, rectificar, eliminar, oponerse, limitar, portar sus datos y retirar su consentimiento en cualquier momento.

    ## 6. SEGURIDAD DE LOS DATOS
    Implementamos encriptación de datos sensibles, autenticación de dos factores, acceso restringido al personal y auditorías de seguridad regulares.

    ## 7. TRANSFERENCIAS INTERNACIONALES
    Todos los datos se almacenan en servidores ubicados en la UE, garantizando el cumplimiento del RGPD.

    ## 8. CONTACTO DEL DPO
    📧 dpo@amani-psicologia.com  |  📞 +34 900 123 456

    ## 9. AUTORIDAD DE CONTROL
    Agencia Española de Protección de Datos (AEPD): www.aepd.es

    Última actualización: 20/05/2024 — Versión: 2.0',
        'lock',
        1,
        '2.0',
        TRUE
    );




