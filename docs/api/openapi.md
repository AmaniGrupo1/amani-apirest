# OpenAPI (Swagger)

Visualización inline de la especificación OpenAPI generada por SpringDoc.

!!! note "Generación de la especificación"
    El archivo `openapi.json` se genera automáticamente durante la fase `verify` de Maven:
    ```bash
    ./mvnw verify -DskipTests
    ```
    El resultado se escribe en `../documentacion-amani/api/openapi/openapi.json`.

---

<swagger-ui src="openapi.yml" />

---

## 🔗 Enlaces útiles

- [Swagger UI en local](http://localhost:8080/swagger-ui.html)
- [API Docs YAML](http://localhost:8080/v3/api-docs.yaml)
- [SpringDoc OpenAPI](https://springdoc.org/)
