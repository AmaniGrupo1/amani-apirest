package com.amani.amaniapirest.services.psicologo;

import com.amani.amaniapirest.dto.dtoPsicologo.response.DireccionPsicologoResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.DireccionRequestDTO;
import com.amani.amaniapirest.models.Direccion;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.repository.DireccionRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DireccionPsicologoService {

    private final DireccionRepository direccionRepository;
    private final PacientesRepository pacientesRepository;

    public DireccionPsicologoService(DireccionRepository direccionRepository, PacientesRepository pacientesRepository) {
        this.direccionRepository = direccionRepository;
        this.pacientesRepository = pacientesRepository;
    }

    /** Listar todas las direcciones de un paciente */
    public List<DireccionPsicologoResponseDTO> findByPaciente(Long idPaciente) {
        getPacienteOrThrow(idPaciente);

        return direccionRepository.findByPaciente_IdPaciente(idPaciente)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /** Buscar una dirección por su ID */
    public DireccionPsicologoResponseDTO findById(Long idDireccion) {
        return toResponse(getDireccionOrThrow(idDireccion));
    }

    /** Crear una nueva dirección */
    public DireccionPsicologoResponseDTO create(DireccionRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        Direccion direccion = new Direccion();
        direccion.setPaciente(paciente);
        direccion.setCalle(request.getCalle());
        direccion.setCiudad(request.getCiudad());
        direccion.setProvincia(request.getProvincia());
        direccion.setCodigoPostal(request.getCodigoPostal());
        direccion.setPais(request.getPais());
        direccion.setDescripcion(request.getDescripcion());

        return toResponse(direccionRepository.save(direccion));
    }

    /** Actualizar una dirección existente */
    public DireccionPsicologoResponseDTO update(Long idDireccion, DireccionRequestDTO request) {
        Direccion direccion = getDireccionOrThrow(idDireccion);
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        direccion.setPaciente(paciente);
        direccion.setCalle(request.getCalle());
        direccion.setCiudad(request.getCiudad());
        direccion.setProvincia(request.getProvincia());
        direccion.setCodigoPostal(request.getCodigoPostal());
        direccion.setPais(request.getPais());
        direccion.setDescripcion(request.getDescripcion());

        return toResponse(direccionRepository.save(direccion));
    }

    /** Eliminar una dirección */
    public void delete(Long idDireccion) {
        direccionRepository.delete(getDireccionOrThrow(idDireccion));
    }

    /** ------------------------ Helpers ------------------------ */

    private Direccion getDireccionOrThrow(Long idDireccion) {
        return direccionRepository.findById(idDireccion)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con id: " + idDireccion));
    }

    private Paciente getPacienteOrThrow(Long idPaciente) {
        return pacientesRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));
    }

    private DireccionPsicologoResponseDTO toResponse(Direccion direccion) {
        return new DireccionPsicologoResponseDTO(
                direccion.getPaciente().getUsuario().getNombre(),
                direccion.getPaciente().getUsuario().getApellido(),
                direccion.getCalle(),
                direccion.getCiudad(),
                direccion.getProvincia(),
                direccion.getCodigoPostal(),
                direccion.getPais(),
                direccion.getDescripcion()
        );
    }
}