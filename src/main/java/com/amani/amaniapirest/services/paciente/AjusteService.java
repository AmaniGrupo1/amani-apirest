package com.amani.amaniapirest.services.paciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.AjusteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.AjusteResponseDTO;
import com.amani.amaniapirest.models.Ajuste;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.AjusteRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para gestionar los ajustes de configuración de los usuarios.
 *
 * <p>Permite crear, consultar, actualizar y eliminar las preferencias personales
 * de cada usuario (idioma, notificaciones, modo oscuro, zona horaria).</p>
 */
@Service
public class AjusteService {

    private final AjusteRepository ajusteRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Construye el servicio inyectando sus repositorios.
     *
     * @param ajusteRepository  repositorio JPA de {@link Ajuste}
     * @param usuarioRepository repositorio JPA de {@link Usuario}
     */
    public AjusteService(AjusteRepository ajusteRepository, UsuarioRepository usuarioRepository) {
        this.ajusteRepository = ajusteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene la lista de todos los ajustes registrados.
     *
     * @return lista de {@link AjusteResponseDTO} con todos los ajustes
     */
    public List<AjusteResponseDTO> findAll() {
        return ajusteRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca un ajuste por su identificador único.
     *
     * @param idAjuste identificador del ajuste
     * @return {@link AjusteResponseDTO} con los datos del ajuste encontrado
     * @throws RuntimeException si no existe un ajuste con el id proporcionado
     */
    public AjusteResponseDTO findById(Long idAjuste) {
        return toResponse(getAjusteOrThrow(idAjuste));
    }

    /**
     * Busca los ajustes de un usuario específico.
     *
     * @param idUsuario identificador del usuario
     * @return {@link AjusteResponseDTO} con los ajustes del usuario
     * @throws RuntimeException si el usuario no tiene ajustes configurados
     */
    public AjusteResponseDTO findByUsuario(Long idUsuario) {
        return ajusteRepository.findByUsuario_IdUsuario(idUsuario)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Ajustes no encontrados para el usuario: " + idUsuario));
    }

    /**
     * Crea un nuevo registro de ajustes para un usuario.
     *
     * @param request {@link AjusteRequestDTO} con las preferencias del usuario
     * @return {@link AjusteResponseDTO} con los ajustes creados
     * @throws RuntimeException si el usuario referenciado no existe
     */
    public AjusteResponseDTO create(AjusteRequestDTO request) {
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());

        Ajuste ajuste = new Ajuste();
        ajuste.setUsuario(usuario);
        ajuste.setIdioma(request.getIdioma());
        ajuste.setNotificaciones(request.getNotificaciones() != null ? request.getNotificaciones() : true);
        ajuste.setDarkMode(request.getDarkMode() != null ? request.getDarkMode() : false);
        ajuste.setTimezone(request.getTimezone());
        ajuste.setUpdatedAt(LocalDateTime.now());

        return toResponse(ajusteRepository.save(ajuste));
    }

    /**
     * Actualiza los ajustes de configuración existentes.
     *
     * @param idAjuste identificador del ajuste a actualizar
     * @param request  {@link AjusteRequestDTO} con las nuevas preferencias
     * @return {@link AjusteResponseDTO} con los ajustes actualizados
     * @throws RuntimeException si el ajuste o el usuario referenciado no existen
     */
    public AjusteResponseDTO update(Long idAjuste, AjusteRequestDTO request) {
        Ajuste ajuste = getAjusteOrThrow(idAjuste);
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());

        ajuste.setUsuario(usuario);
        ajuste.setIdioma(request.getIdioma());
        if (request.getNotificaciones() != null) ajuste.setNotificaciones(request.getNotificaciones());
        if (request.getDarkMode() != null) ajuste.setDarkMode(request.getDarkMode());
        ajuste.setTimezone(request.getTimezone());
        ajuste.setUpdatedAt(LocalDateTime.now());

        return toResponse(ajusteRepository.save(ajuste));
    }

    /**
     * Elimina el ajuste con el identificador indicado.
     *
     * @param idAjuste identificador del ajuste a eliminar
     * @throws RuntimeException si no existe un ajuste con el id proporcionado
     */
    public void delete(Long idAjuste) {
        ajusteRepository.delete(getAjusteOrThrow(idAjuste));
    }

    /**
     * Recupera un ajuste por id o lanza excepción si no existe.
     *
     * @param idAjuste identificador del ajuste
     * @return entidad {@link Ajuste} encontrada
     * @throws RuntimeException si no existe un ajuste con el id proporcionado
     */
    private Ajuste getAjusteOrThrow(Long idAjuste) {
        return ajusteRepository.findById(idAjuste)
                .orElseThrow(() -> new RuntimeException("Ajuste no encontrado con id: " + idAjuste));
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
     * Convierte una entidad {@link Ajuste} en su DTO de respuesta.
     *
     * @param ajuste entidad a convertir
     * @return {@link AjusteResponseDTO} con los datos mapeados
     */
    private AjusteResponseDTO toResponse(Ajuste ajuste) {
        return new AjusteResponseDTO(
                ajuste.getIdAjuste(),
                ajuste.getUsuario() != null ? ajuste.getUsuario().getIdUsuario() : null,
                ajuste.getIdioma(),
                ajuste.isNotificaciones(),
                ajuste.isDarkMode(),
                ajuste.getTimezone(),
                ajuste.getUpdatedAt()
        );
    }
}

