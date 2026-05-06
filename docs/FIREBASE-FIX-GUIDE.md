# 🔥 Guía Completa: Fix de Integración Firebase + Secret Manager

> **Proyecto:** `amani-apirest` · **GCP Project:** `amani-160bf`  
> **Fecha:** 2026-05-06 · **Autor:** Generado por orquestador de agentes IA

---

## 📋 Tabla de Contenidos

- [1. Resumen del Problema](#1-resumen-del-problema)
- [2. Causa Raíz](#2-causa-raíz)
- [3. Plan de Acción Rápido](#3-plan-de-acción-rápido)
- [4. Paso 1 — Infraestructura y Permisos (GCP / IAM)](#4-paso-1--infraestructura-y-permisos-gcp--iam)
  - [4.1 Script Bash (Linux / macOS / Cloud Shell)](#41-script-bash-linux--macos--cloud-shell)
  - [4.2 Script PowerShell (Windows)](#42-script-powershell-windows)
  - [4.3 Configurar Application Default Credentials (ADC)](#43-configurar-application-default-credentials-adc)
  - [4.4 Nota Arquitectónica: Producción](#44-nota-arquitectónica-producción)
- [5. Paso 2 — Configuración Spring Boot](#5-paso-2--configuración-spring-boot)
  - [5.1 application.properties (antes vs después)](#51-applicationproperties-antes-vs-después)
  - [5.2 FirebaseConfig.java (refactorizado)](#52-firebaseconfigjava-refactorizado)
  - [5.3 Explicación de los cambios](#53-explicación-de-los-cambios)
- [6. Paso 3 — Refactorización de Código (Anti-patrón Optional)](#6-paso-3--refactorización-de-código-anti-patrón-optional)
  - [6.1 Por qué Optional<Bean> es un anti-patrón](#61-por-qué-optionalbean-es-un-anti-patrón)
  - [6.2 FirebaseAuthController.java (refactorizado)](#62-firebaseauthcontrollerjava-refactorizado)
  - [6.3 MensajeEventListener.java (refactorizado)](#63-mensajeeventlistenerjava-refactorizado)
  - [6.4 FirebaseNotificationService.java (refactorizado)](#64-firebasenotificationservicejava-refactorizado)
  - [6.5 FirebaseChatService.java (sin cambios necesarios)](#65-firebasechatservicejava-sin-cambios-necesarios)
- [7. Paso 4 — Checklist de Validación y Seguridad](#7-paso-4--checklist-de-validación-y-seguridad)
  - [7.1 Checklist de 5 pasos](#71-checklist-de-5-pasos)
  - [7.2 Verificación de .gitignore](#72-verificación-de-gitignore)
  - [7.3 Mensaje de Commit](#73-mensaje-de-commit)
- [8. Resumen de Archivos Modificados](#8-resumen-de-archivos-modificados)

---

## 1. Resumen del Problema

Al iniciar la aplicación Spring Boot, el proyecto falla porque no se encuentran las credenciales de Firebase. El intento de lectura desde Google Secret Manager devuelve `PERMISSION_DENIED` para el usuario `felixpa2001@gmail.com`, y la política IAM del secreto `firebase-service-account` está vacía (no hay bindings).

**Error principal:**

```
org.springframework.beans.factory.UnsatisfiedDependencyException:
  Error creating bean with name 'firebaseAuthController' ...
Caused by: java.io.IOException: No se encontraron credenciales de Firebase:
  ni en Secret Manager, ni en ruta externa ni en classpath (serviceAccountKey.json).
```

**Errores derivados:**

```
No qualifying bean of type 'com.google.firebase.auth.FirebaseAuth' available
No qualifying bean of type 'com.amani.amaniapirest.services.FirebaseChatService' available
```

---

## 2. Causa Raíz

| # | Causa | Detalle |
|---|---|---|
| 1 | **Política IAM vacía** | El secreto `firebase-service-account` existe pero no tiene bindings. Nadie tiene `roles/secretmanager.versions.access`. |
| 2 | **Parches temporales incorrectos** | Se añadió `Optional<FirebaseAuth>` en controladores y listeners (anti-patrón Spring). |
| 3 | **Lógica sucia en FirebaseConfig** | El método `resolveCredentialsStream()` probaba múltiples formatos de nombre de secreto en lugar de usar `spring.config.import`. |
| 4 | **`matchIfMissing=true`** | `@ConditionalOnProperty` usaba `matchIfMissing=true`, activando Firebase por defecto aunque no hubiera credenciales. |

---

## 3. Plan de Acción Rápido

```
1. ▶ Administrador ejecuta script IAM → concede acceso al secreto
2. ▶ Desarrollador configura ADC en local
3. ▶ Cambiar firebase.enabled=true en application.properties
4. ▶ Arrancar la app → beans Firebase se crean limpiamente
5. ▶ Si firebase.enabled=false → app arranca sin Firebase (sin errores)
```

---

## 4. Paso 1 — Infraestructura y Permisos (GCP / IAM)

### 4.1 Script Bash (Linux / macOS / Cloud Shell)

```bash
#!/usr/bin/env bash
# ============================================================================
# setup-firebase-iam.sh
# Concede acceso al secreto firebase-service-account en Google Secret Manager
# Requiere: gcloud CLI, permisos de administrador del proyecto GCP
# ============================================================================

set -euo pipefail

PROJECT_ID="amani-160bf"
SECRET_ID="firebase-service-account"
SA_ID="firebase-secret-reader"
SA_EMAIL="${SA_ID}@${PROJECT_ID}.iam.gserviceaccount.com"

echo "🔧 Proyecto: ${PROJECT_ID}"
echo "🔧 Secreto:  ${SECRET_ID}"
echo ""

# -------------------------------------------------------
# 1. Crear la Service Account dedicada
# -------------------------------------------------------
echo "1️⃣  Creando Service Account '${SA_ID}'..."
gcloud iam service-accounts create "${SA_ID}" \
  --project="${PROJECT_ID}" \
  --display-name="Lector de secreto Firebase para desarrollo local" \
  || echo "⚠️  La Service Account ya existe, continuando..."

# -------------------------------------------------------
# 2. Conceder rol MÍNIMO sobre el secreto específico
# -------------------------------------------------------
echo "2️⃣  Concediendo roles/secretmanager.secretAccessor a la SA..."
gcloud secrets add-iam-policy-binding "${SECRET_ID}" \
  --project="${PROJECT_ID}" \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/secretmanager.secretAccessor"

# -------------------------------------------------------
# 3. Conceder acceso al usuario humano (desarrollo local)
# -------------------------------------------------------
echo "3️⃣  Concediendo acceso a felixpa2001@gmail.com..."
gcloud secrets add-iam-policy-binding "${SECRET_ID}" \
  --project="${PROJECT_ID}" \
  --member="user:felixpa2001@gmail.com" \
  --role="roles/secretmanager.secretAccessor"

# -------------------------------------------------------
# 4. Descargar clave JSON (solo para desarrollo local)
# -------------------------------------------------------
KEY_FILE="${HOME}/.config/gcloud/${SA_ID}-key.json"
echo "4️⃣  Descargando clave JSON a: ${KEY_FILE}"

mkdir -p "$(dirname "${KEY_FILE}")"

gcloud iam service-accounts keys create "${KEY_FILE}" \
  --project="${PROJECT_ID}" \
  --iam-account="${SA_EMAIL}" \
  --key-file-type=json

# -------------------------------------------------------
# 5. Verificar acceso al secreto
# -------------------------------------------------------
echo ""
echo "5️⃣  Verificando acceso al secreto..."
gcloud secrets versions access latest \
  --secret="${SECRET_ID}" \
  --project="${PROJECT_ID}" | head -c 80

echo ""
echo ""
echo "✅ ─────────────────────────────────────────────"
echo "✅ Configuración IAM completada con éxito."
echo "✅ Clave descargada en: ${KEY_FILE}"
echo "⚠️  NUNCA subas este archivo al repositorio."
echo "✅ ─────────────────────────────────────────────"
```

### 4.2 Script PowerShell (Windows)

```powershell
# ============================================================================
# setup-firebase-iam.ps1
# Concede acceso al secreto firebase-service-account en Google Secret Manager
# Requiere: gcloud CLI, permisos de administrador del proyecto GCP
# ============================================================================

$PROJECT_ID = "amani-160bf"
$SECRET_ID  = "firebase-service-account"
$SA_ID      = "firebase-secret-reader"
$SA_EMAIL   = "${SA_ID}@${PROJECT_ID}.iam.gserviceaccount.com"

Write-Host "🔧 Proyecto: $PROJECT_ID"
Write-Host "🔧 Secreto:  $SECRET_ID"
Write-Host ""

# 1. Crear Service Account
Write-Host "1️⃣  Creando Service Account '$SA_ID'..."
gcloud iam service-accounts create $SA_ID `
  --project=$PROJECT_ID `
  --display-name="Lector de secreto Firebase para desarrollo local"

# 2. Conceder rol mínimo sobre el secreto
Write-Host "2️⃣  Concediendo roles/secretmanager.secretAccessor a la SA..."
gcloud secrets add-iam-policy-binding $SECRET_ID `
  --project=$PROJECT_ID `
  --member="serviceAccount:$SA_EMAIL" `
  --role="roles/secretmanager.secretAccessor"

# 3. Conceder acceso al usuario humano
Write-Host "3️⃣  Concediendo acceso a felixpa2001@gmail.com..."
gcloud secrets add-iam-policy-binding $SECRET_ID `
  --project=$PROJECT_ID `
  --member="user:felixpa2001@gmail.com" `
  --role="roles/secretmanager.secretAccessor"

# 4. Descargar clave JSON
$KEY_FILE = "$env:USERPROFILE\.config\gcloud\$SA_ID-key.json"
Write-Host "4️⃣  Descargando clave JSON a: $KEY_FILE"

New-Item -ItemType Directory -Force -Path (Split-Path $KEY_FILE) | Out-Null

gcloud iam service-accounts keys create $KEY_FILE `
  --project=$PROJECT_ID `
  --iam-account=$SA_EMAIL `
  --key-file-type=json

# 5. Verificar
Write-Host ""
Write-Host "5️⃣  Verificando acceso al secreto..."
gcloud secrets versions access latest --secret=$SECRET_ID --project=$PROJECT_ID | Select-Object -First 1

Write-Host ""
Write-Host "✅ Configuración IAM completada con éxito."
Write-Host "✅ Clave descargada en: $KEY_FILE"
Write-Host "⚠️  NUNCA subas este archivo al repositorio."
```

### 4.3 Configurar Application Default Credentials (ADC)

Después de ejecutar el script IAM, configura las credenciales en tu máquina local:

```bash
# ──────────────────────────────────────────────────────
# OPCIÓN A: Login de usuario (recomendado para desarrollo)
# ──────────────────────────────────────────────────────
gcloud auth application-default login
# Se abre el navegador → iniciar sesión con felixpa2001@gmail.com

# ──────────────────────────────────────────────────────
# OPCIÓN B: Usar la clave de la Service Account
# ──────────────────────────────────────────────────────
export GOOGLE_APPLICATION_CREDENTIALS="$HOME/.config/gcloud/firebase-secret-reader-key.json"

# ──────────────────────────────────────────────────────
# Verificar que ADC funciona
# ──────────────────────────────────────────────────────
gcloud auth application-default print-access-token

# ──────────────────────────────────────────────────────
# Verificar acceso al secreto
# ──────────────────────────────────────────────────────
gcloud secrets versions access latest \
  --secret="firebase-service-account" \
  --project=amani-160bf | head -c 80
# Debe mostrar: { "type": "service_account", "project_id": "amani-160bf" ...
```

### 4.4 Nota Arquitectónica: Producción

| Entorno | Método | Razón |
|---|---|---|
| **Cloud Run** | Asignar SA `firebase-secret-reader` al servicio (sin clave) | La plataforma inyecta credenciales vía metadata server |
| **GKE** | Workload Identity (vincular KSA → GSA) | Credenciales de corta duración, sin claves JSON |
| **CI/CD** | Workload Identity Federation | Acceso sin claves exportadas |
| **Local** | ADC (`gcloud auth application-default login`) o `GOOGLE_APPLICATION_CREDENTIALS` | Solo para desarrollo |

> ⚠️ **Nunca uses claves JSON de Service Account en producción.** Son estáticas, no rotan automáticamente y suponen un riesgo de seguridad si se filtran.

---

## 5. Paso 2 — Configuración Spring Boot

### 5.1 application.properties (antes vs después)

**❌ ANTES (parche temporal):**

```properties
spring.cloud.gcp.project-id=amani-160bf
spring.config.import=sm://
firebase.secret-name=firebase-service-account
firebase.enabled=false
```

**✅ DESPUÉS (solución definitiva):**

```properties
spring.cloud.gcp.project-id=amani-160bf
spring.config.import=optional:sm://firebase-service-account

# Desactivar Firebase en desarrollo si no se dispone del archivo de credenciales
# Poner a 'true' para habilitar Firebase cuando las credenciales estén disponibles
firebase.enabled=false
```

**Cambios explicados:**

| Cambio | Razón |
|---|---|
| `sm://` → `optional:sm://firebase-service-account` | `optional:` permite que la app arranque aunque el secreto no sea accesible. Nombre explícito evita importar TODOS los secretos. |
| Eliminado `firebase.secret-name` | El nombre del secreto ya va en `spring.config.import`, no se necesita propiedad separada. |
| `firebase.enabled=false` | Sigue por defecto en `false` para desarrollo local. Cambiar a `true` cuando las credenciales estén disponibles. |

### 5.2 FirebaseConfig.java (refactorizado)

Archivo: `src/main/java/com/amani/amaniapirest/configuration/FirebaseConfig.java`

```java
package com.amani.amaniapirest.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuración del cliente Firebase Admin SDK.
 *
 * <p>Solo se activa cuando {@code firebase.enabled=true} en application.properties.
 * Cuando la propiedad está ausente o es {@code false}, ninguno de los beans
 * Firebase se registra y la aplicación arranca normalmente sin Firebase.</p>
 */
@Configuration
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.database.url:}")
    private String firebaseDatabaseUrl;

    @Value("${firebase.credentials-path:}")
    private String firebaseCredentialsPath;

    /**
     * Contenido del secreto inyectado por Spring Cloud GCP mediante
     * {@code spring.config.import=optional:sm://firebase-service-account}.
     * Vacío si el secreto no está disponible (entorno local sin acceso a SM).
     */
    @Value("${firebase-service-account:}")
    private String firebaseSecretPayload;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream credentialsStream = resolveCredentialsStream();

        FirebaseOptions.Builder builder = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream));

        if (firebaseDatabaseUrl != null && !firebaseDatabaseUrl.isBlank()) {
            builder.setDatabaseUrl(firebaseDatabaseUrl);
        } else {
            log.warn("[Firebase] No se configuró firebase.database.url; "
                    + "las operaciones de RTDB no estarán disponibles.");
        }

        FirebaseOptions options = builder.build();
        FirebaseApp app = FirebaseApp.initializeApp(options);
        log.info("[Firebase] FirebaseApp inicializada correctamente.");
        return app;
    }

    @Bean
    public FirebaseDatabase firebaseDatabase(FirebaseApp firebaseApp) {
        FirebaseDatabase db = FirebaseDatabase.getInstance(firebaseApp);
        log.info("[Firebase] FirebaseDatabase configurada para la URL: {}", firebaseDatabaseUrl);
        return db;
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        FirebaseAuth auth = FirebaseAuth.getInstance(firebaseApp);
        log.info("[Firebase] FirebaseAuth configurada correctamente.");
        return auth;
    }

    /**
     * Resuelve el stream de credenciales en orden de prioridad:
     * <ol>
     *   <li>Contenido del secreto inyectado por Spring Cloud GCP
     *       (property {@code firebase-service-account}).</li>
     *   <li>Ruta externa configurada en {@code firebase.credentials-path}.</li>
     *   <li>Classpath: {@code serviceAccountKey.json}.</li>
     * </ol>
     */
    private InputStream resolveCredentialsStream() throws IOException {
        // 1. Secreto inyectado por Spring Cloud GCP (spring.config.import=optional:sm://)
        if (firebaseSecretPayload != null && !firebaseSecretPayload.isBlank()) {
            log.info("[Firebase] Cargando credenciales desde Secret Manager "
                    + "(vía spring.config.import). Longitud: {}",
                    firebaseSecretPayload.length());
            return new ByteArrayInputStream(
                    firebaseSecretPayload.trim().getBytes(StandardCharsets.UTF_8));
        }

        // 2. Ruta externa (variable de entorno o propiedad)
        if (firebaseCredentialsPath != null && !firebaseCredentialsPath.isBlank()) {
            Path path = Paths.get(firebaseCredentialsPath);
            if (Files.exists(path)) {
                log.info("[Firebase] Cargando credenciales desde ruta externa: {}",
                        firebaseCredentialsPath);
                return Files.newInputStream(path);
            }
            log.warn("[Firebase] Ruta de credenciales configurada pero archivo no encontrado: {}",
                    firebaseCredentialsPath);
        }

        // 3. Fallback a classpath
        log.info("[Firebase] Cargando credenciales desde classpath: serviceAccountKey.json");
        InputStream stream = getClass().getClassLoader()
                .getResourceAsStream("serviceAccountKey.json");
        if (stream == null) {
            throw new IOException("No se encontraron credenciales de Firebase: "
                    + "ni en Secret Manager (property firebase-service-account vacía), "
                    + "ni en ruta externa ni en classpath (serviceAccountKey.json). "
                    + "Verifique que spring.config.import=optional:sm://firebase-service-account "
                    + "está configurado y que el usuario tiene el rol "
                    + "roles/secretmanager.secretAccessor sobre el secreto.");
        }
        return stream;
    }
}
```

### 5.3 Explicación de los cambios

| Cambio | Antes | Después | Razón |
|---|---|---|---|
| Anotación de clase | `@ConditionalOnProperty(matchIfMissing = true)` | `@ConditionalOnProperty(matchIfMissing = false)` | Firebase desactivado por defecto; solo se activa con `firebase.enabled=true` |
| Inyección de secreto | `SecretManagerTemplate` + bucle de formatos | `@Value("${firebase-service-account:}")` | Usa el mecanismo nativo de Spring Cloud GCP (`spring.config.import`) |
| Resolución de credenciales | Probaba `firebaseSecretName`, `sm://name`, `sm@name`, `projects/...` | Lee la property inyectada directamente | Un solo camino, limpio y mantenible |
| Dependencias | `@Autowired SecretManagerTemplate`, `@Value firebase.secret-name`, `@Value gcp.projectId` | Solo `@Value firebaseSecretPayload` | Menos dependencias, menos puntos de fallo |

---

## 6. Paso 3 — Refactorización de Código (Anti-patrón Optional)

### 6.1 Por qué Optional<Bean> es un anti-patrón

| Problema | Detalle |
|---|---|
| **Rompe el contrato del contenedor** | Spring DI decide en tiempo de arranque si un bean existe o no. `@ConditionalOnBean` es el mecanismo nativo para condicionalidad. |
| **Oculta errores de configuración** | Si `FirebaseAuth` falta por un bug en `FirebaseConfig`, el desarrollador no se entera — solo recibe un 503 silencioso. |
| **Contamina el código de negocio** | Cada método debe hacer `isPresent()` / `isEmpty()`, ensuciando la lógica de dominio con comprobaciones de infraestructura. |
| **Viola SOLID** | Vierte la responsabilidad de "saber si Firebase está disponible" en cada consumidor, cuando es una decisión de configuración que pertenece a un solo lugar. |

**Solución:** `@ConditionalOnBean(FirebaseApp.class)` a nivel de clase. Cuando `firebase.enabled=false` → `FirebaseConfig` no se registra → `FirebaseApp` no existe → toda la cadena Firebase se omite.

### 6.2 FirebaseAuthController.java (refactorizado)

Archivo: `src/main/java/com/amani/amaniapirest/controllers/login/FirebaseAuthController.java`

```java
package com.amani.amaniapirest.controllers.login;

import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.FirebaseApp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para la obtención de tokens personalizados de Firebase.
 *
 * <p>Solo se registra cuando el bean {@link FirebaseApp} está disponible
 * (es decir, cuando {@code firebase.enabled=true}).</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Firebase Auth", description = "Obtención de tokens para Firebase")
@ConditionalOnBean(FirebaseApp.class)
public class FirebaseAuthController {

    private static final Logger log = LoggerFactory.getLogger(FirebaseAuthController.class);
    private final FirebaseAuth firebaseAuth;
    private final UsuarioRepository usuarioRepository;

    @Operation(summary = "Obtener Firebase Custom Token",
               description = "Genera un token de Firebase para el usuario autenticado")
    @GetMapping("/firebase-token")
    public ResponseEntity<Map<String, String>> getFirebaseToken(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            log.warn("[Firebase Auth] Usuario no encontrado para email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String uid = String.valueOf(usuario.getIdUsuario());

            Map<String, Object> additionalClaims = new HashMap<>();
            additionalClaims.put("role", usuario.getRol().name());

            String customToken = firebaseAuth.createCustomToken(uid, additionalClaims);

            Map<String, String> response = new HashMap<>();
            response.put("firebaseToken", customToken);

            log.info("[Firebase Auth] Token generado correctamente para usuario ID: {}", uid);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            log.error("[Firebase Auth] Error al crear custom token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```

**Cambios clave:**

- `Optional<FirebaseAuth>` → `FirebaseAuth` (inyección directa)
- Añadido `@ConditionalOnBean(FirebaseApp.class)` a nivel de clase
- Eliminado `firebaseAuth.isEmpty()` check y `firebaseAuth.get()`
- Eliminado `return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()`

### 6.3 MensajeEventListener.java (refactorizado)

Archivo: `src/main/java/com/amani/amaniapirest/listeners/MensajeEventListener.java`

```java
package com.amani.amaniapirest.listeners;

import com.amani.amaniapirest.events.MensajeNuevoEvent;
import com.amani.amaniapirest.models.Mensaje;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.services.FirebaseChatService;
import com.amani.amaniapirest.services.FirebaseNotificationService;
import com.google.firebase.FirebaseApp;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener del dominio {@link Mensaje} adaptado para no usar WebSocket.
 *
 * <p>Al recibir un {@link MensajeNuevoEvent} escribe el mensaje en Firebase RTDB
 * para entrega en tiempo real a los clientes y desencadena notificación push.</p>
 *
 * <p>Solo se instancia si {@link FirebaseApp} está disponible (es decir,
 * si Firebase está configurado). En entornos sin Firebase este bean se omite.</p>
 */
@Component
@ConditionalOnBean(FirebaseApp.class)
public class MensajeEventListener {

    private final FirebaseChatService firebaseChatService;
    private final FirebaseNotificationService firebaseService;

    public MensajeEventListener(FirebaseChatService firebaseChatService,
                                FirebaseNotificationService firebaseService) {
        this.firebaseChatService = firebaseChatService;
        this.firebaseService = firebaseService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMensajeNuevo(MensajeNuevoEvent event) {
        Mensaje mensaje = event.getMensaje();
        Usuario receiver = mensaje.getReceiver();
        Usuario sender = mensaje.getSender();

        if (receiver == null) return;

        firebaseChatService.enviarMensaje(mensaje);

        if (receiver.getFcmToken() != null && !receiver.getFcmToken().isBlank()) {
            String nombreRemitente = (sender != null)
                    ? sender.getNombre() + " " + sender.getApellido()
                    : "Alguien";
            firebaseService.enviarPush(
                    receiver.getFcmToken(),
                    "Nuevo mensaje de " + nombreRemitente,
                    mensaje.getMensaje());
        }
    }
}
```

**Cambios clave:**

- `Optional<FirebaseChatService>` → `FirebaseChatService` (inyección directa)
- `Optional<FirebaseNotificationService>` → `FirebaseNotificationService` (inyección directa)
- Añadido `@ConditionalOnBean(FirebaseApp.class)` a nivel de clase
- Eliminados todos los `isPresent()` checks y `.get()` calls

### 6.4 FirebaseNotificationService.java (refactorizado)

Archivo: `src/main/java/com/amani/amaniapirest/services/FirebaseNotificationService.java`

```java
package com.amani.amaniapirest.services;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Servicio para el envío de notificaciones push mediante Firebase Cloud Messaging (FCM).
 *
 * <p>Usa Firebase Admin SDK para enviar notificaciones. Si el token es nulo/vacío
 * la notificación se omite. Solo se instancia si {@link FirebaseApp} está disponible.</p>
 */
@Service
@ConditionalOnBean(FirebaseApp.class)
public class FirebaseNotificationService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseNotificationService.class);

    public void enviarPush(String fcmToken, String titulo, String cuerpo) {
        if (!StringUtils.hasText(fcmToken)) {
            log.debug("[FCM] Token vacío — notificación omitida. Título: '{}'", titulo);
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(cuerpo)
                            .build())
                    .build();

            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("[FCM] Notificación enviada. messageId={}", messageId);
        } catch (Exception ex) {
            log.error("[FCM] Error enviando notificación: {}", ex.getMessage(), ex);
        }
    }
}
```

**Cambios clave:**

- Añadido `@ConditionalOnBean(FirebaseApp.class)` (antes no lo tenía)
- Sin esta anotación, el servicio se instanciaría siempre y fallaría al usar `FirebaseMessaging.getInstance()` si Firebase no está configurado

### 6.5 FirebaseChatService.java (sin cambios necesarios)

Ya tenía `@ConditionalOnBean(FirebaseApp.class)`. No se modificó.

---

## 7. Paso 4 — Checklist de Validación y Seguridad

### 7.1 Checklist de 5 pasos

Ejecutar estos comandos en orden en la terminal de `felixpa2001@gmail.com`:

```bash
# ┌─────────────────────────────────────────────────────────────┐
# │ PASO 1: Verificar acceso al secreto en Secret Manager      │
# └─────────────────────────────────────────────────────────────┘
gcloud secrets versions access latest \
  --secret="firebase-service-account" \
  --project=amani-160bf | head -c 80
# ✅ Esperado: { "type": "service_account", "project_id": "amani-160bf" ...
# ❌ Si da PERMISSION_DENIED → volver al Paso 1 y ejecutar el script IAM

# ┌─────────────────────────────────────────────────────────────┐
# │ PASO 2: Verificar Application Default Credentials           │
# └─────────────────────────────────────────────────────────────┘
gcloud auth application-default print-access-token
# ✅ Esperado: un token de acceso (string largo)
# ❌ Si da error → ejecutar: gcloud auth application-default login

# ┌─────────────────────────────────────────────────────────────┐
# │ PASO 3: Arrancar la app con Firebase habilitado             │
# └─────────────────────────────────────────────────────────────┘
# Cambiar en application.properties: firebase.enabled=true
./mvnw spring-boot:run
# ✅ Esperado en el log: "[Firebase] FirebaseApp inicializada correctamente."
# ❌ Si aparece UnsatisfiedDependencyException → revisar ADC y permisos

# ┌─────────────────────────────────────────────────────────────┐
# │ PASO 4: Probar el endpoint de Firebase Token                │
# └─────────────────────────────────────────────────────────────┘
curl -s -H "Authorization: Bearer TU_JWT_TOKEN" \
  http://localhost:8080/api/auth/firebase-token
# ✅ Esperado: {"firebaseToken":"..."}
# ❌ Si devuelve 404 → firebase.enabled no está en true o FirebaseApp no se creó

# ┌─────────────────────────────────────────────────────────────┐
# │ PASO 5: Verificar que la app arranca SIN Firebase           │
# └─────────────────────────────────────────────────────────────┘
# Cambiar en application.properties: firebase.enabled=false
./mvnw spring-boot:run
# ✅ Esperado: la app arranca sin errores de Firebase
# ✅ El endpoint /api/auth/firebase-token NO existe (404, no 503)
# ❌ Si aparece error de bean faltante → hay un @ConditionalOnBean faltante
```

### 7.2 Verificación de .gitignore

```bash
# Confirmar que las claves JSON de Service Account están excluidas
git check-ignore *serviceAccount*.json firebase-*.json
# ✅ Debe mostrar las rutas (confirmación de que están ignoradas)

# Confirmar que no hay claves JSON en el historial de git
git log --all --diff-filter=A --name-only --pretty=format: -- '*serviceAccount*.json' 'firebase-*.json'
# ✅ Debe estar vacío (ninguna clave fue commiteada)
# ❌ Si aparecen archivos → ejecutar: git filter-branch o BFG Repo-Cleaner
```

**Patrones añadidos al `.gitignore`:**

```gitignore
### Firebase / GCP Service Account Keys ###
*serviceAccount*.json
firebase-*.json
```

### 7.3 Mensaje de Commit

```bash
git add -A
git commit -m "fix(firebase): replace Optional DI anti-pattern with @ConditionalOnBean and clean Secret Manager integration

- Remove Optional<FirebaseAuth>, Optional<FirebaseChatService>,
  Optional<FirebaseNotificationService> from FirebaseAuthController
  and MensajeEventListener
- Add @ConditionalOnBean(FirebaseApp.class) to FirebaseAuthController,
  MensajeEventListener, FirebaseNotificationService
- Refactor FirebaseConfig: replace SecretManagerTemplate multi-format
  hack with @Value from spring.config.import=optional:sm://
- Set @ConditionalOnProperty(matchIfMissing=false) on FirebaseConfig
- Update application.properties: optional:sm://firebase-service-account,
  remove firebase.secret-name property
- Add service account key patterns to .gitignore

BREAKING CHANGE: firebase.enabled now defaults to false; set to true
explicitly when Firebase credentials are available

Refs: #PERMISSION_DENIED"
```

---

## 8. Resumen de Archivos Modificados

| Archivo | Cambio |
|---|---|
| `FirebaseConfig.java` | Eliminado `SecretManagerTemplate` + lógica multi-formato; usa `@Value("${firebase-service-account:}")` desde `optional:sm://`; `matchIfMissing=false` |
| `FirebaseAuthController.java` | Eliminado `Optional<FirebaseAuth>`; `@ConditionalOnBean(FirebaseApp.class)`; inyección directa |
| `MensajeEventListener.java` | Eliminado `Optional<FirebaseChatService>` y `Optional<FirebaseNotificationService>`; `@ConditionalOnBean(FirebaseApp.class)`; inyección directa |
| `FirebaseNotificationService.java` | Añadido `@ConditionalOnBean(FirebaseApp.class)` (no lo tenía) |
| `application.properties` | `optional:sm://firebase-service-account`; eliminado `firebase.secret-name` |
| `.gitignore` | Añadidos `*serviceAccount*.json` y `firebase-*.json` |

### Cadena de beans (firebase.enabled=true)

```
FirebaseConfig (@ConditionalOnProperty)
  ├── FirebaseApp
  ├── FirebaseDatabase
  └── FirebaseAuth
        │
        ├── FirebaseAuthController (@ConditionalOnBean FirebaseApp)
        ├── FirebaseChatService (@ConditionalOnBean FirebaseApp)
        ├── FirebaseNotificationService (@ConditionalOnBean FirebaseApp)
        └── MensajeEventListener (@ConditionalOnBean FirebaseApp)
```

### Cadena de beans (firebase.enabled=false)

```
FirebaseConfig → NO se registra
FirebaseApp → NO existe
Todos los @ConditionalOnBean(FirebaseApp.class) → NO se registran
App arranca limpia, sin beans Firebase
```
