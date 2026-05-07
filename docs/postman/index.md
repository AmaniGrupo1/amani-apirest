# Postman

Colección de requests para testing manual y exploración de la API REST.

---

## 📥 Importar colección

1. Abre Postman.
2. Haz clic en **Import** → **File**.
3. Selecciona `docs/postman/amani-api.postman_collection.json`.

---

## 🏷 Colecciones incluidas

- **Auth** — Login, registro, refresh token
- **Pacientes** — CRUD de pacientes, citas, diario emocional
- **Psicólogos** — Agenda, pacientes asignados, historial clínico
- **Admin** — Gestión de usuarios, estadísticas, soporte
- **Chat** — Conversaciones, envío de mensajes, historial

---

## 🔐 Variables de entorno

Crea un entorno Postman con estas variables:

| Variable | Valor inicial | Descripción |
|---|---|---|
| `base_url` | `http://localhost:8080/api` | URL base de la API |
| `token` | *(vacía)* | JWT obtenido tras login |

El token se asigna automáticamente con un **Tests script** en el endpoint de login:

```javascript
pm.environment.set("token", pm.response.json().token);
```
