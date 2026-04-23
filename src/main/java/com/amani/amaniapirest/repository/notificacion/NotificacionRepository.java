package com.amani.amaniapirest.repository.notificacion;

import com.amani.amaniapirest.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuario_IdUsuarioOrderByCreadaEnDesc(Long idUsuario);

    long countByUsuario_IdUsuarioAndLeidaFalse(Long idUsuario);
}