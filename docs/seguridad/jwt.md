# Autenticación con JWT (Stateless)

La protección del servicio se delega de extremo a extremo a JSON Web Tokens (JWT). Esta estrategia puramente **Stateless** evita la administración de sesiones en el servidor y favorece la escalabilidad del sistema.

## Generación del Token (`JwtUtil`)

1. El usuario realiza una petición `POST /auth/login` con sus credenciales.
2. Si el hash (`BCrypt`) coindice, `JwtUtil` empaqueta un Token HMAC-SHA256 con:
    - `Subject`: El email del usuario.
    - `Claims`: Un mapa con los permisos y roles asignados al usuario en la BD (`ROLE_ADMIN`, `ROLE_PACIENTE`, `ROLE_PSICOLOGO`).
    - `Expiration`: Definido a 24 horas por defecto.

## Configuración y Seguridad Global
Se deshabilitan las directivas tradicionales de renderizado web en la clase `SecurityConfig` para priorizar interacciones RESTful:
- CSRF Desactivado (inecesario bajo JWT Bearer Authorization).
- CORS Habilitado.
- Exclusión total del componente `SessionManagement` (STATELESS policy).
