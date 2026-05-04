# Integración Firebase Realtime Database — Chat en Tiempo Real

## Resumen de cambios (fase 2 — correcciones)

### A. Doble escritura RTDB — CORREGIDO

**Problema**: `ChatService.sendMessage()` llamaba directamente a `firebaseChatService.enviarMensaje()`, y si el flujo pasara por `MensajeService.create()`, el `MensajeEventListener` también escribiría en RTDB. Aunque no ocurría doble escritura en un mismo flujo (los paths son mutuamente excluyentes), había dos vías de publicación independientes, lo que era confuso y frágil.

**Corrección**: `ChatService.sendMessage()` ahora publica `MensajeNuevoEvent` (el mismo evento de dominio que usa `MensajeService`), y elimina la dependencia directa de `FirebaseChatService`. El `MensajeEventListener` es la **única vía** de escritura a RTDB.

**Flujo corregido**:
```
ChatService.sendMessage()
  → mensajeRepository.save()          // persiste en PostgreSQL
  → eventPublisher.publishEvent()      // publica MensajeNuevoEvent
    → MensajeEventListener.onMensajeNuevo()   // ÚNICA escritura a RTDB
    → FirebaseChatService.enviarMensaje()
    → FirebaseNotificationService.enviarPush()
```

### B. `/api/chats/conversations` — CORREGIDO

**Problema**: Devolvía lista vacía porque no resolvía el userId del JWT.

**Corrección**: Ahora inyecta `UsuarioRepository`, extrae el email del `@AuthenticationPrincipal UserDetails`, y resuelve el `idUsuario` con `usuarioRepository.findByEmail()`. Este es el mismo patrón usado por `UserDetailsServiceImpl`.

### C. Reglas de Firebase — ACTUALIZADO

**Hallazgo crítico**: La app Android **accede directamente a Firebase RTDB** a través de `ChatFirebaseService`. Esto significa que las reglas cerradas (`".read": false, ".write": false`) **ROMPERÍAN la app Android**.

**Rutas usadas por Android**:
- `/chats/{roomId}/messages` — lectura y escritura (mensajes)
- `/typing/{roomId}/{userId}` — indicadores de escritura
- `/users/{userId}/isOnline` — estado online
- `/users/{userId}/lastSeen` — última conexión

**Reglas propuestas** (en `src/main/resources/firebase-rules.json`):
- `/chats/$chatId/messages` — autenticado puede leer/escribir
- `/typing/$roomId/$userId` — autenticado puede leer; solo el propio usuario puede escribir
- `/users/$userId/isOnline` y `lastSeen` — autenticado puede leer; solo el propio usuario puede escribir

**⚠️ REQUIRES CONFIRMATION**: Estas reglas deben desplegarse manualmente en Firebase Console. No se han desplegado automáticamente.

### D. ChatWebSocketController — CLASIFICADO

**Estado**: Vacío (0 bytes), sin referencias en el código. Dado que Android usa Firebase RTDB directamente para tiempo real, este controller es **candidato para eliminación**.

**Clasificación**: `unused — candidate for removal`

**⚠️ REQUIRES CONFIRMATION**: No se ha eliminado sin aprobación explícita.

### E. `SendMessageRequestDTO` — Import corregido

Se corrigió el import en `ChatController` para usar el paquete completo en lugar del import implícito.

---

## ⚠️ REQUIERE CONFIRMACIÓN

1. **Android escribe directamente en RTDB sin pasar por el backend** — `ChatFirebaseService.sendMessage()` en Android escribe directamente en `/chats/{roomId}/messages`. Esto significa que los mensajes enviados desde Android NO se persisten en PostgreSQL. Si se desea backend-first estricto, la app Android debe llamar al endpoint `POST /api/chats/messages` del backend y dejar que el backend publique en RTDB. **Esto es una decisión arquitectónica importante.**

2. **Reglas de Firebase** — Las reglas propuestas están en `firebase-rules.json` pero NO se han desplegado. Debes copiarlas a Firebase Console → Realtime Database → Rules.

3. **`serviceAccountKey.json` en git** — Sigue trackeado. Se recomienda `git rm --cached src/main/resources/serviceAccountKey.json` y corregir `.gitignore`.

4. **`ChatWebSocketController` vacío** — Candidato para eliminación. Confirmar si se elimina.

5. **Doble escritura desde Android** — Si Android sigue escribiendo directamente en RTDB Y el backend también escribe via `MensajeEventListener`, los mensajes enviados desde el endpoint REST aparecerán duplicados en la vista de Android (una vez escritos por Android, otra por el backend). Esto solo ocurre si el usuario usa AMBOS canales simultáneamente.

---

## Archivos modificados en esta fase

| Archivo | Cambio |
|---------|--------|
| `services/chat/ChatService.java` | Eliminada dependencia de `FirebaseChatService`. Ahora usa `ApplicationEventPublisher` para publicar `MensajeNuevoEvent`. Vía única de publicación RTDB. |
| `controllers/chat/ChatController.java` | Inyectado `UsuarioRepository`. `/api/chats/conversations` ahora resuelve userId desde JWT via `@AuthenticationPrincipal`. |
| `resources/firebase-rules.json` | Actualizadas reglas para permitir acceso autenticado (Android necesita acceso directo a RTDB). |
| `services/chat/ChatServiceTest.java` | Actualizado para reflejar que `ChatService` ya no depende de `FirebaseChatService`, sino de `ApplicationEventPublisher`. Test de doble escritura eliminado; verificado que solo se publica evento. |

## Archivos NO tocados

- Todos los archivos de login/JWT (intactos)
- `MensajeEventListener.java` (intacto — sigue siendo la única vía de publicación a RTDB)
- `FirebaseChatService.java` (intacto — solo lo llama el listener)
- `FirebaseNotificationService.java` (intacto)
- `MensajeService.java` (intacto)
- `MensajePsicologoService.java` (intacto)
- Todos los controllers existentes (intactos)
- `ChatWebSocketController.java` (intacto — vacío, pendiente de confirmación para eliminar)

---

## Instrucciones de configuración

```properties
# Firebase — ya existentes
firebase.database.url=https://amani-160bf-default-rtdb.europe-west1.firebasedatabase.app/

# Firebase — nuevas propiedades
firebase.enabled=true
firebase.credentials-path=/path/to/serviceAccountKey.json   # Opcional; si no se establece, usa classpath
```

---

## Ejemplos de request/response

### Enviar mensaje
```
POST /api/chats/messages
Authorization: Bearer <JWT>
Content-Type: application/json

{"idSender":1,"idReceiver":2,"mensaje":"Hola, ¿cómo estás?"}

→ 201 Created
{"idMensaje":42,"idSender":1,"nombreSender":"Laura García","idReceiver":2,"nombreReceiver":"Carlos López","mensaje":"Hola, ¿cómo estás?","enviadoEn":"2026-05-04T14:30:00","leido":false,"idCita":null}
```

### Historial de chat
```
GET /api/chats/1_2/messages?limit=50
Authorization: Bearer <JWT>

→ 200 OK
[{"idMensaje":40,"idSender":1,"nombreSender":"Laura García",...}, ...]
```

### Conversaciones del usuario autenticado
```
GET /api/chats/conversations
Authorization: Bearer <JWT>

→ 200 OK
[{"chatId":"1_2","otherUserId":2,"otherUserName":"Carlos López","lastMessage":"Hey!","lastMessageAt":"2026-05-04T14:05:00","lastMessageRead":false,"unreadCount":3}]
```
