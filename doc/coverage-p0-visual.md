# Cobertura P0 (Visual)

Generado desde `target/site/jacoco/jacoco.csv`.

## Resumen Ejecutivo

| Metrica | Valor | Barra | Estado |
|---|---|---|---|
| Lineas P0 | **36,93%** (363/983) | ███████░░░░░░░░░░░░░ | 🔴 ROJO |
| Ramas P0  | **23,01%** (75/326) | ████░░░░░░░░░░░░░░░░ | 🔴 ROJO |

## Semaforo por clase

| Clase | Lineas | Barra | Ramas | Estado |
|---|---|---|---|---|
| `com.amani.amaniapirest.configuration.JwtAuthFilter` | 80,65% (25/31) | ████████████████░░░░ | 57,14% (8/14) | 🟢 VERDE |
| `com.amani.amaniapirest.configuration.UserDetailsServiceImpl` | 100,00% (6/6) | ████████████████████ | 0,00% (0/0) | 🟢 VERDE |
| `com.amani.amaniapirest.configuration.SecurityConfig` | 100,00% (44/44) | ████████████████████ | 0,00% (0/0) | 🟢 VERDE |
| `com.amani.amaniapirest.configuration.JwtUtil` | 100,00% (23/23) | ████████████████████ | 75,00% (3/4) | 🟢 VERDE |
| `com.amani.amaniapirest.services.psicologo.CitaServicePsicologo` | 3,95% (3/76) | ░░░░░░░░░░░░░░░░░░░░ | 0,00% (0/18) | 🔴 ROJO |
| `com.amani.amaniapirest.controllers.login.HasearContrasenia` | 0,00% (0/6) | ░░░░░░░░░░░░░░░░░░░░ | 0,00% (0/0) | 🔴 ROJO |
| `com.amani.amaniapirest.controllers.login.AuthController` | 66,67% (8/12) | █████████████░░░░░░░ | 50,00% (1/2) | 🟡 AMARILLO |
| `com.amani.amaniapirest.services.serviciosLogin.AuthService` | 45,27% (110/243) | █████████░░░░░░░░░░░ | 48,57% (34/70) | 🔴 ROJO |
| `com.amani.amaniapirest.services.CitaService` | 4,38% (6/137) | ░░░░░░░░░░░░░░░░░░░░ | 0,00% (0/62) | 🔴 ROJO |
| `com.amani.amaniapirest.services.CitaAgendaService` | 32,07% (118/368) | ██████░░░░░░░░░░░░░░ | 18,18% (28/154) | 🔴 ROJO |
| `com.amani.amaniapirest.controllers.controladorPsicologo.CitaControladorPsicologo` | 54,05% (20/37) | ██████████░░░░░░░░░░ | 50,00% (1/2) | 🔴 ROJO |

## Histograma de cobertura de lineas

JwtAuthFilter                                      ████████████████░░░░  80,65% 🟢
UserDetailsServiceImpl                             ████████████████████ 100,00% 🟢
SecurityConfig                                     ████████████████████ 100,00% 🟢
JwtUtil                                            ████████████████████ 100,00% 🟢
CitaServicePsicologo                               ░░░░░░░░░░░░░░░░░░░░   3,95% 🔴
HasearContrasenia                                  ░░░░░░░░░░░░░░░░░░░░   0,00% 🔴
AuthController                                     █████████████░░░░░░░  66,67% 🟡
AuthService                                        █████████░░░░░░░░░░░  45,27% 🔴
CitaService                                        ░░░░░░░░░░░░░░░░░░░░   4,38% 🔴
CitaAgendaService                                  ██████░░░░░░░░░░░░░░  32,07% 🔴
CitaControladorPsicologo                           ██████████░░░░░░░░░░  54,05% 🔴

## Objetivo recomendado

- P0 lineas: >= 80%
- P0 ramas: >= 70%
- Ninguna clase P0 en ROJO

## Como verlo

1. Ejecuta `./mvnw test` (o tus tests objetivo).
2. Ejecuta `bash scripts/generate-p0-coverage-visual.sh`.
3. Abre `target/site/jacoco/index.html` y este markdown para resumen ejecutivo.
