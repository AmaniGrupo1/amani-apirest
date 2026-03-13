package com.amani.amaniapirest.services.paciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.MensajeRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.MensajeResponseDTO;
import com.amani.amaniapirest.models.Mensaje;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.MensajeRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para gestionar mensajes entre usuarios.
 *
 * <p>Permite enviar, consultar y eliminar mensajes, validando la existencia
 * del remitente y destinatario. También provee consultas por bandeja de
 * entrada y de salida de cada usuario.</p>
 */
@Service
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Construye el servicio inyectando sus repositorios.
     *
     * @param mensajeRepository repositorio JPA de {@link Mensaje}
     * @param usuarioRepository repositorio JPA de {@link Usuario}
     */
    public MensajeService(MensajeRepository mensajeRepository, UsuarioRepository usuarioRepository) {
        this.mensajeRepository = mensajeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene la lista completa de mensajes registrados.
     *
     * @return lista de {@link MensajeResponseDTO} con todos los mensajes
     */
    public List<MensajeResponseDTO> findAll() {
        return mensajeRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca un mensaje por su identificador único.
     *
     * @param idMensaje identificador del mensaje
     * @return {@link MensajeResponseDTO} con los datos encontrados
     * @throws RuntimeException si no existe un mensaje con el id proporcionado
     */
    public MensajeResponseDTO findById(Long idMensaje) {
        return toResponse(getMensajeOrThrow(idMensaje));
    }

    /**
     * Obtiene todos los mensajes enviados por un usuario.
     *
     * @param idUsuario identificador del remitente
     * @return lista de {@link MensajeResponseDTO} enviados por el usuario
     */
    public List<MensajeResponseDTO> findEnviados(Long idUsuario) {
        return mensajeRepository.findBySender_IdUsuario(idUsuario)
                .stream().map(this::toResponse).toList();
    }

    /**
     * Obtiene todos los mensajes recibidos por un usuario.
     *
     * @param idUsuario identificador del destinatario
     * @return lista de {@link MensajeResponseDTO} recibidos por el usuario
     */
    public List<MensajeResponseDTO> findRecibidos(Long idUsuario) {
        return mensajeRepository.findByReceiver_IdUsuario(idUsuario)
                .stream().map(this::toResponse).toList();
    }

    /**
     * Envía un nuevo mensaje entre dos usuarios.
     *
     * @param request {@link MensajeRequestDTO} con los datos del mensaje a enviar
     * @return {@link MensajeResponseDTO} con los datos del mensaje creado
     * @throws RuntimeException si el remitente o destinatario no existen
     */
    public MensajeResponseDTO create(MensajeRequestDTO request) {
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

    /**
     * Marca un mensaje como leído.
     *
     * @param idMensaje identificador del mensaje a marcar
     * @return {@link MensajeResponseDTO} con el estado actualizado
     * @throws RuntimeException si no existe un mensaje con el id proporcionado
     */
    public MensajeResponseDTO marcarLeido(Long idMensaje) {
        Mensaje mensaje = getMensajeOrThrow(idMensaje);
        mensaje.setLeido(true);
        return toResponse(mensajeRepository.save(mensaje));
    }

    /**
     * Elimina el mensaje con el identificador indicado.
     *
     * @param idMensaje identificador del mensaje a eliminar
     * @throws RuntimeException si no existe un mensaje con el id proporcionado
     */
    public void delete(Long idMensaje) {
        mensajeRepository.delete(getMensajeOrThrow(idMensaje));
    }

    /**
     * Recupera un mensaje por id o lanza excepción si no existe.
     *
     * @param idMensaje identificador del mensaje
     * @return entidad {@link Mensaje} encontrada
     * @throws RuntimeException si no existe un mensaje con el id proporcionado
     */
    private Mensaje getMensajeOrThrow(Long idMensaje) {
        return mensajeRepository.findById(idMensaje)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado con id: " + idMensaje));
    }

    /**
     * Recupera un usuario por id o lanza excepción si no existe.
     *
     * @param idUsuario identificador del usuario
     * @return entidad {@link Usuario} encontrada
     * @throws RuntimeException si no existe un usuario con el id proporcionado
     */
    private Usuario getUsuarioOrThrow(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));
    }

    /**
     * Convierte una entidad {@link Mensaje} en su DTO de respuesta.
     *
     * @param mensaje entidad a convertir
     * @return {@link MensajeResponseDTO} con los datos mapeados
     */
    private MensajeResponseDTO toResponse(Mensaje mensaje) {
        return new MensajeResponseDTO(
                mensaje.getSender().getNombre(),
                mensaje.getMensaje(),
                mensaje.getEnviadoEn(),
                mensaje.isLeido()
        );
    }
}

