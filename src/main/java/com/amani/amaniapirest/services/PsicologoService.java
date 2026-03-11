package com.amani.amaniapirest.services;

import com.amani.amaniapirest.dto.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.response.PsicologoResponseDTO;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para operaciones CRUD de psicólogos.
 *
 * <p>Gestiona la creación, consulta, actualización y eliminación de perfiles
 * de psicólogo, validando la existencia del usuario vinculado y realizando
 * el mapeo entre entidades y DTOs.</p>
 */
@Service
public class PsicologoService {

    private final PsicologoRepository psicologoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Construye el servicio inyectando los repositorios necesarios.
     *
     * @param psicologoRepository repositorio JPA de {@link Psicologo}
     * @param usuarioRepository   repositorio JPA de {@link Usuario}
     */
    public PsicologoService(PsicologoRepository psicologoRepository, UsuarioRepository usuarioRepository) {
        this.psicologoRepository = psicologoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene la lista completa de psicólogos registrados.
     *
     * @return lista de {@link PsicologoResponseDTO} con todos los psicólogos
     */
    public List<PsicologoResponseDTO> findAll() {
        return psicologoRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca un psicólogo por su identificador único.
     *
     * @param idPsicologo identificador del psicólogo a buscar
     * @return {@link PsicologoResponseDTO} con los datos del psicólogo encontrado
     * @throws RuntimeException si no existe un psicólogo con el id proporcionado
     */
    public PsicologoResponseDTO findById(Long idPsicologo) {
        return toResponse(getPsicologoOrThrow(idPsicologo));
    }

    /**
     * Crea un nuevo perfil de psicólogo a partir de los datos del request.
     *
     * @param request {@link PsicologoRequestDTO} con la información del psicólogo a crear
     * @return {@link PsicologoResponseDTO} con los datos del psicólogo creado
     * @throws RuntimeException si el usuario referenciado por {@code idUsuario} no existe
     */
    public PsicologoResponseDTO create(PsicologoRequestDTO request) {
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());

        Psicologo psicologo = new Psicologo();
        psicologo.setUsuario(usuario);
        psicologo.setEspecialidad(request.getEspecialidad());
        psicologo.setExperiencia(request.getExperiencia() != null ? request.getExperiencia() : 0);
        psicologo.setDescripcion(request.getDescripcion());
        psicologo.setLicencia(request.getLicencia());
        psicologo.setCreatedAt(LocalDateTime.now());
        psicologo.setUpdatedAt(LocalDateTime.now());

        return toResponse(psicologoRepository.save(psicologo));
    }

    /**
     * Actualiza los datos de un psicólogo existente.
     *
     * @param idPsicologo identificador del psicólogo a actualizar
     * @param request     {@link PsicologoRequestDTO} con los nuevos datos del psicólogo
     * @return {@link PsicologoResponseDTO} con los datos actualizados
     * @throws RuntimeException si el psicólogo o el usuario referenciado no existen
     */
    public PsicologoResponseDTO update(Long idPsicologo, PsicologoRequestDTO request) {
        Psicologo psicologo = getPsicologoOrThrow(idPsicologo);
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());

        psicologo.setUsuario(usuario);
        psicologo.setEspecialidad(request.getEspecialidad());
        psicologo.setExperiencia(request.getExperiencia() != null ? request.getExperiencia() : psicologo.getExperiencia());
        psicologo.setDescripcion(request.getDescripcion());
        psicologo.setLicencia(request.getLicencia());
        psicologo.setUpdatedAt(LocalDateTime.now());

        return toResponse(psicologoRepository.save(psicologo));
    }

    /**
     * Elimina el psicólogo con el identificador indicado.
     *
     * @param idPsicologo identificador del psicólogo a eliminar
     * @throws RuntimeException si no existe un psicólogo con el id proporcionado
     */
    public void delete(Long idPsicologo) {
        Psicologo psicologo = getPsicologoOrThrow(idPsicologo);
        psicologoRepository.delete(psicologo);
    }

    /**
     * Recupera un psicólogo por id o lanza excepción si no existe.
     *
     * @param idPsicologo identificador del psicólogo
     * @return entidad {@link Psicologo} encontrada
     * @throws RuntimeException si no existe un psicólogo con el id proporcionado
     */
    private Psicologo getPsicologoOrThrow(Long idPsicologo) {
        return psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado con id: " + idPsicologo));
    }

    /**
     * Recupera un usuario por id o lanza excepción si no existe.
     *
     * @param idUsuario identificador del usuario
     * @return entidad {@link Usuario} encontrada
     * @throws RuntimeException si no existe un usuario con el id proporcionado
     */
    private Usuario getUsuarioOrThrow(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));
    }

    /**
     * Convierte una entidad {@link Psicologo} en su DTO de respuesta.
     *
     * @param psicologo entidad a convertir
     * @return {@link PsicologoResponseDTO} con los datos mapeados
     */
    private PsicologoResponseDTO toResponse(Psicologo psicologo) {
        return new PsicologoResponseDTO(
                psicologo.getIdPsicologo(),
                psicologo.getUsuario() != null ? psicologo.getUsuario().getIdUsuario() : null,
                psicologo.getEspecialidad(),
                psicologo.getExperiencia(),
                psicologo.getDescripcion(),
                psicologo.getLicencia(),
                psicologo.getCreatedAt(),
                psicologo.getUpdatedAt()
        );
    }
}

