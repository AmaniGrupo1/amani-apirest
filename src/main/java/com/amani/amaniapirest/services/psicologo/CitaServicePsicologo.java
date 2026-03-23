package com.amani.amaniapirest.services.psicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.AgendaPsicologoItemDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.CitaPsicologoResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.DisponibilidadPsicologoDTO;
import java.util.ArrayList;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para la gestión de citas desde la perspectiva del psicólogo.
 */
@Service
public class CitaServicePsicologo {

    private final CitaRepository citaRepository;

    public CitaServicePsicologo(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }


    // =========================================
    // LISTAR CITAS DEL PSICÓLOGO
    // =========================================

    public List<CitaPsicologoResponseDTO> findAllByPsicologo(Long idPsicologo) {

        return citaRepository.findByPsicologo_IdPsicologo(idPsicologo)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =========================================
    // OBTENER UNA CITA
    // =========================================

    public CitaPsicologoResponseDTO findByIdPsicologo(Long id) {

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        return toResponse(cita);
    }

    // =========================================
    // ACTUALIZAR ESTADO
    // =========================================

    public CitaPsicologoResponseDTO updateEstadoCita(Long id, CitaRequestDTO request) {

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setEstado(request.getEstado());
        cita.setUpdatedAt(LocalDateTime.now());

        return toResponse(citaRepository.save(cita));
    }

    // =========================================
    // AGENDA MENSUAL DEL PSICÓLOGO
    // =========================================

    // Agenda mensual consolidada del psicólogo
    public List<AgendaPsicologoItemDTO> getAgendaPsicologoMes(Long idPsicologo, String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        // Buscar citas del psicólogo en ese rango
        List<Cita> citas = citaRepository.findByPsicologo_IdPsicologo(idPsicologo).stream()
                .filter(c -> !c.getStartDatetime().isBefore(start) && !c.getStartDatetime().isAfter(end))
                .collect(Collectors.toList());
        // Mapear a DTO
        return citas.stream().map(cita -> new AgendaPsicologoItemDTO(
                cita.getStartDatetime().toLocalDate(),
                cita.getStartDatetime().toLocalTime(),
                cita.getStartDatetime().toLocalTime().plusMinutes(cita.getDurationMinutes()),
                "cita",
                cita.getMotivo(),
                cita.getIdCita()
        )).collect(Collectors.toList());
    }

    // =========================================
    // DISPONIBILIDAD DEL PSICÓLOGO
    // =========================================

    // Disponibilidad diaria del psicólogo
    public DisponibilidadPsicologoDTO getDisponibilidadPsicologo(Long idPsicologo, LocalDate fecha) {
        // Franja de trabajo por defecto: 08:00 a 20:00
        LocalTime inicioJornada = LocalTime.of(8, 0);
        LocalTime finJornada = LocalTime.of(20, 0);
        // Obtener citas del psicólogo para ese día
        List<Cita> citas = citaRepository.findByPsicologo_IdPsicologo(idPsicologo).stream()
                .filter(c -> c.getStartDatetime().toLocalDate().equals(fecha))
                .collect(Collectors.toList());
        // Construir franjas ocupadas
        List<DisponibilidadPsicologoDTO.FranjaDisponible> ocupadas = new ArrayList<>();
        for (Cita cita : citas) {
            LocalTime ini = cita.getStartDatetime().toLocalTime();
            LocalTime fin = ini.plusMinutes(cita.getDurationMinutes());
            ocupadas.add(new DisponibilidadPsicologoDTO.FranjaDisponible(ini, fin));
        }
        // Calcular franjas libres
        List<DisponibilidadPsicologoDTO.FranjaDisponible> libres = new ArrayList<>();
        LocalTime cursor = inicioJornada;
        ocupadas.sort((a, b) -> a.getHoraInicio().compareTo(b.getHoraInicio()));
        for (DisponibilidadPsicologoDTO.FranjaDisponible franja : ocupadas) {
            if (cursor.isBefore(franja.getHoraInicio())) {
                libres.add(new DisponibilidadPsicologoDTO.FranjaDisponible(cursor, franja.getHoraInicio()));
            }
            if (cursor.isBefore(franja.getHoraFin())) {
                cursor = franja.getHoraFin();
            }
        }
        if (cursor.isBefore(finJornada)) {
            libres.add(new DisponibilidadPsicologoDTO.FranjaDisponible(cursor, finJornada));
        }
        return new DisponibilidadPsicologoDTO(libres);
    }

    // =========================================
    // MAPPER ENTITY → DTO
    // =========================================

    private CitaPsicologoResponseDTO toResponse(Cita cita) {

        return new CitaPsicologoResponseDTO(

                cita.getIdCita(),
                cita.getPaciente().getIdPaciente(),

                cita.getPaciente().getUsuario().getNombre(),
                cita.getPaciente().getUsuario().getApellido(),

                cita.getStartDatetime(),
                cita.getDurationMinutes(),

                cita.getEstado().name(),
                cita.getMotivo()
        );
    }

    // =========================================
    // PARSE ESTADO
    // =========================================

    private EstadoCita parseEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            return EstadoCita.pendiente;
        }

        try {
            return EstadoCita.valueOf(estado.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + estado);
        }
    }

    // Cancela una cita (cambia su estado a cancelada)
    public void cancelarCita(Long idCita) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setEstado(com.amani.amaniapirest.enums.EstadoCita.cancelada);
        cita.setUpdatedAt(java.time.LocalDateTime.now());
        citaRepository.save(cita);
    }

    // Stub: Actualiza el horario del psicólogo
    public void actualizarHorarioPsicologo(Long idPsicologo, Object horarioRequest) {
        // TODO: Implementar lógica real cuando exista entidad/tabla de horario
    }

    // Stub: Agrega un día no disponible
    public void agregarDiaNoDisponible(Long idPsicologo, Object diaNoDisponibleRequest) {
        // TODO: Implementar lógica real cuando exista entidad/tabla de bloqueos
    }

    // Stub: Elimina un día no disponible
    public void eliminarDiaNoDisponible(Long idPsicologo, String fecha) {
        // TODO: Implementar lógica real cuando exista entidad/tabla de bloqueos
    }
}