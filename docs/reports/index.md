# Informes

Dashboards y reportes generados automáticamente sobre calidad de código y ejecución de tests.

---

## 📖 Contenido

- [Dashboard de cobertura](coverage-dashboard.md) — KPIs, tendencias históricas y tabla filtrable de clases.

---

## 🔄 Regenerar informes

```bash
bash scripts/generate-dashboard.sh
```

El script:
1. Ejecuta `./mvnw test`
2. Parsea `target/site/jacoco/jacoco.csv`
3. Genera `docs/reports/coverage-dashboard.html` (interactivo)
4. Genera `docs/reports/coverage-dashboard.png` (screenshot)
