# Pruebas e Informes (Testing)

Amani API aborda la calidad del software a través de un esquema de Testing robusto con métricas visibles.

## Tipos de Pruebas Implementadas

1. **Pruebas Unitarias (Unit Testing):** Realizadas con **JUnit 5** y **Mockito**. Estas validan piezas discretas de la arquitectura, mayormente los métodos de las clases `Service` donde reside la lógica pesada, abstrayendo (mockeando) el acceso a la base de datos para garantizar inmediatez.
2. **Pruebas de Integración (Integration Testing):** Utilizan configuraciones embebidas (como `H2` o contenedores `Testcontainers`) para verificar la comunicación end-to-end (e.g., desde el `@RestController` hasta la base de datos).

## Cobertura e Informes

Se utiliza el plugin **JaCoCo** (`org.jacoco:jacoco-maven-plugin`) durante la fase `prepare-agent` y `report` de Maven.
Tras la ejecución de las pruebas (`mvn clean test jacoco:report`), un informe analítico es autogenerado.

El equipo aspira a mantener coberturas altas en las rutas críticas (Autenticación y Pagos).
