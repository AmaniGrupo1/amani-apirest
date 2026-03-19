package com.amani.amaniapirest.services.serviceAdmin;

import com.amani.amaniapirest.dto.dtoAdmin.response.DiarioEmocionAdminResponseDTO;
import com.amani.amaniapirest.models.DiarioEmocion;
import com.amani.amaniapirest.repository.DiarioEmocionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de administración para consultar los diarios emocionales de todos los pacientes.
 */
@Service
public class DiarioEmocionAdminService {

    private final DiarioEmocionRepository diarioEmocionRepository;

    public DiarioEmocionAdminService(DiarioEmocionRepository diarioEmocionRepository) {
        this.diarioEmocionRepository = diarioEmocionRepository;
    }

    /** Listado completo de todas las entradas del diario emocional */
    public List<DiarioEmocionAdminResponseDTO> findAll() {
        return diarioEmocionRepository.findAll()
                .stream()
                .map(this::toAdminResponse)
                .collect(Collectors.toList());
    }

    /** Obtener una entrada específica */
    public DiarioEmocionAdminResponseDTO findById(Long idDiario) {
        DiarioEmocion entrada = diarioEmocionRepository.findById(idDiario)
                .orElseThrow(() -> new RuntimeException("Entrada de diario no encontrada con id: " + idDiario));
        return toAdminResponse(entrada);
    }

    private DiarioEmocionAdminResponseDTO toAdminResponse(DiarioEmocion entrada) {
        return new DiarioEmocionAdminResponseDTO(
                entrada.getPaciente().getUsuario().getNombre(),
                entrada.getPaciente().getUsuario().getApellido(),
                entrada.getFecha(),
                entrada.getEmocion(),
                entrada.getIntensidad(),
                entrada.getNota(),
                entrada.getPaciente().getCreatedAt(),
                entrada.getPaciente().getUpdatedAt()
        );
    }
}