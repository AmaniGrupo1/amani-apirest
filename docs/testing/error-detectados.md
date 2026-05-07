
## Errores detectados y correcciones

---

### **Problema 1: Constructor de `LoginResponseDTO` no coincide con las llamadas en `AuthService`**

`LoginResponseDTO` tiene 4 campos (`idUsuario`, `nombre`, `rol`, `token`) y por tanto `@AllArgsConstructor` genera un constructor de 4 parámetros. Sin embargo, en `AuthService` se invoca con solo 3 argumentos (falta `token`), lo que provoca un error de compilación en las 4 llamadas al constructor.

**Antes:**
```java
return new LoginResponseDTO(
        usuario.getIdUsuario(),
        usuario.getNombre(),
        usuario.getRol().name()
);
```

**Después:**
```java
return new LoginResponseDTO(
        usuario.getIdUsuario(),
        usuario.getNombre(),
        usuario.getRol().name(),
        null  // token (se asigna más adelante si aplica)
);
```

---

### **Problema 2: Método inexistente `findByPsicologo_IdPsicologo` en `CitaRepository`**

En `CitaRepository` el método se llama `findByPsicologoIdPsicologo` (sin guion bajo), pero tanto `CitaServicePsicologo` como `CitaService` lo invocan como `findByPsicologo_IdPsicologo` (con guion bajo). Ambas formas son válidas en Spring Data JPA, pero deben coincidir con lo declarado en el repositorio.

**Antes (en `CitaServicePsicologo` y `CitaService`):**
```java
citaRepository.findByPsicologo_IdPsicologo(idPsicologo)
```

**Después:**
```java
citaRepository.findByPsicologoIdPsicologo(idPsicologo)
```

---

### **Problema 3: Método `findByStartDatetimeBetweenAndEstado` no declarado en `CitaRepository`**

`CitaRecordatorioScheduler` llama a `findByStartDatetimeBetweenAndEstado(...)` pero este método no existe en `CitaRepository`. Es necesario añadirlo.

**Antes (`CitaRepository`):**
```java
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByPsicologoIdPsicologo(Long idPsicologo);
    List<Cita> findByPaciente_IdPaciente(Long idPaciente);
    // falta el método
}
```

**Después:**
```java
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByPsicologoIdPsicologo(Long idPsicologo);
    List<Cita> findByPaciente_IdPaciente(Long idPaciente);
    List<Cita> findByStartDatetimeBetweenAndEstado(LocalDateTime desde, LocalDateTime hasta, String estado);
}
```

---

### **Problema 4: `@Autowired` en campo `final` con inyección por constructor en `CitaServicePsicologo`**

El campo `citaRepository` está marcado como `final` y a la vez con `@Autowired`. Dado que ya se inyecta a través del constructor, la anotación `@Autowired` sobre el campo es redundante e incorrecta (un campo `final` no puede reasignarse por inyección de campo).

**Antes:**
```java
@Autowired
private final CitaRepository citaRepository;
```
**Después:**
```java
private final CitaRepository citaRepository;
```
