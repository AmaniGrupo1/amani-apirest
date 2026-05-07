# Colección de Postman

Archivo: [`amani-api.postman_collection.json`](amani-api.postman_collection.json)

---

## 🚀 Cómo usar

1. Importa la colección en Postman (ver [índice de Postman](index.md)).
2. Configura las variables de entorno (`base_url`, `token`).
3. Ejecuta primero el request de **Login** para obtener el JWT.
4. El resto de requests usan automáticamente el token mediante la cabecera:

```http
Authorization: Bearer {{token}}
```

---

## 📁 Estructura de la colección

```
amani-api
├── Auth
│   ├── POST /auth/login
│   ├── POST /auth/register
│   └── POST /auth/firebase-token
├── Paciente
│   ├── GET /api/citas
│   ├── POST /api/citas
│   ├── GET /api/diario
│   └── GET /api/progreso
├── Psicólogo
│   ├── GET /api/psicologos/{id}/agenda
│   ├── GET /api/psicologos/{id}/pacientes
│   └── GET /api/historial/{pacienteId}
├── Admin
│   ├── GET /api/admin/usuarios
│   ├── PUT /api/admin/usuarios/{id}/estado
│   └── GET /api/admin/estadisticas
└── Chat
    ├── GET /api/chats/conversations
    ├── POST /api/chats/messages
    └── GET /api/chats/history/{roomId}
```
