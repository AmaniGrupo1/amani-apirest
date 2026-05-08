-- ============================================================
-- AMANI — Schema PostgreSQL (DEFINITIVO)
-- Fusión completa: v1-fusionado + v2
-- ============================================================

DROP SCHEMA IF EXISTS psicologia_app CASCADE;
CREATE SCHEMA psicologia_app;
SET search_path TO psicologia_app;

-- ==============================
-- ENUMS CORE
-- ==============================

CREATE TYPE rol_usuario    AS ENUM ('admin', 'psicologo', 'paciente');
CREATE TYPE estado_cita    AS ENUM ('pendiente', 'confirmada', 'cancelada', 'completada');
CREATE TYPE estado_pago    AS ENUM ('PENDIENTE', 'PAGADO', 'FALLIDO', 'REEMBOLSADO');
CREATE TYPE metodo_pago    AS ENUM ('PRESENCIAL', 'ONLINE');
CREATE TYPE modalidad_cita AS ENUM ('PRESENCIAL', 'LLAMADA');

-- ==============================
-- ENUMS SOPORTE
-- ==============================

CREATE TYPE tipo_ticket_soporte AS ENUM ('problema', 'pregunta', 'sugerencia');
CREATE TYPE categoria_ticket_soporte AS ENUM ('tecnico', 'cuenta', 'pago', 'app', 'otro');
CREATE TYPE estado_ticket_soporte AS ENUM ('abierto', 'en_progreso', 'cerrado');

-- ==============================
-- ENUM DOCUMENTOS LEGALES
-- ==============================

CREATE TYPE tipo_documento_legal AS ENUM ('terminos', 'privacidad');

-- ==============================
-- TABLAS CORE
-- ==============================

CREATE TABLE usuarios
(
    id_usuario             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre                 VARCHAR(100)        NOT NULL,
    apellido               VARCHAR(100),
    foto_perfil_url        TEXT,
    dni                    VARCHAR(50) UNIQUE,
    email                  VARCHAR(150) UNIQUE NOT NULL,
    password               VARCHAR(255)        NOT NULL,
    rol                    rol_usuario         NOT NULL,
    activo                 BOOLEAN             NOT NULL DEFAULT TRUE,
    fecha_registro         TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_baja             TIMESTAMP,
    fcm_token              VARCHAR(512),                    -- ampliado para tokens FCM largos
    notificaciones_activas BOOLEAN             NOT NULL DEFAULT TRUE
);

CREATE TABLE psicologos
(
    id_psicologo     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario       BIGINT       NOT NULL,
    especialidad     VARCHAR(150),
    experiencia      INT CHECK (experiencia >= 0),
    descripcion      TEXT,
    licencia         VARCHAR(100),
    duracion_default INT          NOT NULL DEFAULT 50,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario)
        ON DELETE CASCADE
);

CREATE TABLE pacientes
(
    id_paciente      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario       BIGINT    NOT NULL,
    id_psicologo     BIGINT,                               -- FK diferida ↓
    fecha_nacimiento DATE,
    genero           VARCHAR(30),
    telefono         VARCHAR(30),
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

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

-- ==============================
-- RELACIÓN PSICÓLOGO ↔ PACIENTE
-- ==============================

-- Historial de asignaciones (puede haber varias en el tiempo)
CREATE TABLE psicologo_paciente
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente  BIGINT    NOT NULL,
    id_psicologo BIGINT    NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_fin    TIMESTAMP,                                -- NULL = relación activa

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE,

    FOREIGN KEY (id_psicologo)
        REFERENCES psicologos (id_psicologo)
        ON DELETE CASCADE
);

-- ==============================
-- TIPOS DE TERAPIA
-- ==============================

CREATE TABLE tipos_terapia
(
    id_tipo          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre           VARCHAR(100),
    duracion_minutos INT            NOT NULL,
    precio           DECIMAL(10, 2) NOT NULL DEFAULT 0,
    activo           BOOLEAN                 DEFAULT TRUE
);

-- ==============================
-- CITAS Y SESIONES
-- ==============================

