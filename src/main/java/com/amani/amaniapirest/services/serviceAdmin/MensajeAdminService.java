package com.amani.amaniapirest.services.serviceAdmin;


import com.amani.amaniapirest.dto.dtoPaciente.request.MensajeRequestDTO;
import com.amani.amaniapirest.dto.dtoAdmin.response.MensajeAdminResponseDTO;
import com.amani.amaniapirest.models.Mensaje;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.MensajeRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de administración para consultar y gestionar todos los mensajes del sistema.
 */
@Service
public class MensajeAdminService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;

    public MensajeAdminService(MensajeRepository mensajeRepository, UsuarioRepository usuarioRepository) {
        this.mensajeRepository = mensajeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<MensajeAdminResponseDTO> findAll() {
        return mensajeRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    public MensajeAdminResponseDTO findById(Long idMensaje) {
        return toResponse(getMensajeOrThrow(idMensaje));
    }

    public MensajeAdminResponseDTO create(MensajeRequestDTO request) {
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

    public MensajeAdminResponseDTO marcarLeido(Long idMensaje) {
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

    private MensajeAdminResponseDTO toResponse(Mensaje mensaje) {
        return new MensajeAdminResponseDTO(
                mensaje.getSender().getNombre(),
                mensaje.getSender().getEmail(),
                mensaje.getReceiver().getNombre(),
                mensaje.getReceiver().getEmail(),
                mensaje.getMensaje(),
                mensaje.getEnviadoEn(),
                mensaje.isLeido()
        );
    }
}