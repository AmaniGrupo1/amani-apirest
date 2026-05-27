# Entidades y Modelos

Las entidades (Models) mapean directamente con las tablas relacionales de **PostgreSQL** mediante Spring Data JPA e Hibernate. 

Utilizamos Project Lombok para reducir el "boilerplate", dotando a nuestras entidades de constructores genéricos, validadores y mutadores inyectados en tiempo de compilación.

## Catálogo de Entidades Principales

| Entidad | Descripción de Negocio |
|---|---|
| `Usuario` | Entidad raíz de autenticación. Posee credenciales (encriptadas vía BCrypt), correo, rol de sistema y estado de acceso. |
| `Paciente` | Entidad de dominio que representa a los usuarios que buscan terapia. Contiene información demográfica y clínica base. Vinculada 1:1 a un `Usuario`. |
| `Psicologo` | Representa al profesional clínico. Contiene información de colegiación profesional, años de experiencia y disponibilidad base. Vinculada 1:1 a un `Usuario`. |
| `PsicologoPaciente` | Entidad pivote/transaccional que representa la relación terapéutica **activa** (M:N) entre profesionales y pacientes. |
| `Cita` | Representa el evento transaccional central: una sesión agendada en un horizonte temporal específico. |
| `Pago` | Trazabilidad del cobro asociado a una `Cita`. Contiene metadatos enlazados al PaymentIntent de Stripe. |
| `Consentimiento` | Auditoría legal obligatoria para pacientes. Registra el acuse de recibo de RGPD, confidencialidad y telemedicina. |

## Consideraciones de Diseño

1. **Auditoría Transaccional:** La gran mayoría de entidades poseen marcas de tiempo automáticas como `createdAt` o `fechaRegistro`, permitiendo auditar la evolución de los datos.
2. **Lazy Loading:** Las relaciones `@OneToMany` o `@ManyToMany` se modelan empleando `FetchType.LAZY` por defecto para prevenir impactos masivos de memoria.
3. **Casos Especiales:** Los Menores de Edad validan la presencia de al menos una entidad `Tutor` anidada.
