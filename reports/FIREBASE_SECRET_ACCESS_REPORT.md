# Informe: Problema de acceso a credenciales de Firebase (Secret Manager)

Fecha: 2026-05-05
Proyecto: amani-apirest
Autor: (Generado automáticamente)

---

## 1. Resumen ejecutivo

Al iniciar la aplicación Spring Boot, el proyecto falla porque no se encuentran las credenciales de Firebase. El intento de lectura desde Google Secret Manager devuelve `PERMISSION_DENIED` para el usuario autenticado (`felixpa2001@gmail.com`) y la política IAM del secreto `firebase-service-account` está vacía (no hay bindings). Como consecuencia, Spring no puede crear `FirebaseApp` y aparecen errores por beans faltantes (por ejemplo `FirebaseAuth`, `FirebaseChatService`). Se introdujeron cambios temporales en el código para permitir que la aplicación arranque sin Firebase y para mejorar la trazabilidad.

---

## 2. Evidencias (entradas de log y salidas relevantes suministradas)

1) Exception principal (stacktrace recortado, tal como informado):

```
org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'firebaseAuthController' ...
Caused by: java.io.IOException: No se encontraron credenciales de Firebase: ni en Secret Manager, ni en ruta externa ni en classpath (serviceAccountKey.json).
```

2) Salida de `gcloud auth list` y `gcloud config get-value project` (usuario activo y proyecto):

```
ACTIVE: *
ACCOUNT: felixpa2001@gmail.com

gcloud config get-value project
amani-160bf
```

3) Resultado de intentar acceder al secreto por CLI:

```
gcloud secrets versions access latest --secret="firebase-service-account" --project=amani-160bf
ERROR: (gcloud.secrets.versions.access) PERMISSION_DENIED: Permission 'secretmanager.versions.access' denied ...
```

4) Resultado de `gcloud secrets get-iam-policy firebase-service-account --project=amani-160bf`:

```
etag: ACAB
```
(Aparece sin bindings — no hay miembros con `roles/secretmanager.secretAccessor` para ese secreto.)

5) Mensajes de error posteriores al endurecimiento del arranque (mostrados durante pruebas):

- `No qualifying bean of type 'com.google.firebase.auth.FirebaseAuth' available` (resuelto haciendo la inyección opcional en el controlador)
- `No qualifying bean of type 'com.amani.amaniapirest.services.FirebaseChatService' available` (resuelto haciendo Optional<> injection en listener)

---

## 3. Localización del código relacionado (trazabilidad)

Archivos relevantes modificados o implicados:

- `src/main/java/com/amani/amaniapirest/configuration/FirebaseConfig.java`
  - Método principal: `firebaseApp()` (líneas aprox. 51-68)
  - Método: `resolveCredentialsStream()` (líneas aprox. 104-139)
  - Función: intenta leer credenciales desde Secret Manager, ruta externa (`firebase.credentials-path`) y, como fallback, `classpath:serviceAccountKey.json`.
  - Cambios realizados: se añadió inyección de `spring.cloud.gcp.project-id` y lógica para probar varios formatos de nombre de secreto (`firebaseSecretName`, `sm://...`, `sm@...`, `projects/.../secrets/.../versions/latest`) con logging ampliado.

- `src/main/resources/application.properties`
  - Se añadió: `firebase.enabled=false` para poder arrancar en entornos de desarrollo sin credenciales.
  - Contiene: `spring.config.import=sm://` y `spring.cloud.gcp.project-id=amani-160bf`.

- `src/main/java/com/amani/amaniapirest/controllers/login/FirebaseAuthController.java`
  - Antes: inyectaba `FirebaseAuth` como `final FirebaseAuth firebaseAuth;` (provocaba fallo de arranque si bean no existía).
  - Cambios: inyección ahora `Optional<FirebaseAuth> firebaseAuth;` y método devuelve 503 si Firebase no está disponible.

- `src/main/java/com/amani/amaniapirest/listeners/MensajeEventListener.java`
  - Antes: `@ConditionalOnBean(FirebaseChatService.class)` y dependencias directas a `FirebaseChatService` y `FirebaseNotificationService`.
  - Cambios: ahora inyecta `Optional<FirebaseChatService>` y `Optional<FirebaseNotificationService>` y protege las llamadas con `isPresent()`.

