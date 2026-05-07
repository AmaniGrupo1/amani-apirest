# Cobertura de tests

Métricas de cobertura generadas desde JaCoCo (`target/site/jacoco/jacoco.csv`).

---

## 📊 Informes visuales detallados

- [Cobertura P0 (clases críticas)](coverage-p0-visual.md)
- [Cobertura P1 (alta prioridad)](coverage-p1-visual.md)

---

## 🎯 Clasificación P0 / P1 / P2

| Prioridad | Descripción | Umbral líneas | Umbral ramas |
|---|---|---|---|
| **P0** | Auth, Security, Citas (críticos) | ≥ 80% | ≥ 70% |
| **P1** | Resto de controllers y services | ≥ 80% | ≥ 70% |
| **P2** | Listeners, configuración, utilidades | ≥ 60% | ≥ 50% |

---

## 🔧 Regenerar informes

```bash
# Ejecutar tests con JaCoCo
./mvnw test jacoco:report

# Copiar CSV para análisis visual
mkdir -p docs/testing
cp target/site/jacoco/jacoco.csv docs/testing/
```

---

## 📈 Dashboard HTML interactivo

Disponible en [`reports/coverage-dashboard.html`](../reports/coverage-dashboard.md).
