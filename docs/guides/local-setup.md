# Configuración local

Guía paso a paso para levantar el backend AMANI en tu máquina de desarrollo.

---

## ⚠️ Variables de entorno

!!! warning "No hardcodear secrets"
    Nunca incluyas contraseñas, claves de Firebase ni credenciales de GCP directamente en el código fuente o en commits. Usa variables de entorno, `application-local.yml` o Secret Manager en producción.

| Variable | Descripción | Ejemplo |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring activo | `local` |
| `DB_URL` | URL de PostgreSQL (opcional en local) | `jdbc:postgresql://localhost:5432/psicologia_app` |
| `DB_USERNAME` | Usuario PostgreSQL | `postgres` |
| `DB_PASSWORD` | Contraseña PostgreSQL | *(configurar en local)* |

---

## 📋 Requisitos previos

| Requisito | Versión | Verificar |
|---|---|---|
| Java JDK | 21+ | `java -version` |
| Maven | Wrapper incluido | `./mvnw --version` |
| Docker | 20+ | `docker --version` |
| *(Opcional)* Firebase CLI | 13+ | `firebase --version` |

---

## 🚀 Inicio rápido (1 comando)

```bash
docker compose up -d && ./mvnw spring-boot:run -Dspring.profiles.active=local
```

La aplicación arranca en `http://localhost:8080` con:
- ✅ PostgreSQL en Docker (puerto `5432`)
- ✅ Mailpit para emails (SMTP puerto `1025`, UI en `http://localhost:8025`)
- ✅ Firebase **desactivado** (operaciones de chat/push son no-op)
- ✅ Sin requerir GCP, Secret Manager ni ADC

---

## 🔧 Configuración paso a paso

### 1. Clonar el repositorio

```bash
git clone https://github.com/AmaniGrupo1/amani-apirest.git
cd amani-apirest
```

### 2. Levantar PostgreSQL

```bash
docker compose up -d
```

Esto inicia:
- **PostgreSQL** en el puerto `5432` con la base de datos y schema inicializados
- **Mailpit** en el puerto `8025` (UI) y `1025` (SMTP)

### 3. Arrancar la aplicación

```bash
# Modo local (Firebase desactivado)
./mvnw spring-boot:run -Dspring.profiles.active=local

# O con variable de entorno
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

### 4. Verificar

```bash
# Health check
curl http://localhost:8080/actuator/health

# Swagger UI
open http://localhost:8080/docs
```

---

## 🧪 Perfiles de Spring

| Perfil | Comando | Firebase | Base de datos |
|---|---|---|---|
| `local` | `-Dspring.profiles.active=local` | No-op (desactivado) | PostgreSQL local |
| `local` + emuladores | `firebase.emulator.enabled=true` | Emuladores | PostgreSQL local |
| `gcp` | `-Dspring.profiles.active=gcp` | Real (ADC) | Cloud SQL |
| `test` | `@ActiveProfiles("test")` | No-op (mocks) | H2 en memoria |

---

## 🔥 Usar Firebase Emulators (opcional)

### 1. Instalar Firebase CLI

```bash
npm install -g firebase-tools
firebase login
```

### 2. Iniciar emuladores

```bash
firebase emulators:start
```

Los emuladores arrancan en:
- **Auth Emulator**: `localhost:9099`
- **RTDB Emulator**: `localhost:9000`
- **Storage Emulator**: `localhost:9199`
- **Emulator UI**: `http://localhost:4000`

### 3. Activar emuladores en la aplicación

```bash
./mvnw spring-boot:run -Dspring.profiles.active=local   -Dfirebase.enabled=true   -Dfirebase.emulator.enabled=true
```

---

## 🛠 Troubleshooting

| Problema | Solución |
|---|---|
| `PORT 5432 already in use` | `docker compose down` y reiniciar |
| `FirebaseApp already exists` | Reiniciar la aplicación |
| `PERMISSION_DENIED` en local | Asegurarse de que `firebase.enabled=false` en `application-local.yml` |
| Emuladores no conectan | Verificar que `firebase.emulator.enabled=true` y los puertos coinciden |

---

## 📂 Estructura de perfiles

```
src/main/resources/
├── application.yml           # Configuración base (compartida)
├── application-local.yml     # Perfil LOCAL: sin GCP, sin Firebase
├── application-gcp.yml       # Perfil GCP: ADC, Secret Manager, Firebase real
├── application-test.yml      # Perfil TEST: H2, sin Firebase, sin secrets
└── firebase-rules.json       # Reglas de seguridad RTDB
```

---

## ✅ Verificación final

```bash
# Parar todo
docker compose down

# Parar y eliminar datos
docker compose down -v

# Logs de PostgreSQL
docker compose logs postgres

# Ejecutar tests
./mvnw test

# Compilar sin tests
./mvnw clean package -DskipTests
```
