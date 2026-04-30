# Plan de Pruebas API REST - Amani

## 1. Objetivo
Validar funcionalidad, seguridad por roles, integridad de datos y estabilidad de flujos críticos de la API.

## 2. Alcance
Incluye:
- Autenticación y autorización (`/auth`, `/api/**`)
- Módulo de citas y agenda
- Historial clínico, diario y progreso emocional
- Mensajería, notificaciones y soporte
- Endpoints administrativos clave

No incluye en esta fase:
- Pruebas E2E de frontend móvil/web
- Pruebas de carga masiva WebSocket

## 3. Entorno de prueba
- Perfil: `test`
- Config: `src/test/resources/application-test.properties`
- BD: PostgreSQL de pruebas (recomendado Testcontainers)
- Datos semilla:
  - 1 ADMIN
  - 2 PSICOLOGOS
  - 3 PACIENTES (1 con tutor)
  - Citas en estados `PENDIENTE`, `CONFIRMADA`, `CANCELADA`, `COMPLETADA`

## 4. Criterios de entrada/salida
Entrada:
- API desplegada en entorno de test
- Colección Postman importada
- Tokens por rol disponibles

Salida:
- 100% casos P0 en verde
- Sin defectos críticos o altos abiertos
- Evidencia de ejecución guardada en CI

## 5. Matriz de casos de prueba

## P0 - Críticos
| ID | Módulo | Endpoint | Precondiciones | Request (resumen) | Resultado esperado |
|---|---|---|---|---|---|
| TC-AUTH-01 | Auth | `POST /auth/login` | Usuario activo existente | email/password correctos | `200`, JWT no vacío, rol correcto |
| TC-AUTH-02 | Auth | `POST /auth/login` | Usuario existente | password incorrecta | `401` con error controlado |
| TC-AUTH-03 | Auth | `POST /auth/register-paciente` | Email no registrado | payload válido | `200/201`, paciente creado |
| TC-SEC-01 | Seguridad | `GET /api/citas/mis-citas` | Sin token | sin `Authorization` | `401` |
| TC-SEC-02 | Seguridad | `GET /api/admin/preguntas` | Token PACIENTE | request con JWT PACIENTE | `403` |
| TC-CITA-01 | Citas | `POST /api/citas/psicologo/cita` | Psicólogo y paciente válidos | payload cita válido | `200/201`, cita persistida |
| TC-CITA-02 | Citas | `POST /api/citas/psicologo/cita` | Slot ya ocupado | misma fecha/hora | `409` o error de conflicto |
| TC-CITA-03 | Citas | `GET /api/citas/psicologo/{id}/disponibilidad` | Horario definido + citas previas | fecha objetivo | `200`, slots coherentes |
| TC-CITA-04 | Citas | `PATCH /api/citas/{id}/cancelar` | Cita existente cancelable | id de cita | `200`, estado `CANCELADA` |
| TC-CITA-05 | Citas | `PATCH /api/citas/cambio/{id}/estado` | Cita en estado final | transición inválida | `400/409`, no cambia estado |

## P1 - Alta prioridad
| ID | Módulo | Endpoint | Precondiciones | Request (resumen) | Resultado esperado |
|---|---|---|---|---|---|
| TC-HIST-01 | Historial | `POST /api/historial-clinico` | Paciente válido | payload historial válido | `200/201`, registro creado |
| TC-HIST-02 | Historial | `PUT /api/historial-clinico/{id}` | Historial existente | campos actualizados | `200`, cambios persistidos |
| TC-DIARIO-01 | Diario | `POST /api/diario-emocion` | Paciente válido | emoción/intensidad/nota válidas | `200/201` |
| TC-PROG-01 | Progreso | `POST /api/progreso-emocional` | Paciente válido | métricas válidas | `200/201` |
| TC-PROG-02 | Progreso | `GET /api/progreso-emocional/{id}` | Registro existente | id válido | `200`, DTO correcto |
| TC-MSG-01 | Mensajes | `POST /api/mensajes` | Emisor/receptor válidos | payload mensaje | `200/201`, mensaje creado |
| TC-MSG-02 | Mensajes | `PATCH /api/mensajes/{id}/leido` | Mensaje existente | id válido | `200`, `leido=true` |
| TC-NOT-01 | Notificación | `GET /api/notificaciones/no-leidas/{idUsuario}` | Usuario con notificaciones | idUsuario | `200`, lista/contador correctos |
| TC-SOP-01 | Soporte | `POST /api/tickets-soporte` | Usuario autenticado | payload ticket válido | `200/201` |
| TC-SOP-02 | Soporte | `PUT /api/tickets-soporte/admin/{id}/estado` | Token ADMIN + ticket | cambio estado válido | `200`, estado actualizado |
| TC-ADM-01 | Admin | `POST /api/admin/psicologos/asignar-psicologo` | IDs existentes | asignación válida | `200`, relación persistida |

## P2 - Complementarios
| ID | Módulo | Endpoint | Precondiciones | Request (resumen) | Resultado esperado |
|---|---|---|---|---|---|
| TC-PERF-01 | Perfil | `POST /api/psicologo/{id}/foto` | Token válido + archivo imagen | multipart imagen válida | `200`, URL/path guardado |
| TC-PERF-02 | Perfil | `POST /api/psicologo/{id}/foto` | Archivo no imagen | multipart inválido | `400/415` |
| TC-AGENDA-01 | Agenda | `PUT /api/citas/psicologo/{id}/horario` | Psicólogo existente | horario semanal válido | `200`, horario actualizado |
| TC-AGENDA-02 | Agenda | `POST /api/citas/psicologo/{id}/dias-no-disponibles` | Psicólogo con agenda | fecha bloqueo válida | `200/201` |

## 6. Explicación de por qué estos casos
- `AUTH/SEC`: cualquier fallo aquí expone datos clínicos o rompe acceso total.
- `CITAS`: es el flujo de negocio más sensible por conflictos de agenda y estados.
- `HISTORIAL/DIARIO/PROGRESO`: son datos clínicos, requieren consistencia y aislamiento por paciente.
- `MENSAJERIA/NOTIFICACION`: impacta seguimiento terapéutico y operación diaria.
- `SOPORTE/ADMIN`: cubre operación interna y gestión de incidencias.

## 7. Base de automatización

## 7.1 Unit + Integración (JUnit + MockMvc)
- Estructura sugerida:
  - `src/test/java/.../auth/AuthControllerTest.java`
  - `src/test/java/.../security/SecurityAccessTest.java`
  - `src/test/java/.../citas/CitaControllerIntegrationTest.java`
  - `src/test/java/.../historial/HistorialClinicoIntegrationTest.java`
- Anotaciones recomendadas:
  - Unitarias: `@ExtendWith(MockitoExtension.class)`
  - Web capa: `@WebMvcTest(...)`
  - Integración: `@SpringBootTest` + `@AutoConfigureMockMvc`

## 7.2 Contrato/API (Postman + Newman)
Colección existente:
- `doc/postman/amani-api.postman_collection.json`

Comando ejemplo:
```bash
newman run doc/postman/amani-api.postman_collection.json \
  --env-var baseUrl=http://localhost:8080 \
  --reporters cli,junit \
  --reporter-junit-export target/newman-report.xml
```

## 7.3 Pipeline CI recomendado
1. `./mvnw -q -DskipTests=false test`
2. Levantar app de test
3. Ejecutar Newman
4. Publicar reportes JUnit + Newman

## 8. Definición de Done de QA
- P0 y P1 ejecutados y aprobados
- Evidencia en reportes
- Defectos críticos/altos cerrados
- Regresión básica de seguridad completada
