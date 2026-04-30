#!/usr/bin/env bash
set -euo pipefail

CSV_PATH="${1:-target/site/jacoco/jacoco.csv}"
OUT_DIR="${2:-doc}"

if [[ ! -f "$CSV_PATH" ]]; then
  echo "No existe el archivo: $CSV_PATH" >&2
  echo "Ejecuta: ./mvnw test" >&2
  exit 1
fi

mkdir -p "$OUT_DIR"

# ============================================================
# DEFINICION DE CLASES P0 Y P1
# ============================================================

# P0 - Criticos: Auth, Seguridad, Citas
is_p0() {
  local pkg="$1"
  local cls="$2"
  case "$pkg" in
    com.amani.amaniapirest.controllers.login)
      return 0
      ;;
    com.amani.amaniapirest.configuration)
      case "$cls" in
        JwtAuthFilter|SecurityConfig|JwtUtil|UserDetailsServiceImpl)
          return 0
          ;;
      esac
      ;;
    com.amani.amaniapirest.controllers.controladorPsicologo)
      case "$cls" in
        CitaControladorPsicologo)
          return 0
          ;;
      esac
      ;;
    com.amani.amaniapirest.services.serviciosLogin)
      case "$cls" in
        AuthService)
          return 0
          ;;
      esac
      ;;
    com.amani.amaniapirest.services)
      case "$cls" in
        CitaAgendaService|CitaService)
          return 0
          ;;
      esac
      ;;
    com.amani.amaniapirest.services.psicologo)
      case "$cls" in
        CitaServicePsicologo)
          return 0
          ;;
      esac
      ;;
  esac
  return 1
}

# P1 - Alta prioridad: resto de controllers y services con lineas cubiertas
is_p1() {
  local pkg="$1"
  local cls="$2"
  # Excluir P0
  if is_p0 "$pkg" "$cls"; then
    return 1
  fi
  # Solo controllers y services con codigo ejecutable
  case "$pkg" in
    com.amani.amaniapirest.controllers.*)
      return 0
      ;;
    com.amani.amaniapirest.services*)
      return 0
      ;;
  esac
  return 1
}