CREATE TABLE citas
(
    id_cita          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente      BIGINT         NOT NULL,
    id_psicologo     BIGINT         NOT NULL,
    id_tipo_terapia  BIGINT         NOT NULL,
    start_datetime   TIMESTAMP      NOT NULL,
    duration_minutes INT                     DEFAULT 50,
    modalidad        modalidad_cita NOT NULL DEFAULT 'PRESENCIAL',
    estado           estado_cita    NOT NULL DEFAULT 'pendiente',
    motivo           TEXT,
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE RESTRICT,

    FOREIGN KEY (id_psicologo)
        REFERENCES psicologos (id_psicologo)
        ON DELETE RESTRICT,

    FOREIGN KEY (id_tipo_terapia)
        REFERENCES tipos_terapia (id_tipo)
        ON DELETE RESTRICT
);

CREATE TABLE pagos
(
    id_pago                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_cita                  BIGINT         NOT NULL UNIQUE,
    monto                    DECIMAL(10, 2) NOT NULL,
    metodo_pago              metodo_pago    NOT NULL DEFAULT 'ONLINE',
    estado_pago              estado_pago    NOT NULL DEFAULT 'PENDIENTE',
    stripe_payment_intent_id VARCHAR(255),
    stripe_charge_id         VARCHAR(255),
    idempotency_key          VARCHAR(255),
    currency                 VARCHAR(3)     NOT NULL DEFAULT 'EUR',
    fecha_creacion           TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    fecha_pago               TIMESTAMP,
    fecha_actualizacion      TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_cita)
        REFERENCES citas (id_cita)
        ON DELETE CASCADE
);

CREATE TABLE sesiones
(
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

    FOREIGN KEY (id_cita)
        REFERENCES citas (id_cita)
        ON DELETE CASCADE,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE,

    FOREIGN KEY (id_psicologo)
        REFERENCES psicologos (id_psicologo)
        ON DELETE SET NULL
);

-- ==============================
-- AGENDA DE PSICÓLOGOS
-- ==============================

-- Horario semanal recurrente (0=lunes … 6=domingo)
CREATE TABLE horario_psicologo
(
    id_horario   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_psicologo BIGINT   NOT NULL,
    dia_semana   SMALLINT NOT NULL,
    hora_inicio  TIME     NOT NULL,
    hora_fin     TIME     NOT NULL,
    activo       BOOLEAN  NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CHECK (dia_semana BETWEEN 0 AND 6),
    CHECK (hora_fin > hora_inicio),

    FOREIGN KEY (id_psicologo)
        REFERENCES psicologos (id_psicologo)
        ON DELETE CASCADE
);

-- Bloqueos puntuales (hora_inicio/fin = NULL → día completo)
CREATE TABLE bloqueos_agenda
(
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

    FOREIGN KEY (id_psicologo)
        REFERENCES psicologos (id_psicologo)
        ON DELETE CASCADE
);

-- ==============================
-- HISTORIAL CLÍNICO Y SEGUIMIENTO
-- ==============================

CREATE TABLE historial_clinico
(
    id_history    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente   BIGINT NOT NULL,
    titulo        VARCHAR(200),
    diagnostico   TEXT,
    tratamiento   TEXT,
    observaciones TEXT,
    creado_en     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE
);

CREATE TABLE diario_emociones
(
    id_diario   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente BIGINT NOT NULL,
    titulo      TEXT,
    fecha       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    emocion     VARCHAR(100),
    intensidad  INT CHECK (intensidad BETWEEN 1 AND 10),
    nota        TEXT,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE
);

CREATE TABLE progreso_emocional
(
    id_progress    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente    BIGINT NOT NULL,
    fecha          DATE   NOT NULL,
    nivel_estres   INT,
    nivel_ansiedad INT,
    nivel_animo    INT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE
);

-- ==============================
-- SITUACIONES CLÍNICAS
-- ==============================

