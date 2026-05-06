# 🩺 Amani — API REST Backend

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/JWT-HS256-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" />
  <img src="https://img.shields.io/badge/WebSocket-STOMP-010101?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Firebase-Admin_SDK-DD2C00?style=for-the-badge&logo=firebase&logoColor=white" />
  <img src="https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" />
  <img src="https://img.shields.io/badge/Licencia-Privada-red?style=for-the-badge" />
</p>

<p align="center">
  API REST para la plataforma de psicología clínica <strong>Amani</strong>.<br/>
  Gestiona el flujo completo entre <strong>administradores</strong>, <strong>psicólogos</strong> y <strong>pacientes</strong>:<br/>
  registro de usuarios, citas, sesiones, historiales clínicos, diario emocional,<br/>
  mensajería en tiempo real, test inicial y notificaciones push.
</p>

---

## 📋 Tabla de Contenidos

- [🛠 Stack Tecnológico](#-stack-tecnológico)
- [📁 Requisitos Previos](#-requisitos-previos)
- [🚀 Instalación y Configuración](#-instalación-y-configuración)
- [🔑 Primer Acceso](#-primer-acceso)
- [📂 Estructura del Proyecto](#-estructura-del-proyecto)
- [✨ Funcionalidades](#-funcionalidades)
- [🏗 Arquitectura](#-arquitectura)
- [⚙️ Variables de Configuración](#️-variables-de-configuración)
- [📬 Colección Postman](#-colección-postman)
- [🔍 Calidad de Código](#-calidad-de-código)
- [👤 Contribuidores](#-contribuidores)
- [📄 Licencia](#-licencia)

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
| **Documentación** | SpringDoc OpenAPI / Swagger UI |

---

## 📁 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- ✅ **Java 21** (JDK)
- ✅ **PostgreSQL** corriendo en el puerto `5433`
- ✅ **Maven** (o usa el wrapper incluido `./mvnw`)
- ✅ *(Opcional)* Archivo `serviceAccountKey.json` de Firebase para notificaciones push

---

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/AmaniGrupo1/amani-apirest.git
cd amani-apirest
```

### 2. Crear la base de datos

El esquema SQL se encuentra en `src/main/resources/amani.sql`. Créalo y ejecútalo:

```bash
psql -U postgres -f src/main/resources/amani.sql
```

Esto genera el esquema `psicologia_app` con todas las tablas, índices, vistas y datos iniciales.

### 3. Configurar las credenciales

Edita `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/psicologia_app
spring.datasource.username=postgres
spring.datasource.password=TU_CONTRASEÑA
```

> ⚠️ **Nunca subas credenciales reales al repositorio.** Usa variables de entorno o un archivo `.env` ignorado por `.gitignore`.

### 4. (Opcional) Configurar Firebase

Coloca el archivo `serviceAccountKey.json` en `src/main/resources/` para habilitar las notificaciones push.

### 5. Compilar y ejecutar

```bash
# Modo desarrollo
./mvnw spring-boot:run

# Generar JAR y ejecutar
./mvnw clean package -DskipTests
java -jar target/amaniApirest-0.0.1-SNAPSHOT.jar
```

### 6. Acceder a la documentación

Una vez en marcha, Swagger UI está disponible en:

```
http://localhost:8080/docs
```

---

## 🔑 Primer Acceso

Al iniciar la aplicación se crea automáticamente un usuario administrador por defecto:

| Campo | Valor |
|---|---|
| **Email** | `admin@amani.com` |
| **Contraseña** | `admin1234` |

> ⚠️ **Importante:** Cambia esta contraseña antes de desplegar en producción.

---

## 📂 Estructura del Proyecto

```
src/main/java/com/amani/amaniapirest/
│
├── configuration/              # Security, JWT, WebSocket, Firebase, CORS…
│
├── controllers/
│   ├── chat/                   # WebSocket — mensajería en tiempo real
│   ├── controladorAdministador/# Endpoints de administrador
│   ├── controladorPaciente/    # Endpoints de paciente
│   ├── controladorPsicologo/   # Endpoints de psicólogo
│   ├── login/                  # Autenticación y registro
│   ├── preguntasController/    # Test inicial
│   ├── profileController/      # Fotos de perfil
│   └── situacionController/    # Catálogo de situaciones
│
├── dto/                        # Data Transfer Objects (por rol)
├── enums/                      # RolUsuario, EstadoCita, MetodoPago, EstadoPago
├── events/                     # Eventos de aplicación (CitaCreada, MensajeNuevo…)
├── listeners/                  # Listeners (email, push, WebSocket)
├── mappers/                    # Mapeo de entidades a DTOs
├── models/                     # Entidades JPA
├── repositories/               # Repositorios JPA
├── services/                   # Lógica de negocio (por rol)
│
└── AmaniApirestApplication.java
```

---

## ✨ Funcionalidades

### 🔐 Autenticación y Usuarios

- Login con JWT (caducidad de 24 h)
- Registro público de pacientes (con gestión de menores y tutores)
- Roles: `ADMIN`, `PSICOLOGO`, `PACIENTE`

### 📅 Citas y Agenda

| Funcionalidad | Detalle |
|---|---|
| CRUD de citas | Estados: `PENDIENTE`, `CONFIRMADA`, `CANCELADA`, `COMPLETADA` |
| Agenda mensual | Vista por psicólogo |
| Disponibilidad diaria | Con detección de conflictos |
| Horario semanal | Recurrente + bloqueos de agenda |
| Recordatorios | Automáticos 24 h antes (email + push) |

### 🏥 Clínica

- Sesiones de terapia
- Historial clínico por paciente
- Diario emocional (emoción, intensidad 1–10, nota)
- Progreso emocional (estrés, ansiedad, estado de ánimo)
- Test inicial con preguntas, opciones y respuestas

### 💬 Comunicación en Tiempo Real

- Mensajería entre usuarios con marca de lectura
- **Entrega inteligente:**
  - WebSocket (STOMP) si el usuario está **online**
  - Firebase push si el usuario está **offline**
- Notificaciones por email (citas creadas/canceladas, registro)

### 👤 Perfil y Configuración

- Fotos de perfil con avatar por defecto
- Direcciones de paciente
- Ajustes de usuario (idioma, notificaciones, modo oscuro, zona horaria)

---

## 🏗 Arquitectura

La API sigue una arquitectura por capas con separación por roles:

```
Controller (por rol)
      │
      ▼
Service (por rol)
      │
      ▼
Repository
      │
      ▼
Models (JPA)
```

Las notificaciones siguen un patrón **event-driven** con `ApplicationEvent` y `@TransactionalEventListener`, desacoplando la lógica de negocio del envío de emails y notificaciones push:

```
Service ──► ApplicationEvent ──► @TransactionalEventListener
                                        ├── EmailListener
                                        ├── PushListener (Firebase)
                                        └── WebSocketListener (STOMP)
```

---

## ⚙️ Variables de Configuración

Principales propiedades en `application.properties`:

| Propiedad | Valor por defecto | Descripción |
|---|---|---|
| `server.port` | `8080` | Puerto de la aplicación |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5433/postgres` | URL de la base de datos |
| `spring.jpa.properties.hibernate.default_schema` | `psicologia_app` | Esquema PostgreSQL |
| `jwt.secret` | *(configurar en producción)* | Clave de firmado JWT |
| `jwt.expiration` | `86400000` | Caducidad del token (ms) — 24 h |
| `spring.mail.host` | `localhost` | Servidor SMTP |
| `spring.mail.port` | `1025` | Puerto SMTP |
| `file.upload-dir` | `uploads` | Directorio de subida de archivos |
| `springdoc.swagger-ui.path` | `/docs` | Ruta de Swagger UI |

---

## 📬 Colección Postman

Se incluye una colección de Postman lista para importar y probar todos los endpoints:

```
doc/postman/amani-api.postman_collection.json
```

---

## 🔍 Calidad de Código

El proyecto incluye plugins de Maven para análisis estático:

| Herramienta | Propósito |
|---|---|
| **JaCoCo** | Cobertura de tests |
| **SpotBugs** | Detección de bugs en bytecode |
| **PMD** | Detección de code smells |
| **Checkstyle** | Estilo de código (guía Google) |
| **SonarQube** | Integración continua de calidad |

Generar informe completo:

```bash
./mvnw site
bash generate-pdf-report.sh
```

---

## 👤 Contribuidores

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/irilopa">
        <img src="https://github.com/irilopa.png" width="80px" style="border-radius:50%" /><br/>
        <sub><b>Ivan Lopez Rilopa</b></sub>
      </a><br/>
      <sub>Backend / Arquitectura</sub><br/>
      </td>
    <td align="center">
      <a href="https://github.com/FelixPatricio29">
        <img src="https://github.com/FelixPatricio29.png" width="80px" style="border-radius:50%" /><br/>
        <sub><b>Felix Patricio Peñafel Burgos</b></sub>
      </a><br/>
      <sub>Backend / Lógica</sub><br/>
    </td>
  </tr>
</table>

---

## 🏗️ Arquitectura Firebase (Nueva)

La integración Firebase sigue el patrón **Gateway/Adapter** para desacoplar
el dominio de la infraestructura:

```
Domain Layer (sin dependencias Firebase)
  ├── FirebaseAuthController  → FirebaseTokenGateway (interface)
  ├── MensajeEventListener   → ChatGateway + PushNotificationGateway (interfaces)
  ├── CitaEventListener     → PushNotificationGateway (interface)
  └── NotificationServices   → PushNotificationGateway (interface)
        │
        ▼
Infrastructure Layer (implementaciones concretas)
  ├── firebase.enabled=true  → FirebaseChatGateway + FirebasePushNotificationGateway + FirebaseTokenGatewayImpl
  └── firebase.enabled=false → NoOpChatGateway + NoOpPushNotificationGateway + NoOpFirebaseTokenGateway
```

**Perfiles Spring:**

| Perfil | Comando | Firebase | Base de datos |
|---|---|---|---|
| `local` | `-Dspring.profiles.active=local` | No-op (desactivado) | PostgreSQL local |
| `local` + emuladores | `firebase.emulator.enabled=true` | Emuladores | PostgreSQL local |
| `gcp` | `-Dspring.profiles.active=gcp` | Real (ADC) | Cloud SQL |
| `test` | `@ActiveProfiles("test")` | No-op (mocks) | H2 en memoria |

📖 Ver [README_LOCAL.md](README_LOCAL.md) para desarrollo local.
📖 Ver [README_GCP.md](README_GCP.md) para despliegue en GCP.


## 📄 Licencia

Este proyecto es **privado**. Todos los derechos reservados.

Desarrollado como parte del currículo del **IES Enrique Tierno Galvan**.

---

<p align="center">
  Hecho con ❤️ y Java por el equipo <strong>Amani</strong>
</p>
