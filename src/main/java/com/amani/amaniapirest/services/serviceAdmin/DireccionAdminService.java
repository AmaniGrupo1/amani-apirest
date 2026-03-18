package com.amani.amaniapirest.services.serviceAdmin;


import com.amani.amaniapirest.dto.dtoAdmin.response.DireccionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.DireccionRequestDTO;
import com.amani.amaniapirest.models.Direccion;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.repository.DireccionRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Servicio de administración para gestionar las direcciones de todos los usuarios.
 */
@Service
public class DireccionAdminService {

    private final DireccionRepository direccionRepository;
    private final PacientesRepository pacientesRepository;

    public DireccionAdminService(DireccionRepository direccionRepository, PacientesRepository pacientesRepository) {
        this.direccionRepository = direccionRepository;
        this.pacientesRepository = pacientesRepository;
    }

    public List<DireccionAdminResponseDTO> findAll() {
        return direccionRepository.findAll().stream().map(this::toResponse).toList();
    }

    public DireccionAdminResponseDTO findById(Long idDireccion) {
        return toResponse(getDireccionOrThrow(idDireccion));
    }

    public DireccionAdminResponseDTO create(DireccionRequestDTO direccion) {
        // Validamos paciente
        Paciente paciente = pacientesRepository.findById(direccion.getIdPaciente()).orElse(null);
        Direccion direccion1 = new Direccion();
        direccion1.setPaciente(paciente);
        direccion1.setCalle(direccion.getCalle());
        direccion1.setCiudad(direccion.getCiudad());
        direccion1.setProvincia(direccion.getProvincia());
        direccion1.setCodigoPostal(direccion.getCodigoPostal());
        direccion1.setPais(direccion.getPais());
        direccion1.setDescripcion(direccion.getDescripcion());

        return toResponse(direccionRepository.save(direccion1));
    }

    public DireccionAdminResponseDTO update(Direccion direccion) {
        Direccion existing = getDireccionOrThrow(direccion.getIdDireccion());

        // Validamos paciente
        Paciente paciente = pacientesRepository.findById(direccion.getPaciente().getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + direccion.getPaciente().getUsuario().getIdUsuario()));

        existing.setPaciente(paciente);
        existing.setCalle(direccion.getCalle());
        existing.setCiudad(direccion.getCiudad());
        existing.setProvincia(direccion.getProvincia());
        existing.setCodigoPostal(direccion.getCodigoPostal());
        existing.setPais(direccion.getPais());
        existing.setDescripcion(direccion.getDescripcion());

        return toResponse(direccionRepository.save(existing));
    }

    public void delete(Long idDireccion) {
        direccionRepository.delete(getDireccionOrThrow(idDireccion));
    }

    private Direccion getDireccionOrThrow(Long idDireccion) {
        return direccionRepository.findById(idDireccion)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con id: " + idDireccion));
    }

    private DireccionAdminResponseDTO toResponse(Direccion direccion) {
        return new DireccionAdminResponseDTO(
                direccion.getPaciente().getUsuario().getNombre(),
                direccion.getPaciente().getUsuario().getApellido(),
                direccion.getCalle(),
                direccion.getCiudad(),
                direccion.getProvincia(),
                direccion.getCodigoPostal(),
                direccion.getPais(),
                direccion.getDescripcion(),
                direccion.getPaciente().getUpdatedAt(),
                direccion.getPaciente().getUpdatedAt()

        );
    }
}
