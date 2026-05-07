# Justfile — AMANI Documentation
# Comandos de desarrollo para MkDocs con uv

# Variables
python := "python3"
mkdocs_env := "DISABLE_MKDOCS_2_WARNING=true"

# ============================================================
#  DESARROLLO LOCAL
# ============================================================

# Servidor local con hot-reload
serve:
    {{mkdocs_env}} uv run mkdocs serve

# Build estático
build:
    {{mkdocs_env}} uv run mkdocs build

# Verificar links rotos (strict mode)
check:
    {{mkdocs_env}} uv run mkdocs build --strict

# Limpiar build y caché
clean:
    rm -rf site/
    find docs -name __pycache__ -type d -exec rm -rf {} + 2>/dev/null || true

# Instalar dependencias (primera vez)
install:
    uv sync

# ============================================================
#  PUSH SEPARADO — main vs gh-pages
# ============================================================

# — Push a main (código + docs markdown) —
# Uso: just push-main MSG="feat: nuevo endpoint"
push-main MSG="docs: update":
    git add .
    git reset HEAD site/ 2>/dev/null || true
    git commit -m "{{MSG}}"
    git push origin main

# — Push a gh-pages (solo sitio estático) —
# Uso: just push-docs
# NOTA: mkdocs gh-deploy gestiona gh-pages internamente como rama huérfana
push-docs:
    {{mkdocs_env}} uv run mkdocs gh-deploy --force --clean \
      --message "docs: deploy $(git rev-parse --short HEAD)"

# — Flujo completo en un solo comando —
# Uso: just release MSG="feat: auth flow + docs"
release MSG="release":
    just push-main MSG="{{MSG}}"
    just push-docs

# — Verificar aislamiento de ramas —
verify:
    bash scripts/verify-branches.sh
