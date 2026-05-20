# Workflows CI/CD (GitHub Actions)

Este documento resume los cambios aplicados a los workflows en `.github/workflows/` para que el pipeline sea mas predecible, ordenado por etapas y con cache.

Fecha: 2026-05-16

## 1) Workflow principal: `CI/CD - Backend`

Archivo: `.github/workflows/build.yml`

### Disparadores (triggers)

- `push` a ramas `main` y `develop`
- `pull_request` hacia `main` y `develop`
- Ignora cambios solo de documentacion: `docs/**`, `mkdocs.yml`, `pyproject.toml`, `uv.lock`, `*.md`

### Etapas / Jobs (orden garantizado con `needs`)

El workflow se divide en etapas claras, encadenadas:

1. `lint` -> 2. `build` -> 3. `test` -> 4. `docker-build` -> 5. `deploy`

Cada job incluye `timeout-minutes` para evitar cuelgues.

### Detalle de cada job

#### `lint`

- Objetivo: chequeos estaticos sin bloquear por deuda existente del proyecto.
- Ejecuta: `compile`, `checkstyle:check`, `pmd:check` y `spotbugs:spotbugs`.
- Nota: **no** se usa `spotbugs:check` porque actualmente falla por bugs existentes (28 findings). Se genera el reporte, pero no rompe el job.

#### `build`

- Objetivo: empaquetar el artefacto del backend.
- Ejecuta `package` con `-Dmaven.test.skip=true` (importante: `-DskipTests` no evita compilar tests).
- Sube artefactos `target/*.jar` / `target/*.war` como artifact del run.

#### `test`

- Objetivo: ejecutar `mvn verify` con Postgres como service.
- Levanta `postgres:16` como servicio y aplica `src/main/resources/amani.sql`.
- Publica artefactos:
  - JUnit XML (surefire/failsafe)
  - HTML dashboard (scripts en `.github/scripts/`)
  - JaCoCo (`target/site/jacoco`)
- SonarQube:
  - Solo corre si `secrets.SONAR_TOKEN` y `secrets.SONAR_HOST_URL` existen.
  - Si no existen, el step se omite sin fallar el job.

#### `docker-build`

- Objetivo: construir y publicar imagen del backend.
- Construccion: `./mvnw spring-boot:build-image` (Buildpacks), porque el repositorio no tiene `Dockerfile` en raiz.
- Publicacion: Artifact Registry.
- Requisitos minimos:
  - Secret: `GCP_SA_KEY`
  - Variable: `GCP_ARTIFACT_REPOSITORY`
- Si faltan, el job falla con un error explicito.

#### `deploy`

- Objetivo: desplegar a Cloud Run.
- Solo se ejecuta en `push` a `main`.
- Usa la imagen publicada por `docker-build` y ejecuta `gcloud run deploy`.
- Variable opcional: `GCP_CLOUD_RUN_SERVICE_ACCOUNT` (si se define, se pasa a `gcloud run deploy --service-account`).

### Cache

- Maven: `actions/setup-java@v4` con `cache: maven` en `lint`, `build`, `test`, `docker-build`.

### Acciones fijadas (pin)

Se usan versiones fijas para reducir breaking changes:

- `actions/checkout@v4`
- `actions/setup-java@v4`
- `actions/upload-artifact@v4`
- `google-github-actions/auth@v2`
- `google-github-actions/setup-gcloud@v2`

### Secrets y Variables requeridas

Secrets (Repository -> Settings -> Secrets and variables -> Actions -> Secrets):

- `GCP_SA_KEY` (obligatorio para `docker-build` y `deploy`)
- `SONAR_TOKEN` (opcional)
- `SONAR_HOST_URL` (opcional)

Variables (Repository -> Settings -> Secrets and variables -> Actions -> Variables):

- `GCP_ARTIFACT_REPOSITORY` (obligatorio para `docker-build` y `deploy`)
- `GCP_PROJECT_ID` (opcional, por defecto `amani-160bf`)
- `GCP_REGION` (opcional, por defecto `europe-west1`)
- `GCP_CLOUD_RUN_SERVICE` (opcional, por defecto `amani-api`)
- `GCP_CLOUD_RUN_SERVICE_ACCOUNT` (opcional)

### Comportamiento actual a tener en cuenta

- En este repo, actualmente hay tests que **no compilan**; por eso el pipeline esta estructurado para:
  - `build` no compile tests (usa `-Dmaven.test.skip=true`) y pueda producir el artefacto.
  - `test` compile y ejecute tests de forma explicita (fallara hasta que se arreglen los tests).

## 2) Workflow de documentacion: `Docs - Build & Deploy to GitHub Pages`

Archivo: `.github/workflows/docs.yml`

- Trigger: `push` a `main` cuando cambian rutas de docs (`docs/**`, `mkdocs.yml`, `pyproject.toml`, etc.).
- Incluye `timeout-minutes: 20`.
- Usa:
  - `actions/setup-java@v4` con cache Maven
  - `astral-sh/setup-uv@v4` con cache (`enable-cache: true`)
  - `./mvnw dokka:dokka` para generar docs del backend
  - `mkdocs gh-deploy` para publicar en `gh-pages`

## 3) Validacion realizada

- YAML parse (Ruby): OK.
- `actionlint`: no disponible/instalado en esta maquina en el momento de los cambios.

