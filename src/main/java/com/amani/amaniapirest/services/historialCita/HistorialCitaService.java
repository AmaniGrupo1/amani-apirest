package com.amani.amaniapirest.services.historialCita;

import com.amani.amaniapirest.dto.historialCita.HistorialCitaResponseDTO;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Pago;
import com.amani.amaniapirest.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialCitaService {

    private final CitaRepository citaRepository;

    public List<HistorialCitaResponseDTO> obtenerHistorialCitas() {

        List<Cita> citas = citaRepository.obtenerHistorialCitas();

        return citas.stream()
                .map(this::mapToDTO)
                .toList();
    }

    private HistorialCitaResponseDTO mapToDTO(Cita cita) {

        Pago pago = cita.getPago();

        return new HistorialCitaResponseDTO(
                cita.getIdCita(),
                cita.getStartDatetime(),
                cita.getDurationMinutes(),
                cita.getEstado().name(),
                cita.getMotivo(),
                cita.getModalidad().name(),

                cita.getPaciente().getUsuario().getNombre(),

                cita.getPsicologo().getUsuario().getNombre(),

                cita.getTipoTerapia().getNombre(),

                cita.getTipoTerapia().getPrecio(),

                pago != null
                        ? pago.getEstadoPago().name()
                        : "SIN_PAGO"
        );
    }
}