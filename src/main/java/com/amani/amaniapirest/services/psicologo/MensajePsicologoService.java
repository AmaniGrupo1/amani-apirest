package com.amani.amaniapirest.services.psicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.MensajeRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.MensajePsicologoResponseDTO;
import com.amani.amaniapirest.models.Mensaje;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.MensajeRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para gestionar la mensajería del psicólogo con sus pacientes.
 */
@Service
public class MensajePsicologoService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;

    public MensajePsicologoService(MensajeRepository mensajeRepository, UsuarioRepository usuarioRepository) {
        this.mensajeRepository = mensajeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<MensajePsicologoResponseDTO> findEnviados(Long idUsuario) {
        return mensajeRepository.findBySender_IdUsuario(idUsuario)
                .stream().map(this::toResponse).toList();
    }

    public List<MensajePsicologoResponseDTO> findRecibidos(Long idUsuario) {
        return mensajeRepository.findByReceiver_IdUsuario(idUsuario)
                .stream().map(this::toResponse).toList();
    }

    public MensajePsicologoResponseDTO create(MensajeRequestDTO request) {
        Usuario sender = getUsuarioOrThrow(request.getIdSender());
        Usuario receiver = getUsuarioOrThrow(request.getIdReceiver());

        Mensaje mensaje = new Mensaje();
        mensaje.setSender(sender);
        mensaje.setReceiver(receiver);
        mensaje.setMensaje(request.getMensaje());
        mensaje.setEnviadoEn(LocalDateTime.now());
        mensaje.setLeido(false);

        return toResponse(mensajeRepository.save(mensaje));
    }

    public MensajePsicologoResponseDTO marcarLeido(Long idMensaje) {
        Mensaje mensaje = getMensajeOrThrow(idMensaje);
        mensaje.setLeido(true);
        return toResponse(mensajeRepository.save(mensaje));
    }

    public void delete(Long idMensaje) {
        mensajeRepository.delete(getMensajeOrThrow(idMensaje));
    }

    /** ---------------- Helpers ---------------- */

    private Mensaje getMensajeOrThrow(Long idMensaje) {
        return mensajeRepository.findById(idMensaje)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado con id: " + idMensaje));
    }

    private Usuario getUsuarioOrThrow(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));
    }

    private MensajePsicologoResponseDTO toResponse(Mensaje mensaje) {
        return new MensajePsicologoResponseDTO(
                mensaje.getSender().getNombre(),
                mensaje.getReceiver().getNombre(),
                mensaje.getMensaje(),
                mensaje.getEnviadoEn(),
                mensaje.isLeido()
        );
    }
}