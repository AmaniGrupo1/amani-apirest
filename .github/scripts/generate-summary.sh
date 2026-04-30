#!/usr/bin/env bash
set -uo pipefail

SUREFIRE_DIR="${1:-target/surefire-reports}"
FAILSAFE_DIR="${2:-target/failsafe-reports}"
JACOCO_CSV="${3:-target/site/jacoco/jacoco.csv}"
OUT="${GITHUB_STEP_SUMMARY:-/dev/stdout}"

barra() {
  local pct=$1
  local n=$((pct / 5))
  local bar=""
  for ((j=0;j<20;j++)); do
    if ((j < n)); then bar="${bar}█"; else bar="${bar}░"; fi
  done
  echo "$bar"
}

emoji() {
  local pct=$1
  local g=${2:-80}
  local y=${3:-60}
  if ((pct >= g)); then echo "🟢"; elif ((pct >= y)); then echo "🟡"; else echo "🔴"; fi
}

status_txt() {
  local pct=$1
  local g=${2:-80}
  local y=${3:-60}
  if ((pct >= g)); then echo "VERDE"; elif ((pct >= y)); then echo "AMARILLO"; else echo "ROJO"; fi
}

{
echo "## 📊 Resumen de CI"
echo ""

# ─── Diagrama ASCII del pipeline ───
echo "### 🔄 Flujo del Pipeline"
echo ""
echo '```'
echo ' ┌──────────┐     ┌────────────┐     ┌─────────────┐'
echo ' │ Checkout │────▶│ Setup JDK  │────▶│ Maven Cache │'
echo ' └──────────┘     └────────────┘     └─────────────┘'
echo '                                              │'
echo '                                              ▼'
echo '                                       ┌────────────┐'
echo '                                       │Build & Test│'
echo '                                       └─────┬──────┘'
echo '                                             │'
echo '                    ┌────────────────────────┼────────────────────────┐'
echo '                    │                        │                        │'
echo '                    ▼                        ▼                        ▼'
echo '           ┌──────────────┐      ┌───────────────┐      ┌─────────────┐'
echo '           │JaCoCo Coverage│      │Test Reporter  │      │SonarQube    │'
echo '           └──────────────┘      └───────────────┘      └─────────────┘'
echo '                    │                        │'
echo '                    └────────────┬───────────┘'
echo '                                 ▼'
echo '                          ┌────────────┐'
echo '                          │ Artifacts  │'
echo '                          └────────────┘'
echo '```'
echo ""

# ─── Tests ───
echo "### 🧪 Resultados de Tests"
echo ""

TOTAL_TESTS=0
TOTAL_FAILURES=0
TOTAL_ERRORS=0
TOTAL_SKIPPED=0
TOTAL_TIME=0

parse_xml() {
  local dir="$1"
  if [[ -d "$dir" ]]; then
    while IFS= read -r -d '' xml; do
      t=$(grep -oP 'tests="\K[0-9]+' "$xml" | tail -1 || echo 0)
      f=$(grep -oP 'failures="\K[0-9]+' "$xml" | tail -1 || echo 0)
      e=$(grep -oP 'errors="\K[0-9]+' "$xml" | tail -1 || echo 0)
      s=$(grep -oP 'skipped="\K[0-9]+' "$xml" | tail -1 || echo 0)
      tm=$(grep -oP 'time="\K[0-9.]+' "$xml" | tail -1 || echo 0)
      TOTAL_TESTS=$((TOTAL_TESTS + t))
      TOTAL_FAILURES=$((TOTAL_FAILURES + f))
      TOTAL_ERRORS=$((TOTAL_ERRORS + e))
      TOTAL_SKIPPED=$((TOTAL_SKIPPED + s))
      TOTAL_TIME=$(echo "$TOTAL_TIME + $tm" | bc -l 2>/dev/null || echo 0)
    done < <(find "$dir" -maxdepth 1 -name 'TEST-*.xml' -print0 2>/dev/null)
  fi
}

parse_xml "$SUREFIRE_DIR"
parse_xml "$FAILSAFE_DIR"

TOTAL_SUCCESS=$((TOTAL_TESTS - TOTAL_FAILURES - TOTAL_ERRORS - TOTAL_SKIPPED))
if ((TOTAL_SUCCESS < 0)); then TOTAL_SUCCESS=0; fi

if ((TOTAL_TESTS > 0)); then
  PASS_PCT=$(echo "scale=2; 100 * $TOTAL_SUCCESS / $TOTAL_TESTS" | bc)
else
  PASS_PCT=0
fi

echo "| Metrica | Valor |"
echo "|---|---|"
echo "| 🧪 Tests totales | **$TOTAL_TESTS** |"
echo "| ✅ Pasados | $TOTAL_SUCCESS |"
echo "| ❌ Fallidos | $TOTAL_FAILURES |"
echo "| 💥 Errores | $TOTAL_ERRORS |"
echo "| ⏭️  Skipped | $TOTAL_SKIPPED |"
echo "| ⏱️ Tiempo total | ${TOTAL_TIME}s |"
echo ""

PASS_BAR=$(barra ${PASS_PCT%.*})
PASS_EMOJI=$(emoji ${PASS_PCT%.*} 100 80)
echo "**Tasa de exito**: $PASS_PCT% $PASS_EMOJI"
echo ""
echo "\`$PASS_BAR\`"
echo ""

# ─── Cobertura JaCoCo ───
echo "### 📈 Cobertura de Codigo (JaCoCo)"
echo ""

if [[ -f "$JACOCO_CSV" ]]; then
  LC_NUMERIC=C awk -F',' '
  NR==1 { next }
  {
    li_m=$8+0; li_c=$9+0; br_m=$6+0; br_c=$7+0;
    lt=li_m+li_c; bt=br_m+br_c;
    if (lt>0) {
      sum_line_cov+=li_c; sum_line_total+=lt;
      sum_branch_cov+=br_c; sum_branch_total+=bt;
    }
  }
  END {
    lp=(sum_line_total>0)?(100*sum_line_cov/sum_line_total):0;
    bp=(sum_branch_total>0)?(100*sum_branch_cov/sum_branch_total):0;
    printf "%.2f %.2f %d %d %d %d\n", lp, bp, sum_line_cov, sum_line_total, sum_branch_cov, sum_branch_total;
  }
  ' "$JACOCO_CSV" | {
    read -r LINE_PCT BRANCH_PCT LINE_COV LINE_TOT BRANCH_COV BRANCH_TOT

    LINE_BAR=$(barra ${LINE_PCT%.*})
    BRANCH_BAR=$(barra ${BRANCH_PCT%.*})
    LINE_EMOJI=$(emoji ${LINE_PCT%.*} 80 60)
    BRANCH_EMOJI=$(emoji ${BRANCH_PCT%.*} 70 50)
    LINE_STATUS=$(status_txt ${LINE_PCT%.*} 80 60)
    BRANCH_STATUS=$(status_txt ${BRANCH_PCT%.*} 70 50)

    echo "| Metrica | Valor | Barra | Estado |"
    echo "|---|---|---|---|"
    echo "| Lineas | **${LINE_PCT}%** ($LINE_COV/$LINE_TOT) | $LINE_BAR | $LINE_EMOJI $LINE_STATUS |"
    echo "| Ramas | **${BRANCH_PCT}%** ($BRANCH_COV/$BRANCH_TOT) | $BRANCH_BAR | $BRANCH_EMOJI $BRANCH_STATUS |"
    echo ""
  }
