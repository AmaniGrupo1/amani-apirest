# Filtros y Roles de Acceso

## El `JwtAuthFilter`
A nivel de pipeline HTTP, todas las peticiones con destino protegido deben superar un filtro de seguridad en la clase `JwtAuthFilter`.

1. **Extracción del Header:** El filtro captura la cabecera `Authorization: Bearer <token>`.
2. **Descriptado y Verificación:** Valida las firmas matemáticas contra la secret key del backend para evitar suplantaciones de carga (payload tampering).
3. **Inyección en el Contexto:** Tras la validez exhaustiva, se recuperan las autorizaciones (Roles) y se establece la autenticación en el objeto thread-local `SecurityContextHolder`.

## Políticas de Rol
La cadena de filtros y anotaciones de método de Amani API prohíbe explícitamente las escaladas de privilegios:

- Una petición REST intentando alcanzar `/admin/**` por un usuario con Rol Paciente será rechazada en una fracción de milisegundo devolviendo el código HTTP `403 Forbidden`.
- Adicionalmente a nivel de capa `Service`, se aseguran de forzar que el identificador del usuario extraído mediante el contexto coindida con los IDs a modificar.
