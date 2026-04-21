package com.amani.amaniapirest.services.notificacion;


import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.services.FirebaseNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationServices {

    private final FirebaseNotificationService firebase;


    public void notificar(Usuario usuario, String titulo, String mensaje) {

        if (usuario == null || usuario.getFcmToken() == null) return;

        firebase.enviarPush(
                usuario.getFcmToken(),
                titulo,
                mensaje
        );
    }
}