else
  echo "⚠️ No se encontro el archivo JaCoCo CSV."
  echo ""
fi

# ─── Top 5 clases con menor cobertura ───
echo "### 🎯 Top 5 Clases con Menor Cobertura de Lineas"
echo ""

if [[ -f "$JACOCO_CSV" ]]; then
  LC_NUMERIC=C awk -F',' '
  NR==1 { next }
  {
    pkg=$2; cls=$3;
    li_m=$8+0; li_c=$9+0;
    lt=li_m+li_c;
    if (lt>0) {
      lp=(100*li_c/lt);
      print lp, pkg "." cls, li_c, lt;
    }
  }
  ' "$JACOCO_CSV" | sort -t' ' -k1 -n | head -5 | while read -r lp name lc lt; do
    bar=$(barra ${lp%.*})
    em=$(emoji ${lp%.*} 80 60)
    short=$(echo "$name" | sed 's/^.*\.//')
    printf "| \`%-40s\` | %6.2f%% (%d/%d) | %s | %s |\n" "$short" "$lp" "$lc" "$lt" "$bar" "$em"
  done | {
    echo "| Clase | Lineas | Barra | Estado |"
    echo "|---|---|---|---|"
    cat
  }
  echo ""
else
  echo "No disponible."
  echo ""
fi

