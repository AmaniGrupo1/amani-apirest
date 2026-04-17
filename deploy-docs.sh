#!/bin/bash
# Despliegue de documentación a GitHub Pages

set -e

echo "=== Desplegando documentación a GitHub Pages ==="

cd /home/ivan/documentacion-amani

# Crear estructura docs/
rm -rf docs
mkdir -p docs/api/openapi

# Copiar contenido a docs/
cp -r api arquitectura guias javadoc search assets docs/ 2>/dev/null || true
cp mkdocs.yml index.md docs/

# Actualizar mkdocs.yml - docs_dir es docs, site_dir es _site (fuera de docs)
cat > docs/mkdocs.yml << 'MKEOF'
site_name: Amani Docs
site_description: Documentación técnica de la plataforma Amani
site_author: Iván
docs_dir: .
site_dir: ../_site

theme:
  name: material
  language: es
  palette:
    - scheme: default
      primary: deep purple
      accent: purple
    - scheme: slate
      primary: deep purple
      accent: purple
  font:
    text: Inter
    code: JetBrains Mono
  features:
    - navigation.tabs
    - navigation.tabs.sticky
    - navigation.sections
    - navigation.expand
    - navigation.top
    - navigation.indexes
    - toc.follow
    - search.suggest
    - search.highlight
    - content.code.copy
    - content.code.annotate

plugins:
  - search:
      lang: es
  - awesome-pages
  - section-index
  - literate-nav:
      nav_file: SUMMARY.md
  - minify:
      minify_html: true

markdown_extensions:
  - admonition
  - pymdownx.details
  - pymdownx.superfences
  - pymdownx.highlight:
      anchor_linenums: true
  - pymdownx.inlinehilite
  - pymdownx.tabbed:
      alternate_style: true
  - pymdownx.emoji:
      emoji_index: !!python/name:material.extensions.emoji.twemoji
      emoji_generator: !!python/name:material.extensions.emoji.to_svg
  - attr_list
  - md_in_html
  - toc:
      permalink: true

nav:
  - Inicio: index.md
  - API REST:
      - Overview: api/index.md
      - Autenticación: api/auth.md
      - Pacientes: api/pacientes.md
      - Psicólogos: api/psicologos.md
      - Administración: api/admin.md
      - Citas: api/citas.md
      - Diario Emocional: api/diario-emocional.md
      - Progreso Emocional: api/progreso-emocional.md
      - Historial Clínico: api/historial-clinico.md
      - Sesiones: api/sesiones.md
      - Mensajes: api/mensajes.md
      - Ajustes: api/ajustes.md
      - Direcciones: api/direcciones.md
      - Situaciones: api/situaciones.md
      - OpenAPI: api/openapi/index.md
  - Arquitectura:
    - arquitectura/index.md
    - Diagrama general: arquitectura/diagrama.md
    - Base de datos: arquitectura/base-de-datos.md
  - Guías:
    - guias/index.md
    - Instalación: guias/instalacion.md
    - Docker Compose: guias/docker.md
    - Notificaciones: guias/notificaciones.md
    - WebSocket: guias/websocket.md
  - Referencia:
      - Javadoc: javadoc/index.md

extra:
  version: 1.0.0
  author: Ivan
  license: MIT
  social:
    - icon: fontawesome/brands/github
      link: https://github.com/amanigrupo1/amani-apirest
MKEOF

# Crear OpenAPI index
cat > docs/api/openapi/index.md << 'OEOF'
# OpenAPI - Especificación de la API

Documentación generada automáticamente desde el código Java usando SpringDoc OpenAPI.

## Acceso Interactivo

### Swagger UI

La documentación interactiva Swagger UI está disponible en:

```
http://localhost:8080/swagger-ui.html
```

### Endpoints de metadatos

- **YAML**: `http://localhost:8080/v3/api-docs.yaml`
- **JSON**: `http://localhost:8080/v3/api-docs`

## Endpoints de la API

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/auth/login` | Iniciar sesión |
| POST | `/auth/register-paciente` | Registrar paciente |
| POST | `/auth/register-admin` | Registrar administrador |

### Paciente
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/citas` | Listar citas |
| GET | `/api/citas/paciente/{id}/agenda` | Agenda mensual |

### Psicólogo
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/psicologo/pacientes` | Listar pacientes |

### Administrador
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/admin/psicologos` | Listar psicólogos |
| GET | `/api/admin/pacientes` | Listar pacientes |

## Versión

OpenAPI 3.0 - SpringDoc 2.5.0
OEOF

# Crear SUMMARY.md si no existe
if [ ! -f docs/SUMMARY.md ]; then
cat > docs/SUMMARY.md << 'SEOF'
- [Inicio](../index.md)
- [API REST](api/index.md)
  - [Autenticación](api/auth.md)
  - [Pacientes](api/pacientes.md)
  - [Psicólogos](api/psicologos.md)
  - [Administración](api/admin.md)
  - [Citas](api/citas.md)
  - [Diario Emocional](api/diario-emocional.md)
  - [Progreso Emocional](api/progreso-emocional.md)
  - [Historial Clínico](api/historial-clinico.md)
  - [Sesiones](api/sesiones.md)
  - [Mensajes](api/mensajes.md)
  - [Ajustes](api/ajustes.md)
  - [Direcciones](api/direcciones.md)
  - [Situaciones](api/situaciones.md)
  - [OpenAPI](api/openapi/index.md)
- [Arquitectura](arquitectura/index.md)
  - [Diagrama general](arquitectura/diagrama.md)
  - [Base de datos](arquitectura/base-de-datos.md)
- [Guías](guias/index.md)
  - [Instalación](guias/instalacion.md)
  - [Docker Compose](guias/docker.md)
  - [Notificaciones](guias/notificaciones.md)
  - [WebSocket](guias/websocket.md)
- [Referencia](javadoc/index.md)
SEOF
fi

# Construir
echo "Construyendo..."
cd docs
/home/ivan/documentacion-amani/.venv/bin/mkdocs build --strict

# Desplegar usando gh-deploy
echo "Desplegando..."
cd /home/ivan/documentacion-amani
/home/ivan/documentacion-amani/.venv/bin/mkdocs gh-deploy --remote-branch gh-pages --message "docs: update documentation" --force --no-history -f docs/mkdocs.yml

echo "Despliegue completado en: https://amanigrupo1.github.io/amani-apirest/"
