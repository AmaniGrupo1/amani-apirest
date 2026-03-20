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
    fecha_baja     TIMESTAMP,
    fcm_token VARCHAR(100)
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
CREATE TABLE archivos
(
    id_archivo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_sesion BIGINT NOT NULL,
    nombre VARCHAR(200),
    tipo_mime VARCHAR(100),
    datos BYTEA NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_archivo_sesion
        FOREIGN KEY (id_sesion)
            REFERENCES sesiones (id_sesion)
            ON DELETE CASCADE
);

-- ==============================
-- Tabla preguntas
-- ==============================
CREATE TABLE preguntas
(
    id_pregunta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    texto       TEXT NOT NULL,
    tipo        VARCHAR(50) DEFAULT 'texto', -- texto, opcion_multiple, escala, etc.
    creado_en   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==============================
-- Tabla opciones (si la pregunta tiene opciones)
-- ==============================
CREATE TABLE opciones
(
    id_opcion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_pregunta BIGINT NOT NULL,
    texto       VARCHAR(200) NOT NULL,
    valor       INT, -- opcional: para escalas de 1-5, por ejemplo
    FOREIGN KEY (id_pregunta)
        REFERENCES preguntas (id_pregunta)
        ON DELETE CASCADE
);

-- ==============================
-- Respuestas del paciente
-- ==============================
CREATE TABLE respuestas
(
    id_respuesta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente  BIGINT NOT NULL,
    id_pregunta  BIGINT NOT NULL,
    id_opcion    BIGINT, -- si es opción múltiple
    texto        TEXT,   -- si es respuesta abierta
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

-- ==============================
-- Índices
-- ==============================

CREATE INDEX idx_usuarios_email ON usuarios (email);
CREATE INDEX idx_citas_start ON citas (start_datetime);
CREATE INDEX idx_archivos_sesion ON archivos(id_sesion);


-- ==============================
-- Usuarios
-- ==============================
INSERT INTO usuarios (nombre, apellido, email, password, rol)
VALUES
('Ana', 'Gomez', 'ana.gomez@example.com', 'pass123', 'paciente'),
('Luis', 'Martinez', 'luis.martinez@example.com', 'pass123', 'paciente'),
('Clara', 'Ruiz', 'clara.ruiz@example.com', 'pass123', 'psicologo'),
('Mario', 'Sanchez', 'mario.sanchez@example.com', 'pass123', 'admin');

-- ==============================
-- Pacientes
-- ==============================
INSERT INTO pacientes (id_usuario, fecha_nacimiento, genero, telefono)
VALUES
(1, '1990-05-12', 'Femenino', '600123456'),
(2, '1985-10-20', 'Masculino', '600654321');

-- ==============================
-- Psicologos
-- ==============================
INSERT INTO psicologos (id_usuario, especialidad, experiencia, descripcion, licencia)
VALUES
(3, 'Terapia Cognitivo-Conductual', 5, 'Especialista en ansiedad y estrés.', 'LIC12345');

-- ==============================
-- Direcciones
-- ==============================
INSERT INTO direcciones (id_paciente, calle, ciudad, provincia, codigo_postal, pais)
VALUES
(1, 'Calle Mayor 10', 'Madrid', 'Madrid', '28001', 'España'),
(2, 'Avenida Central 45', 'Barcelona', 'Barcelona', '08001', 'España');

-- ==============================
-- Citas
-- ==============================
INSERT INTO citas (id_paciente, id_psicologo, start_datetime, duration_minutes, estado, motivo)
VALUES
(1, 1, '2026-03-15 10:00:00', 50, 'pendiente', 'Estrés laboral'),
(2, 1, '2026-03-16 11:00:00', 50, 'confirmada', 'Ansiedad');

-- ==============================
-- Sesiones
-- ==============================
INSERT INTO sesiones (id_cita, id_paciente, id_psicologo, session_date, duration_minutes, notas, recomendaciones)
VALUES
(1, 1, 1, '2026-03-15 10:00:00', 50, 'Paciente mostró ansiedad moderada.', 'Ejercicios de respiración'),
(2, 2, 1, '2026-03-16 11:00:00', 50, 'Paciente con síntomas de estrés.', 'Diario emocional diario');

-- ==============================
-- Historial Clínico
-- ==============================
INSERT INTO historial_clinico (id_paciente, titulo, diagnostico, tratamiento, observaciones)
VALUES
(1, 'Consulta inicial', 'Estrés laboral', 'Terapia cognitivo-conductual', 'Recomendado seguimiento semanal'),
(2, 'Consulta inicial', 'Ansiedad', 'Terapia cognitivo-conductual', 'Recomendado registro de emociones');

-- ==============================
-- Diario Emociones
-- ==============================
INSERT INTO diario_emociones (id_paciente, fecha, emocion, intensidad, nota)
VALUES
(1, '2026-03-10 08:00:00', 'Ansiedad', 6, 'Tuve mucho estrés en el trabajo'),
(1, '2026-03-11 20:00:00', 'Felicidad', 8, 'Salí a pasear al parque'),
(2, '2026-03-10 09:00:00', 'Tristeza', 5, 'Problemas familiares');

-- ==============================
-- Progreso Emocional
-- ==============================
INSERT INTO progreso_emocional (id_paciente, fecha, nivel_estres, nivel_ansiedad, nivel_animo)
VALUES
(1, '2026-03-10', 6, 5, 7),
(1, '2026-03-11', 4, 3, 8),
(2, '2026-03-10', 5, 6, 5);

-- ==============================
-- Mensajes
-- ==============================
INSERT INTO mensajes (sender_id, receiver_id, id_cita, mensaje, leido)
VALUES
(1, 3, 1, 'Hola, necesito ayuda con el estrés.', TRUE),
(2, 3, 2, 'Hola, estoy un poco ansioso.', FALSE),
(3, 1, 1, 'Recibido, nos vemos en la cita.',TRUE);

-- ==============================
-- Ajustes
-- ==============================
INSERT INTO ajustes (id_usuario, idioma, notificaciones, dark_mode, timezone)
VALUES
(1, 'es', TRUE, FALSE, 'Europe/Madrid'),
(2, 'es', TRUE, TRUE, 'Europe/Madrid'),
(3, 'es', TRUE, FALSE, 'Europe/Madrid'),
(4, 'es', TRUE, FALSE, 'Europe/Madrid');

-- ==============================
-- Archivos (adjuntos a sesiones)
-- ==============================
INSERT INTO archivos (id_sesion, nombre, tipo_mime, datos)
VALUES
(1, 'sesion1_notas.pdf', 'application/pdf', decode('255044462d312e350a25d0d4c5d80a34', 'hex')),
(2, 'sesion2_notas.pdf', 'application/pdf', decode('255044462d312e350a25d0d4c5d80a34', 'hex'));

select * from preguntas
join opciones using (id_pregunta)
;
select * from pacientes;
select * from usuarios;

select * from usuarios u
where u.id_usuario in (
select p.id_usuario
from pacientes p
)
