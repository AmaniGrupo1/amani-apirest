DROP SCHEMA IF EXISTS psicologia_app CASCADE;
CREATE SCHEMA psicologia_app;

SET SCHEMA psicologia_app;

-- ==============================
-- TABLAS
-- ==============================

CREATE TABLE usuarios
(
    id_usuario      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          VARCHAR(100)        NOT NULL,
    apellido        VARCHAR(100),
    dni             VARCHAR(50),
    email           VARCHAR(150) UNIQUE NOT NULL,
    password        VARCHAR(255)        NOT NULL,
    rol             VARCHAR(50)         NOT NULL,
    activo          BOOLEAN             NOT NULL DEFAULT TRUE,
    fecha_registro  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_baja      TIMESTAMP,
    fcm_token       VARCHAR(512),
    foto_perfil_url VARCHAR(500),
    notificaciones_activas BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE psicologos
(
    id_psicologo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario   BIGINT NOT NULL,
    especialidad VARCHAR(150),
    experiencia  INT CHECK (experiencia >= 0),
    descripcion  TEXT,
    licencia     VARCHAR(100),
    duracion_default INT NOT NULL DEFAULT 50,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario)
        ON DELETE CASCADE
);

CREATE TABLE pacientes
(
    id_paciente      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario       BIGINT NOT NULL,
    id_psicologo     BIGINT, -- asignación principal
    fecha_nacimiento DATE,
    genero           VARCHAR(30),
    telefono         VARCHAR(30),
    metodo_pago      VARCHAR(50) NOT NULL DEFAULT 'PRESENCIAL',
    estado_pago      VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario)
        ON DELETE CASCADE,
        
    FOREIGN KEY (id_psicologo)
        REFERENCES psicologos (id_psicologo)
        ON DELETE SET NULL
);

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

CREATE TABLE tipos_terapia
(
    id_tipo_terapia  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    descripcion      TEXT
);

CREATE TABLE citas
(
    id_cita          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente      BIGINT      NOT NULL,
    id_psicologo     BIGINT      NOT NULL,
    id_tipo_terapia  BIGINT,
    start_datetime   TIMESTAMP   NOT NULL,
    duration_minutes INT                  DEFAULT 50,
    estado           VARCHAR(50) NOT NULL DEFAULT 'pendiente',
    modalidad        VARCHAR(50) NOT NULL DEFAULT 'ONLINE',
    motivo           TEXT,
    created_at       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE RESTRICT,

    FOREIGN KEY (id_psicologo)
        REFERENCES psicologos (id_psicologo)
        ON DELETE RESTRICT,

    FOREIGN KEY (id_tipo_terapia)
        REFERENCES tipos_terapia (id_tipo_terapia)
        ON DELETE SET NULL
);

CREATE TABLE pagos
(
    id_pago          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    monto            DECIMAL(10,2) NOT NULL,
    metodo_pago      VARCHAR(50) NOT NULL,
    estado_pago      VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    fecha_pago       TIMESTAMP,
    fecha_creacion   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_cita          BIGINT NOT NULL UNIQUE,

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

CREATE TABLE situaciones
(
    id_situacion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre       VARCHAR(150) NOT NULL,
    categoria    VARCHAR(100),
    descripcion  TEXT,
    activo       BOOLEAN      DEFAULT TRUE,
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tutores
(
    id_tutor    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente BIGINT       NOT NULL,
    nombre      VARCHAR(255) NOT NULL,
    telefono    VARCHAR(30),
    email       VARCHAR(150),
    dni         VARCHAR(20),
    tipo        VARCHAR(50)  NOT NULL,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE
);

CREATE TABLE consentimientos
(
    id_consentimiento      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente            BIGINT    NOT NULL,
    fecha_aceptacion       TIMESTAMP NOT NULL,
    version_documento      VARCHAR(255) NOT NULL,
    acepta_terminos       BOOLEAN   NOT NULL DEFAULT FALSE,
    acepta_videoconferencia BOOLEAN   DEFAULT FALSE,
    acepta_comunicacion    BOOLEAN   DEFAULT FALSE,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE
);

CREATE TABLE paciente_situacion
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente     BIGINT    NOT NULL,
    id_situacion    BIGINT    NOT NULL,
    fecha_registro  TIMESTAMP,

    FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON DELETE CASCADE,

    FOREIGN KEY (id_situacion)
        REFERENCES situaciones (id_situacion)
        ON DELETE CASCADE
);

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

CREATE TABLE tickets_soporte
(
    id_ticket       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario      BIGINT NOT NULL,
    titulo          VARCHAR(200) NOT NULL,
    descripcion     TEXT NOT NULL,
    tipo            VARCHAR(50) NOT NULL,
    categoria       VARCHAR(50) NOT NULL,
    estado          VARCHAR(50) NOT NULL DEFAULT 'abierto',
    creado_en       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en  TIMESTAMP,
    cerrado_en      TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario)
        ON DELETE CASCADE
);

-- ==============================
-- DATOS INICIALES PARA TESTS
-- ==============================

INSERT INTO usuarios (nombre, apellido, email, password, rol, activo)
VALUES ('Admin', 'Principal', 'admin@amani.com',
        '$2b$12$Ln0bfXA8cJR.W/NW4.XipOJGsNqvS70SZDJ9KuAXftKGnMrPyMzJe',
        'admin', TRUE);

