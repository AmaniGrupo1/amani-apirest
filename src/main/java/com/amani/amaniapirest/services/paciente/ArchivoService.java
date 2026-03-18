package com.amani.amaniapirest.services.paciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.ArchivoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.ArchivoResponseDTO;
import com.amani.amaniapirest.models.Archivo;
import com.amani.amaniapirest.models.Sesion;
import com.amani.amaniapirest.repository.ArchivoRepository;
import com.amani.amaniapirest.repository.SesionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Servicio de negocio para gestionar archivos adjuntos de sesiones terapéuticas.
 *
 * <p>Administra la subida, consulta y eliminación de archivos vinculados a
 * sesiones. El contenido se recibe en Base64 y se almacena como bytes en la
 * base de datos.</p>
 */
@Service
public class ArchivoService {

    private final ArchivoRepository archivoRepository;
    private final SesionRepository sesionRepository;

    /**
     * Construye el servicio inyectando sus repositorios.
     *
     * @param archivoRepository repositorio JPA de {@link Archivo}
     * @param sesionRepository  repositorio JPA de {@link Sesion}
     */
    public ArchivoService(ArchivoRepository archivoRepository, SesionRepository sesionRepository) {
        this.archivoRepository = archivoRepository;
        this.sesionRepository = sesionRepository;
    }

    /**
     * Obtiene la lista de todos los archivos registrados.
     *
     * @return lista de {@link ArchivoResponseDTO} con todos los archivos
     */
    public List<ArchivoResponseDTO> findAll() {
        return archivoRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca un archivo por su identificador único.
     *
     * @param idArchivo identificador del archivo
     * @return {@link ArchivoResponseDTO} con los datos del archivo encontrado
     * @throws RuntimeException si no existe un archivo con el id proporcionado
     */
    public ArchivoResponseDTO findById(Long idArchivo) {
        return toResponse(getArchivoOrThrow(idArchivo));
    }

    /**
     * Obtiene todos los archivos asociados a una sesión específica.
     *
     * @param idSesion identificador de la sesión
     * @return lista de {@link ArchivoResponseDTO} de la sesión indicada
     */
    public List<ArchivoResponseDTO> findBySesion(Long idSesion) {
        return archivoRepository.findBySesion_IdSesion(idSesion)
                .stream().map(this::toResponse).toList();
    }

    /**
     * Sube un nuevo archivo adjunto a una sesión.
     * El contenido en Base64 del request es decodificado a bytes antes de persistir.
     *
     * @param request {@link ArchivoRequestDTO} con los datos y contenido del archivo
     * @return {@link ArchivoResponseDTO} con los datos del archivo creado
     * @throws RuntimeException si la sesión referenciada no existe
     */
    public ArchivoResponseDTO create(ArchivoRequestDTO request) {
        Sesion sesion = getSesionOrThrow(request.getIdSesion());

        Archivo archivo = new Archivo();
        archivo.setSesion(sesion);
        archivo.setNombre(request.getNombre());
        archivo.setTipoMime(request.getTipoMime());
        if (request.getDatosBase64() != null) {
            archivo.setDatos(Base64.getDecoder().decode(request.getDatosBase64()));
        }
        archivo.setCreadoEn(LocalDateTime.now());

        return toResponse(archivoRepository.save(archivo));
    }

    /**
     * Elimina el archivo con el identificador indicado.
     *
     * @param idArchivo identificador del archivo a eliminar
     * @throws RuntimeException si no existe un archivo con el id proporcionado
     */
    public void delete(Long idArchivo) {
        archivoRepository.delete(getArchivoOrThrow(idArchivo));
    }

    public byte[] getBytes(Long idArchivo) {
        return getArchivoOrThrow(idArchivo).getDatos();
    }

    /**
     * Recupera un archivo por id o lanza excepción si no existe.
     *
     * @param idArchivo identificador del archivo
     * @return entidad {@link Archivo} encontrada
     * @throws RuntimeException si no existe un archivo con el id proporcionado
     */
    private Archivo getArchivoOrThrow(Long idArchivo) {
        return archivoRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado con id: " + idArchivo));
    }

    /**
     * Recupera una sesión por id o lanza excepción si no existe.
     *
     * @param idSesion identificador de la sesión
     * @return entidad {@link Sesion} encontrada
     * @throws RuntimeException si no existe una sesión con el id proporcionado
     */
    private Sesion getSesionOrThrow(Long idSesion) {
        return sesionRepository.findById(idSesion)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada con id: " + idSesion));
    }

    /**
     * Convierte una entidad {@link Archivo} en su DTO de respuesta.
     * No incluye el contenido binario para mantener respuestas ligeras.
     *
     * @param archivo entidad a convertir
     * @return {@link ArchivoResponseDTO} con los datos mapeados
     */
    private ArchivoResponseDTO toResponse(Archivo archivo) {
        return new ArchivoResponseDTO(
                archivo.getIdArchivo(),
                archivo.getSesion() != null ? archivo.getSesion().getIdSesion() : null,
                archivo.getNombre(),
                archivo.getTipoMime(),
                archivo.getCreadoEn()
        );
    }
}

