---
title: Backend (Dokka/Javadoc)
---

# Referencia API — Backend

La documentación del backend se genera automáticamente desde el código fuente Java/Kotlin con [Dokka](https://github.com/Kotlin/dokka).

## Generar documentación

```bash
./mvnw dokka:dokka -q
```

## Navegar la documentación

### Dokka
La documentación Dokka se genera durante el workflow y queda disponible en esta sección.

<iframe src="dokka/index.html" width="100%" height="600px" style="border: 1px solid #e0e0e0; border-radius: 8px;"></iframe>

### Javadoc
Si prefieres el formato clásico de Javadoc:

<iframe src="javadoc/index.html" width="100%" height="600px" style="border: 1px solid #e0e0e0; border-radius: 8px;"></iframe>
