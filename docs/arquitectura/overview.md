# Arquitectura de Software

La aplicación **Amani API REST** adopta el patrón de arquitectura por capas estandarizado de Spring Boot (Layered Architecture). Esta separación asegura bajo acoplamiento, alta cohesión y facilita el testing unitario y de integración.

## Capas Principales

### 1. Controllers (Capa de Presentación)
Los `@RestController` se encargan de la interacción con el exterior:
- Validan el formato del payload utilizando `jakarta.validation`.
- Delegan la validación de negocio a la capa inferior.
- Mapean respuestas a objetos DTO o entidades genéricas como `ResponseEntity`.
- Gestionan el contrato OpenAPI (`@Operation`, `@ApiResponse`).

### 2. Services (Capa de Negocio)
Anotados con `@Service`, contienen la inteligencia y reglas core de Amani.
- Realizan validaciones exhaustivas.
- Ejecutan operaciones transaccionales marcadas con `@Transactional`.
- Gestionan integraciones con servicios externos (e.g. Firebase para notificaciones, Stripe para pagos).

### 3. Repositories (Capa de Persistencia)
Interfaces que extienden `JpaRepository` o utilizan `@Query` personalizadas.
- Se comunican de forma agnóstica con PostgreSQL a través de Hibernate.
- Aprovechan las proyecciones y optimizaciones mediante `JOIN FETCH` para evitar el problema N+1.

### 4. Models y DTOs (Capa de Datos)
- **Modelos (`@Entity`)**: Representan tablas relacionales, gestionados por JPA. Utilizan anotaciones de Lombok para generar constructores y getters sin añadir ruido.
- **DTOs (Data Transfer Objects)**: Encapsulan los datos transmitidos en los requests/responses, impidiendo la serialización de datos sensibles de la base de datos (e.g. contraseñas de Usuarios).
