# Caddy Proxy para desarrollo con Android Emulator

Este contenedor proporciona un proxy inverso usando Caddy que permite al emulador de Android Studio acceder a la API Spring Boot en desarrollo.

## Arquitectura

```
┌─────────────────┐
│  Android        │
│  Emulator       │
│  (10.0.2.2)     │
└──────┬──────────┘
       │ HTTP
       │
┌──────▼──────────┐
│  Caddy (80)     │
│  Proxy          │
└──────┬──────────┘
       │
       │ reverse_proxy
       │
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

### 3. Acceder desde Android Emulator

En tu aplicación Android, configura la base URL:

```kotlin
const val BASE_URL = "http://10.0.2.2/"
```

O si usas Retrofit:

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2/")
    .build()
```

## Verificación

```bash
# Probar desde el host
curl http://localhost/docs

# Ver logs del contenedor
docker logs amani-caddy
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
