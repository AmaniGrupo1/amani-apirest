package com.amani.amaniapirest.services.serviceAdmin;

import com.amani.amaniapirest.dto.dtoPaciente.request.TicketSoporteEstadoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.TicketSoporteResponseDTO;
import com.amani.amaniapirest.enums.EstadoTicketSoporte;
import com.amani.amaniapirest.models.TicketSoporte;
import com.amani.amaniapirest.repository.TicketSoporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de administracion para gestionar todos los tickets de soporte del sistema.
 */
@Service
public class SoporteTicketAdminService {

    private final TicketSoporteRepository ticketSoporteRepository;

    public SoporteTicketAdminService(TicketSoporteRepository ticketSoporteRepository) {
        this.ticketSoporteRepository = ticketSoporteRepository;
    }

    /**
     * Obtiene la lista completa de tickets de soporte ordenados por fecha de creacion descendente.
     *
     * @return lista de {@link TicketSoporteResponseDTO} con todos los tickets
     */
    @Transactional(readOnly = true)
    public List<TicketSoporteResponseDTO> findAll() {
        return ticketSoporteRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getCreadoEn().compareTo(a.getCreadoEn()))
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene la lista de tickets filtrados por estado.
     *
     * @param estado estado por el que filtrar
     * @return lista de {@link TicketSoporteResponseDTO} filtrados
     */
    @Transactional(readOnly = true)
    public List<TicketSoporteResponseDTO> findByEstado(EstadoTicketSoporte estado) {
        return ticketSoporteRepository.findAll()
                .stream()
                .filter(t -> t.getEstado() == estado)
                .sorted((a, b) -> b.getCreadoEn().compareTo(a.getCreadoEn()))
                .map(this::toResponse)
                .toList();
    }

    /**
     * Busca un ticket por su identificador unico.
     *
     * @param idTicket identificador del ticket
     * @return {@link TicketSoporteResponseDTO} con los datos del ticket
     */
    @Transactional(readOnly = true)
    public TicketSoporteResponseDTO findById(Long idTicket) {
        return toResponse(getTicketOrThrow(idTicket));
    }

    /**
     * Actualiza el estado de un ticket existente.
     *
     * @param idTicket identificador del ticket a actualizar
     * @param request  DTO con el nuevo estado
     * @return {@link TicketSoporteResponseDTO} con los datos actualizados
     */
    @Transactional
    public TicketSoporteResponseDTO updateEstado(Long idTicket, TicketSoporteEstadoRequestDTO request) {
        TicketSoporte ticket = getTicketOrThrow(idTicket);
        ticket.setEstado(request.getEstado());
        return toResponse(ticketSoporteRepository.save(ticket));
    }

    /**
     * Elimina un ticket por su identificador.
     *
     * @param idTicket identificador del ticket a eliminar
     */
    @Transactional
    public void delete(Long idTicket) {
        ticketSoporteRepository.delete(getTicketOrThrow(idTicket));
    }

    private TicketSoporte getTicketOrThrow(Long idTicket) {
        return ticketSoporteRepository.findById(idTicket)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con id: " + idTicket));
    }

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
