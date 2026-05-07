---
hide:
  - navigation
  - toc
---

# 🩺 AMANI API REST

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" />
  <img src="https://img.shields.io/badge/JWT-HS256-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" />
  <img src="https://img.shields.io/badge/WebSocket-STOMP-010101?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Firebase-Admin_SDK-DD2C00?style=for-the-badge&logo=firebase&logoColor=white" />
  <img src="https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" />
</p>

<p align="center">
  <strong>Plataforma de psicología clínica</strong> que conecta pacientes, psicólogos y administradores.<br/>
  API REST con mensajería en tiempo real, diario emocional, citas, historial clínico y notificaciones push.
</p>

---

## 🚀 Inicio rápido

### 1. Instalar uv

```bash
curl -LsSf https://astral.sh/uv/install.sh | sh
```

### 2. Instalar dependencias

```bash
uv sync
```

### 3. Servidor local con hot-reload

```bash
uv run mkdocs serve
```

El sitio estará disponible en `http://localhost:8000`.

---

## 📚 Secciones de la documentación

<div class="grid cards" markdown>

- :material-api: **[API REST](api/rest-api.md)**
  Referencia de endpoints, modelos de datos y especificación OpenAPI.

- :material-test-tube: **[Testing](testing/index.md)**
  Plan de pruebas, cobertura de código y problemas conocidos.

- :material-chart-line: **[Informes](reports/index.md)**
  Dashboards interactivos de tests y métricas de calidad.

- :material-book-open: **[Guías](guides/index.md)**
  Configuración local, despliegue en GCP y solución de problemas Firebase.

- :material-postman: **[Postman](postman/index.md)**
  Colección de requests para testing manual de la API.

- :material-history: **[Changelog](changelog.md)**
  Registro de cambios de integración y evolución del proyecto.

</div>

---

## 🛠 Stack Tecnológico

| Capa | Tecnología |
|---|---|
| **Lenguaje** | Java 21 |
| **Framework** | Spring Boot 4.0.3 |
| **Build** | Apache Maven |
| **ORM** | Spring Data JPA / Hibernate |
| **Base de datos** | PostgreSQL |
| **Seguridad** | Spring Security + JWT (HS256) |
| **Tiempo real** | WebSocket (STOMP sobre SockJS) |
| **Notificaciones push** | Firebase Admin SDK |
| **Email** | Spring Boot Mail (JavaMailSender) |
| **Documentación API** | SpringDoc OpenAPI / Swagger UI |

---

## 🏗 Arquitectura de alto nivel

```mermaid
graph TD
    A[Cliente Android<br/>Kotlin / Jetpack Compose] -->|HTTPS + JWT| B[AMANI API REST<br/>Spring Boot]
    C[Firebase RTDB + FCM] -->|Push / Tiempo real| A
    B -->|Admin SDK| C
    B -->|JDBC| D[(PostgreSQL)]
    B -->|SMTP| E[Mailpit / SendGrid]
    F[GCP Cloud Run] --> B
```

---

## 📂 Repositorios relacionados

- [amani-apirest](https://github.com/AmaniGrupo1/amani-apirest) — Backend (este repo)
- [AmaniAndroid](https://github.com/AmaniGrupo1/AmaniAndroid) — Cliente Android

---

## 📄 Licencia

Proyecto privado. Todos los derechos reservados © 2026 Amani Team.