# ============================================================
# FUNCION PARA GENERAR REPORTE VISUAL
# ============================================================
generar_reporte() {
  local tipo="$1"      # P0 o P1
  local out_file="$2"
  local awk_filter="$3"

  awk -F',' -v OUT="$out_file" -v TIPO="$tipo" -v FILTER="$awk_filter" '
  function barra(pct) {
    n = int(pct / 5);
    bar = "";
    for (j = 0; j < 20; j++) {
      if (j < n) bar = bar "█";
      else bar = bar "░";
    }
    return bar;
  }
  function emoji(pct, umbral_verde, umbral_amarillo) {
    if (pct >= umbral_verde) return "🟢";
    if (pct >= umbral_amarillo) return "🟡";
    return "🔴";
  }

  NR == 1 { next }
  {
    pkg = $2; cls = $3;
    li_m = $8 + 0; li_c = $9 + 0;
    br_m = $6 + 0; br_c = $7 + 0;

    lt = li_m + li_c;
    bt = br_m + br_c;

    if (lt == 0) next;  # Sin lineas ejecutables = ignorar

    # Evaluar filtro P0 o P1
    is_match = 0;
    if (FILTER == "P0") {
      # Auth/Login
      if (pkg ~ /controllers\.login/) is_match = 1;
      # Seguridad
      if (pkg == "com.amani.amaniapirest.configuration" && (cls == "JwtAuthFilter" || cls == "SecurityConfig" || cls == "JwtUtil" || cls == "UserDetailsServiceImpl")) is_match = 1;
      # Citas psicologo
      if (pkg ~ /controllers\.controladorPsicologo/ && cls ~ /CitaControladorPsicologo/) is_match = 1;
      # AuthService
      if (pkg == "com.amani.amaniapirest.services.serviciosLogin" && cls == "AuthService") is_match = 1;
      # Cita services
      if (pkg == "com.amani.amaniapirest.services" && (cls == "CitaAgendaService" || cls == "CitaService")) is_match = 1;
      if (pkg == "com.amani.amaniapirest.services.psicologo" && cls == "CitaServicePsicologo") is_match = 1;
    } else if (FILTER == "P1") {
      # Resto de controllers y services (no P0, no DTOs, no models)
      if (pkg ~ /controllers\./) is_match = 1;
      if (pkg ~ /services/) is_match = 1;
      # Excluir P0
      if (pkg ~ /controllers\.login/) is_match = 0;
      if (pkg ~ /controllers\.controladorPsicologo/ && cls ~ /CitaControladorPsicologo/) is_match = 0;
      if (pkg == "com.amani.amaniapirest.configuration" && (cls == "JwtAuthFilter" || cls == "SecurityConfig" || cls == "JwtUtil" || cls == "UserDetailsServiceImpl")) is_match = 0;
      if (pkg == "com.amani.amaniapirest.services.serviciosLogin" && cls == "AuthService") is_match = 0;
      if (pkg == "com.amani.amaniapirest.services" && (cls == "CitaAgendaService" || cls == "CitaService")) is_match = 0;
      if (pkg == "com.amani.amaniapirest.services.psicologo" && cls == "CitaServicePsicologo") is_match = 0;
    }

    if (is_match == 1) {
      count++;
      name[count] = pkg "." cls;

      lp = (lt > 0) ? (100 * li_c / lt) : 0;
      bp = (bt > 0) ? (100 * br_c / bt) : 0;

      line_cov[count] = li_c; line_total[count] = lt; line_pct[count] = lp;
      branch_cov[count] = br_c; branch_total[count] = bt; branch_pct[count] = bp;

      sum_line_cov += li_c; sum_line_total += lt;
      sum_branch_cov += br_c; sum_branch_total += bt;
    }
  }
  END {
    if (count == 0) {
      print "# Cobertura " TIPO "\n\nNo se encontraron clases " TIPO " en el CSV." > OUT;
      exit 0;
    }

    total_line_pct = (sum_line_total > 0) ? (100 * sum_line_cov / sum_line_total) : 0;
    total_branch_pct = (sum_branch_total > 0) ? (100 * sum_branch_cov / sum_branch_total) : 0;

    line_emoji = (total_line_pct >= 80) ? "🟢" : ((total_line_pct >= 60) ? "🟡" : "🔴");
    branch_emoji = (total_branch_pct >= 70) ? "🟢" : ((total_branch_pct >= 50) ? "🟡" : "🔴");

    line_status = (total_line_pct >= 80) ? "VERDE" : ((total_line_pct >= 60) ? "AMARILLO" : "ROJO");
    branch_status = (total_branch_pct >= 70) ? "VERDE" : ((total_branch_pct >= 50) ? "AMARILLO" : "ROJO");

    print "# Cobertura " TIPO " (Visual)" > OUT;
    print "" > OUT;
    print "Generado desde `target/site/jacoco/jacoco.csv`." > OUT;
    print "" > OUT;

    print "## Resumen Ejecutivo" > OUT;
    print "" > OUT;
    print "| Metrica | Valor | Barra | Estado |" > OUT;
    print "|---|---|---|---|" > OUT;
    printf("| Lineas %s | **%.2f%%** (%d/%d) | %s | %s %s |\n", TIPO, total_line_pct, sum_line_cov, sum_line_total, barra(total_line_pct), line_emoji, line_status) > OUT;
    printf("| Ramas %s  | **%.2f%%** (%d/%d) | %s | %s %s |\n", TIPO, total_branch_pct, sum_branch_cov, sum_branch_total, barra(total_branch_pct), branch_emoji, branch_status) > OUT;
    print "" > OUT;

    print "## Semaforo por clase" > OUT;
    print "" > OUT;
    print "| Clase | Lineas | Barra | Ramas | Estado |" > OUT;
    print "|---|---|---|---|---|" > OUT;

    for (i = 1; i <= count; i++) {
      lp = line_pct[i]; bp = branch_pct[i];
      st = (lp >= 80) ? "VERDE" : ((lp >= 60) ? "AMARILLO" : "ROJO");
      em = (lp >= 80) ? "🟢" : ((lp >= 60) ? "🟡" : "🔴");
      bar = barra(lp);
      printf("| `%s` | %.2f%% (%d/%d) | %s | %.2f%% (%d/%d) | %s %s |\n", name[i], lp, line_cov[i], line_total[i], bar, bp, branch_cov[i], branch_total[i], em, st) > OUT;
    }

    print "" > OUT;
    print "## Histograma de cobertura de lineas" > OUT;
    print "" > OUT;
    for (i = 1; i <= count; i++) {
      lp = line_pct[i];
      short = name[i];
      gsub(/^.*\./, "", short);
      bar = barra(lp);
      em = (lp >= 80) ? "🟢" : ((lp >= 60) ? "🟡" : "🔴");
      printf("%-50s %s %6.2f%% %s\n", short " ", bar, lp, em) > OUT;
    }
    print "" > OUT;

    print "## Objetivo recomendado" > OUT;
    print "" > OUT;
    print "- " TIPO " lineas: >= 80%" > OUT;
    print "- " TIPO " ramas: >= 70%" > OUT;
    print "- Ninguna clase " TIPO " en ROJO" > OUT;

    print "" > OUT;
    print "## Como verlo" > OUT;
    print "" > OUT;
    print "1. Ejecuta `./mvnw test` (o tus tests objetivo)." > OUT;
    print "2. Ejecuta `bash scripts/generate-p0-coverage-visual.sh`." > OUT;
    print "3. Abre `target/site/jacoco/index.html` y este markdown para resumen ejecutivo." > OUT;
  }
  ' "$CSV_PATH"
}

# ============================================================
# GENERAR REPORTES
# ============================================================

echo "Generando reporte P0..."
generar_reporte "P0" "$OUT_DIR/coverage-p0-visual.md" "P0"

echo "Generando reporte P1..."
generar_reporte "P1" "$OUT_DIR/coverage-p1-visual.md" "P1"

echo ""
echo "Reportes generados:"
echo "  - $OUT_DIR/coverage-p0-visual.md"
echo "  - $OUT_DIR/coverage-p1-visual.md"
