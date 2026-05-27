#!/usr/bin/env bash
# =============================================================================
# Setup GCP Infrastructure for Amani API (Cloud Run + Cloud SQL)
# =============================================================================
set -euo pipefail

# --- Variables ---
PROJECT_ID="amani-160bf"
REGION="europe-west1"
DB_INSTANCE="amani-db"
DB_USER="postgres"
DB_PASS="postgres_password_change_me" # Cambiar por una contraseña segura
REPO_NAME="docker-repo"
SA_NAME="github-actions-sa"
WIF_POOL="github-actions-pool"
WIF_PROVIDER="github-provider"
GITHUB_REPO="AmaniGrupo1/amani-apirest"

echo "Configurando infraestructura para el proyecto: $PROJECT_ID"
gcloud config set project "$PROJECT_ID"

# 1. Habilitar APIs necesarias
echo "1. Habilitando APIs (Run, SQL, Secret Manager, Artifact Registry, IAM)..."
gcloud services enable \
  run.googleapis.com \
  sqladmin.googleapis.com \
  secretmanager.googleapis.com \
  artifactregistry.googleapis.com \
  iamcredentials.googleapis.com \
  cloudresourcemanager.googleapis.com

# 2. Crear instancia Cloud SQL (PostgreSQL - la más barata db-f1-micro)
echo "2. Comprobando instancia Cloud SQL..."
if ! gcloud sql instances describe "$DB_INSTANCE" >/dev/null 2>&1; then
  echo "Creando instancia de PostgreSQL (puede tardar unos minutos)..."
  gcloud sql instances create "$DB_INSTANCE" \
    --database-version=POSTGRES_15 \
    --tier=db-f1-micro \
    --region="$REGION" \
    --storage-type=HDD \
    --storage-size=10GB \
    --root-password="$DB_PASS"
else
  echo "La instancia Cloud SQL '$DB_INSTANCE' ya existe."
fi

# 3. Crear repositorio en Artifact Registry
echo "3. Comprobando repositorio de Artifact Registry..."
if ! gcloud artifacts repositories describe "$REPO_NAME" --location="$REGION" >/dev/null 2>&1; then
  echo "Creando repositorio en Artifact Registry..."
  gcloud artifacts repositories create "$REPO_NAME" \
    --repository-format=docker \
    --location="$REGION" \
    --description="Repositorio para imágenes de Amani API"
else
  echo "El repositorio '$REPO_NAME' ya existe."
fi

# 4. Crear Service Account para GitHub Actions y asignar roles
echo "4. Configurando Service Account para CI/CD..."
SA_EMAIL="${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"
if ! gcloud iam service-accounts describe "$SA_EMAIL" >/dev/null 2>&1; then
  gcloud iam service-accounts create "$SA_NAME" \
    --display-name="GitHub Actions Service Account"
fi

# Roles necesarios para hacer build, push a registry y deploy a Cloud Run
gcloud projects add-iam-policy-binding "$PROJECT_ID" \
  --member="serviceAccount:$SA_EMAIL" \
  --role="roles/artifactregistry.writer"
gcloud projects add-iam-policy-binding "$PROJECT_ID" \
  --member="serviceAccount:$SA_EMAIL" \
  --role="roles/run.admin"
gcloud projects add-iam-policy-binding "$PROJECT_ID" \
  --member="serviceAccount:$SA_EMAIL" \
  --role="roles/iam.serviceAccountUser"

# 5. Workload Identity Federation (WIF) para GitHub
echo "5. Configurando Workload Identity Federation para GitHub..."
# Usamos 'list' con filtro para evitar errores de NOT_FOUND en la salida
POOL_EXISTS=$(gcloud iam workload-identity-pools list --location="global" --filter="name:$WIF_POOL" --format="value(name)")

if [ -z "$POOL_EXISTS" ]; then
  gcloud iam workload-identity-pools create "$WIF_POOL" \
    --location="global" \
    --display-name="GitHub Actions Pool"
fi

export WORKLOAD_IDENTITY_POOL_ID=$(gcloud iam workload-identity-pools describe "$WIF_POOL" \
  --location="global" --format="value(name)")

PROVIDER_EXISTS=$(gcloud iam workload-identity-pools providers list --workload-identity-pool="$WIF_POOL" --location="global" --filter="name:$WIF_PROVIDER" --format="value(name)")

if [ -z "$PROVIDER_EXISTS" ]; then
  gcloud iam workload-identity-pools providers create-oidc "$WIF_PROVIDER" \
    --location="global" \
    --workload-identity-pool="$WIF_POOL" \
    --display-name="GitHub provider" \
    --attribute-mapping="google.subject=assertion.sub,attribute.actor=assertion.actor,attribute.repository=assertion.repository" \
    --attribute-condition="assertion.repository == '${GITHUB_REPO}'" \
    --issuer-uri="https://token.actions.githubusercontent.com"
fi

export WIF_PROVIDER_ID=$(gcloud iam workload-identity-pools providers describe "$WIF_PROVIDER" \
  --workload-identity-pool="$WIF_POOL" \
  --location="global" --format="value(name)")

# Permitir que el repo de GitHub asuma la Service Account
gcloud iam service-accounts add-iam-policy-binding "$SA_EMAIL" \
  --role="roles/iam.workloadIdentityUser" \
  --member="principalSet://iam.googleapis.com/${WORKLOAD_IDENTITY_POOL_ID}/attribute.repository/${GITHUB_REPO}"

echo ""
echo "=================================================================="
echo "✅ Infraestructura creada correctamente."
echo "Guarda estos valores para configurarlos en GitHub Secrets/Variables:"
echo "GCP_PROJECT_ID: $PROJECT_ID"
echo "GCP_REGION: $REGION"
echo "GCP_WORKLOAD_IDENTITY_PROVIDER: $WIF_PROVIDER_ID"
echo "GCP_SERVICE_ACCOUNT: $SA_EMAIL"
echo "GCP_ARTIFACT_REGISTRY: ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}"
echo "=================================================================="
echo ""
echo "NOTA: Para la BD, asegúrate de guardar el password en Secret Manager:"
echo "echo -n '$DB_PASS' | gcloud secrets create db-password --data-file=-"
echo "Y conceder acceso a Cloud Run (service account por defecto o dedicada):"
echo "gcloud secrets add-iam-policy-binding db-password \\"
echo "  --member='serviceAccount:${SA_EMAIL}' \\"
echo "  --role='roles/secretmanager.secretAccessor'"