- `src/main/java/com/amani/amaniapirest/services/FirebaseChatService.java`
  - Está anotado con `@ConditionalOnBean(FirebaseApp.class)` y por ello solo se crea cuando `FirebaseApp` se inicializa correctamente.

---

## 4. Causa raíz

- El secreto `firebase-service-account` existe, pero su política IAM no contiene bindings, por lo tanto nadie tiene permiso para acceder a su versión (`secretmanager.versions.access`).
- El usuario local autenticado (`felixpa2001@gmail.com`) no tiene permiso para modificar la política del secreto (al intentar añadir el binding recibió `secretmanager.secrets.setIamPolicy` denied). Por eso no se puede conceder acceso vía `gcloud` con esa cuenta.
- Al no poder leer las credenciales desde Secret Manager y no existir `serviceAccountKey.json` en classpath ni `firebase.credentials-path` apuntando a un archivo válido, `FirebaseConfig.resolveCredentialsStream()` lanza IOException y evita la creación del bean `FirebaseApp`. Esto causa fallos por beans dependientes.

---

## 5. Acciones correctivas realizadas en el repositorio

1. Añadida propiedad en `application.properties`:
   - `firebase.enabled=false` (evita que `FirebaseConfig` intente inicializarse por defecto en dev).
2. Mejoras en `FirebaseConfig.resolveCredentialsStream()`:
   - Prueba varios nombres de secreto y registra intentos con nivel INFO/DEBUG.
   - Inyecta `spring.cloud.gcp.project-id` para construir `projects/.../secrets/.../versions/latest` como candidate.
3. Ajustes para tolerancia al fallar la inicialización de Firebase:
   - `FirebaseAuthController` ahora inyecta `Optional<FirebaseAuth>` y gestiona el caso en que Firebase no está disponible (devuelve 503 con log).
   - `MensajeEventListener` ahora inyecta `Optional<FirebaseChatService>` y `Optional<FirebaseNotificationService` y sólo ejecuta acciones si los servicios están presentes.

> Nota: Estos cambios permiten arrancar la aplicación y ejecutar funcionalidades no relacionadas con Firebase sin fallos. No son un sustituto de configurar correctamente Secret Manager en entornos donde se requiera Firebase.

---

## 6. Recomendaciones de resolución (pasos reproducibles y comandos)

A continuación se detallan opciones ordenadas por seguridad y buenas prácticas.

### Opción recomendada para desarrollo/CI (crear Service Account y usar su key)

1) Crear un Service Account y otorgarle acceso al secreto (ejecutar en una cuenta con permisos adecuados - Owner/Editor o IAM Admin):

```powershell
# Crear service account
gcloud iam service-accounts create firebase-secret-reader --project=amani-160bf --display-name="firebase-secret-reader"

# Dar permiso para leer el secreto (a nivel del secreto)
gcloud secrets add-iam-policy-binding firebase-service-account --project=amani-160bf --member="serviceAccount:firebase-secret-reader@amani-160bf.iam.gserviceaccount.com" --role="roles/secretmanager.secretAccessor"

# Generar clave JSON (guardar en local de forma segura)
gcloud iam service-accounts keys create "C:\Users\felix\.gcp\firebase-secret-reader-key.json" --iam-account=firebase-secret-reader@amani-160bf.iam.gserviceaccount.com --project=amani-160bf
```

2) En tu máquina local (PowerShell), exporta la variable de entorno para ADC y habilita Firebase:

```powershell
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\Users\felix\.gcp\firebase-secret-reader-key.json"
$env:FIREBASE_ENABLED="true"
.\mvnw spring-boot:run
```

Esto hace que las bibliotecas de Google utilicen la key del SA para acceder a Secret Manager y el `FirebaseConfig` podrá recuperar el secreto y crear `FirebaseApp`.

### Opción si solo el admin puede modificar IAM (solicitud para admin)

Pide al administrador del proyecto que ejecute (o pega este comando en un ticket):

```bash
# Otorgar a tu usuario permiso mínimo para leer el secreto
gcloud secrets add-iam-policy-binding firebase-service-account --project=amani-160bf --member="user:felixpa2001@gmail.com" --role="roles/secretmanager.secretAccessor"
```

Después de esto, en tu máquina local puedes hacer:
```powershell
gcloud auth application-default login
# o si ya usas gcloud auth login, probar lectura
gcloud secrets versions access latest --secret="firebase-service-account" --project=amani-160bf
```

