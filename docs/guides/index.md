# Guías

Documentación operativa para configurar, desplegar y resolver problemas del backend AMANI.

---

## 📖 Contenido

- [Configuración local](local-setup.md) — Desarrollo en local con Docker, PostgreSQL y perfiles de Spring.
- [Despliegue GCP](gcp-deploy.md) — Arquitectura en Cloud Run, Cloud SQL, IAM y Secret Manager.
- [Firebase y Secret Manager](firebase-fix.md) — Fix de integración Firebase, permisos IAM y credenciales.

---

## ⚡ Inicio rápido

```bash
# Desarrollo local (1 comando)
docker compose up -d && ./mvnw spring-boot:run -Dspring.profiles.active=local

# Deploy en GCP
./mvnw spring-boot:run -Dspring.profiles.active=gcp
```
