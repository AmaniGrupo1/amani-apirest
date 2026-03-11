package com.amani.amaniapirest.services;

import com.amani.amaniapirest.dto.dtoAdmin.request.PsicologoAdminRequestDTO;
import com.amani.amaniapirest.dto.dtoAdmin.response.PsicologoAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.request.PsicologoSelfRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PsicologoSelfResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.PsicologoResponseDTO;
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
 * de psicólogo con métodos específicos por rol: administrador (acceso total)
 * y psicólogo (autogestión de su propio perfil).</p>
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

    // =========================================================
    // MÉTODOS PARA ROL: ADMIN
    // =========================================================

    /**
     * Obtiene la lista completa de psicólogos (vista de administrador).
     *
     * @return lista de {@link PsicologoAdminResponseDTO} con todos los psicólogos
     */
    public List<PsicologoAdminResponseDTO> findAllAdmin() {
        return psicologoRepository.findAll().stream().map(this::toAdminResponse).toList();
    }

    /**
     * Busca un psicólogo por su id (vista de administrador).
     *
     * @param idPsicologo identificador del psicólogo
     * @return {@link PsicologoAdminResponseDTO} con los datos completos
     * @throws RuntimeException si no existe el psicólogo
     */
    public PsicologoAdminResponseDTO findByIdAdmin(Long idPsicologo) {
        return toAdminResponse(getPsicologoOrThrow(idPsicologo));
    }

    /**
     * Crea un nuevo psicólogo desde la vista de administrador.
     *
     * @param request {@link PsicologoAdminRequestDTO} con los datos del psicólogo
     * @return {@link PsicologoAdminResponseDTO} con los datos del psicólogo creado
     * @throws RuntimeException si el usuario referenciado no existe
     */
    public PsicologoAdminResponseDTO createAdmin(PsicologoAdminRequestDTO request) {
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());

        Psicologo psicologo = new Psicologo();
        psicologo.setUsuario(usuario);
        psicologo.setEspecialidad(request.getEspecialidad());
        psicologo.setExperiencia(request.getExperiencia() != null ? request.getExperiencia() : 0);
        psicologo.setDescripcion(request.getDescripcion());
        psicologo.setLicencia(request.getLicencia());
        psicologo.setCreatedAt(LocalDateTime.now());
        psicologo.setUpdatedAt(LocalDateTime.now());

        return toAdminResponse(psicologoRepository.save(psicologo));
    }

    /**
     * Actualiza un psicólogo existente desde la vista de administrador.
     *
     * @param idPsicologo identificador del psicólogo a actualizar
     * @param request     {@link PsicologoAdminRequestDTO} con los nuevos datos
     * @return {@link PsicologoAdminResponseDTO} con los datos actualizados
     * @throws RuntimeException si el psicólogo o el usuario no existen
     */
    public PsicologoAdminResponseDTO updateAdmin(Long idPsicologo, PsicologoAdminRequestDTO request) {
        Psicologo psicologo = getPsicologoOrThrow(idPsicologo);
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());

        psicologo.setUsuario(usuario);
        psicologo.setEspecialidad(request.getEspecialidad());
        psicologo.setExperiencia(request.getExperiencia() != null ? request.getExperiencia() : psicologo.getExperiencia());
        psicologo.setDescripcion(request.getDescripcion());
        psicologo.setLicencia(request.getLicencia());
        psicologo.setUpdatedAt(LocalDateTime.now());

        return toAdminResponse(psicologoRepository.save(psicologo));
    }

    // =========================================================
    // MÉTODOS PARA ROL: PSICÓLOGO (autogestión)
    // =========================================================

    /**
     * Permite al psicólogo consultar su propio perfil profesional.
     *
     * @param idPsicologo identificador del psicólogo autenticado
     * @return {@link PsicologoSelfResponseDTO} con los datos del perfil propio
     * @throws RuntimeException si no existe el psicólogo
     */
    public PsicologoSelfResponseDTO findSelf(Long idPsicologo) {
        return toSelfResponse(getPsicologoOrThrow(idPsicologo));
    }

    /**
     * Permite al psicólogo actualizar su propio perfil profesional.
     *
     * @param idPsicologo identificador del psicólogo autenticado
     * @param request     {@link PsicologoSelfRequestDTO} con los nuevos datos del perfil
     * @return {@link PsicologoSelfResponseDTO} con los datos actualizados
     * @throws RuntimeException si no existe el psicólogo
     */
    public PsicologoSelfResponseDTO updateSelf(Long idPsicologo, PsicologoSelfRequestDTO request) {
        Psicologo psicologo = getPsicologoOrThrow(idPsicologo);

        psicologo.setEspecialidad(request.getEspecialidad());
        psicologo.setExperiencia(request.getExperiencia() != null ? request.getExperiencia() : psicologo.getExperiencia());
        psicologo.setDescripcion(request.getDescripcion());
        psicologo.setLicencia(request.getLicencia());
        psicologo.setUpdatedAt(LocalDateTime.now());

        return toSelfResponse(psicologoRepository.save(psicologo));
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

    // =========================================================
    // MÉTODOS PRIVADOS DE MAPEO
    // =========================================================

    /**
     * Convierte una entidad {@link Psicologo} en {@link PsicologoAdminResponseDTO}.
     *
     * @param psicologo entidad a convertir
     * @return DTO con la vista completa para administrador
     */
    private PsicologoAdminResponseDTO toAdminResponse(Psicologo psicologo) {
        Usuario u = psicologo.getUsuario();
        return new PsicologoAdminResponseDTO(
                psicologo.getIdPsicologo(),
                u != null ? u.getIdUsuario() : null,
                u != null ? u.getNombre() : null,
                u != null ? u.getApellido() : null,
                u != null ? u.getEmail() : null,
                psicologo.getEspecialidad(),
                psicologo.getExperiencia(),
                psicologo.getDescripcion(),
                psicologo.getLicencia(),
                psicologo.getCreatedAt(),
                psicologo.getUpdatedAt()
        );
    }

    /**
     * Convierte una entidad {@link Psicologo} en {@link PsicologoSelfResponseDTO}.
     *
     * @param psicologo entidad a convertir
     * @return DTO con los datos del perfil propio del psicólogo
     */
    private PsicologoSelfResponseDTO toSelfResponse(Psicologo psicologo) {
        return new PsicologoSelfResponseDTO(
                psicologo.getIdPsicologo(),
                psicologo.getEspecialidad(),
                psicologo.getExperiencia(),
                psicologo.getDescripcion(),
                psicologo.getLicencia()
        );
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

