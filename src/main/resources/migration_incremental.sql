-- ============================================================
-- AMANI — Script SQL Incremental (ALTER TABLE)
-- Lleva la BD del schema actual al schema definitivo v2
-- ⚠️  Ejecutar con: SET search_path TO psicologia_app;
-- ⚠️  NO contiene DROP TABLE ni CREATE TABLE
-- ============================================================

SET search_path TO psicologia_app;

-- ==============================
-- usuarios — ajuste de columnas
-- ==============================

-- fcm_token: 512 → 255 chars (estándar FCM actual)
ALTER TABLE usuarios ALTER COLUMN fcm_token TYPE VARCHAR(255);

-- dni: 50 → 20 chars + constraint UNIQUE
ALTER TABLE usuarios ALTER COLUMN dni TYPE VARCHAR(20);
ALTER TABLE usuarios ADD CONSTRAINT usuarios_dni_unique UNIQUE (dni);

-- foto_perfil_url: VARCHAR(500) → TEXT (URLs sin límite fijo)
ALTER TABLE usuarios ALTER COLUMN foto_perfil_url TYPE TEXT;


-- ==============================
-- pacientes — eliminar columnas obsoletas + NOT NULL
-- ==============================

-- metodo_pago y estado_pago se gestionan ahora en la tabla pagos
ALTER TABLE pacientes DROP COLUMN IF EXISTS metodo_pago;
ALTER TABLE pacientes DROP COLUMN IF EXISTS estado_pago;

-- Garantizar NOT NULL en auditoría (deben tener DEFAULT, por lo que SET NOT NULL es seguro)
ALTER TABLE pacientes ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE pacientes ALTER COLUMN updated_at SET NOT NULL;


-- ==============================
-- psicologos — nueva columna + NOT NULL
-- ==============================

-- duracion_default: nueva columna con valor por defecto 50 minutos
ALTER TABLE psicologos
    ADD COLUMN IF NOT EXISTS duracion_default INT NOT NULL DEFAULT 50;

ALTER TABLE psicologos ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE psicologos ALTER COLUMN updated_at SET NOT NULL;


-- ==============================
-- tipos_terapia — ajuste de nombre
-- ==============================

-- nombre: VARCHAR(150) → VARCHAR(100) (alineado con SQL definitivo)
ALTER TABLE tipos_terapia ALTER COLUMN nombre TYPE VARCHAR(100);


-- ==============================
-- citas — id_tipo_terapia NOT NULL + FK RESTRICT + NOT NULL auditoría
-- ==============================

-- Crear tipo enum modalidad_cita si no existe
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type t JOIN pg_namespace n ON t.typnamespace = n.oid
                   WHERE t.typname = 'modalidad_cita' AND n.nspname = 'psicologia_app') THEN
        CREATE TYPE modalidad_cita AS ENUM ('PRESENCIAL', 'LLAMADA');
    END IF;
END $$;

-- Añadir columna modalidad si no existe, o cambiar su tipo al enum
ALTER TABLE citas ADD COLUMN IF NOT EXISTS modalidad modalidad_cita NOT NULL DEFAULT 'PRESENCIAL';
-- Si la columna ya existe como VARCHAR, convertirla al enum
ALTER TABLE citas ALTER COLUMN modalidad TYPE modalidad_cita USING modalidad::modalidad_cita;

-- Primero eliminar la FK existente (SET NULL) para recrearla como RESTRICT
ALTER TABLE citas DROP CONSTRAINT IF EXISTS citas_id_tipo_terapia_fkey;

-- Poner NOT NULL (asegurarse de que no hay NULLs antes; en dev es seguro)
ALTER TABLE citas ALTER COLUMN id_tipo_terapia SET NOT NULL;

-- Recrear FK con ON DELETE RESTRICT
ALTER TABLE citas
    ADD CONSTRAINT citas_id_tipo_terapia_fkey
        FOREIGN KEY (id_tipo_terapia)
            REFERENCES tipos_terapia (id_tipo)
            ON DELETE RESTRICT;

