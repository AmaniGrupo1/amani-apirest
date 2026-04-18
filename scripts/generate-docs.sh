#!/bin/bash
# Script para generar toda la documentación (Javadoc y OpenAPI)
# y copiarla a la documentación de MkDocs

set -e

DOCS_DIR="/home/ivan/documentacion-amani"

echo "=== Generando documentación Amani ==="

# 1. Generar Javadoc
echo "1. Generando Javadoc..."
cd /home/ivan/amani-apirest
./mvnw javadoc:javadoc -q

# 2. Copiar Javadoc a docs
echo "2. Copiando Javadoc a documentación..."
cp -r target/site/apidocs/* "$DOCS_DIR/docs/javadoc/"
rm -f "$DOCS_DIR/docs/javadoc/*.md"  # Eliminar MD antiguos

# 3. Generar OpenAPI (requiere que la app esté corrienda)
echo "3. Generando OpenAPI..."
echo "   Nota: Para generar OpenAPI completo, inicia la app y visita:"
echo "   http://localhost:8080/v3/api-docs.yaml"
echo "   Luego guarda el YAML en docs/api/openapi/openapi.yaml"

# 4. Construir MkDocs
echo "4. Construyendo MkDocs..."
cd "$DOCS_DIR"
.venv/bin/mkdocs build --strict

echo ""
echo "=== Documentación generada en $DOCS_DIR/build ==="
