# 📊 Sistema de Monitoreo — Amani API REST

Stack completo de monitoreo para producción con **Prometheus + Grafana + Alertmanager (Slack) + Loki + Promtail**.

---

## 🏗️ Arquitectura

```
Spring Boot (puerto 8081/actuator/prometheus)
    │
    ▼
Prometheus ──────────────► Alertmanager ──► Slack
    │                           (9093)      (#amani-alerts-critical)
    │                                       (#amani-alerts-warning)
    │                                       (#amani-security)
    ▼
Grafana (3000)
    ├── Dashboards JVM/HTTP/DB
    ├── Dashboard Negocio (Citas, Pagos, Auth)
    └── Dashboard Logs (Loki)

Promtail ──► Loki (3100) ──► Grafana (Explore + Logs Dashboard)

Node Exporter (9100) ──► Prometheus
Postgres Exporter (9187) ──► Prometheus
```

---

## 🚀 Inicio Rápido (Local / Staging)

### 1. Configurar variables de entorno

Copia el archivo `.env` y añade tu webhook de Slack:

```bash
# .env (ya existe en el proyecto)
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/T.../B.../...
GRAFANA_ADMIN_PASSWORD=tu-contraseña-segura
POSTGRES_PASSWORD=Sandia4you   # ya está en .env
```

### 2. Arrancar el stack completo

```bash
# Solo base de datos + API (desarrollo)
docker compose up -d

# Con monitoreo completo (staging/producción local)
docker compose -f docker-compose.yml -f docker-compose.monitoring.yml up -d
```

### 3. Acceder a los servicios

| Servicio | URL | Credenciales |
|---|---|---|
| **Grafana** | http://localhost:3000 | admin / ver `GRAFANA_ADMIN_PASSWORD` |
| **Prometheus** | http://localhost:9090 | - |
| **Alertmanager** | http://localhost:9093 | - |
| **Actuator/Health** | http://localhost:8081/actuator/health | - |
| **Actuator/Prometheus** | http://localhost:8081/actuator/prometheus | - |

---

## 📊 Dashboards Disponibles

Los dashboards se provisionan **automáticamente** en Grafana bajo la carpeta `Amani API REST`:

| Dashboard | UID | Descripción |
|---|---|---|
| **Amani API — Overview** | `amani-overview` | JVM, HTTP, HikariCP, estado general |
| **Amani — Business Metrics** | `amani-business` | Citas, Pagos Stripe, Autenticación, WebSocket |
| **Amani — Logs Centralizados** | `amani-logs` | Logs en tiempo real via Loki |

---

## 🔔 Alertas Configuradas

### Disponibilidad
- `AmaniApiDown` — 🚨 API caída más de 1 minuto → **#amani-alerts-critical**
- `AmaniApiHighRestartRate` — ⚠️ Más de 2 reinicios en 15 min

### HTTP
- `AmaniHighErrorRate` — 🚨 Tasa 5xx > 5%
- `AmaniHighLatency` — ⚠️ Latencia P95 > 2s
- `AmaniHighRequestVolume` — ⚠️ > 1000 req/s

### JVM
- `AmaniJvmHeapUsageHigh` — ⚠️ Heap > 85%
- `AmaniJvmHeapCritical` — 🚨 Heap > 95%
- `AmaniGcPauseHigh` — ⚠️ GC pausas frecuentes

### Base de Datos
- `AmaniDbDown` — 🚨 PostgreSQL caído
- `AmaniDbConnectionPoolExhausted` — 🚨 Pool HikariCP agotado
- `AmaniDbHighConnections` — ⚠️ > 80 conexiones activas

### Host
- `AmaniHostCpuHigh` — ⚠️ CPU > 85%
- `AmaniHostMemoryLow` — ⚠️ RAM disponible < 15%
- `AmaniHostDiskFull` — 🚨 Disco < 15% libre

### Negocio (Amani específico)
- `AmaniHighLoginFailureRate` — ⚠️ Tasa de fallos login > 30% → **#amani-security**
- `AmaniStripePaymentFailures` — ⚠️ Fallos de pago Stripe

---

## 🌐 Configuración para GCP Cloud Run (Producción)

