# API y Endpoints REST

La API de Amani adopta las mejores prácticas de **RESTful API Design**, utilizando verbos HTTP (`GET`, `POST`, `PUT`, `DELETE`) de forma semántica. 

## Especificación OpenAPI 3.0

Todas las interacciones de red se auto-documentan utilizando **springdoc-openapi**.

### Accesos Swagger
En el entorno local o de desarrollo, la especificación en vivo se puede consultar en:
- Interfaz Gráfica UI: `http://localhost:8080/swagger-ui/index.html`
- JSON Raw: `http://localhost:8080/v3/api-docs`

> [!NOTE]
> Para probar endpoints seguros desde la UI de Swagger, es imperativo hacer click en el candado superior "Authorize" e inyectar el Bearer Token devuelto por `/auth/login`.

## Dominios de Operación

### 1. Autenticación (`/auth/**`)
Expone la interfaz pública (desprotegida de JWT) para la generación de tokens, el registro de nuevos pacientes en el sistema y la rotación de claves.

### 2. Paciente (`/paciente/**`)
Endpoints restringidos por rol de seguridad `ROLE_PACIENTE`. 
Permiten interacciones orientadas a solicitar citas, cancelar horarios o llenar formularios terapéuticos y diarios emocionales.

### 3. Psicólogo (`/psicologo/**`)
Asegurados bajo `ROLE_PSICOLOGO`.
Habilitan la capacidad para aceptar citas pendientes, bloquear horarios, consultar el expediente de pacientes adscritos o crear perfiles de paciente internamente.

### 4. Administrador (`/admin/**`)
Restringidos a `ROLE_ADMIN`.
Acceso a todos los recursos del sistema sin delimitaciones, posibilitando la auditoría clínica completa y la gestión global.
