package com.amani.amaniapirest.services.serviceAdmin;

import com.amani.amaniapirest.dto.dtoAdmin.response.DiarioEmocionAdminResponseDTO;
import com.amani.amaniapirest.models.DiarioEmocion;
import com.amani.amaniapirest.repository.DiarioEmocionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de administración para consultar los diarios emocionales de todos los pacientes.
 *
 * <p>Proporciona acceso de solo lectura a los registros del diario emocional desde el
 * panel de administración, exponiendo los datos de emoción, intensidad y notas junto
 * con los datos de identificación del paciente autor.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
@Service
public class DiarioEmocionAdminService {

    private final DiarioEmocionRepository diarioEmocionRepository;

    public DiarioEmocionAdminService(DiarioEmocionRepository diarioEmocionRepository) {
        this.diarioEmocionRepository = diarioEmocionRepository;
    }

    /**
     * Obtiene todas las entradas del diario emocional de todos los pacientes del sistema.
     *
     * @return lista de {@link DiarioEmocionAdminResponseDTO} con el detalle de cada entrada.
     */
    public List<DiarioEmocionAdminResponseDTO> findAll() {
        return diarioEmocionRepository.findAll()
                .stream()
                .map(this::toAdminResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una entrada concreta del diario emocional por su identificador.
     *
     * @param idDiario identificador único de la entrada del diario.
     * @return {@link DiarioEmocionAdminResponseDTO} con el detalle de la entrada.
     * @throws RuntimeException si no existe una entrada con el identificador proporcionado.
     */
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