# Plan de pruebas

Este documento resume la estrategia de testing del backend AMANI.

!!! note "Documento original"
    El plan de pruebas completo con todas las métricas detalladas está en [`plan-pruebas-api-rest.md`](plan-pruebas-api-rest.md).

---

## 1. Objetivo

Validar funcionalidad, seguridad por roles, integridad de datos y estabilidad de flujos críticos de la API REST de AMANI.

## 2. Alcance

### Incluye
- Autenticación y autorización (`/auth`, `/api/**`)
- Módulo de citas y agenda (creación, solapamientos, cancelación, bloqueos)
- Historial clínico, diario emocional, progreso emocional
- Mensajería, notificaciones FCM, soporte
- Endpoints administrativos (CRUD completo)
- Perfil de usuario y subida de archivos
- Seguridad: filtros JWT, roles (PACIENTE, PSICOLOGO, ADMIN), respuestas 401/403

### No incluye (esta fase)
- Pruebas E2E de frontend móvil/web
- Pruebas de carga masiva WebSocket
- Pruebas de estrés o rendimiento

## 3. Entorno de prueba

| Elemento | Valor |
|---|---|
| Perfil Spring | `test` |
| Config | `src/test/resources/application-test.properties` |
| BD | H2 en memoria, esquema `psicologia_app` |
| Seed | `DataInitializer`: 1 ADMIN |
| Firebase | Desactivado |

## 4. Criterios de entrada/salida

### Entrada
- API desplegada en entorno de test
- Colección Postman importada
- Tokens por rol disponibles

### Salida
- 100% casos P0 en verde
- Sin defectos críticos o altos abiertos
- Cobertura P0 ≥ 80% líneas, ≥ 70% ramas
- Cobertura P1 ≥ 80% líneas, ≥ 70% ramas

## 5. Datos de prueba por rol

| Rol | Email | Password |
|---|---|---|
| ADMIN | `admin@amani.com` | `admin1234` |
| PSICOLOGO | `psicologo_it@amani.com` | `pass123` |
| PACIENTE | `paciente_it@amani.com` | `pass123` |
