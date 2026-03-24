DROP SCHEMA IF EXISTS psicologia_app CASCADE;
CREATE SCHEMA psicologia_app;

SET search_path TO psicologia_app;

-- ==============================
-- ENUMS
-- ==============================

CREATE TYPE rol_usuario AS ENUM ('admin', 'psicologo', 'paciente');
CREATE TYPE estado_cita AS ENUM ('pendiente', 'confirmada', 'cancelada', 'completada');

-- ==============================
-- TABLAS
-- ==============================

CREATE TABLE usuarios
(
    id_usuario     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre         VARCHAR(100)        NOT NULL,
    apellido       VARCHAR(100),
    email          VARCHAR(150) UNIQUE NOT NULL,
    password       VARCHAR(255)        NOT NULL,
    rol            rol_usuario         NOT NULL,
    activo         BOOLEAN             NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_baja     TIMESTAMP,
    fcm_token      VARCHAR(100)
);

CREATE TABLE pacientes
(
    id_paciente      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario       BIGINT NOT NULL,
    id_psicologo     BIGINT, -- asignación principal
    fecha_nacimiento DATE,
    genero           VARCHAR(30),
    telefono         VARCHAR(30),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario)
        ON DELETE CASCADE
);

CREATE TABLE psicologos
(
    id_psicologo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario   BIGINT NOT NULL,
    especialidad VARCHAR(150),
    experiencia  INT CHECK (experiencia >= 0),
    descripcion  TEXT,
    licencia     VARCHAR(100),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario)
        ON DELETE CASCADE
);

-- FK diferida: pacientes.id_psicologo → psicologos (se añade tras crear psicologos)
ALTER TABLE pacientes
    ADD CONSTRAINT fk_paciente_psicologo
        FOREIGN KEY (id_psicologo)
            REFERENCES psicologos (id_psicologo)
            ON DELETE SET NULL;

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

