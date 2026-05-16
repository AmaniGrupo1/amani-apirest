#!/usr/bin/env bash

# Genera un informe PDF a partir del Maven site.

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SITE_DIR="$PROJECT_DIR/target/site"
TMP_DIR="$PROJECT_DIR/target/pdf-parts"
OUTPUT_PDF="$PROJECT_DIR/target/amani-apirest-informe.pdf"
SKIP_SITE=false
KEEP_TEMP=false
VERBOSE=false

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

PAGES=(
    "index:Introduccion"
    "jacoco/index:Cobertura de tests"
    "pmd:Informe PMD"
    "cpd:Informe CPD"
    "checkstyle:Informe Checkstyle"
    "spotbugs:Informe SpotBugs"
    "summary:Resumen del proyecto"
    "dependencies:Dependencias"
    "plugins:Plugins"
)

PART_PDFS=()
INCLUDED_LABELS=()
SKIPPED_MESSAGES=()
FAILED_MESSAGES=()

usage() {
    cat <<'EOF'
Uso:
  ./generate-pdf-report.sh [opciones]

Opciones:
  --skip-site         No ejecuta "mvn site"; usa el site ya generado.
  --output <ruta>     Ruta del PDF de salida.
  --keep-temp         Conserva los PDF parciales en target/pdf-parts.
  --verbose           Muestra stderr de wkhtmltopdf.
  -h, --help          Muestra esta ayuda.
EOF
}

cleanup() {
    if [[ "$KEEP_TEMP" == false && -d "$TMP_DIR" ]]; then
        rm -rf "$TMP_DIR"
    fi
}

trap cleanup EXIT

log_info() {
    echo -e "${YELLOW}▶ $*${NC}"
}

log_ok() {
    echo -e "${GREEN}✔ $*${NC}"
}

log_error() {
    echo -e "${RED}✖ $*${NC}" >&2
}

require_command() {
    local command_name="$1"
    if ! command -v "$command_name" >/dev/null 2>&1; then
        log_error "Falta la dependencia requerida: $command_name"
        exit 1
    fi
}

parse_args() {
    while [[ $# -gt 0 ]]; do
        case "$1" in
            --skip-site)
                SKIP_SITE=true
                shift
                ;;
            --output)
                [[ $# -ge 2 ]] || { log_error "La opcion --output requiere una ruta."; exit 1; }
                OUTPUT_PDF="$2"
                shift 2
                ;;
            --keep-temp)
                KEEP_TEMP=true
                shift
                ;;
            --verbose)
                VERBOSE=true
                shift
                ;;
            -h|--help)
                usage
                exit 0
                ;;
            *)
                log_error "Opcion no reconocida: $1"
                usage
                exit 1
                ;;
        esac
    done
}

prepare_output_dir() {
    local output_dir
    output_dir="$(dirname "$OUTPUT_PDF")"
    mkdir -p "$output_dir"
}

generate_site() {
    if [[ "$SKIP_SITE" == true ]]; then
        return
    fi

    log_info "Generando Maven site con informes de calidad"
    (
        cd "$PROJECT_DIR"
        ./mvnw site -DskipTests
    )
    log_ok "Site generado en: $SITE_DIR"
}

validate_site() {
    if [[ ! -d "$SITE_DIR" ]]; then
        log_error "No se encontro el site en $SITE_DIR"
        echo "Ejecuta primero: ./mvnw site -DskipTests" >&2
        exit 1
    fi
}

prepare_temp_dir() {
    if [[ "$TMP_DIR" == "/" || "$TMP_DIR" == "$PROJECT_DIR" ]]; then
        log_error "TMP_DIR tiene un valor inseguro: $TMP_DIR"
        exit 1
    fi

    rm -rf "$TMP_DIR"
    mkdir -p "$TMP_DIR"
}

