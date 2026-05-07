# Testing

Estrategia de pruebas, cobertura de código y registro de problemas conocidos del backend AMANI.

---

## 📖 Contenido

- [Plan de pruebas](test-plan.md) — Estrategia, alcance, entorno y criterios de cobertura.
- [Cobertura de tests](coverage.md) — Métricas visuales P0 (críticos) y P1 (alta prioridad).
- [Problemas conocidos](known-issues.md) — Errores detectados y correcciones aplicadas.

---

## 🔢 Resumen de cobertura (JaCoCo)

| Capa | Líneas | Ramas | Estado |
|---|---|---|---|
| **P0 — Críticos** (13 clases) | 52,31% | 27,27% | 🔴 |
| **P1 — Alta prioridad** (73 clases) | 46,88% | 25,35% | 🔴 |
| **Controllers** (43 clases) | 36,2% | 32,6% | 🔴 |
| **Security** (4 clases) | 79,9% | 54,5% | 🟡 |

!!! danger "Cobertura insuficiente"
    Los umbrales actuales están por debajo de los objetivos (≥ 80% líneas, ≥ 70% ramas).
    Se requiere incrementar tests unitarios e integración antes del siguiente release.

---

## 🧪 Perfil de test

```yaml
spring:
  profiles:
    active: test
  datasource:
    url: jdbc:h2:mem:psicologia_app
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

| Elemento | Valor |
|---|---|
| BD | H2 en memoria, esquema `psicologia_app` |
| Seed | 1 ADMIN (`admin@amani.com` / `admin1234`) |
| Firebase | Desactivado (`firebase.enabled=false`) |
| Tests | 370 tests, 51 archivos |

---

## 🚀 Ejecutar tests

```bash
# Todos los tests
./mvnw test

# Solo tests de integración
./mvnw test -Dtest="*IntegrationTest"

# Con perfil local (PostgreSQL en Docker)
docker compose up -d
./mvnw test -Dspring.profiles.active=local
```
