# Amani API REST

Bienvenido a la documentación oficial del sistema **Amani API REST**. Esta plataforma expone los servicios centrales para la gestión de pacientes, psicólogos, citas, y seguimiento emocional.

## Resumen Ejecutivo

Amani es una plataforma de telepsicología construida bajo una **arquitectura de microservicios monolíticos** con **Spring Boot 3**. El sistema provee un entorno seguro, escalable y observable para:
- Autenticación segura mediante **JWT Stateless**.
- Gestión en tiempo real de citas, chats (WebSockets) y videollamadas.
- Pagos integrados con **Stripe**.
- Notificaciones PUSH gestionadas a través de **Firebase Cloud Messaging**.
- Persistencia robusta en **PostgreSQL** mediante Spring Data JPA.

## Estructura de la Documentación

Esta documentación está diseñada para onboarding rápido de desarrolladores, ingenieros DevOps y arquitectos:

- 🏛️ **[Arquitectura](arquitectura/overview.md):** Diagramas, patrones de diseño y flujo de vida de peticiones.
- ⚙️ **[Backend Core](backend/servicios.md):** Especificación técnica de Servicios, Modelos y DTOs basados en el código fuente.
- 🔌 **[API y Endpoints](api/endpoints.md):** Contratos OpenAPI interactivos.
- 🛡️ **[Seguridad](seguridad/jwt.md):** Explicación de los filtros y roles de autorización.
- 📊 **[Observabilidad](observabilidad/monitoreo.md):** Métricas operativas exportadas vía Prometheus y Grafana.
- 🐳 **[DevOps y Despliegue](devops/docker.md):** Contenerización y canalizaciones CI/CD para puestas en producción rápidas.

> [!TIP]
> Puedes utilizar el buscador superior para encontrar rápidamente la clase Java, endpoint o configuración específica que necesites.