ALTER TABLE citas ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE citas ALTER COLUMN updated_at SET NOT NULL;


-- ==============================
-- pagos — nueva columna fecha_creacion
-- ==============================

-- fecha_creacion: timestamp de creación del registro de pago
ALTER TABLE pagos
    ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;


-- ==============================
-- sesiones — NOT NULL en auditoría
-- ==============================

ALTER TABLE sesiones ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE sesiones ALTER COLUMN updated_at SET NOT NULL;


-- ==============================
-- historial_clinico — creado_en NOT NULL
-- ==============================

ALTER TABLE historial_clinico ALTER COLUMN creado_en SET NOT NULL;


-- ==============================
-- bloqueos_agenda — motivo TEXT + created_at NOT NULL
-- ==============================

-- motivo: VARCHAR(200) → TEXT
ALTER TABLE bloqueos_agenda ALTER COLUMN motivo TYPE TEXT;
ALTER TABLE bloqueos_agenda ALTER COLUMN created_at SET NOT NULL;


-- ==============================
-- horario_psicologo — NOT NULL en auditoría
-- ==============================

ALTER TABLE horario_psicologo ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE horario_psicologo ALTER COLUMN updated_at SET NOT NULL;


-- ==============================
-- psicologo_paciente — fecha_inicio NOT NULL
-- ==============================

ALTER TABLE psicologo_paciente ALTER COLUMN fecha_inicio SET NOT NULL;


-- ==============================
-- tutores — ajuste de longitudes
-- ==============================

-- nombre: VARCHAR(255) → VARCHAR(100)
ALTER TABLE tutores ALTER COLUMN nombre TYPE VARCHAR(100);
-- tipo: VARCHAR(50) → VARCHAR(20)
ALTER TABLE tutores ALTER COLUMN tipo TYPE VARCHAR(20);


-- ==============================
-- consentimientos — nuevo campo + ajuste de longitudes
-- ==============================

-- acepta_terminos: nuevo campo BOOLEAN NOT NULL DEFAULT FALSE
ALTER TABLE consentimientos
    ADD COLUMN IF NOT EXISTS acepta_terminos BOOLEAN NOT NULL DEFAULT FALSE;

-- version_documento: VARCHAR(255) → VARCHAR(20)
ALTER TABLE consentimientos ALTER COLUMN version_documento TYPE VARCHAR(20);

-- fecha_aceptacion: quitar NOT NULL para permitir DEFAULT CURRENT_TIMESTAMP
ALTER TABLE consentimientos ALTER COLUMN fecha_aceptacion SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE consentimientos ALTER COLUMN fecha_aceptacion DROP NOT NULL;


-- ==============================
-- ÍNDICES NUEVOS — mensajería
-- ==============================

-- Bandeja de enviados: filtrar mensajes por emisor + fecha
CREATE INDEX IF NOT EXISTS idx_mensajes_sender   ON mensajes (sender_id, enviado_en);
-- Bandeja de entrada: filtrar por destinatario + estado de lectura
CREATE INDEX IF NOT EXISTS idx_mensajes_receiver ON mensajes (receiver_id, leido);


-- ==============================
-- VISTAS — recrear para asegurar consistencia
-- ==============================

CREATE OR REPLACE VIEW vista_agenda_psicologo AS
SELECT p.id_psicologo,
       u.nombre || ' ' || COALESCE(u.apellido, '')                              AS nombre_psicologo,
       c.start_datetime::DATE                                                   AS fecha,
       c.start_datetime::TIME                                                   AS hora_inicio,
       (c.start_datetime + (c.duration_minutes || ' minutes')::INTERVAL)::TIME  AS hora_fin,
       'cita'                                                                   AS tipo,
       c.estado::TEXT                                                           AS detalle,
       c.id_cita                                                                AS referencia_id
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

