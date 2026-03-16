package com.amani.amaniapirest.services.psicologo;

import com.amani.amaniapirest.dto.dtoPsicologo.response.DiarioEmocionalPsicologoDTO;
import com.amani.amaniapirest.models.DiarioEmocion;
import com.amani.amaniapirest.repository.DiarioEmocionRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiarioEmocionPsicologoService {

    private final DiarioEmocionRepository diarioEmocionRepository;
    private final PacientesRepository pacientesRepository;

    public DiarioEmocionPsicologoService(DiarioEmocionRepository diarioEmocionRepository,
                                         PacientesRepository pacientesRepository) {
        this.diarioEmocionRepository = diarioEmocionRepository;
        this.pacientesRepository = pacientesRepository;
    }

    /** Listado de todas las entradas de un paciente para el psicólogo */
    public List<DiarioEmocionalPsicologoDTO> findByPaciente(Long idPaciente) {
        return diarioEmocionRepository.findByPaciente_IdPaciente(idPaciente)
                .stream()
                .map(this::toPsicologoResponse)
                .collect(Collectors.toList());
    }

    /** Obtener una entrada específica de un paciente */
    public DiarioEmocionalPsicologoDTO findById(Long idDiario) {
        DiarioEmocion entrada = diarioEmocionRepository.findById(idDiario)
                .orElseThrow(() -> new RuntimeException("Entrada de diario no encontrada con id: " + idDiario));
        return toPsicologoResponse(entrada);
    }

    private DiarioEmocionalPsicologoDTO toPsicologoResponse(DiarioEmocion entrada) {
        return new DiarioEmocionalPsicologoDTO(
                entrada.getIdDiario(),
                entrada.getPaciente().getIdPaciente(),
                entrada.getPaciente().getUsuario().getNombre(),
                entrada.getPaciente().getUsuario().getApellido(),
                entrada.getFecha(),
                entrada.getEmocion(),
                entrada.getIntensidad(),
                entrada.getNota()
        );
    }
}