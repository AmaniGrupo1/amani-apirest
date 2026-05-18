#!/bin/bash
# Script para generar OpenAPI JSON usando el plugin de Maven

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

echo "Generando OpenAPI JSON via Maven..."

# Requiere que la app pueda compilar y arrancar el contexto (usamos perfil test para H2)
./mvnw springdoc-openapi:generate -Dspring.profiles.active=test -DskipTests -q

if [ -f "docs/api/openapi.json" ]; then
    echo "OpenAPI JSON generado correctamente en docs/api/openapi.json"
else
    echo "Error: No se pudo generar el archivo docs/api/openapi.json"
    exit 1
fi
