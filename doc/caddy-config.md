# Configuración de Caddy para desarrollo local

Este documento describe cómo configurar Caddy como proxy inverso para que el emulador de Android Studio pueda acceder a la API en modo desarrollo.

## Arquitectura

```
Emulador Android (10.0.2.2)
         |
         | HTTP
         |
    Caddy (puerto 80)
         |
         | reverse_proxy
         |
    Spring Boot (puerto 8080)
```

## Instalación de Caddy

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install caddy

# Verificar instalación
caddy version
```

## Configuración

### 1. Copiar el Caddyfile

```bash
sudo cp doc/Caddyfile /etc/caddy/Caddyfile
sudo chown caddy:caddy /etc/caddy/Caddyfile
```

### 2. Añadir al /etc/hosts

```bash
echo "127.0.0.1 amani.local" | sudo tee -a /etc/hosts
```

### 3. Reiniciar Caddy

```bash
sudo systemctl restart caddy
sudo systemctl status caddy  # Verificar que está activo
```

### 4. Verificar configuración

```bash
# Probar desde el host
curl http://amani.local/docs

# Probar desde el emulador
# En tu app Android, usa: http://amani.local/api/...
```

## Uso desde Android Emulator

En tu aplicación Android, usa `http://amani.local/` como base URL para los endpoints de la API.

Si usas Retrofit o OkHttp, configura la base URL así:

```kotlin
const val BASE_URL = "http://amani.local/"
```

## Troubleshooting

### Caddy no inicia
```bash
sudo systemctl status caddy
sudo journalctl -u caddy -f
```

### Error de conexión desde emulador
- Verifica que la API esté corriendo en el puerto 8080
- Verifica que Caddy esté activo: `sudo systemctl status caddy`
- Verifica el /etc/hosts: `cat /etc/hosts | grep amani.local`

### Certificados HTTPS
Caddy por defecto genera certificados autofirmados. Para desarrollo local con HTTP solo, no es necesario.

## Archivos relacionados

- `doc/Caddyfile` - Configuración principal
- `src/main/resources/application.properties` - Configuración de la API