En GCP, la app corre en **Cloud Run** sin infraestructura fija. Hay dos opciones:

### Opción A: Google Managed Prometheus (GMP) — **Recomendada**

GCP ofrece Prometheus gestionado que hace scraping de Cloud Run automáticamente.

```bash
# 1. Habilitar la API de Managed Prometheus
gcloud services enable monitoring.googleapis.com

# 2. Añadir anotación en Cloud Run para que GMP detecte el endpoint
gcloud run services update amani-api \
  --region=europe-west1 \
  --set-annotations=run.googleapis.com/launch-stage=BETA
```

Luego configura el `PodMonitoring` o `ClusterPodMonitoring` en tu proyecto GCP.

### Opción B: Prometheus externo scrapeando Cloud Run

En `monitoring/prometheus/prometheus.yml`, reemplaza el target local:

```yaml
scrape_configs:
  - job_name: "amani-api"
    metrics_path: "/actuator/prometheus"
    scheme: https
    static_configs:
      - targets:
          - "amani-api-XXXX-ew.a.run.app"  # URL de tu servicio Cloud Run
    tls_config:
      insecure_skip_verify: false
```

> ⚠️ El endpoint `/actuator/prometheus` está en el puerto de management (8081). En Cloud Run, asegúrate de que ese puerto esté accesible internamente o usa el puerto principal (8080) si configuras Spring Boot sin puerto separado para GCP.

### Variables de entorno en Cloud Run

```bash
gcloud run services update amani-api \
  --set-env-vars="SPRING_PROFILES_ACTIVE=gcp" \
  --set-env-vars="MANAGEMENT_METRICS_TAGS_ENVIRONMENT=production"
```

---

## 📈 Métricas Personalizadas de Amani

La aplicación expone las siguientes métricas de negocio:

| Métrica | Tipo | Descripción |
|---|---|---|
| `amani_auth_login_success_total` | Counter | Logins exitosos |
| `amani_auth_login_failure_total` | Counter | Intentos fallidos de login |
| `amani_citas_created_total` | Counter | Citas creadas |
| `amani_citas_cancelled_total` | Counter | Citas canceladas |
| `amani_payments_stripe_success_total` | Counter | Pagos Stripe exitosos |
| `amani_payments_stripe_failure_total` | Counter | Pagos Stripe fallidos |
| `amani_websocket_sessions_active` | Gauge | Sesiones WebSocket activas |

---

## 🛠️ Comandos Útiles

```bash
# Ver estado de todos los servicios de monitoreo
docker compose -f docker-compose.yml -f docker-compose.monitoring.yml ps

# Ver logs de Prometheus
docker logs amani-prometheus -f

# Ver logs de Grafana
docker logs amani-grafana -f

# Recargar configuración de Prometheus sin reiniciar
curl -X POST http://localhost:9090/-/reload

# Recargar configuración de Alertmanager
curl -X POST http://localhost:9093/-/reload

# Verificar que los targets están UP en Prometheus
open http://localhost:9090/targets

# Consultar métricas directamente
curl http://localhost:8081/actuator/prometheus | grep amani_

# Detener solo el stack de monitoreo
docker compose -f docker-compose.monitoring.yml down

# Detener todo (incluido la app)
docker compose -f docker-compose.yml -f docker-compose.monitoring.yml down
```

---

## 📁 Estructura de Archivos

```
monitoring/
├── prometheus/
│   ├── prometheus.yml          # Configuración de scraping
│   └── alerts.yml              # Reglas de alerta (17 alertas)
├── alertmanager/
│   └── alertmanager.yml        # Enrutamiento a Slack (3 canales)
├── loki/
│   └── loki-config.yml         # Configuración Loki (retención 30d)
├── promtail/
│   └── promtail-config.yml     # Recolección de logs Docker
└── grafana/
    ├── provisioning/
    │   ├── datasources/
    │   │   └── datasources.yml  # Prometheus + Loki auto-configurados
    │   └── dashboards/
    │       └── dashboards.yml   # Provider de dashboards
    └── dashboards/
        ├── amani-overview.json  # Dashboard principal JVM/HTTP/DB
        ├── amani-business.json  # Dashboard de negocio
        └── amani-logs.json      # Dashboard de logs Loki
```
