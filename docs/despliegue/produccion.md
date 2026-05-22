# Despliegue en Producción

El ecosistema de Amani debe ser instanciado bajo contextos protegidos y altamente disponibles al tratarse de un servicio de telemedicina crítica.

## Prerrequisitos de Despliegue
Un servidor de producción (e.g. en Google Cloud Platform - Compute Engine) requiere:
- Docker y Docker Compose instalados.
- Tráfico HTTP/HTTPS y puertos de WebSockets expuestos si se usan pasarelas Nginx inversas.

## Variables de Entorno y Secrets
Por cuestiones críticas de **Secret Management**, nunca se incrustan (hardcodean) configuraciones en el código. El archivo `.env` o la configuración del sistema de orquestación debe inyectar las siguientes credenciales:

| Entorno / Clave | Propósito |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexión de producción a la BD de PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Credenciales cifradas de base de datos |
| `JWT_SECRET_KEY` | Firma privada generadora de tokens JWT. **Debe** ser robusta y alfanumérica. |
| `STRIPE_API_KEY` | Llave privada otorgada por el panel de Stripe Developers |
| `FIREBASE_CREDENTIALS` | Referencia al archivo `.json` de Google Cloud (Service Account) para envío de notificaciones. |

## Instrucciones Clásicas (Docker)

```bash
# 1. Clonar el repositorio
git clone <URL_DEL_REPO>

# 2. Configurar variables
cp .env.example .env
nano .env

# 3. Levantar dependencias en segundo plano
docker-compose up -d --build
```
