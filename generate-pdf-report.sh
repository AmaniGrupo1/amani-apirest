#!/bin/bash
# =============================================================
# generate-pdf-report.sh
# Genera un informe PDF con los resultados de calidad del código.
#
# Requisitos: wkhtmltopdf, pdfunite (poppler-utils)
#   sudo apt install wkhtmltopdf poppler-utils
#
# Uso:
#   ./generate-pdf-report.sh            (genera site + PDF)
#   ./generate-pdf-report.sh --skip-site (solo convierte a PDF, si el site ya existe)
# =============================================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SITE_DIR="$PROJECT_DIR/target/site"
TMP_DIR="$PROJECT_DIR/target/pdf-parts"
OUTPUT_PDF="$PROJECT_DIR/target/amani-apirest-informe.pdf"

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# ---- 1. Generar el site Maven (si no se usa --skip-site) ----
if [ "$1" != "--skip-site" ]; then
    echo -e "${YELLOW}▶ Generando site Maven con informes de calidad...${NC}"
    cd "$PROJECT_DIR"
    ./mvnw site -DskipTests -q
    echo -e "${GREEN}✔ Site generado en: $SITE_DIR${NC}"
fi

# ---- 2. Verificar que el site existe ----
if [ ! -d "$SITE_DIR" ]; then
    echo "❌ No se encontró el site en $SITE_DIR. Ejecuta primero: ./mvnw site -DskipTests"
    exit 1
fi

# ---- 3. Preparar directorio temporal ----
rm -rf "$TMP_DIR"
mkdir -p "$TMP_DIR"

# ---- 4. Lista de páginas a incluir (en orden) ----
PAGES=(
    "index:Introducción"
    "jacoco/index:Cobertura de Tests"
    "pmd:Informe PMD"
    "cpd:Informe CPD"
    "checkstyle:Informe Checkstyle"
    "spotbugs:Informe SpotBugs"
    "summary:Resumen del Proyecto"
    "dependencies:Dependencias"
    "plugins:Plugins"
)

PART_PDFS=()
COUNT=0

echo -e "${YELLOW}▶ Convirtiendo páginas HTML a PDF...${NC}"

for entry in "${PAGES[@]}"; do
    FILE="${entry%%:*}"
    LABEL="${entry##*:}"
    HTML="$SITE_DIR/${FILE}.html"

    if [ -f "$HTML" ]; then
        COUNT=$((COUNT + 1))
        # Reemplazar '/' por '-' para el nombre del archivo PDF temporal
        SAFE_NAME=$(echo "${FILE}" | tr '/' '-')
        PART="$TMP_DIR/$(printf '%02d' $COUNT)-${SAFE_NAME}.pdf"

        echo "  📄 $LABEL ($FILE.html)"
        wkhtmltopdf \
            --page-size A4 \
            --margin-top 15mm \
            --margin-bottom 15mm \
            --margin-left 12mm \
            --margin-right 12mm \
            --encoding UTF-8 \
            --enable-local-file-access \
            --no-stop-slow-scripts \
            --javascript-delay 500 \
            "$HTML" "$PART" 2>/dev/null || true

        if [ -f "$PART" ]; then
            PART_PDFS+=("$PART")
        fi
    fi
done

# ---- 5. Unir todos los PDFs parciales en uno solo ----
if [ ${#PART_PDFS[@]} -eq 0 ]; then
    echo "❌ No se pudo convertir ninguna página a PDF."
    exit 1
fi

if [ ${#PART_PDFS[@]} -eq 1 ]; then
    cp "${PART_PDFS[0]}" "$OUTPUT_PDF"
else
    echo -e "${YELLOW}▶ Uniendo ${#PART_PDFS[@]} secciones en un solo PDF...${NC}"
    pdfunite "${PART_PDFS[@]}" "$OUTPUT_PDF"
fi

# ---- 6. Limpieza ----
rm -rf "$TMP_DIR"

echo ""
echo -e "${GREEN}✔ PDF generado correctamente:${NC}"
echo "  📄 $OUTPUT_PDF"
SIZE=$(du -h "$OUTPUT_PDF" | cut -f1)
echo "  📦 Tamaño: $SIZE"
echo "  📊 Secciones incluidas: ${#PART_PDFS[@]}"
echo ""
echo "  Contenido:"
for entry in "${PAGES[@]}"; do
    FILE="${entry%%:*}"
    LABEL="${entry##*:}"
    [ -f "$SITE_DIR/${FILE}.html" ] && echo "    • $LABEL"
done

