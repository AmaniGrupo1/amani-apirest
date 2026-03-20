package com.amani.amaniapirest.services.psicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.CitaPsicologoResponseDTO;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para la gestión de citas desde la perspectiva del psicólogo.
 */
@Service
public class CitaServicePsicologo {

    @Autowired
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
}