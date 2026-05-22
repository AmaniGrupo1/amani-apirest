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
 * Servicio de administración para gestionar las direcciones postales de los pacientes.
 *
 * <p>Permite al panel de administración listar, consultar, crear, actualizar y eliminar
 * las direcciones asociadas a un paciente. En la creación y actualización se valida
 * la existencia del paciente referenciado.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
@Service
public class DireccionAdminService {

    private final DireccionRepository direccionRepository;
    private final PacientesRepository pacientesRepository;

    public DireccionAdminService(DireccionRepository direccionRepository, PacientesRepository pacientesRepository) {
        this.direccionRepository = direccionRepository;
        this.pacientesRepository = pacientesRepository;
    }

    /**
     * Obtiene todas las direcciones registradas en el sistema.
     *
     * @return lista de {@link DireccionAdminResponseDTO} con todas las direcciones.
     */
    public List<DireccionAdminResponseDTO> findAll() {
        return direccionRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Obtiene los datos de una dirección identificada por su ID.
     *
     * @param idDireccion identificador único de la dirección.
     * @return {@link DireccionAdminResponseDTO} con los datos de la dirección.
     * @throws RuntimeException si no existe una dirección con el identificador proporcionado.
     */
    public DireccionAdminResponseDTO findById(Long idDireccion) {
        return toResponse(getDireccionOrThrow(idDireccion));
    }

    /**
     * Crea una nueva dirección postal y la asocia al paciente indicado en el DTO.
     *
     * @param direccion DTO con los datos de la nueva dirección y el ID del paciente.
     * @return {@link DireccionAdminResponseDTO} con los datos de la dirección creada.
     */
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

        return toResponse(direccionRepository.save(direccion1));
    }

    /**
     * Actualiza los campos de una dirección existente identificada por el ID de la entidad.
     *
     * @param direccion entidad con el ID de la dirección a actualizar y los nuevos valores.
     * @return {@link DireccionAdminResponseDTO} con los datos actualizados.
     * @throws RuntimeException si la dirección o el paciente referenciado no existen.
     */
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

    /**
     * Elimina una dirección del sistema por su identificador.
     *
     * @param idDireccion identificador de la dirección a eliminar.
     * @throws RuntimeException si no existe una dirección con el identificador proporcionado.
     */
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