convert_page() {
    local index="$1"
    local file="$2"
    local label="$3"
    local html="$SITE_DIR/${file}.html"
    local safe_name="${file//\//-}"
    local part="$TMP_DIR/$(printf '%02d' "$index")-${safe_name}.pdf"
    local stderr_file="$TMP_DIR/$(printf '%02d' "$index")-${safe_name}.stderr.log"

    if [[ ! -f "$html" ]]; then
        SKIPPED_MESSAGES+=("$label: falta $html")
        return
    fi

    echo "  - $label ($file.html)"

    local -a cmd=(
        wkhtmltopdf
        --page-size A4
        --margin-top 15mm
        --margin-bottom 15mm
        --margin-left 12mm
        --margin-right 12mm
        --encoding UTF-8
        --enable-local-file-access
        --no-stop-slow-scripts
        --javascript-delay 500
        "$html"
        "$part"
    )

    if "${cmd[@]}" > /dev/null 2> "$stderr_file"; then
        if [[ -f "$part" && -s "$part" ]]; then
            PART_PDFS+=("$part")
            INCLUDED_LABELS+=("$label")
            [[ "$VERBOSE" == true ]] || rm -f "$stderr_file"
            return
        fi
        FAILED_MESSAGES+=("$label: wkhtmltopdf termino sin generar un PDF valido")
    else
        local reason="fallo de wkhtmltopdf"
        if [[ -s "$stderr_file" ]]; then
            reason="$(tr '\n' ' ' < "$stderr_file" | sed 's/[[:space:]]\+/ /g' | cut -c1-240)"
        fi
        FAILED_MESSAGES+=("$label: $reason")
    fi

    [[ "$VERBOSE" == true ]] || rm -f "$stderr_file"
}

convert_pages() {
    log_info "Convirtiendo paginas HTML a PDF"

    local count=0
    local entry file label
    for entry in "${PAGES[@]}"; do
        file="${entry%%:*}"
        label="${entry##*:}"
        count=$((count + 1))
        convert_page "$count" "$file" "$label"
    done
}

merge_pdfs() {
    if [[ ${#PART_PDFS[@]} -eq 0 ]]; then
        log_error "No se pudo convertir ninguna pagina a PDF"
        print_summary
        exit 1
    fi

    if [[ ${#PART_PDFS[@]} -eq 1 ]]; then
        cp "${PART_PDFS[0]}" "$OUTPUT_PDF"
        return
    fi

    log_info "Uniendo ${#PART_PDFS[@]} secciones en un unico PDF"
    pdfunite "${PART_PDFS[@]}" "$OUTPUT_PDF"
}

print_summary() {
    echo
    if [[ -f "$OUTPUT_PDF" ]]; then
        local size
        size="$(du -h "$OUTPUT_PDF" | cut -f1)"
        log_ok "PDF generado correctamente"
        echo "  Archivo: $OUTPUT_PDF"
        echo "  Tamano: $size"
        echo "  Secciones incluidas: ${#PART_PDFS[@]}"
    fi

    if [[ ${#INCLUDED_LABELS[@]} -gt 0 ]]; then
        echo "  Incluidas:"
        printf '    - %s\n' "${INCLUDED_LABELS[@]}"
    fi

    if [[ ${#SKIPPED_MESSAGES[@]} -gt 0 ]]; then
        echo "  Omitidas:"
        printf '    - %s\n' "${SKIPPED_MESSAGES[@]}"
    fi

    if [[ ${#FAILED_MESSAGES[@]} -gt 0 ]]; then
        echo "  Fallidas:"
        printf '    - %s\n' "${FAILED_MESSAGES[@]}"
    fi
}

main() {
    parse_args "$@"
    require_command wkhtmltopdf
    require_command pdfunite
    require_command du
    [[ -x "$PROJECT_DIR/mvnw" ]] || { log_error "No se encontro mvnw ejecutable en $PROJECT_DIR"; exit 1; }

    prepare_output_dir
    generate_site
    validate_site
    prepare_temp_dir
    convert_pages
    merge_pdfs
    print_summary

    if [[ ${#FAILED_MESSAGES[@]} -gt 0 ]]; then
        log_error "Se genero el PDF, pero hubo secciones que no se pudieron convertir"
        exit 1
    fi
}

main "$@"
