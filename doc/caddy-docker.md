# Caddy Proxy para desarrollo local

Este contenedor proporciona un proxy inverso usando Caddy que permite acceder a la API Spring Boot en desarrollo desde múltiples clientes.

## Accesos soportados

| Cliente | URL | Descripción |
|---------|-----|-------------|
| Emulador Android | `http://10.0.2.2/` | Gateway del emulador a la máquina host |
| Terminal físico (localhost) | `http://localhost/` | Acceso local desde el navegador |
| Terminal físico (domain) | `http://amani.local/` | Acceso usando domain local |
| Red local | `http://[IP-MAQUINA]/` | Acceso desde otros dispositivos en la red |

## Arquitectura

```
┌─────────────────┐     ┌─────────────────┐
│  Android        │     │  Terminal       │
│  Emulator       │     │  (navegador)    │
│  (10.0.2.2)     │     │  localhost      │
└──────┬──────────┘     └────────┬────────┘
       │ HTTP                    │ HTTP
       │                         │
┌──────▼──────────┐    ┌────────▼──────────┐
│  Caddy (80)     │    │  Caddy (80)       │
│  Proxy          │    │  Proxy            │
└──────┬──────────┘    └────────┬──────────┘
       │                         │
       │ reverse_proxy           │
       │                         │
┌──────▼──────────┐
│  Spring Boot    │
│  (localhost:8080)│
└─────────────────┘
```

## Uso

### 1. Construir la imagen

```bash
docker build -t amani-caddy -f dockerfile .
```

### 2. Ejecutar el contenedor

```bash
docker run -d \
  --name amani-caddy \
  -p 80:80 \
  --network host \
  amani-caddy
```

> **Nota:** Usar `--network host` para que Caddy pueda acceder a `localhost:8080` del host.

### 3. Configuración del cliente

#### Android Emulator

En tu aplicación Android, configura la base URL:

```kotlin
const val BASE_URL = "http://10.0.2.2/"
```

#### Terminal físico (navegador)

- `http://localhost/docs` - Swagger UI
- `http://localhost/api/...` - Endpoints de la API

#### Terminal físico (curl, Postman, etc.)

```bash
curl http://localhost/docs
curl http://localhost/api/endpoint
```

## Verificación

```bash
# Probar desde el host
curl http://localhost/docs

# Ver logs del contenedor
docker logs amani-caddy

# Verificar conectividad desde emulador
adb shell curl http://10.0.2.2/docs
```

## Caddyfile

La configuración se encuentra en `doc/Caddyfile`:

```
:80 {
    reverse_proxy localhost:8080
}
```

## Notas

- El emulador de Android accede a la máquina host vía `10.0.2.2` (no `localhost`)
- Si la API no está corriendo, Caddy mostrará un error 502
- Para producción, considera usar una configuración más robusta con HTTPS
- El proxy funciona tanto en Docker como en instalación directa en el sistema