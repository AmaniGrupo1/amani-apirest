# Referencia de endpoints

Listado de los endpoints REST principales de AMANI, organizados por dominio.

!!! note "Estado de la documentación"
    La fuente de verdad más actualizada es la especificación **OpenAPI/Swagger** generada automáticamente por SpringDoc.

---

## 🔐 Autenticación

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/auth/login` | Login con email y password |
| `POST` | `/auth/register` | Registro de nuevo usuario |
| `POST` | `/auth/firebase-token` | Intercambio de token Firebase por JWT propio |

---

## 👤 Usuarios y perfiles

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/usuarios/perfil` | Perfil del usuario autenticado |
| `PUT` | `/api/usuarios/perfil` | Actualizar perfil |
| `POST` | `/api/usuarios/avatar` | Subir avatar |

---

## 📅 Citas y agenda

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/citas` | Listar citas del usuario |
| `POST` | `/api/citas` | Crear nueva cita |
| `PUT` | `/api/citas/{id}` | Modificar cita |
| `DELETE` | `/api/citas/{id}` | Cancelar cita |
| `GET` | `/api/psicologos/{id}/agenda` | Disponibilidad de un psicólogo |

---

## 🧠 Psicólogos

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/psicologos` | Listar psicólogos activos |
| `GET` | `/api/psicologos/{id}` | Detalle de psicólogo |
| `GET` | `/api/psicologos/{id}/pacientes` | Pacientes asignados |

---

## 💬 Mensajes (chat)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/chats/conversations` | Conversaciones del usuario |
| `POST` | `/api/chats/messages` | Enviar mensaje (persiste en PostgreSQL y RTDB) |
| `GET` | `/api/chats/history/{roomId}` | Historial de mensajes de una sala |

!!! warning "Arquitectura dual"
    El backend persiste en PostgreSQL y publica en Firebase RTDB.
    La app Android puede leer/escribir directamente en RTDB para tiempo real.

---

## 📔 Diario emocional

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/diario` | Entradas del diario del paciente |
| `POST` | `/api/diario` | Nueva entrada |
| `GET` | `/api/diario/{id}` | Detalle de entrada |

---

## 📊 Progreso emocional

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/progreso` | Evolución emocional del paciente |
| `POST` | `/api/progreso` | Registrar métrica |

---

## 🏥 Historial clínico

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/historial/{pacienteId}` | Historial clínico de un paciente |
| `POST` | `/api/historial` | Añadir nota clínica |

---

## 🔔 Notificaciones

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/notificaciones` | Notificaciones del usuario |
| `PUT` | `/api/notificaciones/{id}/leer` | Marcar como leída |

---

## ⚙️ Administración

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/admin/usuarios` | Listar todos los usuarios |
| `PUT` | `/api/admin/usuarios/{id}/estado` | Activar / desactivar usuario |
| `GET` | `/api/admin/estadisticas` | Estadísticas del sistema |

---

## 📎 Códigos de respuesta

| Código | Significado |
|---|---|
| `200 OK` | Éxito |
| `201 Created` | Recurso creado |
| `400 Bad Request` | Datos inválidos |
| `401 Unauthorized` | Token ausente o inválido |
| `403 Forbidden` | Sin permisos para el recurso |
| `404 Not Found` | Recurso no existe |
| `409 Conflict` | Conflicto de negocio (ej. solapamiento de cita) |
| `500 Internal Server Error` | Error inesperado del servidor |
