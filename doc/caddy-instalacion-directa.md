# Instalación directa de Caddy (sin Docker)

Si prefieres instalar Caddy directamente en tu sistema en lugar de usar Docker:

## Instalación

### Ubuntu/Debian

```bash
sudo apt update
sudo apt install caddy
```

### macOS (Homebrew)

```bash
brew install caddy
```

### Arch Linux

```bash
sudo pacman -S caddy
```

## Configuración

### 1. Copiar el Caddyfile

```bash
sudo cp doc/Caddyfile /etc/caddy/Caddyfile
sudo chown caddy:caddy /etc/caddy/Caddyfile
```

### 2. Añadir entries al /etc/hosts (opcional)

```bash
echo "127.0.0.1 amani.local" | sudo tee -a /etc/hosts
```

### 3. Reiniciar Caddy

```bash
sudo systemctl restart caddy
sudo systemctl enable caddy  # Para inicio automático
```

### 4. Verificar

```bash
# Verificar estado
sudo systemctl status caddy

# Ver logs
sudo journalctl -u caddy -f

# Probar
curl http://localhost/docs
```

## Accesos soportados

| Cliente | URL |
|---------|-----|
| Emulador Android | `http://10.0.2.2/` |
| Terminal físico (localhost) | `http://localhost/` |
| Red local | `http://[IP-MAQUINA]/` |

## Troubleshooting

### Caddy no inicia

```bash
# Ver errores
sudo journalctl -u caddy -n 50

# Verificar configuración
sudo caddy validate --config /etc/caddy/Caddyfile
```

### Error 502 Bad Gateway

- Verifica que la API esté corriendo: `curl http://localhost:8080/docs`
- Verifica que no haya conflictos de puerto: `sudo lsof -i :80`

### Permisos

```bash
# Asegurar permisos correctos
sudo chown -R caddy:caddy /etc/caddy
sudo chmod -R 755 /etc/caddy
```
