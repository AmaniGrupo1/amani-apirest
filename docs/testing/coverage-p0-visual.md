# Cobertura P0 (Visual)

Generado desde `target/site/jacoco/jacoco.csv`.

## Resumen Ejecutivo

| Metrica | Valor | Barra | Estado |
|---|---|---|---|---|
| Lineas P0 | **52,74%** (616/1168) | ██████████░░░░░░░░░░ | 🔴 ROJO |
| Ramas P0  | **27,84%** (98/352) | █████░░░░░░░░░░░░░░░ | 🔴 ROJO |

## Semaforo por clase

| Clase | Lineas | Barra | Ramas | Estado |
|---|---|---|---|---|
| `com.amani.amaniapirest.configuration.JwtAuthFilter` | 70,73% (29/41) | ██████████████░░░░░░ | 50,00% (9/18) | 🟡 AMARILLO |
| `com.amani.amaniapirest.configuration.UserDetailsServiceImpl` | 0,00% (0/6) | ░░░░░░░░░░░░░░░░░░░░ | 0,00% (0/0) | 🔴 ROJO |
| `com.amani.amaniapirest.configuration.SecurityConfig` | 84,81% (67/79) | ████████████████░░░░ | 0,00% (0/0) | 🟢 VERDE |
| `com.amani.amaniapirest.configuration.JwtUtil` | 100,00% (23/23) | ████████████████████ | 75,00% (3/4) | 🟢 VERDE |
| `com.amani.amaniapirest.services.psicologo.CitaServicePsicologo` | 63,16% (48/76) | ████████████░░░░░░░░ | 11,11% (2/18) | 🟡 AMARILLO |
| `com.amani.amaniapirest.controllers.login.FirebaseAuthController` | 85,71% (18/21) | █████████████████░░░ | 100,00% (2/2) | 🟢 VERDE |
| `com.amani.amaniapirest.controllers.login.AuthController` | 66,67% (8/12) | █████████████░░░░░░░ | 50,00% (1/2) | 🟡 AMARILLO |
| `com.amani.amaniapirest.services.serviciosLogin.AuthService` | 70,66% (183/259) | ██████████████░░░░░░ | 52,86% (37/70) | 🟡 AMARILLO |
| `com.amani.amaniapirest.services.CitaService` | 4,38% (6/137) | ░░░░░░░░░░░░░░░░░░░░ | 0,00% (0/62) | 🔴 ROJO |
| `com.amani.amaniapirest.services.CitaAgendaService` | 39,67% (146/368) | ███████░░░░░░░░░░░░░ | 24,68% (38/154) | 🔴 ROJO |
| `com.amani.amaniapirest.services.paciente.CitaService` | 75,29% (64/85) | ███████████████░░░░░ | 31,25% (5/16) | 🟡 AMARILLO |
| `com.amani.amaniapirest.controllers.controladorPsicologo.CitaControladorPsicologo` | 48,78% (20/41) | █████████░░░░░░░░░░░ | 50,00% (1/2) | 🔴 ROJO |
| `com.amani.amaniapirest.controllers.controladorPaciente.CitaController` | 20,00% (4/20) | ████░░░░░░░░░░░░░░░░ | 0,00% (0/4) | 🔴 ROJO |

## Histograma de cobertura de lineas

JwtAuthFilter                                      ██████████████░░░░░░  70,73% 🟡
UserDetailsServiceImpl                             ░░░░░░░░░░░░░░░░░░░░   0,00% 🔴
SecurityConfig                                     ████████████████░░░░  84,81% 🟢
JwtUtil                                            ████████████████████ 100,00% 🟢
CitaServicePsicologo                               ████████████░░░░░░░░  63,16% 🟡
FirebaseAuthController                             █████████████████░░░  85,71% 🟢
AuthController                                     █████████████░░░░░░░  66,67% 🟡
AuthService                                        ██████████████░░░░░░  70,66% 🟡
CitaService                                        ░░░░░░░░░░░░░░░░░░░░   4,38% 🔴
CitaAgendaService                                  ███████░░░░░░░░░░░░░  39,67% 🔴
CitaService                                        ███████████████░░░░░  75,29% 🟡
CitaControladorPsicologo                           █████████░░░░░░░░░░░  48,78% 🔴
CitaController                                     ████░░░░░░░░░░░░░░░░  20,00% 🔴

## Objetivo recomendado

- P0 lineas: >= 80%
- P0 ramas: >= 70%
- Ninguna clase P0 en ROJO

## Como verlo

1. Ejecuta `./mvnw test jacoco:report`.
2. Ejecuta `bash scripts/generate-p0-coverage-visual.sh`.
3. Abre `target/site/jacoco/index.html` y este markdown para resumen ejecutivo.
