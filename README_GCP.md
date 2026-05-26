# ☁️ Amani — Guía de Despliegue en GCP

## Arquitectura en Producción

```
GitHub Actions (CI/CD) ──► Artifact Registry (Docker)
          │
          └──► Cloud Run ──► Secret Manager ──► Firebase Admin SDK
                   │              │
                   │              └── firebase-service-account (JSON)
                   │
                   └──► Cloud SQL (PostgreSQL)
                   └──► Firebase RTDB + Auth + FCM
```

---

## 1. Configurar Infraestructura (Primer despliegue)

Para asegurar la **estabilidad, simplicidad y bajo coste**, hemos creado un script que levanta toda la infraestructura en GCP automáticamente (Cloud SQL, Artifact Registry, Secret Manager y Workload Identity para GitHub Actions).

Ejecuta este script una única vez (requieres permisos de Owner/Editor en el proyecto):

```bash
./scripts/setup-gcp-infrastructure.sh
```

El script mostrará al final una lista de variables que debes configurar en tu repositorio de GitHub (Settings > Secrets and variables > Actions):

**Variables (`Repository variables`):**
- `GCP_PROJECT_ID`: amani-160bf
- `GCP_REGION`: europe-west1
- `GCP_WORKLOAD_IDENTITY_PROVIDER`: (El valor generado por el script)
- `GCP_SERVICE_ACCOUNT`: github-actions-sa@amani-160bf.iam.gserviceaccount.com
- `GCP_ARTIFACT_REGISTRY`: europe-west1-docker.pkg.dev/amani-160bf/docker-repo

**Secrets (`Repository secrets`):**
- Crea el secreto de base de datos en GCP manualmente y añade la contraseña a Secret Manager:
  ```bash
  echo -n "tu_contraseña_fuerte" | gcloud secrets create db-password --data-file=-
  ```
  Y da permisos a la Service Account de Cloud Run (la por defecto o `github-actions-sa`):
  ```bash
  gcloud secrets add-iam-policy-binding db-password \
    --member="serviceAccount:github-actions-sa@amani-160bf.iam.gserviceaccount.com" \
    --role="roles/secretmanager.secretAccessor"
  ```

---

## 2. Despliegue Automático (CI/CD)

El despliegue está **100% automatizado** usando GitHub Actions.

Al hacer `push` a la rama `main`:
1. GitHub Actions se autentica de forma segura en GCP usando Workload Identity Federation (sin JSON keys).
2. Construye la imagen Docker eficientemente usando `jib-maven-plugin`.
3. Sube la imagen a Artifact Registry.
4. Despliega la nueva versión en Cloud Run usando Cloud SQL Auth Proxy (nativo).

### Despliegue Manual (solo si es estrictamente necesario)

Si necesitas desplegar manualmente desde tu PC usando Jib:

```bash
# 1. Autenticarse
gcloud auth login
gcloud auth configure-docker europe-west1-docker.pkg.dev

# 2. Construir imagen
./mvnw clean compile jib:build -Denv.REGISTRY_URL=europe-west1-docker.pkg.dev/amani-160bf/docker-repo

# 3. Desplegar
gcloud run deploy amani-api \
  --image europe-west1-docker.pkg.dev/amani-160bf/docker-repo/amani-apirest:latest \
  --region europe-west1 \
  --allow-unauthenticated \
  --set-env-vars=SPRING_PROFILES_ACTIVE=gcp \
  --set-env-vars=DB_URL=jdbc:postgresql:///postgres?cloudSqlInstance=amani-160bf:europe-west1:amani-db \
  --set-env-vars=DB_USERNAME=postgres \
  --set-secrets=DB_PASSWORD=db-password:latest \
  --add-cloudsql-instances=amani-160bf:europe-west1:amani-db \
  --service-account=github-actions-sa@amani-160bf.iam.gserviceaccount.com
```

---

## 3. Desarrollo local con acceso a GCP

Si necesitas probar con Firebase real y BD real en local:

```bash
# Configurar ADC
gcloud auth application-default login

# Arrancar con perfil GCP
./mvnw spring-boot:run -Dspring.profiles.active=gcp -DDB_URL="jdbc:postgresql://IP_PUBLICA_CLOUD_SQL:5432/postgres" -DDB_USERNAME=postgres -DDB_PASSWORD=tu_password
```

---

## Seguridad

### ❌ Nunca hacer

- Subir claves JSON de Service Account al repositorio
- Usar `GOOGLE_APPLICATION_CREDENTIALS` en producción
- Asignar roles de Owner o Editor a Service Accounts para despliegue
- Hardcodear secretos en `application-gcp.properties`

### ✅ Siempre hacer

- Usar ADC en GCP (automático en Cloud Run).
- Usar Workload Identity Federation para CI/CD (GitHub Actions).
- Asignar `roles/secretmanager.secretAccessor` (mínimo privilegio).
- Conectar a Cloud SQL mediante los sockets Unix inyectados (`--add-cloudsql-instances`).

---

## Health Check

```bash
# En GCP
curl https://amani-api-xxxxx-ez.a.run.app/actuator/health

# Respuesta esperada (GCP):
{
  "status": "UP",
  "components": {
    "firebase": { "status": "UP", "details": { "status": "CONNECTED", "mode": "gcp" } },
    "db": { "status": "UP" }
  }
}
```