# ─── P0 / P1 Cobertura ───
echo "### 🚦 Cobertura P0 y P1"
echo ""

if [[ -f "$JACOCO_CSV" ]]; then
  LC_NUMERIC=C awk -F',' '
  NR==1 { next }
  {
    pkg=$2; cls=$3;
    li_m=$8+0; li_c=$9+0;
    br_m=$6+0; br_c=$7+0;
    lt=li_m+li_c; bt=br_m+br_c;
    if (lt==0) next;

    is_p0=0;
    if (pkg ~ /controllers\.login/) is_p0=1;
    if (pkg == "com.amani.amaniapirest.configuration" && (cls=="JwtAuthFilter" || cls=="SecurityConfig" || cls=="JwtUtil" || cls=="UserDetailsServiceImpl")) is_p0=1;
    if (pkg ~ /controllers\.controladorPsicologo/ && cls ~ /CitaControladorPsicologo/) is_p0=1;
    if (pkg == "com.amani.amaniapirest.services.serviciosLogin" && cls=="AuthService") is_p0=1;
    if (pkg == "com.amani.amaniapirest.services" && (cls=="CitaAgendaService" || cls=="CitaService")) is_p0=1;
    if (pkg == "com.amani.amaniapirest.services.psicologo" && cls=="CitaServicePsicologo") is_p0=1;

    if (is_p0) {
      p0_line_cov+=li_c; p0_line_total+=lt;
      p0_branch_cov+=br_c; p0_branch_total+=bt;
    } else if ((pkg ~ /controllers\./ || pkg ~ /services/) && !(pkg ~ /dto\./)) {
      p1_line_cov+=li_c; p1_line_total+=lt;
      p1_branch_cov+=br_c; p1_branch_total+=bt;
    }
  }
  END {
    p0_lp=(p0_line_total>0)?(100*p0_line_cov/p0_line_total):0;
    p0_bp=(p0_branch_total>0)?(100*p0_branch_cov/p0_branch_total):0;
    p1_lp=(p1_line_total>0)?(100*p1_line_cov/p1_line_total):0;
    p1_bp=(p1_branch_total>0)?(100*p1_branch_cov/p1_branch_total):0;
    printf "%.2f %.2f %.2f %.2f\n", p0_lp, p0_bp, p1_lp, p1_bp;
  }
  ' "$JACOCO_CSV" | {
    read -r P0_LINE P0_BRANCH P1_LINE P1_BRANCH

    P0_LINE_BAR=$(barra ${P0_LINE%.*})
    P0_BRANCH_BAR=$(barra ${P0_BRANCH%.*})
    P1_LINE_BAR=$(barra ${P1_LINE%.*})
    P1_BRANCH_BAR=$(barra ${P1_BRANCH%.*})

    P0_LINE_EMOJI=$(emoji ${P0_LINE%.*} 80 60)
    P0_BRANCH_EMOJI=$(emoji ${P0_BRANCH%.*} 70 50)
    P1_LINE_EMOJI=$(emoji ${P1_LINE%.*} 80 60)
    P1_BRANCH_EMOJI=$(emoji ${P1_BRANCH%.*} 70 50)

    echo "| Prioridad | Lineas | Barra | Ramas | Barra |"
    echo "|---|---|---|---|---|"
    echo "| **P0** | ${P0_LINE}% | $P0_LINE_BAR $P0_LINE_EMOJI | ${P0_BRANCH}% | $P0_BRANCH_BAR $P0_BRANCH_EMOJI |"
    echo "| **P1** | ${P1_LINE}% | $P1_LINE_BAR $P1_LINE_EMOJI | ${P1_BRANCH}% | $P1_BRANCH_BAR $P1_BRANCH_EMOJI |"
    echo ""
  }
else
  echo "No disponible."
  echo ""
fi

# ─── Estado final ───
echo "### ✅ Estado Final del Pipeline"
echo ""
if ((TOTAL_FAILURES == 0 && TOTAL_ERRORS == 0)); then
  echo "🟢 **BUILD EXITOSO** — Todos los tests pasaron."
else
  echo "🔴 **BUILD FALLIDO** — Hay tests fallidos o con errores."
fi
echo ""

} >> "$OUT"
