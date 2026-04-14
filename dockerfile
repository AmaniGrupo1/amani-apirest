# Caddy as proxy reverse for Android emulator development
# Android emulator access via 10.0.2.2 (gateway to host)

FROM caddy:2-alpine

# Copy Caddyfile configuration
COPY doc/Caddyfile /etc/caddy/Caddyfile

# Expose port 80 for Android emulator access
EXPOSE 80

# Start Caddy with config
CMD ["caddy", "run", "--config", "/etc/caddy/Caddyfile"]
