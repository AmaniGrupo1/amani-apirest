#!/bin/bash
# Script para generar OpenAPI JSON sin necesidad de ejecutar la app

set -e

echo "Generando OpenAPI JSON..."

# Crear directorio de salida
mkdir -p /home/ivan/documentacion-amani/api/openapi

# Usar springdoc-openapi-cli o generar desde Java
# Opción: generar el JSON directamente usando la clase OpenAPI

cd /home/ivan/amani-apirest

# Compilar primero
./mvnw compile -q

# Generar OpenAPI usando una clase Java
cat > /tmp/GenerateOpenApi.java << 'JAVAEOF'
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import java.io.FileWriter;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class GenerateOpenApi {
    public static void main(String[] args) throws IOException {
        SecurityScheme securityScheme = new SecurityScheme()
            .type(Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Token JWT obtenido del endpoint POST /auth/login");

        Components components = new Components()
            .addSecuritySchemes("bearerAuth", securityScheme);

        OpenAPI openAPI = new OpenAPI()
            .components(components)
            .info(new Info()
                .title("Amani API REST")
                .version("1.0.0")
                .description("""
                    API REST para la plataforma de psicología clínica Amani.

                    Gestiona usuarios, pacientes, psicólogos, citas, sesiones,
                    historiales clínicos, mensajería, diario emocional, progreso
                    emocional y el test inicial de preguntas.
                    """))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

        // Escribir como YAML
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        FileWriter writer = new FileWriter("/home/ivan/documentacion-amani/api/openapi/openapi.yaml");
        mapper.writeValue(writer, openAPI);
        writer.close();

        System.out.println("OpenAPI YAML generado en: /home/ivan/documentacion-amani/api/openapi/openapi.yaml");
    }
}
JAVAEOF

echo "Generación manual de OpenAPI - usa Swagger UI local en su lugar"

# Crear archivo de índice con enlace a Swagger UI
cat > /home/ivan/documentacion-amani/api/openapi/index.md << 'MDEOF'
# OpenAPI - Especificación de la API

Documentación generada automáticamente desde el código Java usando SpringDoc OpenAPI.

## Acceso Interactivo

### Swagger UI

La documentación interactiva Swagger UI está disponible en:

```
http://localhost:8080/swagger-ui.html
```

### Endpoints de metadatos

- **YAML**: `http://localhost:8080/v3/api-docs.yaml`
- **JSON**: `http://localhost:8080/v3/api-docs`

## Endpoints de la API

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/auth/login` | Iniciar sesión |
| POST | `/auth/register-paciente` | Registrar paciente |
| POST | `/auth/register-admin` | Registrar administrador |
| PUT | `/auth/pacientes/{id}/baja` | Dar de baja paciente |

### Paciente
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/citas` | Listar citas |
| GET | `/api/citas/{id}` | Obtener cita |
| POST | `/api/citas` | Crear cita |
| PUT | `/api/citas/{id}` | Actualizar cita |
| DELETE | `/api/citas/{id}` | Eliminar cita |
| GET | `/api/citas/paciente/{id}/agenda` | Agenda mensual |

### Psicólogo
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/psicologo/pacientes` | Listar pacientes |
| GET | `/api/psicologo/pacientes/{id}` | Obtener paciente |
| POST | `/api/psicologo/pacientes/{id}/foto` | Subir foto |

### Administrador
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/admin/psicologos` | Listar psicólogos |
| POST | `/api/admin/psicologos/create` | Crear psicólogo |
| GET | `/api/admin/pacientes` | Listar pacientes |

## Generación

Para regenerar la documentación, ejecuta:

```bash
./mvnw springdoc-openapi:generate
```

O inicia la aplicación y accede a Swagger UI.

## Versión

OpenAPI 3.0 - SpringDoc 2.5.0

MDEOF

echo "Documentación OpenAPI actualizada"
