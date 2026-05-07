# Problemas conocidos

Registro de errores detectados y las correcciones aplicadas.

!!! tip "Documento original"
    El registro completo está en [`error-detectados.md`](error-detectados.md).

---

## ✅ Correcciones aplicadas

### Constructor de `LoginResponseDTO`

`LoginResponseDTO` tenía 4 campos pero se invocaba con 3 argumentos en `AuthService`.

**Corrección:**
```java
return new LoginResponseDTO(
        usuario.getIdUsuario(),
        usuario.getNombre(),
        usuario.getRol().name(),
        null  // token
);
```

---

### Método inexistente `findByPsicologo_IdPsicologo`

En `CitaRepository` el método se declara como `findByPsicologoIdPsicologo` (sin guion bajo).

**Corrección:**
```java
citaRepository.findByPsicologoIdPsicologo(idPsicologo)
```

---

### Método `findByStartDatetimeBetweenAndEstado` faltante

`CitaRecordatorioScheduler` lo llamaba pero no existía en `CitaRepository`.

**Corrección:**
```java
List<Cita> findByStartDatetimeBetweenAndEstado(
    LocalDateTime desde, LocalDateTime hasta, String estado);
```

---

### `@Autowired` en campo `final`

En `CitaServicePsicologo`, el campo `final` tenía `@Autowired` redundante.

**Corrección:**
```java
private final CitaRepository citaRepository;  // sin @Autowired
```

---

## ⚠️ Pendientes

| # | Problema | Estado |
|---|---|---|
| 1 | `ChatWebSocketController` vacío (sin referencias) | Candidato a eliminación |
| 2 | `serviceAccountKey.json` trackeado en git | Requiere `git rm --cached` |
| 3 | Reglas de Firebase NO desplegadas | Copiar a Firebase Console manualmente |
| 4 | Android escribe directamente en RTDB sin persistir en PostgreSQL | Decisión arquitectónica pendiente |
