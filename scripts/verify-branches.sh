#!/usr/bin/env bash
set -euo pipefail

MAIN_REF="${MAIN_REF:-origin/main}"
PAGES_REF="${PAGES_REF:-origin/gh-pages}"

require_ref() {
  local ref="$1"

  if ! git rev-parse --verify --quiet "$ref^{commit}" >/dev/null; then
    echo "ERROR: no existe la referencia '$ref'. Ejecuta: git fetch origin main gh-pages" >&2
    exit 2
  fi
}

list_tree() {
  local ref="$1"
  git ls-tree -r --name-only "$ref"
}

fail_if_present() {
  local ref="$1"
  local pattern="$2"
  local description="$3"

  if list_tree "$ref" | grep -Eq "$pattern"; then
    echo "ERROR: $description encontrado en $ref" >&2
    list_tree "$ref" | grep -E "$pattern" >&2
    exit 1
  fi
}

print_summary() {
  local ref="$1"
  local title="$2"

  echo
  echo "== $title ($ref) =="
  echo "Total archivos: $(list_tree "$ref" | wc -l | tr -d ' ')"
  echo "Primeros 40 archivos:"
  list_tree "$ref" | sed -n '1,40p'
}

require_ref "$MAIN_REF"
require_ref "$PAGES_REF"

fail_if_present "$MAIN_REF" '^site(/|$)' "site/"
fail_if_present "$PAGES_REF" '^(pom\.xml|src/)' "codigo fuente Java/Spring"

print_summary "$MAIN_REF" "main"
print_summary "$PAGES_REF" "gh-pages"

echo
echo "OK: ramas aisladas correctamente."