CREATE TABLE situaciones
(
    id_situacion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre       VARCHAR(150) NOT NULL,
    categoria    VARCHAR(100),
    descripcion  TEXT,
    activo       BOOLEAN   DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE paciente_situacion
(
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente    BIGINT NOT NULL,
    id_situacion   BIGINT NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE,

    FOREIGN KEY (id_situacion)
        REFERENCES situaciones (id_situacion)
        ON DELETE CASCADE
);

-- ==============================
-- MENSAJERÍA
-- ==============================

CREATE TABLE mensajes
(
    id_mensaje  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sender_id   BIGINT,
    receiver_id BIGINT,
    id_cita     BIGINT,
    mensaje     TEXT      NOT NULL,
    enviado_en  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leido       BOOLEAN   DEFAULT FALSE,

    FOREIGN KEY (sender_id)   REFERENCES usuarios (id_usuario) ON DELETE SET NULL,
    FOREIGN KEY (receiver_id) REFERENCES usuarios (id_usuario) ON DELETE SET NULL,
    FOREIGN KEY (id_cita)     REFERENCES citas (id_cita)       ON DELETE SET NULL
);

-- ==============================
-- CUESTIONARIOS
-- ==============================

CREATE TABLE preguntas
(
    id_pregunta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    texto       TEXT        NOT NULL,
    tipo        VARCHAR(50) DEFAULT 'texto',
    creado_en   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE opciones
(
    id_opcion   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_pregunta BIGINT       NOT NULL,
    texto       VARCHAR(200) NOT NULL,
    valor       INT,

    FOREIGN KEY (id_pregunta)
        REFERENCES preguntas (id_pregunta)
        ON DELETE CASCADE
);

CREATE TABLE respuestas
(
    id_respuesta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente  BIGINT NOT NULL,
    id_pregunta  BIGINT NOT NULL,
    id_opcion    BIGINT,
    texto        TEXT,
    creado_en    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE,

    FOREIGN KEY (id_pregunta)
        REFERENCES preguntas (id_pregunta)
        ON DELETE CASCADE,

    FOREIGN KEY (id_opcion)
        REFERENCES opciones (id_opcion)
        ON DELETE SET NULL
);

-- ==============================
-- DATOS COMPLEMENTARIOS
-- ==============================

CREATE TABLE direcciones
(
    id_direccion  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente   BIGINT NOT NULL,
    calle         VARCHAR(150),
    ciudad        VARCHAR(100),
    provincia     VARCHAR(100),
    codigo_postal VARCHAR(20),
    pais          VARCHAR(100),
    descripcion   TEXT,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE
);

CREATE TABLE ajustes
(
    id_ajuste      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario     BIGINT       NOT NULL,
    idioma         VARCHAR(10)  DEFAULT 'es',
    notificaciones BOOLEAN      DEFAULT TRUE,
    dark_mode      BOOLEAN      DEFAULT FALSE,
    timezone       VARCHAR(100) DEFAULT 'Europe/Madrid',
    updated_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario)
        ON DELETE CASCADE
);

CREATE TABLE archivos
(
    id_archivo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_sesion  BIGINT       NOT NULL,
    nombre     VARCHAR(200),
    tipo_mime  VARCHAR(100),
    datos      BYTEA        NOT NULL,
    creado_en  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_sesion)
        REFERENCES sesiones (id_sesion)
        ON DELETE CASCADE
);

-- ==============================
-- TUTORES (menores de edad)
-- ==============================

CREATE TABLE tutores
(
    id_tutor    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente BIGINT       NOT NULL,
    nombre      VARCHAR(255) NOT NULL,
    telefono    VARCHAR(30),
    email       VARCHAR(150),
    dni         VARCHAR(20),
    tipo        VARCHAR(50)  NOT NULL,                     -- 'MADRE', 'PADRE', 'TUTOR'

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE
);

-- ==============================
-- CONSENTIMIENTOS RGPD
-- ==============================

CREATE TABLE consentimientos
(
    id_consentimiento       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente             BIGINT       NOT NULL,
    fecha_aceptacion        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    acepta_terminos         BOOLEAN      NOT NULL DEFAULT FALSE,
    version_documento       VARCHAR(255) NOT NULL,
    acepta_videoconferencia BOOLEAN               DEFAULT FALSE,
    acepta_comunicacion     BOOLEAN               DEFAULT FALSE,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE
);

-- ==============================
-- NOTIFICACIONES
-- ==============================

CREATE TABLE notificaciones
(
    id_notificacion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario      BIGINT       NOT NULL,
    titulo          VARCHAR(200),
    mensaje         TEXT,
    leida           BOOLEAN   DEFAULT FALSE,
    creada_en       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario)
        ON DELETE CASCADE
);

-- ==============================
-- SOPORTE
-- ==============================

