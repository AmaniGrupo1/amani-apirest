# Servicios Core

La capa de servicios aloja la inteligencia del dominio de la aplicación. Estos servicios operan sobre entidades y actúan como intermediarios entre los Controladores (API REST) y los Repositorios (Bases de datos). 

Todos los métodos que modifiquen estado operan bajo transaccionalidad mediante `@Transactional`.

## Servicios de Autenticación
### `AuthService`
Gestiona la autenticación y el registro de usuarios en la plataforma.
- **Responsabilidades:** Centraliza la lógica de inicio de sesión (validación de credenciales y generación de JWT). Gestiona el registro de pacientes con soporte para menores de edad (incluyendo validación de tutores y directivas de consentimiento).
- **Integraciones:** Módulo JWT (`JwtUtil`), `SecurityConfig`.

## Servicios de Dominio Clínico
### `CitaService` (Módulo Paciente)
Servicio que implementa la lógica de negocio para la gestión de citas clínicas por parte de los pacientes.
- **Responsabilidades:** Agendar nuevas citas, validar disponibilidad de horarios en la agenda compartida de los psicólogos, cancelar reservas y consultar el histórico.
- **Eventos asíncronos:** Desencadena tareas para notificar al psicólogo sobre modificaciones a través de correos y FCM.

### `ProgresoEmocionalService`
- **Responsabilidades:** Gestiona el seguimiento clínico del avance terapéutico del paciente, registrando de forma objetiva la evolución dictada por el profesional durante las sesiones.

### `DiarioEmocionService`
- **Responsabilidades:** Gestiona el registro del diario emocional del paciente, posibilitando a los usuarios almacenar actualizaciones cualitativas acerca de su estado anímico diario, las cuales son legibles posteriormente por el psicólogo.

## Servicios Transversales e Integraciones
### `PaymentService` & `WebhookService`
- **Responsabilidades:** Integración completa con la pasarela de pagos Stripe. Genera `PaymentIntents`, cobra sesiones y responde de manera asíncrona a los webhooks emitidos por Stripe para asentar el `EstadoPago` de las citas sin bloqueo de hilos.

### `FirebaseNotificationService`
- **Responsabilidades:** Construcción de cargas útiles (payloads) y dispatching de Notificaciones PUSH hacia la plataforma de mensajería Firebase Cloud Messaging (FCM). 

### `ChatService` & `WebSocketPresenceTracker`
- **Responsabilidades:** Gestión de los estados de conexión en tiempo real de los usuarios en la interfaz de mensajería. Coordina la distribución de mensajes a través de los canales STOMP configurados sobre WebSocket.
