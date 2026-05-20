#!/usr/bin/env bash
# sync_docs.sh
# Genera y sincroniza documentación KDoc/Javadoc con MkDocs.
# Uso: ./sync_docs.sh [android|backend|all]

set -euo pipefail

TARGET="${1:-all}"

echo "🧹 Limpiando directorio de API docs anterior..."
rm -rf "docs/api/backend/dokka"
mkdir -p "docs/api/backend/dokka"

generate_android() {
    if [ ! -d "android" ]; then
        echo "⚠️  No se encontró la carpeta android/. Omitiendo generación de KDoc."
        return 0
    fi
    echo "📱 Generando KDoc (Android)..."
    cd android/
    ./gradlew dokkaGfm --no-daemon
    cd ..
    echo "✅ KDoc Android generado"
}

generate_backend() {
    echo "☕ Generando Dokka (Backend)..."
    ./mvnw dokka:dokka -q
    echo "✅ Dokka Backend generado en docs/api/backend/dokka"
}

case "$TARGET" in
    android) generate_android ;;
    backend) generate_backend ;;
    all)     generate_android; generate_backend ;;
    *)
        echo "❌ Uso: ./sync_docs.sh [android|backend|all]"
        exit 1
        ;;
esac

echo ""
echo "🚀 Levantando MkDocs en local..."
uv run mkdocs serve
