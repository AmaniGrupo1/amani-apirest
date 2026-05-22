package com.amani.amaniapirest.services.paciente;

import com.amani.amaniapirest.dto.dtoPaciente.response.PsicologoResponseDTO;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.repository.PsicologoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de solo lectura que expone el directorio de psicólogos al rol paciente.
 *
 * @see com.amani.amaniapirest.dto.dtoPaciente.response.PsicologoResponseDTO
 */
@Service
public class PsicologoService {

    private final PsicologoRepository psicologoRepository;

    /**
     * Ejecuta la operación correspondiente a PsicologoService.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public PsicologoService(PsicologoRepository psicologoRepository) {
        this.psicologoRepository = psicologoRepository;
    }

    public List<PsicologoResponseDTO> findAll() {
        return psicologoRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Obtiene y retorna la información correspondiente.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public PsicologoResponseDTO findById(Long idPsicologo) {
        return toResponse(getPsicologoOrThrow(idPsicologo));
    }

    private Psicologo getPsicologoOrThrow(Long idPsicologo) {
        return psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado con id: " + idPsicologo));
    }

    private PsicologoResponseDTO toResponse(Psicologo psicologo) {
        return new PsicologoResponseDTO(
                psicologo.getEspecialidad(),
                psicologo.getExperiencia(),
                psicologo.getDescripcion(),
                psicologo.getLicencia(),
                psicologo.getCreatedAt(),
                psicologo.getUpdatedAt()
        );
    }
}