### Opción rápida temporal (no recomendada para producción)

Si alguien con acceso te facilita el JSON del service account seguro, guarda el fichero en `src/main/resources/serviceAccountKey.json` (o en una ruta segura y configura `firebase.credentials-path`) y habilita Firebase localmente:

```powershell
$env:FIREBASE_ENABLED="true"
$env:FIREBASE_CREDENTIALS_PATH="C:\ruta\serviceAccountKey.json" # si usas ruta externa
.\mvnw spring-boot:run
```

> Seguridad: no comitees nunca el JSON en el repositorio.

---

## 7. Qué verificar tras aplicar correcciones

1. Si se aplicó binding o se creó SA: comprobar lectura del secreto desde CLI:

```powershell
gcloud secrets versions access latest --secret="firebase-service-account" --project=amani-160bf
```

Salida esperada: el JSON del service account (texto plano).

2. Arrancar la aplicación con `FIREBASE_ENABLED=true` y `GOOGLE_APPLICATION_CREDENTIALS` apuntando al key del SA (si usas SA). Revisar logs — la inicialización de Firebase deberá mostrar:

```
[Firebase] Cargando credenciales desde Secret Manager (projects/amani-160bf/secrets/firebase-service-account/versions/latest) ...
[Firebase] FirebaseApp inicializada correctamente.
```

3. Verificar que los beans `FirebaseApp`, `FirebaseAuth`, `FirebaseDatabase`, `FirebaseChatService` se instancian sin error.

4. Probar endpoint `/api/auth/firebase-token` y flujos de RTDB (enviar un mensaje y verificar escritura en la RTDB si corresponde).

---

## 8. Historial de cambios (trazabilidad de modificaciones en el repositorio)

Modificaciones aplicadas en este ticket/diagnóstico:

- `src/main/resources/application.properties`
  - + `firebase.enabled=false`

- `src/main/java/com/amani/amaniapirest/configuration/FirebaseConfig.java`
  - + Mejoras en `resolveCredentialsStream()` para probar formatos alternativos y logging ampliado
  - + Inyección de `spring.cloud.gcp.project-id`

- `src/main/java/com/amani/amaniapirest/controllers/login/FirebaseAuthController.java`
  - Cambio a `Optional<FirebaseAuth>` para inyección y manejo de 503

- `src/main/java/com/amani/amaniapirest/listeners/MensajeEventListener.java`
  - Cambio a `Optional<FirebaseChatService>` y `Optional<FirebaseNotificationService>`, protecciones en tiempo de ejecución

> Recomendación: crea una rama nueva para estos cambios y commitea con un mensaje claro, por ejemplo `fix(firebase): tolerate missing firebase credentials in dev`.

---

## 9. Riesgos / notas de seguridad

- Nunca subir claves JSON de service accounts a VCS.
- Asignar siempre el rol mínimo necesario (`roles/secretmanager.secretAccessor`) al service account o usuario que necesite leer secretos.
- Para producción, preferir identidades administradas de la plataforma (Workload Identity para GKE/Cloud Run) en lugar de claves JSON.

---

## 10. Recomendaciones finales y próximos pasos sugeridos

1. Si puedes ejecutar comandos con permisos de Owner/Editor: crea un service account con `roles/secretmanager.secretAccessor` y configura `GOOGLE_APPLICATION_CREDENTIALS` localmente (opción recomendada).
2. Si no tienes permisos: envía al admin el comando del apartado 6 (opción para admin) para que añada el binding a tu usuario o cree un SA para ti.
3. Una vez tengas acceso, habilita `firebase.enabled=true` y prueba nuevamente.
4. Revertir o ajustar el comportamiento de los controllers/listeners opcionales si prefieres que fallen explícitamente cuando Firebase no está presente (según políticas del proyecto). Actualmente se devolvió 503 para endpoints de Firebase para que el resto de la app siga funcionando en dev.

---

Si quieres, puedo:
- Generar el mensaje exacto para enviar al admin (con contexto y riesgos) listo para pegar.
- Crear la rama y el commit con los cambios que apliqué (si me autorizas a hacerlo en el repo). 
- Preparar un pequeño script PowerShell para crear el SA y descargar la key (si tienes permisos).

Indica qué prefieres y procedo.

