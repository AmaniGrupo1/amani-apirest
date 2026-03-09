DROP SCHEMA IF EXISTS psicologia_app CASCADE;
CREATE SCHEMA psicologia_app;

SET search_path TO psicologia_app;

-- ==============================
-- ENUMS
-- ==============================

CREATE TYPE rol_usuario AS ENUM ('admin','psicologo','paciente');

CREATE TYPE estado_cita AS ENUM ('pendiente','confirmada','cancelada','completada');

-- ==============================
-- Tabla usuarios
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
    fecha_baja     TIMESTAMP
);

-- ==============================
-- Tabla pacientes
-- ==============================

CREATE TABLE pacientes
(
    id_paciente      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario       BIGINT NOT NULL,
    fecha_nacimiento DATE,
    genero           VARCHAR(30),
    telefono         VARCHAR(30),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_paciente_usuario
        FOREIGN KEY (id_usuario)
            REFERENCES usuarios (id_usuario)
            ON DELETE CASCADE
);

-- ==============================
-- Tabla psicologos
-- ==============================

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

    CONSTRAINT fk_psicologo_usuario
        FOREIGN KEY (id_usuario)
            REFERENCES usuarios (id_usuario)
            ON DELETE CASCADE
);

-- ==============================
-- Tabla direcciones
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

    CONSTRAINT fk_direccion_paciente
        FOREIGN KEY (id_paciente)
            REFERENCES pacientes (id_paciente)
            ON DELETE CASCADE
);

-- ==============================
-- Tabla citas
-- ==============================

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

    CONSTRAINT fk_cita_paciente
        FOREIGN KEY (id_paciente)
            REFERENCES pacientes (id_paciente)
            ON DELETE RESTRICT,

    CONSTRAINT fk_cita_psicologo
        FOREIGN KEY (id_psicologo)
            REFERENCES psicologos (id_psicologo)
            ON DELETE RESTRICT
);

-- ==============================
-- Tabla sesiones
-- ==============================

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

-- ==============================
-- Tabla historial_clinico
-- ==============================

CREATE TABLE historial_clinico
(
    id_history    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente   BIGINT NOT NULL,
    titulo        VARCHAR(200),
    diagnostico   TEXT,
    tratamiento   TEXT,
    observaciones TEXT,
    creado_en     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_historial_paciente
        FOREIGN KEY (id_paciente)
            REFERENCES pacientes (id_paciente)
            ON DELETE CASCADE
);

-- ==============================
-- Tabla diario emocional
-- ==============================

CREATE TABLE diario_emociones
(
    id_diario   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente BIGINT NOT NULL,
    fecha       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    emocion     VARCHAR(100),
    intensidad  INT CHECK (intensidad BETWEEN 1 AND 10),
    nota        TEXT,

    CONSTRAINT fk_diario_paciente
        FOREIGN KEY (id_paciente)
            REFERENCES pacientes (id_paciente)
            ON DELETE CASCADE
);

-- ==============================
-- Tabla progreso emocional
-- ==============================

CREATE TABLE progreso_emocional
(
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

-- ==============================
-- Tabla mensajes
-- ==============================

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

-- ==============================
-- Tabla ajustes
-- ==============================

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

-- ==============================
-- Índices
-- ==============================

CREATE INDEX idx_usuarios_email ON usuarios (email);
CREATE INDEX idx_citas_start ON citas (start_datetime);

