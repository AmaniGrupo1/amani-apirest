package com.amani.amaniapirest.controllers.notificacion;

import com.amani.amaniapirest.models.Notificacion;
import com.amani.amaniapirest.repository.notificacion.NotificacionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionRepository repo;

    public NotificacionController(NotificacionRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{idUsuario}")
    public List<Notificacion> getNotificaciones(@PathVariable Long idUsuario) {
        return repo.findByUsuario_IdUsuarioOrderByCreadaEnDesc(idUsuario);
    }

    @PutMapping("/leer/{id}")
    public Notificacion marcarLeida(@PathVariable Long id) {
        Notificacion n = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        n.setLeida(true);
        return repo.save(n);
    }
}