package com.amani.amaniapirest.services.paciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.TicketSoporteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.TicketSoporteResponseDTO;
import com.amani.amaniapirest.enums.EstadoTicketSoporte;
import com.amani.amaniapirest.models.TicketSoporte;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.TicketSoporteRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de negocio para gestionar los tickets de soporte de los usuarios.
 *
 * <p>Permite crear, consultar y listar tickets de soporte, validando
 * la existencia del usuario autenticado y aplicando filtros por estado.</p>
 */
@Service
public class SoporteTicketService {

    private final TicketSoporteRepository ticketSoporteRepository;
    private final UsuarioRepository usuarioRepository;

    public SoporteTicketService(TicketSoporteRepository ticketSoporteRepository,
                                UsuarioRepository usuarioRepository) {
        this.ticketSoporteRepository = ticketSoporteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene la lista de tickets del usuario autenticado, opcionalmente filtrados por estado.
     *
     * @param estado estado por el que filtrar; {@code null} para todos
     * @return lista de {@link TicketSoporteResponseDTO} ordenados del mas reciente al mas antiguo
     */
    public List<TicketSoporteResponseDTO> findMisTickets(EstadoTicketSoporte estado) {
        Usuario usuario = getUsuarioAutenticado();
        List<TicketSoporte> tickets;
        if (estado != null) {
            tickets = ticketSoporteRepository.findByUsuario_IdUsuarioAndEstadoOrderByCreadoEnDesc(
                    usuario.getIdUsuario(), estado);
        } else {
            tickets = ticketSoporteRepository.findByUsuario_IdUsuarioOrderByCreadoEnDesc(
                    usuario.getIdUsuario());
        }
        return tickets.stream().map(this::toResponse).toList();
    }

    /**
     * Busca un ticket por su identificador unico verificando que pertenezca al usuario autenticado.
     *
     * @param idTicket identificador del ticket
     * @return {@link TicketSoporteResponseDTO} con los datos del ticket
     * @throws RuntimeException si no existe el ticket o no pertenece al usuario
     */
    public TicketSoporteResponseDTO findById(Long idTicket) {
        Usuario usuario = getUsuarioAutenticado();
        TicketSoporte ticket = getTicketOrThrow(idTicket);
        if (!ticket.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new RuntimeException("No tienes permiso para ver este ticket");
        }
        return toResponse(ticket);
    }

    /**
     * Crea un nuevo ticket de soporte para el usuario autenticado.
     *
     * @param request {@link TicketSoporteRequestDTO} con los datos del ticket
     * @return {@link TicketSoporteResponseDTO} con los datos creados
     */
    public TicketSoporteResponseDTO create(TicketSoporteRequestDTO request) {
        Usuario usuario = getUsuarioAutenticado();

        TicketSoporte ticket = new TicketSoporte();
        ticket.setTitulo(request.getTitulo());
        ticket.setDescripcion(request.getDescripcion());
        ticket.setTipo(request.getTipo());
        ticket.setCategoria(request.getCategoria());
        ticket.setEstado(EstadoTicketSoporte.abierto);
        ticket.setUsuario(usuario);

        return toResponse(ticketSoporteRepository.save(ticket));
    }

    /**
     * Recupera un ticket por id o lanza excepcion si no existe.
     *
     * @param idTicket identificador del ticket
     * @return entidad {@link TicketSoporte} encontrada
     * @throws RuntimeException si no existe un ticket con el id proporcionado
     */
    private TicketSoporte getTicketOrThrow(Long idTicket) {
        return ticketSoporteRepository.findById(idTicket)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con id: " + idTicket));
    }

    /**
     * Obtiene el usuario autenticado desde el contexto de seguridad.
     *
     * @return entidad {@link Usuario} autenticada
     * @throws RuntimeException si el usuario no esta autenticado o no existe
     */
    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("Usuario no autenticado");
        }
        String email = auth.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    }

    /**
     * Convierte una entidad {@link TicketSoporte} en su DTO de respuesta.
     *
     * @param ticket entidad a convertir
     * @return {@link TicketSoporteResponseDTO} con los datos mapeados
     */
    private TicketSoporteResponseDTO toResponse(TicketSoporte ticket) {
        return TicketSoporteResponseDTO.builder()
                .idTicket(ticket.getIdTicket())
                .titulo(ticket.getTitulo())
                .descripcion(ticket.getDescripcion())
                .tipo(ticket.getTipo())
                .categoria(ticket.getCategoria())
                .estado(ticket.getEstado())
                .creadoEn(ticket.getCreadoEn())
                .actualizadoEn(ticket.getActualizadoEn())
                .cerradoEn(ticket.getCerradoEn())
                .idUsuario(ticket.getUsuario() != null ? ticket.getUsuario().getIdUsuario() : null)
                .nombreUsuario(ticket.getUsuario() != null
                        ? ticket.getUsuario().getNombre() + " " + ticket.getUsuario().getApellido()
                        : null)
                .build();
    }
}
