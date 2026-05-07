# Modelos de datos

Principales DTOs y entidades utilizados en la API REST.

---

## 👤 Login

### Request

```java
public class LoginRequestDTO {
    private String email;
    private String password;
}
```

### Response

```java
public class LoginResponseDTO {
    private Long idUsuario;
    private String nombre;
    private String rol;
    private String token;
}
```

---

## 📅 Cita

```java
public class CitaDTO {
    private Long idCita;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private String estado;          // PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA
    private String motivo;
    private Long idPaciente;
    private Long idPsicologo;
}
```

---

## 💬 Mensaje

```java
public class MensajeDTO {
    private Long idMensaje;
    private String roomId;
    private Long idRemitente;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private boolean leido;
}
```

---

## 📔 Diario Emocional

```java
public class DiarioEmocionalDTO {
    private Long idEntrada;
    private Long idPaciente;
    private LocalDate fecha;
    private String estadoAnimo;
    private String contenido;
    private Integer intensidad;     // 1–10
}
```

---

## 🏥 Historial Clínico

```java
public class HistorialClinicoDTO {
    private Long idHistorial;
    private Long idPaciente;
    private Long idPsicologo;
    private LocalDate fecha;
    private String diagnostico;
    private String notas;
    private String tratamiento;
}
```

---

## 🔔 Notificación

```java
public class NotificacionDTO {
    private Long idNotificacion;
    private Long idUsuario;
    private String titulo;
    private String cuerpo;
    private boolean leida;
    private LocalDateTime fechaCreacion;
    private String tipo;            // CITA, MENSAJE, SISTEMA
}
```

---

## ⚙️ enums

| Enum | Valores |
|---|---|
| `Rol` | `ADMIN`, `PSICOLOGO`, `PACIENTE` |
| `EstadoCita` | `PENDIENTE`, `CONFIRMADA`, `CANCELADA`, `COMPLETADA` |
| `TipoNotificacion` | `CITA`, `MENSAJE`, `SISTEMA` |
