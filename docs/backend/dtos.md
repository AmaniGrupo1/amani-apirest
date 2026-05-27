# Objetos de Transferencia (DTOs)

Para proteger la capa de datos y prevenir vulnerabilidades de _Mass Assignment_, Amani API REST se nutre de **Data Transfer Objects (DTOs)** en todo su esquema de red.

## Clasificación

Organizamos los DTOs en las siguientes categorías lógicas, mapeadas en `com.amani.amaniapirest.dto.*`:

- `RequestDTO`: Usados para envolver payloads provenientes del cliente HTTP (e.g., `LoginRequestDTO`, `PacienteRequestDTO`).
- `ResponseDTO`: Usados para la emisión de respuestas hacia el cliente, asegurándose de suprimir dependencias circulares o hashes de claves (e.g., `PerfilPacienteResponseDTO`).

## Open API y Tipado

Todos nuestros DTOs (incluyendo los `Record` introducidos en Java 17+) contienen anotaciones `@Schema` de Swagger:

```java
@Schema(description = "Objeto de Transferencia de Datos (DTO) para enviar el calendario mensual del psicólogo")
public record HorarioResponseDTO(
    @Schema(description = "Lista de franjas horarias disponibles")
    List<FranjaHoraria> franjas
) {}
```

## Ventajas Arquitectónicas
- **Desacoplamiento:** El formato en el que se almacena una `Cita` en PostgreSQL puede cambiar de manera independiente al contrato de respuesta en JSON que devora el frontend móvil.
- **Validación Práctica:** Usamos `@NotNull`, `@Email` y `@Size` directamente sobre los campos DTO. Las excepciones son atrapadas por nuestro `@RestControllerAdvice` unificado en `GlobalExceptionHandler`.
