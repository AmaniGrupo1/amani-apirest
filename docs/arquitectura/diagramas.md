# Diagramas de Arquitectura

## Diagrama de Flujo (Peticiones REST)

El siguiente diagrama detalla cómo viaja una petición desde el cliente web/móvil hasta llegar a la capa de base de datos.

```mermaid
graph TD
    Client[Cliente REST / App] -->|HTTPS / JWT| SecurityFilter(Filtro JWT)
    SecurityFilter -->|Autorización| Controller(Controller)
    Controller -->|DTO Request| Validator[Jakarta Validator]
    Validator -->|DTO Válido| Service(Service Layer)
    Service -->|Operación Transaccional| Ext[Integraciones Externas Stripe/Firebase]
    Service -->|Entidad| Repository(Spring Data Repository)
    Repository -->|Hibernate / JPA| DB[(PostgreSQL)]
    DB -->|Entidad| Repository
    Repository -->|Entidad| Service
    Service -->|DTO Response| Controller
    Controller -->|JSON| Client
```

## Flujo de Autenticación (Login)

```mermaid
sequenceDiagram
    participant User as Usuario
    participant API as AuthController
    participant Auth as AuthService
    participant DB as Base de Datos
    participant JWT as JwtUtil

    User->>API: POST /auth/login {email, password}
    API->>Auth: login(request)
    Auth->>DB: findByEmail(email)
    DB-->>Auth: Entidad Usuario
    Auth->>Auth: Verificar isActivo == true
    Auth->>Auth: matches(password, hash)
    Auth->>JWT: generateToken(UserDetails)
    JWT-->>Auth: tokenString
    Auth-->>API: LoginResponseDTO {token, userId, rol}
    API-->>User: 200 OK + Payload
```

## Relación de Entidades Base (Roles)

```mermaid
erDiagram
    USUARIO ||--o| PACIENTE : "1:1"
    USUARIO ||--o| PSICOLOGO : "1:1"
    PACIENTE }|--|{ PSICOLOGO : "Asignación M:N"
    PACIENTE ||--o{ CITA : "Solicita"
    PSICOLOGO ||--o{ CITA : "Atiende"

    USUARIO {
        Long idUsuario
        String email
        String password
        RolUsuario rol
        Boolean activo
    }
    CITA {
        Long idCita
        DateTime startDatetime
        EstadoCita estado
    }
```