CREATE TABLE citas
(
    id_cita          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente      BIGINT      NOT NULL,
    id_psicologo     BIGINT      NOT NULL,
    start_datetime   TIMESTAMP   NOT NULL,
    duration_minutes INT                  DEFAULT 50,
    estado           estado_cita NOT NULL DEFAULT 'pendiente',
    motivo           TEXT,
    created_at       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE RESTRICT,

    FOREIGN KEY (id_psicologo)
        REFERENCES psicologos (id_psicologo)
        ON DELETE RESTRICT
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
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

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

CREATE TABLE historial_clinico
(
    id_history    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente   BIGINT NOT NULL,
    titulo        VARCHAR(200),
    diagnostico   TEXT,
    tratamiento   TEXT,
    observaciones TEXT,
    creado_en     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE
);

CREATE TABLE diario_emociones
(
    id_diario   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente BIGINT NOT NULL,
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

CREATE TABLE mensajes
(
    id_mensaje  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sender_id   BIGINT,
    receiver_id BIGINT,
    id_cita     BIGINT,
    mensaje     TEXT NOT NULL,
    enviado_en  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leido       BOOLEAN   DEFAULT FALSE,

    FOREIGN KEY (sender_id) REFERENCES usuarios (id_usuario) ON DELETE SET NULL,
    FOREIGN KEY (receiver_id) REFERENCES usuarios (id_usuario) ON DELETE SET NULL,
    FOREIGN KEY (id_cita) REFERENCES citas (id_cita) ON DELETE SET NULL
);

CREATE TABLE ajustes
(
    id_ajuste      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario     BIGINT NOT NULL,
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
    id_sesion  BIGINT NOT NULL,
    nombre     VARCHAR(200),
    tipo_mime  VARCHAR(100),
    datos      BYTEA  NOT NULL,
    creado_en  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_sesion)
        REFERENCES sesiones (id_sesion)
        ON DELETE CASCADE
);

CREATE TABLE preguntas
(
    id_pregunta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    texto       TEXT NOT NULL,
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

CREATE TABLE psicologo_paciente
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente  BIGINT NOT NULL,
    id_psicologo BIGINT NOT NULL,
    fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_fin    TIMESTAMP, -- NULL si está activo

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE,

    FOREIGN KEY (id_psicologo)
        REFERENCES psicologos (id_psicologo)
        ON DELETE CASCADE
);

-- ==============================
-- AGENDA DE PSICÓLOGOS
-- ==============================

-- Horario semanal recurrente
-- dia_semana: 0=lunes, 1=martes, ..., 6=domingo
CREATE TABLE horario_psicologo
(
    id_horario   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_psicologo BIGINT   NOT NULL,
    dia_semana   SMALLINT NOT NULL CHECK (dia_semana BETWEEN 0 AND 6),
    hora_inicio  TIME     NOT NULL,
    hora_fin     TIME     NOT NULL,
    activo       BOOLEAN  NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP         DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP         DEFAULT CURRENT_TIMESTAMP,

    CHECK (hora_fin > hora_inicio),

    FOREIGN KEY (id_psicologo)
        REFERENCES psicologos (id_psicologo)
        ON DELETE CASCADE
);

-- Bloqueos puntuales: día completo o franja horaria concreta
-- hora_inicio/hora_fin = NULL → día completo bloqueado
CREATE TABLE bloqueos_agenda
(
    id_bloqueo   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_psicologo BIGINT NOT NULL,
    fecha        DATE   NOT NULL,
    hora_inicio  TIME,
    hora_fin     TIME,
    motivo       VARCHAR(200),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CHECK (
        (hora_inicio IS NULL AND hora_fin IS NULL)
            OR (hora_inicio IS NOT NULL AND hora_fin IS NOT NULL AND hora_fin > hora_inicio)
        ),

    FOREIGN KEY (id_psicologo)
        REFERENCES psicologos (id_psicologo)
        ON DELETE CASCADE
);

-- ==============================
-- ÍNDICES
-- ==============================

CREATE INDEX idx_usuarios_email ON usuarios (email);
CREATE INDEX idx_citas_start ON citas (start_datetime);
CREATE INDEX idx_citas_psicologo_fecha ON citas (id_psicologo, start_datetime);
CREATE INDEX idx_archivos_sesion ON archivos (id_sesion);
CREATE INDEX idx_horario_psicologo ON horario_psicologo (id_psicologo, dia_semana);
CREATE INDEX idx_bloqueos_psicologo ON bloqueos_agenda (id_psicologo, fecha);

-- ==============================
-- VISTAS
-- ==============================

-- Agenda diaria consolidada: citas activas + bloqueos por psicólogo
CREATE OR REPLACE VIEW vista_agenda_psicologo AS
SELECT p.id_psicologo,
       u.nombre || ' ' || COALESCE(u.apellido, '')                             AS nombre_psicologo,
       c.start_datetime::DATE                                                  AS fecha,
       c.start_datetime::TIME                                                  AS hora_inicio,
       (c.start_datetime + (c.duration_minutes || ' minutes')::INTERVAL)::TIME AS hora_fin,
       'cita'                                                                  AS tipo,
       c.estado::TEXT                                                          AS detalle,
       c.id_cita                                                               AS referencia_id
FROM psicologos p
         JOIN usuarios u ON u.id_usuario = p.id_usuario
         JOIN citas c ON c.id_psicologo = p.id_psicologo
WHERE c.estado IN ('pendiente', 'confirmada')

UNION ALL

SELECT b.id_psicologo,
       u.nombre || ' ' || COALESCE(u.apellido, '') AS nombre_psicologo,
       b.fecha,
       COALESCE(b.hora_inicio, '00:00'::TIME)      AS hora_inicio,
       COALESCE(b.hora_fin, '23:59'::TIME)         AS hora_fin,
       'bloqueo'                                   AS tipo,
       COALESCE(b.motivo, 'Día bloqueado')         AS detalle,
       b.id_bloqueo                                AS referencia_id
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
         JOIN usuarios up ON up.id_usuario = ps.id_usuario
         JOIN pacientes pa ON pa.id_paciente = pp.id_paciente
         JOIN usuarios ua ON ua.id_usuario = pa.id_usuario
WHERE pp.fecha_fin IS NULL
ORDER BY pp.id_psicologo, ua.apellido, ua.nombre;

-- ==============================
-- DATOS INICIALES
-- ==============================

INSERT INTO usuarios (nombre, apellido, email, password, rol, activo)
VALUES ('Admin', 'Principal', 'felixb@example.com',
        '$2a$10$f6IeTQIpuzXWdeXYJ5O8zugtvd2rQESGanenPgsdDqtlRe3xrZIhO',
        'admin', TRUE);

SELECT p.id_psicologo, u.id_usuario, u.email
FROM psicologos p
         JOIN usuarios u ON u.id_usuario = p.id_usuario
WHERE u.email = 'Lorena.garcia@amani.com';