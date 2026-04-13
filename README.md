# Amani API REST

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-HS256-000000?logo=jsonwebtokens&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-010101?logo=websocket&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Admin_SDK-DD2C00?logo=firebase&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?logo=swagger&logoColor=black)
![License](https://img.shields.io/badge/License-Private-red)

API REST para la plataforma de psicología clínica **Amani**. Gestiona el flujo completo entre administradores, psicólogos y pacientes: registro de usuarios, citas, sesiones, historiales clínicos, diario emocional, progreso emocional, mensajería en tiempo real, test inicial y notificaciones.

## Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.3 |
| Build | Apache Maven |
| ORM | Spring Data JPA / Hibernate |
| Base de datos | PostgreSQL |
| Seguridad | Spring Security + JWT (HS256) |
| Tiempo real | WebSocket (STOMP sobre SockJS) |
| Notificaciones push | Firebase Admin SDK |
| Email | Spring Boot Mail (JavaMailSender) |
| Documentación | SpringDoc OpenAPI / Swagger UI |

## Requisitos previos

- **Java 21** (JDK)
- **PostgreSQL** (puerto 5433 por defecto)
- **Maven** (o usar el wrapper incluido `./mvnw`)

## Instalación

1. **Clonar el repositorio:**

   ```bash
   git clone https://github.com/AmaniGrupo1/amani-apirest.git
   cd amani-apirest
   ```

2. **Crear la base de datos:**

   El esquema SQL se encuentra en `src/main/resources/amani.sql`. Crea la base de datos y ejecuta el script:

   ```bash
   psql -U postgres -f src/main/resources/amani.sql
   ```

   Esto crea el esquema `psicologia_app` con todas las tablas, índices, vistas y datos iniciales.

3. **Configurar las credenciales:**

   Edita `src/main/resources/application.properties` con tus datos:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5433/psicologia_app
   spring.datasource.username=postgres
   spring.datasource.password=Sandia4you
   ```

4. **(Opcional) Configurar Firebase:**

   Coloca el archivo `serviceAccountKey.json` en `src/main/resources/` para habilitar las notificaciones push.

5. **Compilar y ejecutar:**

   ```bash
   ./mvnw spring-boot:run
   ```

   O compilar el JAR:

   ```bash
   ./mvnw clean package -DskipTests
   java -jar target/amaniApirest-0.0.1-SNAPSHOT.jar
   ```

6. **Acceder a la documentación:**

   Swagger UI disponible en: `http://localhost:8080/docs`

## Primer acceso

Al iniciar la aplicación se crea automáticamente un usuario administrador por defecto:

| Campo | Valor |
|---|---|
| Email | `admin@amani.com` |
| Contraseña | `admin1234` |

> **Importante:** Cambia esta contraseña en producción.

## Estructura del proyecto

```
src/main/java/com/amani/amaniapirest/
├── configuration/          # Security, JWT, WebSocket, Firebase, etc.
├── controllers/
│   ├── chat/                # WebSocket chat
│   ├── controladorAdministador/  # Endpoints de administrador
│   ├── controladorPaciente/     # Endpoints de paciente
│   ├── controladorPsicologo/    # Endpoints de psicólogo
│   ├── login/               # Autenticación y registro
│   ├── preguntasController/ # Test inicial
│   ├── profileController/   # Fotos de perfil
│   └── situacionController/ # Catálogo de situaciones
├── dto/                     # Data Transfer Objects (por rol)
├── enums/                   # RolUsuario, EstadoCita, MetodoPago, EstadoPago
├── events/                  # Eventos de aplicación (CitaCreada, MensajeNuevo, etc.)
├── listeners/               # Listeners (email, push, WebSocket)
├── mappers/                 # Mapeo de entidades a DTOs
├── models/                  # Entidades JPA
├── repositories/            # Repositorios JPA
├── services/                # Lógica de negocio (por rol)
└── AmaniApirestApplication.java
```

## Funcionalidades principales

### Autenticación y usuarios

- Login con JWT (caducidad de 24h)
- Registro público de pacientes (con gestión de menores y tutores)
- Gestión de roles: `ADMIN`, `PSICOLOGO`, `PACIENTE`

### Citas y agenda

- CRUD de citas con estados (`PENDIENTE`, `CONFIRMADA`, `CANCELADA`, `COMPLETADA`)
- Agenda mensual por psicólogo
- Disponibilidad diaria con detección de conflictos
- Horario semanal recurrente y bloqueos de agenda
- Recordatorios automáticos 24h antes (email + push)

### Clínica

- Sesiones de terapia
- Historial clínico por paciente
- Diario emocional (emoción, intensidad 1-10, nota)
- Progreso emocional (niveles de estrés, ansiedad, estado de ánimo)
- Test inicial de preguntas con opciones y respuestas

### Comunicación

- Mensajería entre usuarios con marca de lectura
- Entrega inteligente: WebSocket si el usuario está online, Firebase push si está offline
- Notificaciones por email (citas creadas, canceladas, registro)

### Perfil y configuración

- Fotos de perfil con avatar por defecto
- Direcciones de paciente
- Ajustes de usuario (idioma, notificaciones, modo oscuro, zona horaria)

## Arquitectura

La API sigue una arquitectura por capas con separación por roles:

```
Controller (por rol) → Service (por rol) → Repository → Models
```

Las notificaciones usan un patrón **event-driven** con `ApplicationEvent` y `@TransactionalEventListener`, lo que desacopla la lógica de negocio del envío de emails y push.

## Variables de configuración

Las principales propiedades en `application.properties`:

| Propiedad | Valor por defecto | Descripción |
|---|---|---|
| `server.port` | `8080` | Puerto de la aplicación |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5433/postgres` | URL de la base de datos |
| `spring.jpa.properties.hibernate.default_schema` | `psicologia_app` | Esquema PostgreSQL |
| `jwt.secret` | *(configurar en producción)* | Clave de firmado JWT |
| `jwt.expiration` | `86400000` | Caducidad del token (ms) |
| `spring.mail.host` | `localhost` | Servidor SMTP |
| `spring.mail.port` | `1025` | Puerto SMTP |
| `file.upload-dir` | `uploads` | Directorio de subidas |
| `springdoc.swagger-ui.path` | `/docs` | Ruta de Swagger UI |

## Colección Postman

Se incluye una colección de Postman para probar los endpoints:

```
doc/postman/amani-api.postman_collection.json
```

## Calidad de código

El proyecto incluye plugins de Maven para análisis estático:

- **JaCoCo** — cobertura de tests
- **SpotBugs** — detección de bugs
- **PMD** — code smells
- **Checkstyle** — estilo Google
- **SonarQube** — integración continua

Generar informe de calidad:

```bash
./mvnw site
bash generate-pdf-report.sh
```

## Licencia

Este proyecto es privado. Todos los derechos reservados.