CREATE TABLE tickets_soporte
(
    id_ticket      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo         VARCHAR(200)             NOT NULL,
    descripcion    TEXT                     NOT NULL,
    tipo           tipo_ticket_soporte      NOT NULL,
    categoria      categoria_ticket_soporte NOT NULL,
    estado         estado_ticket_soporte    NOT NULL DEFAULT 'abierto',
    creado_en      TIMESTAMP                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP                         DEFAULT CURRENT_TIMESTAMP,
    cerrado_en     TIMESTAMP,
    id_usuario     BIGINT                   NOT NULL,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario)
        ON DELETE CASCADE
);

-- ==============================
-- DOCUMENTOS LEGALES
-- ==============================

CREATE TABLE documentos_legales
(
    id_documento         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo                 tipo_documento_legal NOT NULL,
    titulo               VARCHAR(255)         NOT NULL,
    contenido            TEXT                 NOT NULL,
    icono                VARCHAR(100),
    orden_visualizacion  INT                  NOT NULL DEFAULT 0,
    version              VARCHAR(20),
    activo               BOOLEAN              NOT NULL DEFAULT TRUE,
    creado_en            TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en       TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==============================
-- ÍNDICES
-- ==============================

-- Core
CREATE INDEX idx_usuarios_email             ON usuarios (email);
CREATE INDEX idx_citas_start                ON citas (start_datetime);
CREATE INDEX idx_citas_psicologo_fecha      ON citas (id_psicologo, start_datetime);
CREATE INDEX idx_archivos_sesion            ON archivos (id_sesion);

-- Agenda
CREATE INDEX idx_horario_psicologo          ON horario_psicologo (id_psicologo, dia_semana);
CREATE INDEX idx_bloqueos_psicologo         ON bloqueos_agenda (id_psicologo, fecha);

-- Mensajería
CREATE INDEX idx_mensajes_sender            ON mensajes (sender_id, enviado_en);
CREATE INDEX idx_mensajes_receiver          ON mensajes (receiver_id, leido);

-- Soporte
CREATE INDEX idx_tickets_usuario            ON tickets_soporte (id_usuario);
CREATE INDEX idx_tickets_estado             ON tickets_soporte (estado);
CREATE INDEX idx_tickets_creado             ON tickets_soporte (creado_en);

-- Documentos legales
CREATE INDEX idx_documentos_tipo            ON documentos_legales (tipo);
CREATE INDEX idx_documentos_activo          ON documentos_legales (activo);

-- ==============================
-- VISTAS
-- ==============================

-- Agenda diaria consolidada: citas activas + bloqueos por psicólogo
CREATE OR REPLACE VIEW vista_agenda_psicologo AS
SELECT p.id_psicologo,
       u.nombre || ' ' || COALESCE(u.apellido, '')                               AS nombre_psicologo,
       c.start_datetime::DATE                                                     AS fecha,
       c.start_datetime::TIME                                                     AS hora_inicio,
       (c.start_datetime + (c.duration_minutes || ' minutes')::INTERVAL)::TIME   AS hora_fin,
       'cita'                                                                     AS tipo,
       c.estado::TEXT                                                             AS detalle,
       c.id_cita                                                                  AS referencia_id
FROM psicologos p
         JOIN usuarios u ON u.id_usuario = p.id_usuario
         JOIN citas c ON c.id_psicologo = p.id_psicologo
WHERE c.estado IN ('pendiente', 'confirmada')

UNION ALL

SELECT b.id_psicologo,
       u.nombre || ' ' || COALESCE(u.apellido, '') AS nombre_psicologo,
       b.fecha,
       COALESCE(b.hora_inicio, '00:00'::TIME)       AS hora_inicio,
       COALESCE(b.hora_fin, '23:59'::TIME)           AS hora_fin,
       'bloqueo'                                     AS tipo,
       COALESCE(b.motivo, 'Día bloqueado')            AS detalle,
       b.id_bloqueo                                   AS referencia_id
FROM bloqueos_agenda b
         JOIN psicologos p ON p.id_psicologo = b.id_psicologo
         JOIN usuarios u ON u.id_usuario = p.id_usuario

ORDER BY fecha, hora_inicio;

-- Listado de pacientes asignados a un psicólogo (relación activa)
CREATE OR REPLACE VIEW vista_pacientes_psicologo AS
SELECT pp.id_psicologo,
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
         JOIN usuarios up   ON up.id_usuario   = ps.id_usuario
         JOIN pacientes pa  ON pa.id_paciente  = pp.id_paciente
         JOIN usuarios ua   ON ua.id_usuario   = pa.id_usuario
WHERE pp.fecha_fin IS NULL
ORDER BY pp.id_psicologo, ua.apellido, ua.nombre;

-- ==============================
--
-- PostgreSQL database dump
--

\restrict WRiFZlwpSidLnwp83BQyOltHnjV0NqXfbJrphErfFAbGr9JtYlBxfudfWlgzu28

-- Dumped from database version 16.13 (Debian 16.13-1.pgdg13+1)
-- Dumped by pg_dump version 16.13 (Ubuntu 16.13-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--

INSERT INTO psicologia_app.usuarios (id_usuario, nombre, apellido, dni, email, password, rol, activo, fecha_registro, fecha_baja, fcm_token, foto_perfil_url, notificaciones_activas) OVERRIDING SYSTEM VALUE VALUES (1, 'Admin', 'Principal', NULL, 'felixb@example.com', '$2a$10$f6IeTQIpuzXWdeXYJ5O8zugtvd2rQESGanenPgsdDqtlRe3xrZIhO', 'admin', true, '2026-05-06 12:07:34.675224', NULL, NULL, NULL, true);
INSERT INTO psicologia_app.usuarios (id_usuario, nombre, apellido, dni, email, password, rol, activo, fecha_registro, fecha_baja, fcm_token, foto_perfil_url, notificaciones_activas) OVERRIDING SYSTEM VALUE VALUES (2, 'Marta', 'Burgos', NULL, 'marta.burgos@amani.com', '$2a$10$9c.u2qSNY7UwtyC6w6S/7ucy2Kk8xfPizpgZ3Kl7tgMVaFKSMG6.u', 'psicologo', true, '2026-05-06 12:07:59.420882', NULL, NULL, NULL, true);
INSERT INTO psicologia_app.usuarios (id_usuario, nombre, apellido, dni, email, password, rol, activo, fecha_registro, fecha_baja, fcm_token, foto_perfil_url, notificaciones_activas) OVERRIDING SYSTEM VALUE VALUES (3, 'Juan', 'Pérez', NULL, 'juan.perez@email.com', '$2a$10$9c.u2qSNY7UwtyC6w6S/7ucy2Kk8xfPizpgZ3Kl7tgMVaFKSMG6.u', 'paciente', true, '2026-05-06 12:07:59.420882', NULL, NULL, NULL, true);
INSERT INTO psicologia_app.usuarios (id_usuario, nombre, apellido, dni, email, password, rol, activo, fecha_registro, fecha_baja, fcm_token, foto_perfil_url, notificaciones_activas) OVERRIDING SYSTEM VALUE VALUES (4, 'Laura', 'García', NULL, 'laura.garcia@email.com', '$2a$10$9c.u2qSNY7UwtyC6w6S/7ucy2Kk8xfPizpgZ3Kl7tgMVaFKSMG6.u', 'paciente', true, '2026-05-06 12:07:59.420882', NULL, NULL, NULL, true);


--
-- Data for Name: ajustes; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: psicologos; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--

INSERT INTO psicologia_app.psicologos (id_psicologo, id_usuario, especialidad, experiencia, descripcion, licencia, created_at, updated_at, duracion_default) OVERRIDING SYSTEM VALUE VALUES (1, 2, 'Psicología Clínica', 5, 'Especialista en terapia cognitivo-conductual y gestión de ansiedad.', 'COL-12345', '2026-05-06 12:07:59.420882', '2026-05-06 12:07:59.420882', 50);


--
-- Data for Name: pacientes; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--

INSERT INTO psicologia_app.pacientes (id_paciente, id_usuario, id_psicologo, fecha_nacimiento, genero, telefono, metodo_pago, estado_pago, created_at, updated_at) OVERRIDING SYSTEM VALUE VALUES (1, 3, 1, '1990-05-15', 'Masculino', '600000001', 'PRESENCIAL', 'PENDIENTE', '2026-05-06 12:07:59.420882', '2026-05-06 12:07:59.420882');
INSERT INTO psicologia_app.pacientes (id_paciente, id_usuario, id_psicologo, fecha_nacimiento, genero, telefono, metodo_pago, estado_pago, created_at, updated_at) OVERRIDING SYSTEM VALUE VALUES (2, 4, 1, '1985-11-20', 'Femenino', '600000002', 'PRESENCIAL', 'PENDIENTE', '2026-05-06 12:07:59.420882', '2026-05-06 12:07:59.420882');


--
-- Data for Name: tipos_terapia; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: citas; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: sesiones; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: archivos; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: bloqueos_agenda; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: consentimientos; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: diario_emociones; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: direcciones; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: historial_clinico; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: horario_psicologo; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: mensajes; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: notificaciones; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: preguntas; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: opciones; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: situaciones; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: paciente_situacion; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: pagos; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: progreso_emocional; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: psicologo_paciente; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--

INSERT INTO psicologia_app.psicologo_paciente (id, id_paciente, id_psicologo, fecha_inicio, fecha_fin) OVERRIDING SYSTEM VALUE VALUES (1, 1, 1, '2026-05-06 12:07:59.420882', NULL);
INSERT INTO psicologia_app.psicologo_paciente (id, id_paciente, id_psicologo, fecha_inicio, fecha_fin) OVERRIDING SYSTEM VALUE VALUES (2, 2, 1, '2026-05-06 12:07:59.420882', NULL);


--
-- Data for Name: respuestas; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: tickets_soporte; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Data for Name: tutores; Type: TABLE DATA; Schema: psicologia_app; Owner: postgres
--



--
-- Name: ajustes_id_ajuste_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.ajustes_id_ajuste_seq', 1, false);


--
-- Name: archivos_id_archivo_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.archivos_id_archivo_seq', 1, false);


--
-- Name: bloqueos_agenda_id_bloqueo_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.bloqueos_agenda_id_bloqueo_seq', 1, false);


--
-- Name: citas_id_cita_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.citas_id_cita_seq', 1, false);


--
-- Name: consentimientos_id_consentimiento_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.consentimientos_id_consentimiento_seq', 1, false);


--
-- Name: diario_emociones_id_diario_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.diario_emociones_id_diario_seq', 1, false);


--
-- Name: direcciones_id_direccion_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.direcciones_id_direccion_seq', 1, false);


--
-- Name: historial_clinico_id_history_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.historial_clinico_id_history_seq', 1, false);


--
-- Name: horario_psicologo_id_horario_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.horario_psicologo_id_horario_seq', 1, false);


--
-- Name: mensajes_id_mensaje_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.mensajes_id_mensaje_seq', 1, false);


--
-- Name: notificaciones_id_notificacion_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.notificaciones_id_notificacion_seq', 1, false);


--
-- Name: opciones_id_opcion_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.opciones_id_opcion_seq', 1, false);


--
-- Name: paciente_situacion_id_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.paciente_situacion_id_seq', 1, false);


--
-- Name: pacientes_id_paciente_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.pacientes_id_paciente_seq', 2, true);


--
-- Name: pagos_id_pago_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.pagos_id_pago_seq', 1, false);


--
-- Name: preguntas_id_pregunta_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.preguntas_id_pregunta_seq', 1, false);


--
-- Name: progreso_emocional_id_progress_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.progreso_emocional_id_progress_seq', 1, false);


--
-- Name: psicologo_paciente_id_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.psicologo_paciente_id_seq', 2, true);


--
-- Name: psicologos_id_psicologo_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.psicologos_id_psicologo_seq', 1, true);


--
-- Name: respuestas_id_respuesta_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.respuestas_id_respuesta_seq', 1, false);


--
-- Name: sesiones_id_sesion_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.sesiones_id_sesion_seq', 1, false);


--
-- Name: situaciones_id_situacion_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.situaciones_id_situacion_seq', 1, false);


--
-- Name: tickets_soporte_id_ticket_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.tickets_soporte_id_ticket_seq', 1, false);


--
-- Name: tipos_terapia_id_tipo_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.tipos_terapia_id_tipo_seq', 1, false);


--
-- Name: tutores_id_tutor_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.tutores_id_tutor_seq', 1, false);


--
-- Name: usuarios_id_usuario_seq; Type: SEQUENCE SET; Schema: psicologia_app; Owner: postgres
--

SELECT pg_catalog.setval('psicologia_app.usuarios_id_usuario_seq', 4, true);


--
-- PostgreSQL database dump complete
--

\unrestrict WRiFZlwpSidLnwp83BQyOltHnjV0NqXfbJrphErfFAbGr9JtYlBxfudfWlgzu28

