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

