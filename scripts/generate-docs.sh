#!/bin/bash
# Script para generar toda la documentación (Javadoc, Dokka y OpenAPI)
# y dejarla lista para MkDocs

set -e

# Directorio raíz del proyecto
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "=== Generando documentación Amani en $PROJECT_ROOT ==="

cd "$PROJECT_ROOT"

# 1. Generar Javadoc, Dokka y OpenAPI
echo "1. Generando Javadoc, Dokka y OpenAPI..."
./mvnw javadoc:javadoc dokka:dokka springdoc-openapi:generate -Dspring.profiles.active=test -DskipTests -q

echo "2. Verificando archivos generados..."
ls -l docs/api/backend/javadoc/index.html
ls -l docs/api/backend/dokka/index.html
ls -l docs/api/openapi.json

# 3. Construir MkDocs (opcional si se quiere ver localmente)
if command -v mkdocs &> /dev/null; then
    echo "3. Construyendo MkDocs localmente..."
    mkdocs build
    echo "Documentación construida en el directorio 'site/'"
else
    echo "3. MkDocs no está instalado, saltando construcción local."
fi

echo ""
echo "=== Proceso finalizado ==="
