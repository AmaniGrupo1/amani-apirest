# Firebase y Secret Manager

Guía de solución de problemas de integración entre Firebase Realtime Database, Firebase Cloud Messaging y Google Secret Manager.

---

## 📋 Tabla de contenidos

1. Resumen del problema
2. Causa raíz
3. Plan de acción rápido
4. Infraestructura y permisos GCP / IAM
5. Configuración Spring Boot
6. Refactorización de código
7. Checklist de validación

---

## Resumen del problema

Al iniciar la aplicación Spring Boot, el proyecto falla porque no se encuentran las credenciales de Firebase. El intento de lectura desde Google Secret Manager devuelve `PERMISSION_DENIED`.

**Error principal:**

```
org.springframework.beans.factory.UnsatisfiedDependencyException:
  Error creating bean with name 'firebaseAuthController' ...
Caused by: java.io.IOException: No se encontraron credenciales de Firebase
```

**Errores derivados:**

```
No qualifying bean of type 'com.google.firebase.auth.FirebaseAuth' available
No qualifying bean of type 'com.amani.amaniapirest.services.FirebaseChatService' available
```

---

## Causa raíz

| # | Causa | Detalle |
|---|---|---|
| 1 | Política IAM vacía | El secreto `firebase-service-account` no tiene bindings de IAM |
| 2 | Sin ADC configurado | El usuario local no tiene Application Default Credentials |
| 3 | Anti-patrón `Optional<Bean>` | Se usa `Optional` para beans de Firebase, ocultando errores de inicialización |
| 4 | Reglas RTDB cerradas | Las reglas `".read": false` rompen la app Android |

---

## Plan de acción rápido

```bash
# 1. Configurar IAM (ver guía GCP)
gcloud secrets add-iam-policy-binding firebase-service-account ...

# 2. Configurar ADC local
gcloud auth application-default login

# 3. Refactorizar anti-patrón Optional<Bean>
# Ver sección 6 más abajo

# 4. Desplegar reglas de Firebase en Firebase Console
# Copiar src/main/resources/firebase-rules.json → Firebase Console
```

---

## Infraestructura y permisos GCP / IAM

### Script Bash (Linux / macOS / Cloud Shell)

```bash
#!/usr/bin/env bash
set -euo pipefail

PROJECT_ID="amani-160bf"
SECRET_ID="firebase-service-account"
SA_ID="firebase-secret-reader"
SA_EMAIL="${SA_ID}@${PROJECT_ID}.iam.gserviceaccount.com"

# 1. Crear Service Account
gcloud iam service-accounts create "${SA_ID}" \
    --project="${PROJECT_ID}" \
    --display-name="Lector de secreto Firebase"

# 2. Conceder acceso al secreto
gcloud secrets add-iam-policy-binding "${SECRET_ID}" \
    --project="${PROJECT_ID}" \
    --member="serviceAccount:${SA_EMAIL}" \
    --role="roles/secretmanager.secretAccessor"

# 3. Conceder acceso al usuario (desarrollo)
gcloud secrets add-iam-policy-binding "${SECRET_ID}" \
    --project="${PROJECT_ID}" \
    --member="user:felixpa2001@gmail.com" \
    --role="roles/secretmanager.secretAccessor"

# 4. Verificar
gcloud secrets versions access latest \
    --secret="${SECRET_ID}" --project="${PROJECT_ID}" | head -c 80

echo "✅ IAM configurado"
```

### Configurar ADC

```bash
gcloud auth application-default login
gcloud auth application-default print-access-token
```

---

## Configuración Spring Boot

### `application.properties` (antes vs después)

**Antes:**
```properties
spring.config.import=sm://firebase-service-account
```

**Después:**
```properties
spring.config.import=optional:sm://firebase-service-account
firebase.enabled=true
```

---

## Refactorización de código

### Anti-patrón: `Optional<Bean>`

**Problema:** Usar `Optional<FirebaseAuth>` oculta fallos de inicialización y genera beans inválidos.

**Solución:** Inyectar el bean directamente o usar un wrapper condicional.

```java
// ANTES (anti-patrón)
@Autowired
private Optional<FirebaseAuth> firebaseAuth;

// DESPUÉS (correcto)
@Autowired
private FirebaseAuth firebaseAuth;

// O con condicional
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
@Component
public class FirebaseConfig { ... }
```

### FirebaseAuthController

Refactorizado para eliminar `Optional<FirebaseAuth>` y manejar la ausencia de Firebase mediante `@ConditionalOnProperty`.

### MensajeEventListener

Refactorizado para usar inyección directa de `FirebaseChatService` con `@ConditionalOnProperty`.

### FirebaseNotificationService

Refactorizado para usar inyección directa de `FirebaseMessaging`.

---

## Checklist de validación

- [ ] IAM configurado para el secreto `firebase-service-account`
- [ ] ADC configurado en local (`gcloud auth application-default login`)
- [ ] Anti-patrón `Optional<Bean>` eliminado del código
- [ ] `firebase.enabled=false` en `application-local.yml`
- [ ] `serviceAccountKey.json` añadido a `.gitignore`
- [ ] Reglas de Firebase desplegadas en Firebase Console
- [ ] `ChatWebSocketController` clasificado como candidato a eliminación
